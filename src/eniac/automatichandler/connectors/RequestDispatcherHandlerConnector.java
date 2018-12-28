package eniac.automatichandler.connectors;

import eniac.automatichandler.interfaces.RequestDispatcherHandlerI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class RequestDispatcherHandlerConnector
extends AbstractConnector
implements RequestDispatcherHandlerI{


	@Override
	public void addAVMToRequestDispatcher(String requestDispatcherURI)throws Exception {
		((RequestDispatcherHandlerI)this.offering).addAVMToRequestDispatcher(requestDispatcherURI);
		
	}

	@Override
	public void removeAVMFromRequestDispatcher(String requestDispatcherURI) throws Exception{
		((RequestDispatcherHandlerI)this.offering).removeAVMFromRequestDispatcher(requestDispatcherURI);
		
	}

}
