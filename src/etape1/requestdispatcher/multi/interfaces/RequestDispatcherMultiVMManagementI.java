package etape1.requestdispatcher.multi.interfaces;

import etape1.requestdispatcher.multi.AVMUris;
import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface RequestDispatcherMultiVMManagementI extends OfferedI, RequiredI {
	
	public void addAVM(AVMUris avmuris) throws Exception ;
	
	public void connectAVM(String uri) throws Exception ;
	
	public boolean removeAVM(String uri) throws Exception ;
	
	public int getNbAvm() throws Exception ;
}
