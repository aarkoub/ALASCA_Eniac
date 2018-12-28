package eniac.automatichandler.interfaces;

public interface RequestDispatcherHandlerI {
	
	 public void addAVMToRequestDispatcher(String requestDispatcherURI) throws Exception;
	 
	 public void removeAVMFromRequestDispatcher(String requestDispatcherURI) throws Exception;

}
