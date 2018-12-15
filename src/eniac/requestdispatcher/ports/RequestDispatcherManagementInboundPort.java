package eniac.requestdispatcher.ports;

import eniac.requestdispatcher.RequestDispatcher;
import eniac.requestdispatcher.data.AVMUris;
import eniac.requestdispatcher.interfaces.RequestDispatcherManagementI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class RequestDispatcherManagementInboundPort extends AbstractInboundPort
		implements RequestDispatcherManagementI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public				RequestDispatcherManagementInboundPort(
			ComponentI owner
			) throws Exception
		{
			super(RequestDispatcherManagementI.class, owner) ;

			assert	owner != null && owner instanceof RequestDispatcher ;
		}

		public				RequestDispatcherManagementInboundPort(
			String uri,
			ComponentI owner
			) throws Exception
		{
			super(uri, RequestDispatcherManagementI.class, owner);

			assert	owner != null && owner instanceof RequestDispatcher ;
		}
	
	
	@Override
	public void addAVM(AVMUris avmuris) throws Exception {
		this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((RequestDispatcher)this.getOwner()).
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
						return ((RequestDispatcher)this.getOwner()).removeAVM(uri);
					}
				}) ;
	}

	@Override
	public int getNbAvm() throws Exception {
		return this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<Integer>() {
					@Override
					public Integer call() throws Exception {
						return ((RequestDispatcher)this.getOwner()).getNbAvm();
					}
				}) ;
	}

	@Override
	public void connectAVM(String uri) throws Exception {
		this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((RequestDispatcher)this.getOwner()).
							connectAVM(uri);
						return null;
					}
				}) ;
		
	}

	@Override
	public void startPortConnection() throws Exception {
		this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((RequestDispatcher)this.getOwner()).
							startPortConnection();
						return null;
					}
				}) ;
		
		
	}


}
