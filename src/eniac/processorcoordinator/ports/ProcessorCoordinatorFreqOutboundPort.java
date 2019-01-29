package eniac.processorcoordinator.ports;

import eniac.processorcoordinator.interfaces.ProcessorCoordinatorFreqI;
import eniac.processorcoordinator.interfaces.ProcessorCoordinatorManagementI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class ProcessorCoordinatorFreqOutboundPort
extends AbstractOutboundPort
implements ProcessorCoordinatorFreqI{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProcessorCoordinatorFreqOutboundPort(String uri, ComponentI owner ) throws Exception{
		super(uri, ProcessorCoordinatorFreqI.class, owner);
		assert	uri != null && owner != null ;
	}
	
	public ProcessorCoordinatorFreqOutboundPort(ComponentI owner) throws Exception {
		super(ProcessorCoordinatorFreqI.class, owner);
		assert owner!=null;
	}

	@Override
	public boolean setCoreFrequency(String handler_uri, int coreNo, int frequency) throws Exception {
		return ((ProcessorCoordinatorFreqI)connector).setCoreFrequency(handler_uri, coreNo, frequency);
		
	}

	@Override
	public void addProcessorCoordinatorOrderOutboundPort(String handler_uri, String processorCoordinatorOrderInboundPortURI)
			throws Exception {
		((ProcessorCoordinatorFreqI)connector).addProcessorCoordinatorOrderOutboundPort(handler_uri, processorCoordinatorOrderInboundPortURI);		
	}
	
}
