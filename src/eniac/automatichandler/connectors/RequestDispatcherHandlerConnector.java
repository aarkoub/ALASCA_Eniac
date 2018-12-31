package eniac.automatichandler.connectors;

import eniac.automatichandler.interfaces.RequestDispatcherHandlerI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class RequestDispatcherHandlerConnector
extends AbstractConnector
implements RequestDispatcherHandlerI{


	@Override
	public String addAVMToRequestDispatcher(String requestDispatcherURI)throws Exception {
		return ((RequestDispatcherHandlerI)this.offering).addAVMToRequestDispatcher(requestDispatcherURI);
		
	}

	@Override
	public String removeAVMFromRequestDispatcher(String requestDispatcherURI) throws Exception{
		return ((RequestDispatcherHandlerI)this.offering).removeAVMFromRequestDispatcher(requestDispatcherURI);
		
	}

}
