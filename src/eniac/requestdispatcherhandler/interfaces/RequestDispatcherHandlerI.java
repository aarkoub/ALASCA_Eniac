package eniac.requestdispatcherhandler.interfaces;

import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;

public interface RequestDispatcherHandlerI {
	
	 public String addAVMToRequestDispatcher(String requestDispatcherURI) throws Exception;
	 
	 public String removeAVMFromRequestDispatcher(String requestDispatcherURI) throws Exception;

	 public boolean addCoreToAvm(String avm_uri, int nbcores) throws Exception;
	 
	 public boolean removeCoreFromAvm(String avm_uri, AllocatedCore allocatedCore) throws Exception;
}
