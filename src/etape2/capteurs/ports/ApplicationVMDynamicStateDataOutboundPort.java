package etape2.capteurs.ports;

import etape2.capteurs.interfaces.ApplicationVMStateDataConsumerI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.datacenter.ports.AbstractControlledDataOutboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMDynamicStateI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMStaticStateDataI;

public class ApplicationVMDynamicStateDataOutboundPort extends	
AbstractControlledDataOutboundPort
implements ApplicationVMStaticStateDataI{

	
	private String avmUri;


	public ApplicationVMDynamicStateDataOutboundPort(ComponentI owner, String avmUri) throws Exception {
		super(owner);
		this.avmUri = avmUri;
		
		assert owner instanceof ApplicationVMStateDataConsumerI; 
		
	}
	
	public ApplicationVMDynamicStateDataOutboundPort(String uri, ComponentI owner, String avmUri) throws Exception {
		super(uri, owner);
		this.avmUri = avmUri;
		
		assert owner instanceof ApplicationVMStateDataConsumerI; 
		
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void receive(fr.sorbonne_u.components.interfaces.DataRequiredI.DataI d) throws Exception {
		final String uri = this.avmUri ;
		this.owner.handleRequestAsync(
					new AbstractComponent.AbstractService<Void>() {
						@Override
						public Void call() throws Exception {
							((ApplicationVMStateDataConsumerI)this.getOwner()).
								acceptApplicationVMDynamicData(
									uri, ((ApplicationVMDynamicStateI)d)) ;
							return null;
						}
					}) ;
		
	}

}
