package eniac.automatichandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.jfree.ui.RefineryUtilities;

import eniac.applicationvm.ApplicationVMDynamicState;
import eniac.automatichandler.connectors.AutomaticHandlerRequestConnector;
import eniac.automatichandler.interfaces.AutomaticHandlerManagementI;
import eniac.automatichandler.interfaces.AutomaticHandlerRequestI;
import eniac.automatichandler.ports.AutomaticHandlerManagementInboundPort;
import eniac.automatichandler.ports.AutomaticHandlerRequestOutboundPort;
import eniac.processorcoordinator.connectors.ProcessorCoordinatorFreqConnector;
import eniac.processorcoordinator.interfaces.ProcessorCoordinatorOrderI;
import eniac.processorcoordinator.ports.ProcessorCoordinatorFreqOutboundPort;
import eniac.processorcoordinator.ports.ProcessorCoordinatorManagementOutboundPort;
import eniac.processorcoordinator.ports.ProcessorCoordinatorOrderInboundPort;
import eniac.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;
import eniac.requestdispatcher.interfaces.RequestDispatcherStateDataConsumerI;
import eniac.requestdispatcher.interfaces.RequestDispatcherStaticStateI;
import eniac.requestdispatcher.ports.RequestDispatcherDynamicStateDataOutboundPort;
import eniac.requestdispatcher.ports.RequestDispatcherStaticStateDataOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.connectors.DataConnector;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.datacenter.connectors.ControlledDataConnector;
import fr.sorbonne_u.datacenter.hardware.processors.UnacceptableFrequencyException;
import fr.sorbonne_u.datacenter.hardware.processors.UnavailableFrequencyException;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMDynamicStateI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMStaticStateI;

public class AutomaticHandler extends AbstractComponent
implements
RequestDispatcherStateDataConsumerI,
ProcessorCoordinatorOrderI{
	
	protected String autoHand_uri;
	
	protected AutomaticHandlerManagementInboundPort automaticHandlerManagementInboundPort;
	
	protected RequestDispatcherDynamicStateDataOutboundPort requestDispatcherDynamicStateDataOutboundPort;
	protected RequestDispatcherStaticStateDataOutboundPort requestDispatcherStaticStateDataOutboundPort;
	protected AutomaticHandlerRequestOutboundPort requestDispatcherHandlerOutboundPort;
	
	protected String requestDispatcherHandlerInboundPortURI;
	protected String requestDispatcherDynamicStateDataInboundPortURI;
	protected String requestDispatcherStaticStateDataInboundPortURI;
	protected String requestDispatcherURI;
	
	protected RequestDispatcherDynamicStateI current_ds;
	
	protected Map<String, Map<String,Set<Integer>>> admissibleFreqCores;
	
	private ComputeTimeCharts chart;
	
	
	private double lower_bound;
	private double upper_bound;
	
	public static final double ALPHA = 0.5;
	
	private double last;
	public static final int MAX_QUEUE = 3;
	private double lavg ;

	protected Map<String, ProcessorCoordinatorFreqOutboundPort> proc_coord_freq_map;
	protected Map<String, ProcessorCoordinatorManagementOutboundPort>
		coord_map;
	
	protected Map<String, String> proc_coord_order_map;

	protected Map<String, String> processorCoordinatorFreqInportURIS;
	
	protected double averageResponseTime;
	protected int modWait = 20;
	
	public AutomaticHandler(String autoHand_uri,
			String managementInboundPortURI,
			String requestDispatcherUri,
			String requestDispatcherHandlerInboundPortURI,
			String requestDispatcherDynamicStateDataInboundPortURI,
			String requestDispatcherStaticStateDataInboundPortURI,
			Double averageResponseTime,
			HashMap<String, String> processorCoordinatorFreqInportURIS) throws Exception{
		
		super(autoHand_uri,1,1);
		assert autoHand_uri!=null;
		assert managementInboundPortURI!=null;
		assert requestDispatcherHandlerInboundPortURI != null;
		assert requestDispatcherDynamicStateDataInboundPortURI != null;
		assert requestDispatcherStaticStateDataInboundPortURI != null;
		assert requestDispatcherUri != null;
		assert processorCoordinatorFreqInportURIS != null;
			
		this.requestDispatcherURI = requestDispatcherUri;
		this.autoHand_uri = autoHand_uri;
		
		this.requestDispatcherDynamicStateDataInboundPortURI = requestDispatcherDynamicStateDataInboundPortURI;
		this.requestDispatcherStaticStateDataInboundPortURI = requestDispatcherStaticStateDataInboundPortURI;
		this.requestDispatcherHandlerInboundPortURI = requestDispatcherHandlerInboundPortURI;
		
		addOfferedInterface(AutomaticHandlerManagementI.class);
		automaticHandlerManagementInboundPort = new AutomaticHandlerManagementInboundPort(autoHand_uri, this);		
		addPort(automaticHandlerManagementInboundPort);
		automaticHandlerManagementInboundPort.publishPort();
		
		addRequiredInterface(AutomaticHandlerRequestI.class);
		requestDispatcherHandlerOutboundPort = new AutomaticHandlerRequestOutboundPort(this);
		addPort(requestDispatcherHandlerOutboundPort);
		requestDispatcherHandlerOutboundPort.publishPort();
		
		addRequiredInterface(RequestDispatcherDynamicStateI.class);
		requestDispatcherDynamicStateDataOutboundPort = new RequestDispatcherDynamicStateDataOutboundPort(this, requestDispatcherUri);
		addPort(requestDispatcherDynamicStateDataOutboundPort);
		requestDispatcherDynamicStateDataOutboundPort.publishPort();
		
		addRequiredInterface(RequestDispatcherStaticStateI.class);
		requestDispatcherStaticStateDataOutboundPort = new RequestDispatcherStaticStateDataOutboundPort(this, requestDispatcherUri);
		addPort(requestDispatcherStaticStateDataOutboundPort);
		requestDispatcherStaticStateDataOutboundPort.publishPort();
				
		toggleLogging();
		toggleTracing();
		
		chart = new ComputeTimeCharts(autoHand_uri, averageResponseTime);
		chart.pack();
		RefineryUtilities.positionFrameRandomly(chart);
		chart.setVisible(true);
		
		lower_bound = averageResponseTime-500;
		upper_bound = averageResponseTime+500;
		
		proc_coord_order_map = new HashMap<>();	
		coord_map = new HashMap<>();
		proc_coord_freq_map = new HashMap<>();
		
		for(String proc_uri : processorCoordinatorFreqInportURIS.keySet()){
			
			ProcessorCoordinatorFreqOutboundPort outport =
					new ProcessorCoordinatorFreqOutboundPort(this);
			this.addPort(outport);
			outport.publishPort();
			
			String inport_uri = autoHand_uri+"_proc_order_inport";
			
			ProcessorCoordinatorOrderInboundPort inport =
					new ProcessorCoordinatorOrderInboundPort(inport_uri, this);
			this.addPort(inport);
			inport.publishPort();
						
			proc_coord_freq_map.put(proc_uri, outport);	
			proc_coord_order_map.put(proc_uri, inport_uri);
		}
		
		this.processorCoordinatorFreqInportURIS = processorCoordinatorFreqInportURIS;
		
		this.averageResponseTime = averageResponseTime;
		
		
	}
		
	
	@Override
	public void finalise() throws Exception {
		
		requestDispatcherHandlerOutboundPort.doDisconnection();
		requestDispatcherDynamicStateDataOutboundPort.doDisconnection();
		requestDispatcherStaticStateDataOutboundPort.doDisconnection();
		
		for(String uri : proc_coord_freq_map.keySet()){
			ProcessorCoordinatorFreqOutboundPort port = proc_coord_freq_map.get(uri);
			if(port.connected())
				port.doDisconnection();
		}

		super.finalise();
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException{

		try {
			requestDispatcherHandlerOutboundPort.unpublishPort();
			requestDispatcherDynamicStateDataOutboundPort.unpublishPort();
			requestDispatcherStaticStateDataOutboundPort.unpublishPort();
			
			for(String uri : proc_coord_freq_map.keySet()){
				ProcessorCoordinatorFreqOutboundPort port = proc_coord_freq_map.get(uri);
				port.unpublishPort();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		super.shutdown();
	}
	
	@Override
	public void start()  {
		
		try {
			
			doPortConnection(requestDispatcherHandlerOutboundPort.getPortURI(),
					requestDispatcherHandlerInboundPortURI,
					AutomaticHandlerRequestConnector.class.getCanonicalName()
				);
			
			doPortConnection(requestDispatcherDynamicStateDataOutboundPort.getPortURI(), 
			requestDispatcherDynamicStateDataInboundPortURI, 
			ControlledDataConnector.class.getCanonicalName()
			);
			
			doPortConnection(requestDispatcherStaticStateDataOutboundPort.getPortURI(),
					requestDispatcherStaticStateDataInboundPortURI,
					DataConnector.class.getCanonicalName()
					);
			
			

			for(String proc_uri : proc_coord_freq_map.keySet()){
				
				ProcessorCoordinatorFreqOutboundPort outport =
						proc_coord_freq_map.get(proc_uri);

				doPortConnection(outport.getPortURI(),
						processorCoordinatorFreqInportURIS.get(proc_uri),
						ProcessorCoordinatorFreqConnector.class.getCanonicalName());
				
				outport.addProcessorCoordinatorOrderOutboundPort(autoHand_uri,
						proc_coord_order_map.get(proc_uri));
						
			}
			
		System.out.println(this.autoHand_uri+" "+requestDispatcherURI);
		requestDispatcherDynamicStateDataOutboundPort.startUnlimitedPushing(500);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void acceptRequestDispatcherStaticData(String requestDisptacherURI,
			RequestDispatcherStaticStateI staticState) throws Exception {
		Map<String, ApplicationVMStaticStateI > avmStaticStateMap = 
				staticState.getAVMStaticStateMap();
		admissibleFreqCores = new HashMap<>();
		for(String avmUri : avmStaticStateMap.keySet()){
			
			//System.out.println(avmUri + " "+avmStaticStateMap.get(avmUri));
			
			ApplicationVMStaticStateI avmStaticState = avmStaticStateMap.get(avmUri);
			
			Map<Integer, Integer> coreMap = avmStaticState.getIdCores();
			for(Integer core : coreMap.keySet()){
				logMessage(avmUri+" : core number "+core+" ; processor number "+String.valueOf(coreMap.get(core)));
				admissibleFreqCores.put(avmUri, avmStaticState.getAdmissibleFreqCores());
			}
			
			
		}
		
	}
	
	private boolean increaseSpeed(Map<String, ApplicationVMDynamicStateI > avmdynamicstate, String avm) {
		
		try {
			
			ApplicationVMDynamicStateI avmDynamicState = avmdynamicstate.get(avm);	
			Map<String, Set<Integer>> admissibleFreqCoresAVM = admissibleFreqCores.get(avm);
			
			for(String proc_uri : avmDynamicState.getProcCurrentFreqCoresMap().keySet()){
				
				//System.out.println(avm+" "+admissibleFreqCoresAVM);
				Set<Integer> admissibleFreq = admissibleFreqCoresAVM.get(proc_uri);
				
				
				
				for(int core : avmDynamicState.getProcCurrentFreqCoresMap().get(proc_uri).keySet()){
					
					int currentFreq = avmDynamicState.getProcCurrentFreqCoresMap().get(proc_uri).get(core);
					int freq = getNextFreq(currentFreq, admissibleFreq);

					if(currentFreq == freq) return false;

					if(proc_coord_freq_map.get(proc_uri)==null)System.out.println(proc_coord_freq_map.get(proc_uri));
						return proc_coord_freq_map.get(proc_uri).setCoreFrequency(autoHand_uri, core, freq);

					}
				}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	private boolean decreaseSpeed(Map<String, ApplicationVMDynamicStateI > avmdynamicstate, String avm) {
		
		ApplicationVMDynamicStateI avmDynamicState = avmdynamicstate.get(avm);
		Map<String, Set<Integer>> admissibleFreqCoresAVM = admissibleFreqCores.get(avm);
		
		for(String proc_uri : avmDynamicState.getProcCurrentFreqCoresMap().keySet()){
			
			Set<Integer> admissibleFreq = admissibleFreqCoresAVM.get(proc_uri);
			
			for(int core : avmDynamicState.getProcCurrentFreqCoresMap().get(proc_uri).keySet()){
				
				int currentFreq = avmDynamicState.getProcCurrentFreqCoresMap().get(proc_uri).get(core);
				int freq = getPreviousFreq(currentFreq, admissibleFreq);
				
				if(currentFreq == freq) return false;
				
				try {
					System.out.println("Decreasing ");
					return this.proc_coord_freq_map.get(proc_uri).setCoreFrequency(autoHand_uri, core, freq);
				
				} catch (UnavailableFrequencyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				
				} catch (UnacceptableFrequencyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	private int wait = 15;

	private RequestDispatcherDynamicStateI currentDynamicState;
	@Override
	public void acceptRequestDispatcherDynamicData(String requestDisptacherURI,
			RequestDispatcherDynamicStateI dynamicState) throws Exception {
		lavg = dynamicState.getAverageRequestTime();
		chart.addData(lavg);
		
		
		currentDynamicState = dynamicState;

		if(wait%modWait == 0) {
			logMessage("Modulation possible");
			modulateAVM(dynamicState, lavg);
		}
		wait++;
		
		current_ds = dynamicState;
		
	}

	
	public void modulateAVM(RequestDispatcherDynamicStateI dynamicstate, double avg) throws Exception {
		Map<String, String> proc_coord_freq_inport_uri_map;
		if(avg > upper_bound) {
			if(last > avg) {
				last = avg;
				
			}
			else{
				
				logMessage("Response time too long: "+avg+"ms (<"+ upper_bound +" ms wanted)");
				
				for(Map.Entry<String, Double> entry: dynamicstate.getScoresMap().entrySet()) {
					
					if(entry.getValue() > MAX_QUEUE) {
					
						//si la fréquence a pu etre augmentee
						if(increaseSpeed(dynamicstate.getAVMDynamicStateMap(), entry.getKey())) {
							logMessage(entry.getKey()+" frequency increased");
							
						}
						//sinon on ajoute 1 core aux avms
						else{
							
							int nbCoreToAdd ;
							
							if(lavg>2*averageResponseTime) {
								nbCoreToAdd = 2;
							}
							else
								nbCoreToAdd = 1;
							
							if( (proc_coord_freq_inport_uri_map=
									requestDispatcherHandlerOutboundPort.addCoreToAvm
									(autoHand_uri, entry.getKey(), nbCoreToAdd))!=null) {
								
								addNewPortCoord(proc_coord_freq_inport_uri_map);
								System.out.println(autoHand_uri+" new cores = new ports added");
								logMessage(entry.getKey()+" 1 core added");	
								//modWait = 50;
							}
							//sinon on ajoute une avm 
							else{
								if(getUnusedAVMs(dynamicstate).size() == 0 && (proc_coord_freq_inport_uri_map=requestDispatcherHandlerOutboundPort.addAVMToRequestDispatcher(autoHand_uri, requestDispatcherURI))!=null){
									addNewPortCoord(proc_coord_freq_inport_uri_map);
									System.out.println(autoHand_uri+" new avm = new ports added");
									//logMessage(avmUri+" added");
									//modWait = 100;
								}
							}

						}
					//on ajoute directement une avm
					}else {
						if(getUnusedAVMs(dynamicstate).size() == 0 && (proc_coord_freq_inport_uri_map=requestDispatcherHandlerOutboundPort.addAVMToRequestDispatcher(autoHand_uri, requestDispatcherURI))!=null){
							addNewPortCoord(proc_coord_freq_inport_uri_map);
							System.out.println(autoHand_uri+" new avm = new ports added");
							//logMessage(avmUri+" added");
							//modWait = 100;
						}
						
					}
				}
			}
		}
		else{
		
			if(avg < lower_bound) {
				if(last < avg) {
					last = avg;
				
				}
				else{
					//System.out.println(dynamicstate.getScoresMap().toString());
					logMessage("Response time too fast: "+avg+"ms (>"+ lower_bound +" ms wanted)");
					if(removeUnusedAVM(dynamicstate)) {
						//modWait = 10;
						return;
					}
					
					String avm = lowestScore(dynamicstate);
					List<String> proc_freqs_list ;
					if(avm != null) {
						
						System.out.println("ENTREE");
						if((proc_freqs_list=requestDispatcherHandlerOutboundPort.removeCoreFromAvm(autoHand_uri, avm))!=null) {
							
							logMessage(avm+" removed 1 core");
						}
						System.out.println("SORTIE");
				
					}
					else{
					
						for(Map.Entry<String, Double> entry: dynamicstate.getScoresMap().entrySet()) {
							if(entry.getValue() < MAX_QUEUE) {
								if(decreaseSpeed(dynamicstate.getAVMDynamicStateMap(), entry.getKey())) {
									logMessage(entry.getKey()+" speed decreased");
								}
							}
						}
					}
					
				}
				
			}
			else{
				last = avg;
				logMessage("Response time correct");
				wait = 19;
				
			}
		}
	
	}
	
	private void addNewPortCoord(Map<String, String> proc_coord_freq_inport_uri_map) {
		for(String proc_uri : proc_coord_freq_inport_uri_map.keySet()){
			
			try {

				ProcessorCoordinatorFreqOutboundPort outport;
				outport = new ProcessorCoordinatorFreqOutboundPort(this);
				addPort(outport);
				outport.publishPort();
				
				doPortConnection(outport.getPortURI(),proc_coord_freq_inport_uri_map.get(proc_uri), 
						ProcessorCoordinatorFreqConnector.class.getCanonicalName());
				
				System.out.println("PUT FREQ OUTPORT "+proc_uri);
				proc_coord_freq_map.put(proc_uri, outport);
				
				String processorCoordinatorOrderInboundPortURI =
						"proc_coord_order_uri_"+autoHand_uri;
				
				ProcessorCoordinatorOrderInboundPort inport = 
						new ProcessorCoordinatorOrderInboundPort(processorCoordinatorOrderInboundPortURI, this);
				addPort(inport);
				inport.publishPort();
				
				proc_coord_order_map.put(proc_uri, processorCoordinatorOrderInboundPortURI);
				
				outport.addProcessorCoordinatorOrderOutboundPort(autoHand_uri,
						processorCoordinatorOrderInboundPortURI);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}


	private String lowestScore(RequestDispatcherDynamicStateI dynamicstate) {
		String avm = null;
		double score = Double.MAX_VALUE;
		for(Map.Entry<String, Double> entry: dynamicstate.getScoresMap().entrySet()) {
			if(entry.getValue() != 0 && entry.getValue() < score) {
				score = entry.getValue();
				avm = entry.getKey();
			}
		}
		return avm;
	}
	
	private boolean removeUnusedAVM(RequestDispatcherDynamicStateI dynamicstate) {
		List<String> unusedavms = getUnusedAVMs(dynamicstate);
		if(unusedavms.size() <= 1) return false;
		for(String avm: unusedavms) {
			List<String> proc_freqs_list;
			try {
				if((proc_freqs_list=requestDispatcherHandlerOutboundPort.removeAVMFromRequestDispatcher(autoHand_uri, requestDispatcherURI, avm))!=null) {
					logMessage(avm+" removed.");
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	private void removeUnusedAVMs(RequestDispatcherDynamicStateI dynamicstate) {
		List<String> unusedavms = getUnusedAVMs(dynamicstate);
		if(unusedavms.size() <= 1) return;
		for(String avm: unusedavms) {
			List <String> proc_freqs_list;
			try {
				if((proc_freqs_list=requestDispatcherHandlerOutboundPort.removeAVMFromRequestDispatcher(autoHand_uri, requestDispatcherURI, avm))!=null) {
					logMessage(avm+" removed.");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private List<String> getUnusedAVMs(RequestDispatcherDynamicStateI dynamicstate) {
		List<String> avms = new ArrayList<>();
		for(Map.Entry<String, Double> entry: dynamicstate.getScoresMap().entrySet()) {
			if(entry.getValue() == 0) {
				avms.add(entry.getKey());
			}
		}
		return avms;
	}
	
	
public int getNextFreq(int currentFreq, Set<Integer> freqs) {
		
		int ret = currentFreq;
		
		
		for(Integer i : freqs){
			if(i>ret)
				ret = i;
		}
		
		for(Integer i : freqs) {
			if(i>currentFreq){
				if(i<ret){
					ret = i;
				}
			}
		}

		return ret;
	}
	
	public int getPreviousFreq(int currentFreq, Set<Integer> freqs) {
	
		int ret = currentFreq;
		
		for(Integer i : freqs) {
			
			if(i<ret)
				ret = i;			
		}
		
		for(Integer i : freqs){
			if(i<currentFreq){
				if(i>ret)
					ret = i;
			}
		}
		
		return ret;
		
	}


	@Override
	public void setCoreFreqNextTime(String procURI, int coreNo, int frequency) throws Exception {

		System.out.println("GOT IT "+procURI+" "+coreNo+" "+frequency);
				
		proc_coord_freq_map.get(procURI).setCoreFrequency(autoHand_uri, coreNo, frequency);

		
	}


	@Override
	public void removeFreq(String procURI) throws Exception {
		
		System.out.println("UNPUBLISH "+procURI);
		
		this.proc_coord_freq_map.get(procURI).unpublishPort();
		
	}


	
	
	

}
