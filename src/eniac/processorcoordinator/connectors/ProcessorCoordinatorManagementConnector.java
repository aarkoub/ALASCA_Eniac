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

	

}
