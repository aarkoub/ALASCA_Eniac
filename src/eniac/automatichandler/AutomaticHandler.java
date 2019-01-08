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
	
	protected Map<String, Map<String,Set<Integer>>> admissibleFreqCores;
	
	private ComputeTimeCharts chart;
	
	
	private double lower_bound;
	private double upper_bound;
	
	public static final double ALPHA = 0.5;
	
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
			ApplicationVMStaticStateI avmStaticState = avmStaticStateMap.get(avmUri);
			
			Map<Integer, Integer> coreMap = avmStaticState.getIdCores();
			for(Integer core : coreMap.keySet()){
				logMessage(avmUri+" : core number "+core+" ; processor number "+String.valueOf(coreMap.get(core)));
				admissibleFreqCores.put(avmUri, avmStaticState.getAdmissibleFreqCores());
			}
			
			
		}
		
	}
	
	private boolean increaseSpeed(Map<String, ApplicationVMDynamicStateI > avmdynamicstate, String avm) {
		ApplicationVMDynamicStateI avmDynamicState = avmdynamicstate.get(avm);
		Map<String, Set<Integer>> admissibleFreqCoresAVM = admissibleFreqCores.get(avm);
		for(String proc_uri : avmDynamicState.getProcCurrentFreqCoresMap().keySet()){
			System.out.println("AVM: "+avm+"          "+(admissibleFreqCoresAVM==null));
			Set<Integer> admissibleFreq = admissibleFreqCoresAVM.get(proc_uri);
			for(int core : avmDynamicState.getProcCurrentFreqCoresMap().get(proc_uri).keySet()){
				int currentFreq = avmDynamicState.getProcCurrentFreqCoresMap().get(proc_uri).get(core);
				int freq = getNextFreq(currentFreq, admissibleFreq);
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
	
	private int wait = 1;
	@Override
	public void acceptRequestDispatcherDynamicData(String requestDisptacherURI,
			RequestDispatcherDynamicStateI dynamicState) throws Exception {
		lavg = exponentialSmoothing(dynamicState.getAverageRequestTime());
		chart.addData(lavg);
		
		

		if(wait%10 == 0) {
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
					int nbcore = dynamicstate.getAVMDynamicStateMap().get(entry.getKey()).getTotalNumberOfCores();
					int nbtoadd = (int)Math.ceil((double)entry.getValue()/(double)nbcore);
					if(increaseSpeed(dynamicstate.getAVMDynamicStateMap(), entry.getKey())) {
						nbtoadd -= nbcore;
						logMessage(entry.getKey()+" frequency increased");
					}
					if(nbtoadd <= 0) continue;
					if(!requestDispatcherHandlerOutboundPort.addCoreToAvm(entry.getKey(), 1)) {
						if((avmUri=requestDispatcherHandlerOutboundPort.addAVMToRequestDispatcher(requestDispatcherURI))!=null){
							logMessage(avmUri+" added");
						}
						return;
					}
					nbtoadd--;
					logMessage(entry.getKey()+" 1 core added");
					while(nbtoadd > 0 && requestDispatcherHandlerOutboundPort.addCoreToAvm(entry.getKey(), 1)) {
						logMessage(entry.getKey()+" 1 core added");
						nbtoadd--;
					}
				}else {
					if((avmUri=requestDispatcherHandlerOutboundPort.addAVMToRequestDispatcher(requestDispatcherURI))!=null){
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
			logMessage("Response time too fast: "+avg+"ms (>"+ lower_bound +" ms wanted)");
			removeUnusedAVM(dynamicstate);
			for(Map.Entry<String, Double> entry: dynamicstate.getScoresMap().entrySet()) {
				if(entry.getValue() <= 2) {
					if(decreaseSpeed(dynamicstate.getAVMDynamicStateMap(), entry.getKey())) {
						logMessage(entry.getKey()+" speed decreased");
					}
				}
			}
			
			return;
		}
		
		last = avg;
		logMessage("Response time correct");
		return;		
	
	}
	
	private void removeUnusedAVM(RequestDispatcherDynamicStateI dynamicstate) {
		List<String> unusedavms = getUnusedAVMs(dynamicstate);
		if(unusedavms.size() <= 1) return;
		for(String avm: unusedavms) {
			try {
				if(requestDispatcherHandlerOutboundPort.removeAVMFromRequestDispatcher(requestDispatcherURI, avm)) {
					logMessage(avm+" removed.");
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
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


	
	
	

}
