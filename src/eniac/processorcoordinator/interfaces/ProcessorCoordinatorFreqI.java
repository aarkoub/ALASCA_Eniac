package eniac.processorcoordinator.interfaces;

public interface ProcessorCoordinatorFreqI {
	
	public boolean setCoreFrequency(String handler_uri, final int coreNo, final int frequency) throws Exception;
	public void addProcessorCoordinatorOrderOutboundPort(String handler_uri, String processorCoordinatorOrderInboundPortURI) throws Exception;
		
}
