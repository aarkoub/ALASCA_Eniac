package eniac.automatichandler.ports;

import eniac.automatichandler.interfaces.RequestDispatcherListenerI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class RequestDispatcherListenerOutboundPort
extends AbstractOutboundPort
implements RequestDispatcherListenerI{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RequestDispatcherListenerOutboundPort(String uri, ComponentI owner ) throws Exception{
		super(uri, RequestDispatcherListenerI.class, owner);
		assert	uri != null && owner != null ;
	}
	
	public RequestDispatcherListenerOutboundPort(ComponentI owner) throws Exception {
		super(RequestDispatcherListenerI.class, owner);
		assert owner!=null;
	}

	@Override
	public void receiveNewRequestDispatcherURI(String rd_uri, String requestDispatcherDynamicStateDataInboundPortURI,
			String requestDispatcherStaticStateDataInboundPortURI) throws Exception {
		((RequestDispatcherListenerI)this.connector).receiveNewRequestDispatcherURI(rd_uri,
				requestDispatcherDynamicStateDataInboundPortURI, requestDispatcherStaticStateDataInboundPortURI);
		
	}

}
