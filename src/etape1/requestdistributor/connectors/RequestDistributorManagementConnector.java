package etape1.requestdistributor.connectors;

import etape1.requestdistributor.interfaces.RequestDistributorManagementI;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenterclient.requestgenerator.Request;

public class RequestDistributorManagementConnector
extends AbstractConnector
implements RequestDistributorManagementI{

	@Override
	public Request getRequest() throws Exception {
		return ((RequestDistributorManagementI)this.offering).getRequest();
	}

}
