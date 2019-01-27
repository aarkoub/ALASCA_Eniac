package eniac.automatichandler.interfaces;

import java.util.List;
import java.util.Map;

import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.processors.UnacceptableFrequencyException;
import fr.sorbonne_u.datacenter.hardware.processors.UnavailableFrequencyException;

public interface AutomaticHandlerRequestI {
	
	 public Map<String, String> addAVMToRequestDispatcher(String handler_uri, String requestDispatcherURI) throws Exception;
	 	 
	 public List<String> removeAVMFromRequestDispatcher(String handler_uri, String requestDispatcherURI, String avmURI) throws Exception;

	 public Map<String, String> addCoreToAvm(String handler_uri, String avm_uri, int nbcores) throws Exception;
	 
	 public List<String> removeCoreFromAvm(String handler_uri, String avm_uri) throws Exception;

	public void setCoreFrequency(String processor_uri,int coreNo, int frequency)
			throws UnavailableFrequencyException,
			UnacceptableFrequencyException, Exception;
}
