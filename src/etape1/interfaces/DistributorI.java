package etape1.interfaces;

import fr.sorbonne_u.components.interfaces.RequiredI;
import fr.sorbonne_u.datacenterclient.requestgenerator.Request;

public interface DistributorI extends RequiredI {
	
	public Request getRequest()throws Exception;
	
}
