package eniac.requestdispatcherhandler.interfaces;

import java.util.List;

import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.processors.UnacceptableFrequencyException;
import fr.sorbonne_u.datacenter.hardware.processors.UnavailableFrequencyException;

public interface RequestDispatcherHandlerI {
	
	 public String addAVMToRequestDispatcher(String requestDispatcherURI) throws Exception;
	 
	 public String removeAVMFromRequestDispatcher(String requestDispatcherURI) throws Exception;
	 
	 public boolean removeAVMFromRequestDispatcher(String requestDispatcherURI, String avmURI) throws Exception;

	 public List<String> addCoreToAvm(String avm_uri, int nbcores) throws Exception;
	 
	 public boolean removeCoreFromAvm(String avm_uri) throws Exception;

	public void setCoreFrequency(String processor_uri,int coreNo, int frequency)
			throws UnavailableFrequencyException,
			UnacceptableFrequencyException, Exception;
}
