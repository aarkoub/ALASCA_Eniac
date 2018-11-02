package etape1.requestdispatcher.multi.ports;

import etape1.requestdispatcher.multi.AVMUris;
import etape1.requestdispatcher.multi.interfaces.RequestDispatcherMultiVMManagementI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class RequestDispatcherMultiVMManagementOutboundPort extends AbstractOutboundPort
		implements RequestDispatcherMultiVMManagementI {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public				RequestDispatcherMultiVMManagementOutboundPort(
			ComponentI owner
			) throws Exception
		{
			super(RequestDispatcherMultiVMManagementI.class, owner) ;

			assert	owner != null ;
		}

		public				RequestDispatcherMultiVMManagementOutboundPort(
			String uri,
			ComponentI owner
			) throws Exception
		{
			super(uri, RequestDispatcherMultiVMManagementI.class, owner) ;

			assert	uri != null && owner != null ;
		}
	
	
	@Override
	public void addAVM(AVMUris avmuris) throws Exception {
		((RequestDispatcherMultiVMManagementI)this.connector).addAVM(avmuris);

	}

	@Override
	public boolean removeAVM(String uri) throws Exception {
		return ((RequestDispatcherMultiVMManagementI)this.connector).removeAVM(uri);
	}

	@Override
	public int getNbAvm() throws Exception {
		return ((RequestDispatcherMultiVMManagementI)this.connector).getNbAvm();
	}

}
