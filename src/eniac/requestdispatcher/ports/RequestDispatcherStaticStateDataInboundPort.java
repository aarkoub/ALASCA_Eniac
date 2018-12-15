package eniac.requestdispatcher.ports;

import eniac.requestdispatcher.interfaces.RequestDispatcherStaticStateI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedI.DataI;
import fr.sorbonne_u.datacenter.ports.AbstractControlledDataInboundPort;

public class RequestDispatcherStaticStateDataInboundPort 
extends AbstractControlledDataInboundPort
implements RequestDispatcherStaticStateI{

	public RequestDispatcherStaticStateDataInboundPort(ComponentI owner) throws Exception {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	public RequestDispatcherStaticStateDataInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, owner);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public DataI get() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
