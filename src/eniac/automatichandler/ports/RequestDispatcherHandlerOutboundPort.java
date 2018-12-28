package eniac.automatichandler.ports;

import eniac.automatichandler.interfaces.RequestDispatcherHandlerI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class RequestDispatcherHandlerOutboundPort
extends AbstractOutboundPort
implements RequestDispatcherHandlerI{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RequestDispatcherHandlerOutboundPort(String uri, ComponentI owner ) throws Exception{
		super(uri, RequestDispatcherHandlerI.class, owner);
		assert	uri != null && owner != null ;
	}
	
	public RequestDispatcherHandlerOutboundPort(ComponentI owner) throws Exception {
		super(RequestDispatcherHandlerI.class, owner);
		assert owner!=null;
	}



	@Override
	public void addAVMToRequestDispatcher(String requestDispatcherURI) throws Exception {
		((RequestDispatcherHandlerI)this.connector).addAVMToRequestDispatcher(requestDispatcherURI);
		
	}

	@Override
	public void removeAVMFromRequestDispatcher(String requestDispatcherURI) throws Exception {
		((RequestDispatcherHandlerI)this.connector).removeAVMFromRequestDispatcher(requestDispatcherURI);
		
	}

}
