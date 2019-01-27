package eniac.applicationvm.ports;

import eniac.applicationvm.interfaces.ApplicationVMStateDataConsumerI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.datacenter.ports.AbstractControlledDataOutboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMDynamicStateI;

/**
 * Cette classe correspond au port utilisé pour l'envoit des données dynamiques de l'AVM vers le RequestDispatcher.
 * Il a été prit exemple sur BCM.
 * @author L-C
 *
 */
public class ApplicationVMDynamicStateDataOutboundPort extends	
AbstractControlledDataOutboundPort{

	
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
