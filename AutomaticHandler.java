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
import eniac.requestdispatcher.connectors.RequestDispatcherManagementConnector;
import eniac.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;
import eniac.requestdispatcher.interfaces.RequestDispatcherStateDataConsumerI;
import eniac.requestdispatcher.interfaces.RequestDispatcherStaticStateI;
import eniac.requestdispatcher.ports.RequestDispatcherDynamicStateDataOutboundPort;
import eniac.requestdispatcher.ports.RequestDispatcherManagementOutboundPort;
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
	
	protected RequestDispatcherManagementOutboundPort dispatcher_management_outport;
	
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

	protected Map<String, String> proc_coord_order_map;

	protected Map<String, String> processorCoordinatorFreqInportURIS;
	
	protected String requestDispatcherManagementInboundPortURI;
	
	protected double averageResponseTime;
	protected int modWait = 20;
	
	public AutomaticHandler(String autoHand_uri,
			String managementInboundPortURI,
			String requestDispatcherUri,
			String requestDispatcherManagementInboundPortURI,
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
		assert requestDispatcherManagementInboundPortURI != null;
		
		this.requestDispatcherManagementInboundPortURI = requestDispatcherManagementInboundPortURI;
			
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
		
		dispatcher_management_outport = new RequestDispatcherManagementOutboundPort(this);
		addPort(dispatcher_management_outport);
		dispatcher_management_outport.publishPort();
		
		
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
			
			doPortConnection(dispatcher_management_outport.getPortURI(),
					requestDispatcherManagementInboundPortURI, 
					RequestDispatcherManagementConnector.class.getCanonicalName());
			
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
			
			
			ApplicationVMStaticStateI avmStaticState = avmStaticStateMap.get(avmUri);
			
			Map<Integer, Integer> coreMap = avmStaticState.getIdCores();
			for(Integer core : coreMap.keySet()){
				logMessage(avmUri+" : core number "+core+" ; processor number "+String.valueOf(coreMap.get(core)));
				admissibleFreqCores.put(avmUri, avmStaticState.getAdmissibleFreqCores());
			}
			
			
		}
		
	}
	
	private boolean increaseSpeed(Map<String, ApplicationVMDynamicStateI > avmdynamicstate) {
		
		List<Boolean> rets = new ArrayList<>();
		Map<String, Integer> proc_map = new HashMap<>();
		Map<String, Map<Integer, Integer>> currentFreqMap = new HashMap<>();
		Map<String, Set<Integer>> admissibleFreqs = new HashMap<>();
 		ApplicationVMDynamicStateI avmDynamicState;
		try {
			
			for(String avmURI : avmdynamicstate.keySet()){
	
				avmDynamicState = avmdynamicstate.get(avmURI);
			
				for(String proc_uri : avmDynamicState.getProcCurrentFreqCoresMap().keySet()){
					
					for(int core : avmDynamicState.getProcCurrentFreqCoresMap().get(proc_uri).keySet()){
						
						admissibleFreqs.put(proc_uri, admissibleFreqCores.get(avmURI).get(proc_uri));
						proc_map.put(proc_uri, core);
						currentFreqMap.put(proc_uri, avmDynamicState.getProcCurrentFreqCoresMap().get(proc_uri));
						break;
					}
				}
			}
			
			for(String proc_uri : proc_map.keySet()){
				Integer core = proc_map.get(proc_uri);
				int currentFreq  = currentFreqMap.get(proc_uri).get(core);
				Set<Integer> admissibleFreq = admissibleFreqs.get(proc_uri);
				int freq = getNextFreq(currentFreq, admissibleFreq);
				
				if(currentFreq == freq) continue;

					if(proc_coord_freq_map.get(proc_uri).setCoreFrequency(autoHand_uri, core, freq)){
						rets.add(true);
					}
						
					
			
			}

						
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		if(rets.size()!=proc_map.keySet().size())
			return false;
		return true;
	}
	
	private boolean decreaseSpeed(Map<String, ApplicationVMDynamicStateI > avmdynamicstate) {
		List<Boolean> rets = new ArrayList<>();
		Map<String, Integer> proc_map = new HashMap<>();
		Map<String, Map<Integer, Integer>> currentFreqMap = new HashMap<>();
		Map<String, Set<Integer>> admissibleFreqs = new HashMap<>();
 		ApplicationVMDynamicStateI avmDynamicState;
		try {
			
			for(String avmURI : avmdynamicstate.keySet()){
	
				avmDynamicState = avmdynamicstate.get(avmURI);
			
				for(String proc_uri : avmDynamicState.getProcCurrentFreqCoresMap().keySet()){
					
					for(int core : avmDynamicState.getProcCurrentFreqCoresMap().get(proc_uri).keySet()){
						
						admissibleFreqs.put(proc_uri, admissibleFreqCores.get(avmURI).get(proc_uri));
						proc_map.put(proc_uri, core);
						currentFreqMap.put(proc_uri, avmDynamicState.getProcCurrentFreqCoresMap().get(proc_uri));
						break;
					}
				}
			}
			
			for(String proc_uri : proc_map.keySet()){
				Integer core = proc_map.get(proc_uri);
				int currentFreq  = currentFreqMap.get(proc_uri).get(core);
				Set<Integer> admissibleFreq = admissibleFreqs.get(proc_uri);
				int freq = getPreviousFreq(currentFreq, admissibleFreq);
				
				if(currentFreq == freq) continue;

					if(proc_coord_freq_map.get(proc_uri).setCoreFrequency(autoHand_uri, core, freq)){
						rets.add(true);
					}
						
					
			
			}

						
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		if(rets.size()!=proc_map.keySet().size())
			return false;
		return true;
	}
	
	private int wait = 15;

	private RequestDispatcherDynamicStateI currentDynamicState;

	private boolean avmWaitingToRemove;
	
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
							
				//si la fréquence a pu etre augmentee
				if(increaseSpeed(dynamicstate.getAVMDynamicStateMap())) {
					//logMessage(entry.getKey()+" frequency increased");
				}
				//sinon on ajoute 1 core aux avms
				else{
			
					int nbCoreToAdd ;
					
					if(lavg>2*averageResponseTime) {
						nbCoreToAdd = 2;
					}
					else{
						nbCoreToAdd = 1;
					}
					
					boolean ok = true;
					for(String avmToAddCore : dynamicstate.getAVMDynamicStateMap().keySet()){
						if( (proc_coord_freq_inport_uri_map=
								requestDispatcherHandlerOutboundPort.addCoreToAvm
								(autoHand_uri, avmToAddCore, nbCoreToAdd))!=null) {
							
							addNewPortCoord(proc_coord_freq_inport_uri_map);
							logMessage(avmToAddCore+" 1 core added");	
							wait = 10;
						}
						
						else
							ok = false;
					}
					//sinon on ajoute une avm 

					
						if(!ok && getUnusedAVMs(dynamicstate).size() == 0 && (proc_coord_freq_inport_uri_map=requestDispatcherHandlerOutboundPort.addAVMToRequestDispatcher(autoHand_uri, requestDispatcherURI))!=null){
							addNewPortCoord(proc_coord_freq_inport_uri_map);
							logMessage("avm added");
							wait = 10;
					
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
					logMessage("Response time too fast: "+avg+"ms (>"+ lower_bound +" ms wanted)");
					if(removeUnusedAVM(dynamicstate)) {
						wait = 15;
						return;
					}
					
					boolean ok = false;
					
					for(String avm : dynamicstate.getAVMDynamicStateMap().keySet())
						if(requestDispatcherHandlerOutboundPort.removeCoreFromAvm(autoHand_uri, avm)!=null) {	
								logMessage(avm+" removed 1 core");
								wait = 15;
								ok = true;
						}
					
					if(!ok) {
						if(decreaseSpeed(dynamicstate.getAVMDynamicStateMap())) {
							//logMessage(avmUri+" speed decreased");
						}
						wait = 10;
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
	
	private String chooseAVM(Map<String, ApplicationVMDynamicStateI> avmDynamicStateMap) {
		
		Integer nbCores = null;
		String uri=null;
		
		for(String avmURI : avmDynamicStateMap.keySet()){
			
			if(nbCores==null){
				uri = avmURI;
				nbCores = avmDynamicStateMap.get(avmURI).getTotalNumberOfCores();
			}
			else{
				if(nbCores > avmDynamicStateMap.get(avmURI).getTotalNumberOfCores() ){
					uri = avmURI;
					nbCores = avmDynamicStateMap.get(avmURI).getTotalNumberOfCores();
				}
			}
			
			
		}
		return uri;
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
		
		for(String avm: unusedavms) {
			
			try {
				if(requestDispatcherHandlerOutboundPort.removeAVMFromRequestDispatcher(autoHand_uri, requestDispatcherURI, avm)!=null) {
					logMessage(avm+" removed.");
					avmWaitingToRemove = false;
					return true;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	

	private List<String> getUnusedAVMs(RequestDispatcherDynamicStateI dynamicstate) {
		List<String> avms = new ArrayList<>();
		for(Map.Entry<String, Double> entry: dynamicstate.getScoresMap().entrySet()) {
			if(entry.getValue() == 0) {
				avms.add(entry.getKey());
			}
		}
		
		if(avms.size()==0 && !avmWaitingToRemove)
			try {
				dispatcher_management_outport.stopSendingRequestToOneAVM();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else
			avmWaitingToRemove = true;
		
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

		
		proc_coord_freq_map.get(procURI).setCoreFrequency(autoHand_uri, coreNo, frequency);

		
	}


	@Override
	public void removeFreq(String procURI) throws Exception {
		
		
		this.proc_coord_freq_map.remove(procURI).unpublishPort();
		
	}


	
	
	

}
