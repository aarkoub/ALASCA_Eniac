package eniac.automatichandler.connectors;

import eniac.automatichandler.interfaces.RequestDispatcherListenerI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class RequestDispatcherListenerConnector
extends AbstractConnector
implements RequestDispatcherListenerI{

	@Override
	public void receiveNewRequestDispatcherURI(String rd_uri, String requestDispatcherDynamicStateDataInboundPortURI,
			String requestDispatcherStaticStateDataInboundPortURI) throws Exception {
		((RequestDispatcherListenerI)this.offering).receiveNewRequestDispatcherURI(rd_uri, requestDispatcherDynamicStateDataInboundPortURI,
				requestDispatcherStaticStateDataInboundPortURI);
		
	}

}
