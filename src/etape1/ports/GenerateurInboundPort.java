package etape1.ports;

import etape1.components.RequestGenerator;
import etape1.interfaces.GeneratorI;
import etape1.interfaces.DistributorI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class GenerateurInboundPort extends AbstractInboundPort implements GeneratorI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public GenerateurInboundPort(String uri, ComponentI owner ) throws Exception{

		super(uri, GeneratorI.class, owner);
		assert	uri != null && owner != null ;
		
	}
	
	public GenerateurInboundPort(ComponentI owner) throws Exception {
		super(DistributorI.class, owner);
		assert owner!=null;
	}

	@Override
	public String genererRequest() throws Exception {

		return this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<String>() {
					@Override
					public String call() throws Exception {
						return ((RequestGenerator)this.getOwner()).;
					}
				}) ;
	}
	
}
