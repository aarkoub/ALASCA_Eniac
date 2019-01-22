package eniac.automatichandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.analysis.function.Log;
import org.jfree.ui.RefineryUtilities;

import eniac.automatichandler.interfaces.AutomaticHandlerManagementI;
import eniac.automatichandler.ports.AutomaticHandlerManagementInboundPort;
import eniac.processorcoordinator.connectors.ProcessorCoordinatorFreqConnector;
import eniac.processorcoordinator.interfaces.ProcessorCoordinatorManagementI;
import eniac.processorcoordinator.interfaces.ProcessorCoordinatorOrderI;
import eniac.processorcoordinator.ports.ProcessorCoordinatorFreqOutboundPort;
import eniac.processorcoordinator.ports.ProcessorCoordinatorManagementInboundPort;
import eniac.processorcoordinator.ports.ProcessorCoordinatorManagementOutboundPort;
import eniac.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;
import eniac.requestdispatcher.interfaces.RequestDispatcherStateDataConsumerI;
import eniac.requestdispatcher.interfaces.RequestDispatcherStaticStateI;
import eniac.requestdispatcher.ports.RequestDispatcherDynamicStateDataOutboundPort;
import eniac.requestdispatcher.ports.RequestDispatcherStaticStateDataOutboundPort;
import eniac.requestdispatcherhandler.connectors.RequestDispatcherHandlerConnector;
import eniac.requestdispatcherhandler.interfaces.RequestDispatcherHandlerI;
import eniac.requestdispatcherhandler.ports.RequestDispatcherHandlerOutboundPort;
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
	protected RequestDispatcherHandlerOutboundPort requestDispatcherHandlerOutboundPort;
	
	protected String requestDispatcherHandlerInboundPortURI;
	protected String requestDispatcherDynamicStateDataInboundPortURI;
	protected String requestDispatcherStaticStateDataInboundPortURI;
	protected String requestDispatcherURI;
	
	protected Map<String, Map<String,Set<Integer>>> admissibleFreqCores;
	
	private ComputeTimeCharts chart;
	
	
	private double lower_bound;
	private double upper_bound;
	
	public static final double ALPHA = 0.5;
	
	
	protected Map<String, ProcessorCoordinatorManagementOutboundPort>
		coord_map = new HashMap<>();
	
	public AutomaticHandler(String autoHand_uri,
			String managementInboundPortURI,
			String requestDispatcherUri,
			String requestDispatcherHandlerInboundPortURI,
			String requestDispatcherDynamicStateDataInboundPortURI,
			String requestDispatcherStaticStateDataInboundPortURI,
			Double averageResponseTime) throws Exception{
		super(autoHand_uri,1,1);
		
		assert autoHand_uri!=null;
		assert managementInboundPortURI!=null;
		assert requestDispatcherHandlerInboundPortURI != null;
		assert requestDispatcherDynamicStateDataInboundPortURI != null;
		assert requestDispatcherStaticStateDataInboundPortURI != null;
		assert requestDispatcherUri != null;
			
		this.requestDispatcherURI = requestDispatcherUri;
		this.autoHand_uri = autoHand_uri;
		
		this.requestDispatcherDynamicStateDataInboundPortURI = requestDispatcherDynamicStateDataInboundPortURI;
		this.requestDispatcherStaticStateDataInboundPortURI = requestDispatcherStaticStateDataInboundPortURI;
		this.requestDispatcherHandlerInboundPortURI = requestDispatcherHandlerInboundPortURI;

		addOfferedInterface(AutomaticHandlerManagementI.class);
		automaticHandlerManagementInboundPort = new AutomaticHandlerManagementInboundPort(autoHand_uri, this);		
		addPort(automaticHandlerManagementInboundPort);
		automaticHandlerManagementInboundPort.publishPort();
		
		addRequiredInterface(RequestDispatcherHandlerI.class);
		requestDispatcherHandlerOutboundPort = new RequestDispatcherHandlerOutboundPort(this);
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
		
		lower_bound = averageResponseTime-200;
		upper_bound = averageResponseTime+200;
		
		
	}
		
	
	@Override
	public void finalise() throws Exception {
		
		requestDispatcherHandlerOutboundPort.doDisconnection();
		requestDispatcherDynamicStateDataOutboundPort.doDisconnection();
		requestDispatcherStaticStateDataOutboundPort.doDisconnection();

		super.finalise();
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException{

		try {
			requestDispatcherHandlerOutboundPort.unpublishPort();
			requestDispatcherDynamicStateDataOutboundPort.unpublishPort();
			requestDispatcherStaticStateDataOutboundPort.unpublishPort();
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
					RequestDispatcherHandlerConnector.class.getCanonicalName()
				);
			
			doPortConnection(requestDispatcherDynamicStateDataOutboundPort.getPortURI(), 
			requestDispatcherDynamicStateDataInboundPortURI, 
			ControlledDataConnector.class.getCanonicalName()
			);
			
			doPortConnection(requestDispatcherStaticStateDataOutboundPort.getPortURI(),
					requestDispatcherStaticStateDataInboundPortURI,
					DataConnector.class.getCanonicalName()
					);
			
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
				System.out.println(avm+" "+admissibleFreqCoresAVM);
				Set<Integer> admissibleFreq = admissibleFreqCoresAVM.get(proc_uri);
				for(int core : avmDynamicState.getProcCurrentFreqCoresMap().get(proc_uri).keySet()){
					int currentFreq = avmDynamicState.getProcCurrentFreqCoresMap().get(proc_uri).get(core);
					int freq = getNextFreq(currentFreq, admissibleFreq);
					if(currentFreq == freq) return false;
						requestDispatcherHandlerOutboundPort.setCoreFrequency(proc_uri, 
								core, freq);
					}
				}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return true;
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
					requestDispatcherHandlerOutboundPort.setCoreFrequency(proc_uri, 
							core, freq);
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
		return true;
	}
	
	private int wait = 15;
	@Override
	public void acceptRequestDispatcherDynamicData(String requestDisptacherURI,
			RequestDispatcherDynamicStateI dynamicState) throws Exception {
		lavg = dynamicState.getAverageRequestTime();
		chart.addData(lavg);

		if(wait%20 == 0) {
			logMessage("Modulation possible");
			modulateAVM(dynamicState, lavg);
		}
		wait++;
		
	}
	
	private double last = upper_bound-1;
	public static final int MAX_QUEUE = 3;
	private double lavg = upper_bound;
	
	
	public double exponentialSmoothing(double avgraw) {
		return ALPHA*avgraw + (1-ALPHA)*this.lavg;
	}
	
	public void modulateAVM(RequestDispatcherDynamicStateI dynamicstate, double avg) throws Exception {
		String avmUri;
		if(avg > upper_bound) {
			if(last > avg) {
				last = avg;
				return;
			}
			logMessage("Response time too long: "+avg+"ms (<"+ upper_bound +" ms wanted)");
			for(Map.Entry<String, Double> entry: dynamicstate.getScoresMap().entrySet()) {
				if(entry.getValue() > MAX_QUEUE) {
					if(increaseSpeed(dynamicstate.getAVMDynamicStateMap(), entry.getKey())) {
						logMessage(entry.getKey()+" frequency increased");
						continue;
					}
					List<String> proc_coord_freq_inport_uri_list;
					if( (proc_coord_freq_inport_uri_list=requestDispatcherHandlerOutboundPort.addCoreToAvm(entry.getKey(), 2))!=null) {
						
						for(String proc_coord_freq_inport_uri : proc_coord_freq_inport_uri_list){
							
							ProcessorCoordinatorFreqOutboundPort outport = 
									new ProcessorCoordinatorFreqOutboundPort(this);
							addPort(outport);
							outport.publishPort();
							
							doPortConnection(outport.getPortURI(),
									proc_coord_freq_inport_uri, 
									ProcessorCoordinatorFreqConnector.class.getCanonicalName());
							
							//proc_coord_freq_map.put(proc)
							
						}
						
						if(getUnusedAVMs(dynamicstate).size() == 0 && (avmUri=requestDispatcherHandlerOutboundPort.addAVMToRequestDispatcher(requestDispatcherURI))!=null){
							logMessage(avmUri+" added");
						}
						return;
					}else {
						logMessage(entry.getKey()+" 2 core added");
					}
				}else {
					if(getUnusedAVMs(dynamicstate).size() == 0 && (avmUri=requestDispatcherHandlerOutboundPort.addAVMToRequestDispatcher(requestDispatcherURI))!=null){
						logMessage(avmUri+" added");
					}
					return;
				}
			}
			
			return;
		}
		
		
		if(avg < lower_bound) {
			if(last < avg) {
				last = avg;
				return;
			}
			//System.out.println(dynamicstate.getScoresMap().toString());
			logMessage("Response time too fast: "+avg+"ms (>"+ lower_bound +" ms wanted)");
			if(removeUnusedAVM(dynamicstate)) return;
			String avm = lowestScore(dynamicstate);
			if(avm != null) {
				if(requestDispatcherHandlerOutboundPort.removeCoreFromAvm(avm)) {
					logMessage(avm+" removed 1 core");
				}
				if(requestDispatcherHandlerOutboundPort.removeCoreFromAvm(avm)) {
					logMessage(avm+" removed 1 core");
				}
			}
			//removeUnusedAVM(dynamicstate);
			/*for(Map.Entry<String, Double> entry: dynamicstate.getScoresMap().entrySet()) {
				if(entry.getValue() < MAX_QUEUE) {
					if(decreaseSpeed(dynamicstate.getAVMDynamicStateMap(), entry.getKey())) {
						logMessage(entry.getKey()+" speed decreased");
					}
				}
			}*/
			
			return;
		}
		
		last = avg;
		logMessage("Response time correct");
		return;		
	
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
			try {
				if(requestDispatcherHandlerOutboundPort.removeAVMFromRequestDispatcher(requestDispatcherURI, avm)) {
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
			try {
				if(requestDispatcherHandlerOutboundPort.removeAVMFromRequestDispatcher(requestDispatcherURI, avm)) {
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
		
		int next = currentFreq;
		
		for(Integer i : freqs) {
			
			if(i>next) {
				next = i;
				break;
			}
			
		}
		return next;
	}
	
	public int getPreviousFreq(int currentFreq, Set<Integer> freqs) {
	
		int previous = currentFreq;
		
		for(Integer i : freqs) {
			
			if(i<previous) {
				previous = i;
				break;
			}
			
		}
		return previous;
		
	}


	@Override
	public void setCoreFreqNextTime(String procURI, int coreNo, int frequency) throws Exception {
		
		
	}


	
	
	

}
