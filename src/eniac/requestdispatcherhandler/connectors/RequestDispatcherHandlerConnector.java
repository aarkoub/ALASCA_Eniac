package eniac.requestdispatcherhandler.connectors;

import java.util.List;
import java.util.Map;

import eniac.requestdispatcherhandler.interfaces.RequestDispatcherHandlerI;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.processors.UnacceptableFrequencyException;
import fr.sorbonne_u.datacenter.hardware.processors.UnavailableFrequencyException;

public class RequestDispatcherHandlerConnector
extends AbstractConnector
implements RequestDispatcherHandlerI{


	@Override
	public Map<String, String> addAVMToRequestDispatcher(String handler_uri, String requestDispatcherURI)throws Exception {
		return ((RequestDispatcherHandlerI)this.offering).addAVMToRequestDispatcher(handler_uri, requestDispatcherURI);
		
	}
	
	
	@Override
	public List<String> removeAVMFromRequestDispatcher(String handler_uri, String requestDispatcherURI, String avmURI) throws Exception{
		return ((RequestDispatcherHandlerI)this.offering).removeAVMFromRequestDispatcher(handler_uri, requestDispatcherURI, avmURI);
		
	}
	

	@Override
	public Map<String, String> addCoreToAvm(String handler_uri, String avm_uri, int nbcores) throws Exception {
		return ((RequestDispatcherHandlerI)this.offering).addCoreToAvm(handler_uri, avm_uri, nbcores);
	}

	@Override
	public List<String> removeCoreFromAvm(String handler_uri, String avm_uri) throws Exception {
		return ((RequestDispatcherHandlerI)this.offering).removeCoreFromAvm(handler_uri, avm_uri);
	}

	@Override
	public void setCoreFrequency(String processor_uri, int coreNo, int frequency)
			throws UnavailableFrequencyException,
			UnacceptableFrequencyException, Exception {
		((RequestDispatcherHandlerI)this.offering).setCoreFrequency(processor_uri, coreNo, frequency);
		
	}

}
