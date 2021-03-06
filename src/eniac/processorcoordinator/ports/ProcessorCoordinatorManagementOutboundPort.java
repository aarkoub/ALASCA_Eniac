package eniac.processorcoordinator.ports;

import eniac.processorcoordinator.interfaces.ProcessorCoordinatorManagementI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class ProcessorCoordinatorManagementOutboundPort
extends AbstractOutboundPort
implements ProcessorCoordinatorManagementI{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ProcessorCoordinatorManagementOutboundPort(String uri, ComponentI owner ) throws Exception{
		super(uri, ProcessorCoordinatorManagementI.class, owner);
		assert	uri != null && owner != null ;
	}
	
	public ProcessorCoordinatorManagementOutboundPort(ComponentI owner) throws Exception {
		super(ProcessorCoordinatorManagementI.class, owner);
		assert owner!=null;
	}

	@Override
	public String addCoordInboundPort() throws Exception {
		return ((ProcessorCoordinatorManagementI)connector).addCoordInboundPort();
	}

	@Override
	public void removeOrderOutport(String handler_uri) throws Exception {
		((ProcessorCoordinatorManagementI)connector).removeOrderOutport(handler_uri);
		
	}


	@Override
	public void notifyCorePossession(String handler_uri, int coreNum) throws Exception {
		((ProcessorCoordinatorManagementI)connector).notifyCorePossession(handler_uri, coreNum);
		
	}

	@Override
	public void notifyCoreRestitution(String handler_uri, int coreNum) throws Exception {
		((ProcessorCoordinatorManagementI)connector).notifyCoreRestitution(handler_uri, coreNum);
		
	}
	
}
