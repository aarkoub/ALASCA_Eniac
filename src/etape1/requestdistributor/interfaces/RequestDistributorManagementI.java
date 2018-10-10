package etape1.requestdistributor.interfaces;

import fr.sorbonne_u.components.interfaces.RequiredI;
import fr.sorbonne_u.datacenterclient.requestgenerator.Request;

public interface RequestDistributorManagementI extends RequiredI {
	
	public Request getRequest()throws Exception;
	
}
