package ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.RepartiteurI;

public class RepartiteurOutboundPort extends AbstractOutboundPort implements RepartiteurI {

	
	public RepartiteurOutboundPort(String uri, ComponentI owner ) throws Exception{
		super(uri, RepartiteurI.class, owner);
		assert	uri != null && owner != null ;
	}
	
	public RepartiteurOutboundPort(ComponentI owner) throws Exception {
		super(RepartiteurI.class, owner);
		assert owner!=null;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getRequete() throws Exception {
		return ((RepartiteurI)this.connector).getRequete();
	}

}
