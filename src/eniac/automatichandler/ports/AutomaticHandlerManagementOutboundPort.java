package eniac.automatichandler.ports;

import eniac.automatichandler.interfaces.AutomaticHandlerManagementI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class AutomaticHandlerManagementOutboundPort  extends AbstractOutboundPort
implements AutomaticHandlerManagementI{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public AutomaticHandlerManagementOutboundPort(String uri, ComponentI owner ) throws Exception{
		super(uri, AutomaticHandlerManagementI.class, owner);
		assert	uri != null && owner != null ;
	}
	
	public AutomaticHandlerManagementOutboundPort(ComponentI owner) throws Exception {
		super(AutomaticHandlerManagementI.class, owner);
		assert owner!=null;
	}

}
