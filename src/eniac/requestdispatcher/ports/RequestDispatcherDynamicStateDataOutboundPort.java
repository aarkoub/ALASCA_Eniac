package eniac.requestdispatcher.ports;

import eniac.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;
import eniac.requestdispatcher.interfaces.RequestDispatcherStateDataConsumerI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataRequiredI.DataI;
import fr.sorbonne_u.datacenter.ports.AbstractControlledDataOutboundPort;

public class RequestDispatcherDynamicStateDataOutboundPort extends	
AbstractControlledDataOutboundPort{
	
	private String reqDispUri ;


	public RequestDispatcherDynamicStateDataOutboundPort(ComponentI owner, String reqDispUri) throws Exception {
		super(owner);
		
		this.reqDispUri = reqDispUri;
		
		assert owner instanceof RequestDispatcherStateDataConsumerI;
	}

	public RequestDispatcherDynamicStateDataOutboundPort(String uri, ComponentI owner, String reqDispUri) throws Exception {
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
									uri, ((RequestDispatcherDynamicStateI)d)) ;
							return null;
						}
					}) ;
		
	}


}
