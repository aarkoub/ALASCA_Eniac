package eniac.requestdispatcherhandler.interfaces;

public interface RequestDispatcherHandlerI {
	
	 public String addAVMToRequestDispatcher(String requestDispatcherURI) throws Exception;
	 
	 public String removeAVMFromRequestDispatcher(String requestDispatcherURI) throws Exception;

}
