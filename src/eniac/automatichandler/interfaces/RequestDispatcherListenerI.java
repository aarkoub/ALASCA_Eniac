package eniac.automatichandler.interfaces;

public interface RequestDispatcherListenerI {
	
	 public void receiveNewRequestDispatcherURI(String rd_uri, String requestDispatcherDynamicStateDataInboundPortURI,
			String requestDispatcherStaticStateDataInboundPortURI) throws Exception;

}
