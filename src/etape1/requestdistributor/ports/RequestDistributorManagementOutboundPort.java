package etape1.requestdistributor.ports;

import etape1.requestdistributor.interfaces.RequestDistributorManagementI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenterclient.requestgenerator.Request;
import fr.sorbonne_u.datacenterclient.requestgenerator.interfaces.RequestGeneratorManagementI;

public class RequestDistributorManagementOutboundPort extends AbstractOutboundPort
implements RequestDistributorManagementI {

	
	public RequestDistributorManagementOutboundPort(String uri, ComponentI owner ) throws Exception{
		super(uri, RequestDistributorManagementI.class, owner);
		assert	uri != null && owner != null ;
	}
	
	public RequestDistributorManagementOutboundPort(ComponentI owner) throws Exception {
		super(RequestDistributorManagementI.class, owner);
		assert owner!=null;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	

}
