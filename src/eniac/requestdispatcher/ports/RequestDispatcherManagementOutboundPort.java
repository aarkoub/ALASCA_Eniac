package eniac.requestdispatcher.ports;

import eniac.requestdispatcher.data.AVMUris;
import eniac.requestdispatcher.interfaces.RequestDispatcherManagementI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class RequestDispatcherManagementOutboundPort extends AbstractOutboundPort
		implements RequestDispatcherManagementI {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public				RequestDispatcherManagementOutboundPort(
			ComponentI owner
			) throws Exception
		{
			super(RequestDispatcherManagementI.class, owner) ;

			assert	owner != null ;
		}

		public				RequestDispatcherManagementOutboundPort(
			String uri,
			ComponentI owner
			) throws Exception
		{
			super(uri, RequestDispatcherManagementI.class, owner) ;

			assert	uri != null && owner != null ;
		}
	
	
	@Override
	public void addAVM(AVMUris avmuris) throws Exception {
		((RequestDispatcherManagementI)this.connector).addAVM(avmuris);

	}

	@Override
	public boolean removeAVM(String uri) throws Exception {
		return ((RequestDispatcherManagementI)this.connector).removeAVM(uri);
	}

	@Override
	public int getNbAvm() throws Exception {
		return ((RequestDispatcherManagementI)this.connector).getNbAvm();
	}

	@Override
	public void connectAVM(String uri) throws Exception {
		((RequestDispatcherManagementI)this.connector).connectAVM(uri);
	}

	@Override
	public void startPortConnection() throws Exception {
		((RequestDispatcherManagementI)this.connector).startPortConnection();
	}


}
