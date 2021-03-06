package eniac.automatichandler.connectors;

import java.util.List;
import java.util.Map;

import eniac.automatichandler.interfaces.AutomaticHandlerRequestI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class AutomaticHandlerRequestConnector
extends AbstractConnector
implements AutomaticHandlerRequestI{


	@Override
	public Map<String, String> addAVMToRequestDispatcher(String handler_uri, String requestDispatcherURI)throws Exception {
		return ((AutomaticHandlerRequestI)this.offering).addAVMToRequestDispatcher(handler_uri, requestDispatcherURI);
		
	}
	
	
	@Override
	public List<String> removeAVMFromRequestDispatcher(String handler_uri, String requestDispatcherURI, String avmURI) throws Exception{
		return ((AutomaticHandlerRequestI)this.offering).removeAVMFromRequestDispatcher(handler_uri, requestDispatcherURI, avmURI);
		
	}
	

	@Override
	public Map<String, String> addCoreToAvm(String handler_uri, String avm_uri, int nbcores) throws Exception {
		return ((AutomaticHandlerRequestI)this.offering).addCoreToAvm(handler_uri, avm_uri, nbcores);
	}

	@Override
	public List<String> removeCoreFromAvm(String handler_uri, String avm_uri) throws Exception {
		return ((AutomaticHandlerRequestI)this.offering).removeCoreFromAvm(handler_uri, avm_uri);
	}



}
