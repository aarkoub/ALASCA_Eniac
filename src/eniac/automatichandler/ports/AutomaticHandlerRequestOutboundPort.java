package eniac.automatichandler.ports;

import java.util.List;
import java.util.Map;

import eniac.automatichandler.interfaces.AutomaticHandlerRequestI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class AutomaticHandlerRequestOutboundPort
extends AbstractOutboundPort
implements AutomaticHandlerRequestI{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public AutomaticHandlerRequestOutboundPort(String uri, ComponentI owner ) throws Exception{
		super(uri, AutomaticHandlerRequestI.class, owner);
		assert	uri != null && owner != null ;
	}
	
	public AutomaticHandlerRequestOutboundPort(ComponentI owner) throws Exception {
		super(AutomaticHandlerRequestI.class, owner);
		assert owner!=null;
	}



	@Override
	public Map<String, String> addAVMToRequestDispatcher(String handler_uri, String requestDispatcherURI) throws Exception {
		return ((AutomaticHandlerRequestI)this.connector).addAVMToRequestDispatcher(handler_uri, requestDispatcherURI);
		
	}

	
	@Override
	public List<String> removeAVMFromRequestDispatcher(String handler_uri, String requestDispatcherURI, String avmURI) throws Exception {
		return ((AutomaticHandlerRequestI)this.connector).removeAVMFromRequestDispatcher(handler_uri, requestDispatcherURI, avmURI);
		
	}

	@Override
	public Map<String, String> addCoreToAvm(String handler_uri, String avm_uri, int nbcores) throws Exception {
		return ((AutomaticHandlerRequestI)this.connector).addCoreToAvm(handler_uri, avm_uri, nbcores);
	}

	@Override
	public List<String> removeCoreFromAvm(String handler_uri, String avm_uri) throws Exception {
		return ((AutomaticHandlerRequestI)this.connector).removeCoreFromAvm(handler_uri, avm_uri);
	}

}
