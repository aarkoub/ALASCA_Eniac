package etape1;

import etape1.interfaces.GenerateurI;
import etape1.interfaces.RepartiteurI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class RequeteServiceConnector extends AbstractConnector implements RepartiteurI {

	@Override
	public String getRequete() throws Exception {
		
		return ((GenerateurI)this.offering).genererRequest();
	}

}
