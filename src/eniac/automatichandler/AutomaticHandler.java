package eniac.automatichandler;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jfree.ui.RefineryUtilities;

import eniac.automatichandler.interfaces.AutomaticHandlerManagementI;
import eniac.automatichandler.ports.AutomaticHandlerManagementInboundPort;
import eniac.requestdispatcher.connectors.RequestDispatcherManagementConnector;
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
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMDynamicStateI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMStaticStateI;

public class AutomaticHandler extends AbstractComponent
implements
RequestDispatcherStateDataConsumerI{
	
	protected String autoHand_uri;
	
	protected AutomaticHandlerManagementInboundPort automaticHandlerManagementInboundPort;
	
	protected RequestDispatcherDynamicStateDataOutboundPort requestDispatcherDynamicStateDataOutboundPort;
	protected RequestDispatcherStaticStateDataOutboundPort requestDispatcherStaticStateDataOutboundPort;
	protected RequestDispatcherHandlerOutboundPort requestDispatcherHandlerOutboundPort;
	
	protected String requestDispatcherHandlerInboundPortURI;
	protected String requestDispatcherDynamicStateDataInboundPortURI;
	protected String requestDispatcherStaticStateDataInboundPortURI;
	protected String requestDispatcherURI;
	
	protected Map<String, Set<Integer>> admissibleFreqCores;
	
	private ComputeTimeCharts chart;
	
	
	public static final double LOWER_BOUND = 400;
	public static final double UPPER_BOUND = 600;
	
	public AutomaticHandler(String autoHand_uri,
			String managementInboundPortURI,
			String requestDispatcherUri,
			String requestDispatcherHandlerInboundPortURI,
			String requestDispatcherDynamicStateDataInboundPortURI,
			String requestDispatcherStaticStateDataInboundPortURI) throws Exception{
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
		
		chart = new ComputeTimeCharts(autoHand_uri);
		chart.pack();
		RefineryUtilities.positionFrameRandomly(chart);
		chart.setVisible(true);
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
		
		for(String avmUri : avmStaticStateMap.keySet()){
			ApplicationVMStaticStateI avmStaticState = avmStaticStateMap.get(avmUri);
			
			Map<Integer, Integer> coreMap = avmStaticState.getIdCores();
			for(Integer core : coreMap.keySet()){
				logMessage(avmUri+" : core number "+core+" ; processor number "+String.valueOf(coreMap.get(core)));
				admissibleFreqCores = avmStaticState.getAdmissibleFreqCores();
			}
			
			
		}
		
	}
	
	private int wait = 0;
	@Override
	public void acceptRequestDispatcherDynamicData(String requestDisptacherURI,
			RequestDispatcherDynamicStateI dynamicState) throws Exception {
		
		chart.addData(dynamicState.getAverageRequestTime());
		
		/*logMessage("Average request time for "+requestDisptacherURI+
				" = "+dynamicState.getAverageRequestTime());*/
		

		if(wait%5 == 0) {
			logMessage("Action possible");
			modulateAVM(dynamicState);
		}
		wait++;

		
		/*
		Map<String, ApplicationVMDynamicStateI > avmDynamicStateMap = 
				dynamicState.getAVMDynamicStateMap();
		
		for(String avmUri : avmDynamicStateMap.keySet()){
			ApplicationVMDynamicStateI avmDynamicState = avmDynamicStateMap.get(avmUri);
			logMessage(avmDynamicState.getApplicationVMURI()+" "+String.valueOf(avmDynamicState.isIdle()));
			
			if( !avmDynamicState.isIdle()){
				if(requestDispatcherHandlerOutboundPort.addCoreToAvm(avmUri, 1)){
					logMessage("1 core added to "+avmUri);
				}
				else {
					
					for(String proc_uri : avmDynamicState.getProcCurrentFreqCoresMap().keySet()){
						
						
						Set<Integer> admissibleFreq = admissibleFreqCores.get(proc_uri);
						
						
						for(int core : avmDynamicState.getProcCurrentFreqCoresMap().get(proc_uri).keySet()){
							
							int currentFreq = avmDynamicState.getProcCurrentFreqCoresMap().get(proc_uri).get(core);
							int freq = getNextFreq(currentFreq, admissibleFreq);
							
							requestDispatcherHandlerOutboundPort.setCoreFrequency(proc_uri, 
									core, freq);
						}
						
						
					}
					
				}
			}
			else{
				
				if(avmDynamicState.getTotalNumberOfCores()>1){
					
					
					List<AllocatedCore> allocatedCoresList = avmDynamicState.getIdleAllocatedCores();
					for(AllocatedCore ac : allocatedCoresList){
						
						if(requestDispatcherHandlerOutboundPort.removeCoreFromAvm(avmUri,ac)){
							logMessage("1 core removed from "+avmUri);
						}
					}
				}
				else{
					for(String proc_uri : avmDynamicState.getProcCurrentFreqCoresMap().keySet()){
						
						
						Set<Integer> admissibleFreq = admissibleFreqCores.get(proc_uri);
						
						
						for(int core : avmDynamicState.getProcCurrentFreqCoresMap().get(proc_uri).keySet()){
							
							int currentFreq = avmDynamicState.getProcCurrentFreqCoresMap().get(proc_uri).get(core);
							int freq = getPreviousFreq(currentFreq, admissibleFreq);
							
							requestDispatcherHandlerOutboundPort.setCoreFrequency(proc_uri, 
									core, freq);
						}
						
						
					}
				}
				
			}
			
		}*/
	}
	
	public static final int MAX_QUEUE = 3;
	public void modulateAVM(RequestDispatcherDynamicStateI dynamicstate) throws Exception {
		String avmUri;
		if(dynamicstate.getAverageRequestTime() > UPPER_BOUND) {
			logMessage("Response time too long: "+dynamicstate.getAverageRequestTime()+"ms (<"+ UPPER_BOUND +" ms wanted)");
			for(Map.Entry<String, Double> entry: dynamicstate.getScoresMap().entrySet()) {
				if(entry.getValue() > MAX_QUEUE) {
					if(!requestDispatcherHandlerOutboundPort.addCoreToAvm(entry.getKey(), 1)) {
						if((avmUri=requestDispatcherHandlerOutboundPort.addAVMToRequestDispatcher(requestDispatcherURI))!=null){
							logMessage(avmUri+" added");
						}
						return;
					}
					logMessage(entry.getKey()+" 1 core added");
					while(requestDispatcherHandlerOutboundPort.addCoreToAvm(entry.getKey(), 1)) {
						logMessage(entry.getKey()+" 1 core added");
					}
				}
			}
			
			return;
		}
		
		if(dynamicstate.getAverageRequestTime() < LOWER_BOUND) {
			logMessage("Response time too fast: "+dynamicstate.getAverageRequestTime()+"ms (>"+ LOWER_BOUND +" ms wanted)");
			if((avmUri=requestDispatcherHandlerOutboundPort.removeAVMFromRequestDispatcher(requestDispatcherURI))!=null){
				
				logMessage(avmUri+" removed");
			}
			return;
		}
		logMessage("Response time correct");
		return;		
	
	}
	
	
	public int getNextFreq(int currentFreq, Set<Integer> freqs) {
		
		int next = currentFreq;
		
		for(Integer i : freqs) {
			
			if(i>next) {
				next = i;
				break;
			}
			
		}
		System.out.println(next);
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
		System.out.println(previous);
		return previous;
		
	}


	
	
	

}
