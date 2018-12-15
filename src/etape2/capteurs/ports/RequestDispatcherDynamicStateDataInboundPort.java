package etape2.capteurs.ports;

import etape1.requestdispatcher.multi.components.RequestDispatcherMultiVM;
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
		assert owner instanceof RequestDispatcherMultiVM;
	}

	public RequestDispatcherDynamicStateDataInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, owner);
		assert owner instanceof RequestDispatcherMultiVM;
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
						return ((RequestDispatcherMultiVM)this.getOwner()).
										getDynamicState() ;
					}
				}) ;
	}

}
