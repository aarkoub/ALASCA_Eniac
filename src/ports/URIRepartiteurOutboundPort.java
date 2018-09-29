package ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import interfaces.URIRepartiteurI;

public class URIRepartiteurOutboundPort extends AbstractOutboundPort implements URIRepartiteurI {

	
	public URIRepartiteurOutboundPort(String uri, ComponentI owner ) throws Exception{
		super(uri, URIRepartiteurI.class, owner);
		assert	uri != null && owner != null ;
	}
	
	public URIRepartiteurOutboundPort(ComponentI owner) throws Exception {
		super(URIRepartiteurI.class, owner);
		assert owner!=null;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getURI() {
		return ((URIRepartiteurI)this.connector).getURI();
	}

}
