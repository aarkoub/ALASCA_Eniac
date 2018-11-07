package etape1.requestdispatcher.multi.ports;

import etape1.requestdispatcher.multi.AVMUris;
import etape1.requestdispatcher.multi.RequestDispatcherMultiVM;
import etape1.requestdispatcher.multi.interfaces.RequestDispatcherMultiVMManagementI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class RequestDispatcherMultiVMManagementInboundPort extends AbstractInboundPort
		implements RequestDispatcherMultiVMManagementI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public				RequestDispatcherMultiVMManagementInboundPort(
			ComponentI owner
			) throws Exception
		{
			super(RequestDispatcherMultiVMManagementI.class, owner) ;

			assert	owner != null && owner instanceof RequestDispatcherMultiVM ;
		}

		public				RequestDispatcherMultiVMManagementInboundPort(
			String uri,
			ComponentI owner
			) throws Exception
		{
			super(uri, RequestDispatcherMultiVMManagementI.class, owner);

			assert	owner != null && owner instanceof RequestDispatcherMultiVM ;
		}
	
	
	@Override
	public void addAVM(AVMUris avmuris) throws Exception {
		this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((RequestDispatcherMultiVM)this.getOwner()).
							addAVM(avmuris); ;
						return null;
					}
				}) ;
	}

	@Override
	public boolean removeAVM(String uri) throws Exception {
		return this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<Boolean>() {
					@Override
					public Boolean call() throws Exception {
						return ((RequestDispatcherMultiVM)this.getOwner()).removeAVM(uri);
					}
				}) ;
	}

	@Override
	public int getNbAvm() throws Exception {
		return this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<Integer>() {
					@Override
					public Integer call() throws Exception {
						return ((RequestDispatcherMultiVM)this.getOwner()).getNbAvm();
					}
				}) ;
	}

	@Override
	public void connectAVM(String uri) throws Exception {
		this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((RequestDispatcherMultiVM)this.getOwner()).
							connectAVM(uri); ;
						return null;
					}
				}) ;
		
	}

}
