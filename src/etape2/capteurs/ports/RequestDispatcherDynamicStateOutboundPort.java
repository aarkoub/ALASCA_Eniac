package etape2.capteurs.ports;

import etape2.capteurs.interfaces.RequestDispatcherDynamicStateDataI;
import etape2.capteurs.interfaces.RequestDispatcherStateDataConsumerI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataRequiredI.DataI;
import fr.sorbonne_u.datacenter.ports.AbstractControlledDataOutboundPort;

public class RequestDispatcherDynamicStateOutboundPort extends	
AbstractControlledDataOutboundPort{
	
	private String reqDispUri ;


	public RequestDispatcherDynamicStateOutboundPort(ComponentI owner, String reqDispUri) throws Exception {
		super(owner);
		
		this.reqDispUri = reqDispUri;
		
		assert owner instanceof RequestDispatcherStateDataConsumerI;
	}

	public RequestDispatcherDynamicStateOutboundPort(String uri, ComponentI owner, String reqDispUri) throws Exception {
		super(uri, owner);

		this.reqDispUri = reqDispUri;
		
		assert owner instanceof RequestDispatcherStateDataConsumerI;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void receive(DataI d) throws Exception {
		final String uri = this.reqDispUri ;
		this.owner.handleRequestAsync(
					new AbstractComponent.AbstractService<Void>() {
						@Override
						public Void call() throws Exception {
							((RequestDispatcherStateDataConsumerI)this.getOwner()).
								acceptRequestDispatcherDynamicData(
									uri, ((RequestDispatcherDynamicStateDataI)d)) ;
							return null;
						}
					}) ;
		
	}


}
