package eniac.processorcoordinator.connectors;

import eniac.processorcoordinator.interfaces.ProcessorCoordinatorManagementI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class ProcessorCoordinatorManagementConnector
extends AbstractConnector
implements ProcessorCoordinatorManagementI{

	@Override
	public String addCoordInboundPort() throws Exception {
		return ((ProcessorCoordinatorManagementI)offering).addCoordInboundPort();
	}

	@Override
	public void removeOrderOutport(String handler_uri) throws Exception {
		((ProcessorCoordinatorManagementI)offering).removeOrderOutport(handler_uri);		
	}

	@Override
	public void notifyCorePossession(String handler_uri, int coreNum) throws Exception {
		((ProcessorCoordinatorManagementI)offering).notifyCorePossession(handler_uri, coreNum);
		
	}

	@Override
	public void notifyCoreRestitution(String handler_uri, int coreNum) throws Exception {
		((ProcessorCoordinatorManagementI)offering).notifyCoreRestitution(handler_uri, coreNum);
		
	}

	

}
