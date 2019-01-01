package eniac.automatichandler;

import java.util.Map;

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
	
	protected double borne_inf = 20;
	protected double borne_sup = 30;
	
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
			}
			
			
		}
		
	}




	@Override
	public void acceptRequestDispatcherDynamicData(String requestDisptacherURI,
			RequestDispatcherDynamicStateI dynamicState) throws Exception {
		
		logMessage("Average request time for "+requestDisptacherURI+
				" = "+dynamicState.getAverageRequestTime());
		
		modulateAVM(dynamicState.getAverageRequestTime());
		
		Map<String, ApplicationVMDynamicStateI > avmDynamicStateMap = 
				dynamicState.getAVMDynamicStateMap();
		
		for(String avmUri : avmDynamicStateMap.keySet()){
			ApplicationVMDynamicStateI avmDynamicState = avmDynamicStateMap.get(avmUri);
			logMessage(avmDynamicState.getApplicationVMURI()+" "+String.valueOf(avmDynamicState.isIdle()));
		}
	}
	
	
	public void modulateAVM(double averageTime) throws Exception {
		String avmUri;
		if(averageTime > borne_sup) {
			
			if((avmUri=requestDispatcherHandlerOutboundPort.addAVMToRequestDispatcher(requestDispatcherURI))!=null){
				logMessage(avmUri+" added");
			}
		}
		else {
			if(averageTime<borne_inf) {
				
				if((avmUri=requestDispatcherHandlerOutboundPort.removeAVMFromRequestDispatcher(requestDispatcherURI))!=null){
					
					logMessage(avmUri+" removed");
				}
			}
			
		}
				
	
	}
	
	

}
