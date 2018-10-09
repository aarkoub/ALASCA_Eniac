package etape1.connector;

import etape1.interfaces.GeneratorI;
import etape1.interfaces.DistributorI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class RequestServiceConnector extends AbstractConnector implements DistributorI {

	@Override
	public String getRequete() throws Exception {
		
		return ((GeneratorI)this.offering).genererRequest();
	}

}
