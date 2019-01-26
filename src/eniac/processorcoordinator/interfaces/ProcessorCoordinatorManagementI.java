package eniac.processorcoordinator.interfaces;

public interface ProcessorCoordinatorManagementI {

	public String addCoordInboundPort() throws Exception;

	public void removeOrderOutport(String handler_uri) throws Exception;
}
