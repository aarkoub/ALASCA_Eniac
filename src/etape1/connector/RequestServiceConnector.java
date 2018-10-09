package etape1.connector;

import etape1.interfaces.GeneratorI;
import etape1.interfaces.DistributorI;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenterclient.requestgenerator.Request;

public class RequestServiceConnector extends AbstractConnector implements DistributorI {

	@Override
	public Request getRequest() throws Exception {
		
		return ((GeneratorI)this.offering).generateRequest();
	}

}
