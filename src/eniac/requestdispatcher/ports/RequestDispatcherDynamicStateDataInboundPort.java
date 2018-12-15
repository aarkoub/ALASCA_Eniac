package eniac.requestdispatcher.ports;

import eniac.requestdispatcher.RequestDispatcher;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataOfferedI.DataI;
import fr.sorbonne_u.datacenter.hardware.processors.Processor;
import fr.sorbonne_u.datacenter.ports.AbstractControlledDataInboundPort;

public class RequestDispatcherDynamicStateDataInboundPort
extends AbstractControlledDataInboundPort {

	public RequestDispatcherDynamicStateDataInboundPort(ComponentI owner) throws Exception {
		super(owner);
		assert owner instanceof RequestDispatcher;
	}

	public RequestDispatcherDynamicStateDataInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, owner);
		assert owner instanceof RequestDispatcher;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public DataI get() throws Exception {
		return this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<DataOfferedI.DataI>() {
					@Override
					public DataOfferedI.DataI call() throws Exception {
						return ((RequestDispatcher)this.getOwner()).
										getDynamicState() ;
					}
				}) ;
	}

}
