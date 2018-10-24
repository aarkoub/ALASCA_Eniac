package etape1.requestdistributor.ports;

import etape1.requestdispatcher.RequestDispatcher;
import etape1.requestdistributor.interfaces.RequestDistributorManagementI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class RequestDistributorManagementInboundPort extends		AbstractInboundPort
implements	RequestDistributorManagementI {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public	RequestDistributorManagementInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(RequestDistributorManagementI.class, owner) ;

		assert	owner != null && owner instanceof RequestDispatcher ;
	}

	public	RequestDistributorManagementInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, RequestDistributorManagementI.class, owner);

		assert	owner != null && owner instanceof RequestDispatcher ;
	}

	
}
