package etape1.requestdispatcher.multi.connectors;

import etape1.requestdispatcher.multi.data.AVMUris;
import etape1.requestdispatcher.multi.interfaces.RequestDispatcherMultiVMManagementI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class RequestDispatcherMultiVMManagementConnector extends AbstractConnector
		implements RequestDispatcherMultiVMManagementI {

	@Override
	public void addAVM(AVMUris avmuris) throws Exception {
		((RequestDispatcherMultiVMManagementI)this.offering).addAVM(avmuris);
	}

	@Override
	public boolean removeAVM(String uri) throws Exception {
		return ((RequestDispatcherMultiVMManagementI)this.offering).removeAVM(uri);
	}

	@Override
	public int getNbAvm() throws Exception {
		return ((RequestDispatcherMultiVMManagementI)this.offering).getNbAvm();
	}

	@Override
	public void connectAVM(String uri) throws Exception {
		((RequestDispatcherMultiVMManagementI)this.offering).connectAVM(uri);
	}

	@Override
	public void startPortConnection() throws Exception {
		((RequestDispatcherMultiVMManagementI)this.offering).startPortConnection();
		
	}


}
