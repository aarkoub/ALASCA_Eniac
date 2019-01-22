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
	public void setCoreFrequency(int coreNo, int frequency) throws Exception {
		((ProcessorCoordinatorFreqI)connector).setCoreFrequency(coreNo, frequency);
		
	}
	
}
