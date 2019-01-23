package eniac.processorcoordinator.connectors;

import eniac.processorcoordinator.interfaces.ProcessorCoordinatorFreqI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class ProcessorCoordinatorFreqConnector
extends AbstractConnector
implements ProcessorCoordinatorFreqI{

	@Override
	public void setCoreFrequency(String handler_uri, int coreNo, int frequency) throws Exception {
		((ProcessorCoordinatorFreqI)this.offering).setCoreFrequency(handler_uri, coreNo, frequency);
		
	}

	@Override
	public void addProcessorCoordinatorOrderOutboundPort(String handler_uri, String processorCoordinatorOrderInboundPortURI)
			throws Exception {
		((ProcessorCoordinatorFreqI)this.offering).addProcessorCoordinatorOrderOutboundPort(handler_uri, processorCoordinatorOrderInboundPortURI);		
	}

}
