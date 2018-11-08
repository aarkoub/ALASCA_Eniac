package etape1.requestdispatcher.ports;

import etape1.requestdispatcher.interfaces.RequestDispatcherManagementI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class RequestDistributorManagementOutboundPort extends AbstractOutboundPort
implements RequestDispatcherManagementI {

	
	public RequestDistributorManagementOutboundPort(String uri, ComponentI owner ) throws Exception{
		super(uri, RequestDispatcherManagementI.class, owner);
		assert	uri != null && owner != null ;
	}
	
	public RequestDistributorManagementOutboundPort(ComponentI owner) throws Exception {
		super(RequestDispatcherManagementI.class, owner);
		assert owner!=null;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;



	

}
