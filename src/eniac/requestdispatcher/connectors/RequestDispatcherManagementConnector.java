package eniac.requestdispatcher.connectors;

import eniac.requestdispatcher.data.AVMUris;
import eniac.requestdispatcher.interfaces.RequestDispatcherManagementI;
import fr.sorbonne_u.components.connectors.AbstractConnector;
/**
 * Cette classe est un connecteur permettant de faire correspondre les méthodes appelés par le composant qui demande un service et les méthodes du composant
 * qui fournissent le services, car ceux-ci n'ont pas forcément les mêmes noms ou champs.
 * @author L-C
 *
 */
public class RequestDispatcherManagementConnector extends AbstractConnector
		implements RequestDispatcherManagementI {

	/**
	 * Cette méthode demande d'ajouter des données d'une AVM dans le distributeur de requêtes,
	 * le but étant de rajouter une AVM dans le distributeur de requête.
	 * @param avmuris tout les uris relatif à l'AVM
	 */
	@Override
	public void addAVM(AVMUris avmuris) throws Exception {
		((RequestDispatcherManagementI)this.offering).addAVM(avmuris);
	}

	/**
	 * Cette méthode demande de supprimer une AVM particulière du distributeur de requête
	 * @param uri l'uri de l'AVM à supprimer
	 */
	@Override
	public boolean removeAVM(String uri) throws Exception {
		return ((RequestDispatcherManagementI)this.offering).removeAVM(uri);
	}

	/**
	 * Cette méthode demande le nombre d'AVM que possède le distributeur de requête.
	 * @return le nombre d'AVM liées au distributeur de requête
	 */
	@Override
	public int getNbAvm() throws Exception {
		return ((RequestDispatcherManagementI)this.offering).getNbAvm();
	}

	/**
	 * Cette méthode demande d'initier les connexions des ports pour pouvoir mettre en service l'AVM, et donc
	 * de recevoir, traiter et notificer les requêtes transmit par le distributeur de requêtes.
	 * @param uri uri de l'AVM à connecter
	 */
	@Override
	public void connectAVM(String uri) throws Exception {
		((RequestDispatcherManagementI)this.offering).connectAVM(uri);
	}

	/**
	 * Cette méthode demande d'initier les connexions des ports des AVMs de bases du distributeur de requête afin de le 
	 * mettre en route.
	 */
	@Override
	public void startPortConnection() throws Exception {
		((RequestDispatcherManagementI)this.offering).startPortConnection();
		
	}

	@Override
	public void stopSendingRequestToOneAVM() throws Exception {
		((RequestDispatcherManagementI)this.offering).stopSendingRequestToOneAVM();
		
	}


}
