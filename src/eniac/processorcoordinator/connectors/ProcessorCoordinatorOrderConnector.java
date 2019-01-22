package eniac.processorcoordinator.connectors;

import eniac.processorcoordinator.interfaces.ProcessorCoordinatorOrderI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class ProcessorCoordinatorOrderConnector
extends AbstractConnector
implements ProcessorCoordinatorOrderI{

	@Override
	public void setCoreFreqNextTime(String procURI, int coreNo, int frequency) throws Exception {
		((ProcessorCoordinatorOrderI)this.offering).setCoreFreqNextTime(procURI, coreNo, frequency);
		
	}
	
	

}
