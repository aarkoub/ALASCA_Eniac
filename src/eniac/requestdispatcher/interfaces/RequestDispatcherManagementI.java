package eniac.requestdispatcher.interfaces;

import eniac.requestdispatcher.data.AVMUris;
import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * Interface définissant les méthodes que l'ont peut appeler sur le requestDispatcher.
 * Ce sont des méthodes notamment pour contrôler l'ajoute et le retranchement d'AVM.
 * @author L-C
 *
 */

public interface RequestDispatcherManagementI extends OfferedI, RequiredI {
	
	/**
	 * Cette méthode demande d'ajouter des données d'une AVM dans le distributeur de requêtes,
	 * le but étant de rajouter une AVM dans le distributeur de requête.
	 * @param avmuris tout les uris relatif à l'AVM
	 */
	public void addAVM(AVMUris avmuris) throws Exception ;
	
	
	/**
	 * Cette méthode demande d'initier les connexions des ports pour pouvoir mettre en service l'AVM, et donc
	 * de recevoir, traiter et notificer les requêtes transmit par le distributeur de requêtes.
	 * @param uri uri de l'AVM à connecter
	 */
	public void connectAVM(String uri) throws Exception ;
	
	/**
	 * Cette méthode demande de supprimer une AVM particulière du distributeur de requête
	 * @param uri l'uri de l'AVM à supprimer
	 */
	public boolean removeAVM(String uri) throws Exception ;
	
	/**
	 * Cette méthode demande le nombre d'AVM que possède le distributeur de requête.
	 * @return le nombre d'AVM liées au distributeur de requête
	 */
	public int getNbAvm() throws Exception ;	
	
	/**
	 * Cette méthode demande d'initier les connexions des ports des AVMs de bases du distributeur de requête afin de le 
	 * mettre en route.
	 */
	public void startPortConnection() throws Exception;
	
}
