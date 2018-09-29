package ports;

import components.GenerateurRequete;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import interfaces.GenerateurI;
import interfaces.RepartiteurI;

public class GenerateurInboundPort extends AbstractInboundPort implements GenerateurI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GenerateurInboundPort(String uri, ComponentI owner ) throws Exception{
		super(uri, GenerateurI.class, owner);
		assert	uri != null && owner != null ;
	}
	
	public GenerateurInboundPort(ComponentI owner) throws Exception {
		super(RepartiteurI.class, owner);
		assert owner!=null;
	}

	@Override
	public String genererRequest() throws Exception {
		return this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<String>() {
					@Override
					public String call() throws Exception {
						return ((GenerateurRequete)this.getOwner()).
									genererRequeteService() ;
					}
				}) ;
	}
	
}
