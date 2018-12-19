package eniac.automatichandler;

import java.util.Map;

import eniac.applicationvm.ApplicationVMDynamicState;
import eniac.automatichandler.interfaces.AutomaticHandlerManagementI;
import eniac.automatichandler.ports.AutomaticHandlerManagementInboundPort;
import eniac.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;
import eniac.requestdispatcher.interfaces.RequestDispatcherStateDataConsumerI;
import eniac.requestdispatcher.interfaces.RequestDispatcherStaticStateI;
import eniac.requestdispatcher.ports.RequestDispatcherDynamicStateDataOutboundPort;
import eniac.requestdispatcher.ports.RequestDispatcherStaticStateDataOutboundPort;
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
	
	protected String requestDispatcherDynamicStateDataInboundPortURI;
	protected String requestDispatcherStaticStateDataInboundPortURI;
	
	
	public AutomaticHandler(String autoHand_uri,
			String managementInboundPortURI,
			String requestDispatcherUri,
			String requestDispatcherDynamicStateDataInboundPortURI,
			String requestDispatcherStaticStateDataInboundPortURI) throws Exception{
		super(autoHand_uri,1,1);
		
		assert autoHand_uri!=null;
		assert managementInboundPortURI!=null;
		assert requestDispatcherDynamicStateDataInboundPortURI != null;
		assert requestDispatcherStaticStateDataInboundPortURI != null;
		
		this.requestDispatcherDynamicStateDataInboundPortURI = requestDispatcherDynamicStateDataInboundPortURI;
		this.requestDispatcherStaticStateDataInboundPortURI = requestDispatcherStaticStateDataInboundPortURI;

		addOfferedInterface(AutomaticHandlerManagementI.class);
		automaticHandlerManagementInboundPort = new AutomaticHandlerManagementInboundPort(autoHand_uri, this);		
		addPort(automaticHandlerManagementInboundPort);
		automaticHandlerManagementInboundPort.publishPort();
		
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
		
		requestDispatcherDynamicStateDataOutboundPort.doDisconnection();
		requestDispatcherStaticStateDataOutboundPort.doDisconnection();

		super.finalise();
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException{

		try {
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
		
		Map<String, ApplicationVMDynamicStateI > avmDynamicStateMap = 
				dynamicState.getAVMDynamicStateMap();
		
		for(String avmUri : avmDynamicStateMap.keySet()){
			ApplicationVMDynamicStateI avmDynamicState = avmDynamicStateMap.get(avmUri);
			logMessage(avmDynamicState.getApplicationVMURI()+" "+String.valueOf(avmDynamicState.isIdle()));
		}
	}

}
