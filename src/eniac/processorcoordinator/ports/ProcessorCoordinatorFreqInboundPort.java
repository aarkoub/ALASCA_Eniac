package eniac.processorcoordinator.ports;

import eniac.processorcoordinator.ProcessorCoordinator;
import eniac.processorcoordinator.interfaces.ProcessorCoordinatorFreqI;
import eniac.processorcoordinator.interfaces.ProcessorCoordinatorManagementI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class ProcessorCoordinatorFreqInboundPort
extends AbstractInboundPort
implements ProcessorCoordinatorFreqI
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public	ProcessorCoordinatorFreqInboundPort(
			ComponentI owner
			) throws Exception
		{
			super(ProcessorCoordinatorFreqI.class, owner) ;

		}

		public	ProcessorCoordinatorFreqInboundPort(
			String uri,
			ComponentI owner
			) throws Exception
		{
			super(uri, ProcessorCoordinatorFreqI.class, owner);

			
		}

		@Override
		public void setCoreFrequency(String handler_uri, int coreNo, int frequency) throws Exception {
			this.getOwner().handleRequestSync(
					new AbstractComponent.AbstractService<Void>() {
						@Override
						public Void call() throws Exception {
							((ProcessorCoordinator)this.getOwner()).
									setCoreFrequency(handler_uri, coreNo, frequency) ;
							return null;
						}
					});			
			
		}

		@Override
		public void addProcessorCoordinatorOrderOutboundPort(String handler_uri, String processorCoordinatorOrderInboundPortURI)
				throws Exception {
			this.getOwner().handleRequestSync(
					new AbstractComponent.AbstractService<Void>() {
						@Override
						public Void call() throws Exception {
							((ProcessorCoordinator)this.getOwner()).
									addProcessorCoordinatorOrderOutboundPort(handler_uri, processorCoordinatorOrderInboundPortURI);;
							return null;
						}
					});		
			
		}
	
}
