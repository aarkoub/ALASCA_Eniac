package eniac.processorcoordinator.ports;

import eniac.processorcoordinator.interfaces.ProcessorCoordinatorOrderI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class ProcessorCoordinatorOrderOutboundPort
extends AbstractOutboundPort
implements ProcessorCoordinatorOrderI{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public ProcessorCoordinatorOrderOutboundPort(String uri, ComponentI owner ) throws Exception{
		super(uri, ProcessorCoordinatorOrderI.class, owner);
		assert	uri != null && owner != null ;
	}
	
	public ProcessorCoordinatorOrderOutboundPort(ComponentI owner) throws Exception {
		super(ProcessorCoordinatorOrderI.class, owner);
		assert owner!=null;
	}
	

	@Override
	public void setCoreFreqNextTime(String procURI, int coreNo, int frequency) throws Exception {
		((ProcessorCoordinatorOrderI)connector).setCoreFreqNextTime(procURI, coreNo, frequency);
		
	}

	@Override
	public void removeFreqPort(String procURI) throws Exception {
		((ProcessorCoordinatorOrderI)connector).removeFreqPort(procURI);
		
	}

}
