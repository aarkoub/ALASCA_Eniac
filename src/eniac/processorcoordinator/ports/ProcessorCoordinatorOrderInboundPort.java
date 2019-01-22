package eniac.processorcoordinator.ports;

import eniac.automatichandler.AutomaticHandler;
import eniac.automatichandler.interfaces.AutomaticHandlerManagementI;
import eniac.processorcoordinator.ProcessorCoordinator;
import eniac.processorcoordinator.interfaces.ProcessorCoordinatorOrderI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class ProcessorCoordinatorOrderInboundPort
extends AbstractInboundPort
implements ProcessorCoordinatorOrderI{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	public	ProcessorCoordinatorOrderInboundPort(
			ComponentI owner
			) throws Exception
		{
			super(ProcessorCoordinatorOrderI.class, owner) ;

			assert	owner != null && owner instanceof AutomaticHandler ;
		}

		public	ProcessorCoordinatorOrderInboundPort(
			String uri,
			ComponentI owner
			) throws Exception
		{
			super(uri, ProcessorCoordinatorOrderI.class, owner);

			assert	owner != null && owner instanceof AutomaticHandler ;
		}

		@Override
		public void setCoreFreqNextTime(String procURI, int coreNo, int frequency) throws Exception {
			this.getOwner().handleRequestSync(
					new AbstractComponent.AbstractService<Void>() {
						@Override
						public Void call() throws Exception {
							((AutomaticHandler)this.getOwner()).
									setCoreFreqNextTime(procURI, coreNo, frequency);
							return null;
						}
					});			
		}

}
