package eniac.automatichandler;

import java.util.HashMap;
import java.util.Map;

import eniac.automatichandler.connectors.RequestDispatcherListenerConnector;
import eniac.automatichandler.interfaces.AutomaticHandlerManagementI;
import eniac.automatichandler.interfaces.RequestDispatcherListenerI;
import eniac.automatichandler.ports.AutomaticHandlerManagementInboundPort;
import eniac.automatichandler.ports.RequestDispatcherListenerInboundPort;
import eniac.automatichandler.ports.RequestDispatcherListenerOutboundPort;
import eniac.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;
import eniac.requestdispatcher.interfaces.RequestDispatcherStateDataConsumerI;
import eniac.requestdispatcher.interfaces.RequestDispatcherStaticStateI;
import eniac.requestdispatcher.ports.RequestDispatcherDynamicStateDataOutboundPort;
import eniac.requestdispatcher.ports.RequestDispatcherStaticStateDataOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.connectors.DataConnector;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.connectors.ControlledDataConnector;

public class AutomaticHandler extends AbstractComponent
implements RequestDispatcherListenerI,
RequestDispatcherStateDataConsumerI{
	
	protected String autoHand_uri;
	protected String requestDispatcherListenerInboundPortURI;
	protected AutomaticHandlerManagementInboundPort automaticHandlerManagementInboundPort;
	protected RequestDispatcherListenerInboundPort requestDispatcherListenerInboundPort;
	
	protected Map<String, RequestDispatcherDynamicStateDataOutboundPort> disp_dynamic_outports;
	protected Map<String, RequestDispatcherStaticStateDataOutboundPort> disp_static_outports;
	
	
	public AutomaticHandler(String autoHand_uri,
			String managementInboundPortURI,
			String requestDispatcherListenerInboundPortURI) throws Exception{
		super(autoHand_uri,1,1);
		
		assert autoHand_uri!=null;
		assert managementInboundPortURI!=null;
		assert requestDispatcherListenerInboundPortURI != null;
		
		disp_dynamic_outports = new HashMap<>();
		disp_static_outports = new HashMap<>();
		
		this.requestDispatcherListenerInboundPortURI = requestDispatcherListenerInboundPortURI;
		
		addOfferedInterface(AutomaticHandlerManagementI.class);
		automaticHandlerManagementInboundPort = new AutomaticHandlerManagementInboundPort(autoHand_uri, this);		
		addPort(automaticHandlerManagementInboundPort);
		automaticHandlerManagementInboundPort.publishPort();
		
		addOfferedInterface(RequestDispatcherListenerI.class);
		requestDispatcherListenerInboundPort = new RequestDispatcherListenerInboundPort(
				requestDispatcherListenerInboundPortURI,
				this);
		addPort(requestDispatcherListenerInboundPort);
		requestDispatcherListenerInboundPort.publishPort();
		
		
	}
		
	
	@Override
	public void finalise() throws Exception {
		
		for(String rd_uri : disp_dynamic_outports.keySet()){
			try {
				disp_dynamic_outports.get(rd_uri).doDisconnection();
				disp_static_outports.get(rd_uri).doDisconnection();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		super.finalise();
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException{
				
		for(String rd_uri : disp_dynamic_outports.keySet()){
			try {
				disp_dynamic_outports.get(rd_uri).unpublishPort();
				disp_static_outports.get(rd_uri).unpublishPort();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		super.shutdown();
	}
	





	@Override
	public void receiveNewRequestDispatcherURI(String rd_uri, String requestDispatcherDynamicStateDataInboundPortURI,
		String requestDispatcherStaticStateDataInboundPortURI) throws Exception {
	
		RequestDispatcherDynamicStateDataOutboundPort disp_dynamic_outport =
		new RequestDispatcherDynamicStateDataOutboundPort(this, rd_uri);
		addPort(disp_dynamic_outport);
		disp_dynamic_outport.publishPort();
		
		
		RequestDispatcherStaticStateDataOutboundPort disp_static_outport =
			new RequestDispatcherStaticStateDataOutboundPort(this, rd_uri);
		addPort(disp_static_outport);
		disp_static_outport.publishPort();
		
		disp_dynamic_outports.put(rd_uri, disp_dynamic_outport);
		disp_static_outports.put(rd_uri, disp_static_outport);

				
		doPortConnection(disp_dynamic_outport.getPortURI(), 
				requestDispatcherDynamicStateDataInboundPortURI, 
				ControlledDataConnector.class.getCanonicalName()
			);
			
			doPortConnection(disp_static_outport.getPortURI(),
					requestDispatcherStaticStateDataInboundPortURI,
					DataConnector.class.getCanonicalName()
					);
				
		disp_dynamic_outport.startUnlimitedPushing(500);
		
		
		
	}

	@Override
	public void acceptRequestDispatcherStaticData(String requestDisptacherURI,
			RequestDispatcherStaticStateI staticState) throws Exception {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void acceptRequestDispatcherDynamicData(String requestDisptacherURI,
			RequestDispatcherDynamicStateI dynamicState) throws Exception {
		
		logMessage("Average request time for "+requestDisptacherURI+
				" = "+dynamicState.getAverageRequestTime());
		
	}

}
