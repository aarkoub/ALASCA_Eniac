package eniac.processorcoordinator.ports;

import eniac.processorcoordinator.ProcessorCoordinator;
import eniac.processorcoordinator.interfaces.ProcessorCoordinatorManagementI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class ProcessorCoordinatorManagementInboundPort
extends AbstractInboundPort
implements ProcessorCoordinatorManagementI
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public	ProcessorCoordinatorManagementInboundPort(
			ComponentI owner
			) throws Exception
		{
			super(ProcessorCoordinatorManagementI.class, owner) ;

		}

		public	ProcessorCoordinatorManagementInboundPort(
			String uri,
			ComponentI owner
			) throws Exception
		{
			super(uri, ProcessorCoordinatorManagementI.class, owner);

			
		}

		@Override
		public String addCoordInboundPort() throws Exception {
			return this.getOwner().handleRequestSync(
					new AbstractComponent.AbstractService<String>() {
						@Override
						public String call() throws Exception {
							return ((ProcessorCoordinator)this.getOwner()).
									addCoordInboundPort() ;
							
						}
					});			
			
		}

		@Override
		public void removeOrderOutport(String handler_uri) throws Exception {
			this.getOwner().handleRequestSync(
					new AbstractComponent.AbstractService<Void>() {
						@Override
						public Void call() throws Exception {
							((ProcessorCoordinator)this.getOwner()).removeOrderOutport(handler_uri); ;
							return null;
							
						}
					});			
			
		}

		@Override
		public void notifyFreqChanged(String handler_uri) throws Exception {
			this.getOwner().handleRequestAsync(
					new AbstractComponent.AbstractService<Void>() {
						@Override
						public Void call() throws Exception {
							((ProcessorCoordinator)this.getOwner()).notifyFreqChanged(handler_uri);
							return null;
							
						}
					});	
			
		}

		@Override
		public void notifyCorePossession(String handler_uri, int coreNum) throws Exception {
			this.getOwner().handleRequestSync(
					new AbstractComponent.AbstractService<Void>() {
						@Override
						public Void call() throws Exception {
							((ProcessorCoordinator)this.getOwner()).notifyCorePossession(handler_uri, coreNum);
							return null;
							
						}
					});			
			
		}

	
}
