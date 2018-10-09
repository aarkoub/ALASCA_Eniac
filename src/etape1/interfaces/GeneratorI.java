package etape1.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.datacenterclient.requestgenerator.Request;

public interface GeneratorI extends OfferedI {

	Request generateRequest() throws Exception;
	
}
