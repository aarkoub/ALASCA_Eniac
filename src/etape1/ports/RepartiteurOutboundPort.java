package etape1.ports;

import etape1.interfaces.DistributorI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class RepartiteurOutboundPort extends AbstractOutboundPort implements DistributorI {

	
	public RepartiteurOutboundPort(String uri, ComponentI owner ) throws Exception{
		super(uri, DistributorI.class, owner);
		assert	uri != null && owner != null ;
	}
	
	public RepartiteurOutboundPort(ComponentI owner) throws Exception {
		super(DistributorI.class, owner);
		assert owner!=null;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getRequete() throws Exception {

		return ((DistributorI)this.connector).getRequete();

	}

}
