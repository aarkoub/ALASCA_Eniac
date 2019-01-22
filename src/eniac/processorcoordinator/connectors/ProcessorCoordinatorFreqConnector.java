package eniac.processorcoordinator.connectors;

import eniac.processorcoordinator.interfaces.ProcessorCoordinatorFreqI;
import eniac.processorcoordinator.interfaces.ProcessorCoordinatorManagementI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class ProcessorCoordinatorFreqConnector
extends AbstractConnector
implements ProcessorCoordinatorFreqI{

	@Override
	public void setCoreFrequency(int coreNo, int frequency) throws Exception {
		((ProcessorCoordinatorFreqI)this.offering).setCoreFrequency(coreNo, frequency);
		
	}

}
