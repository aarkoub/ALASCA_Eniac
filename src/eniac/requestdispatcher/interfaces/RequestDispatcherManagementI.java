package eniac.requestdispatcher.interfaces;

import eniac.requestdispatcher.data.AVMUris;
import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface RequestDispatcherManagementI extends OfferedI, RequiredI {
	
	public void addAVM(AVMUris avmuris) throws Exception ;
	
	public void connectAVM(String uri) throws Exception ;
	
	public boolean removeAVM(String uri) throws Exception ;
	
	public int getNbAvm() throws Exception ;	
	
	public void startPortConnection() throws Exception;
	
}
