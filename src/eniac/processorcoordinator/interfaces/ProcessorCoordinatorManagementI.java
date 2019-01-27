package eniac.processorcoordinator.interfaces;

public interface ProcessorCoordinatorManagementI {

	public String addCoordInboundPort() throws Exception;

	public void removeOrderOutport(String handler_uri) throws Exception;
	
	public void notifyFreqChanged(String handler_uri) throws Exception;
}
