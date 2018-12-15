package eniac.requestdispatcher.connectors;

import eniac.requestdispatcher.data.AVMUris;
import eniac.requestdispatcher.interfaces.RequestDispatcherManagementI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class RequestDispatcherManagementConnector extends AbstractConnector
		implements RequestDispatcherManagementI {

	@Override
	public void addAVM(AVMUris avmuris) throws Exception {
		((RequestDispatcherManagementI)this.offering).addAVM(avmuris);
	}

	@Override
	public boolean removeAVM(String uri) throws Exception {
		return ((RequestDispatcherManagementI)this.offering).removeAVM(uri);
	}

	@Override
	public int getNbAvm() throws Exception {
		return ((RequestDispatcherManagementI)this.offering).getNbAvm();
	}

	@Override
	public void connectAVM(String uri) throws Exception {
		((RequestDispatcherManagementI)this.offering).connectAVM(uri);
	}

	@Override
	public void startPortConnection() throws Exception {
		((RequestDispatcherManagementI)this.offering).startPortConnection();
		
	}


}
