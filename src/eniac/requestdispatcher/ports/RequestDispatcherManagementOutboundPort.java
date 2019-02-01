package eniac.requestdispatcher.ports;

import eniac.requestdispatcher.data.AVMUris;
import eniac.requestdispatcher.interfaces.RequestDispatcherManagementI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
/**
 * La classe RequestDispatcherManagementOutboundPort est un port de sortie pour contrôler l'ajout et le retranchement d'AVM du RequestDispatcher.
 * @author L-C
 *
 */
public class RequestDispatcherManagementOutboundPort extends AbstractOutboundPort
		implements RequestDispatcherManagementI {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public				RequestDispatcherManagementOutboundPort(
			ComponentI owner
			) throws Exception
		{
			super(RequestDispatcherManagementI.class, owner) ;

			assert	owner != null ;
		}

		public				RequestDispatcherManagementOutboundPort(
			String uri,
			ComponentI owner
			) throws Exception
		{
			super(uri, RequestDispatcherManagementI.class, owner) ;

			assert	uri != null && owner != null ;
		}
	
	
		/**
		 * Cette méthode demande d'ajouter des données d'une AVM dans le distributeur de requêtes,
		 * le but étant de rajouter une AVM dans le distributeur de requête.
		 * @param avmuris tout les uris relatif à l'AVM
		 * @throws Exception exception
		 */
		@Override
		public void addAVM(AVMUris avmuris) throws Exception {
			((RequestDispatcherManagementI)this.connector).addAVM(avmuris);
	
		}
	
		/**
		 * Cette méthode demande de supprimer une AVM particulière du distributeur de requête
		 * @param uri l'uri de l'AVM à supprimer
		 * @return true s'il a été supprimé/false sinon
		 * @throws Exception exception
		 */
		@Override
		public boolean removeAVM(String uri) throws Exception {
			return ((RequestDispatcherManagementI)this.connector).removeAVM(uri);
		}
	
		/**
		 * Cette méthode demande le nombre d'AVM que possède le distributeur de requête.
		 * @return le nombre d'AVM liées au distributeur de requête
		 * @throws Exception exception
		 */
		@Override
		public int getNbAvm() throws Exception {
			return ((RequestDispatcherManagementI)this.connector).getNbAvm();
		}
	
		/**
		 * Cette méthode demande d'initier les connexions des ports pour pouvoir mettre en service l'AVM, et donc
		 * de recevoir, traiter et notificer les requêtes transmit par le distributeur de requêtes.
		 * @param uri uri de l'AVM à connecter
		 * @throws Exception exception
		 */
		@Override
		public void connectAVM(String uri) throws Exception {
			((RequestDispatcherManagementI)this.connector).connectAVM(uri);
		}
	
		/**
		 * Cette méthode demande d'initier les connexions des ports des AVMs de bases du distributeur de requête afin de le 
		 * mettre en route.
		 * @throws Exception exception
		 */
		@Override
		public void startPortConnection() throws Exception {
			((RequestDispatcherManagementI)this.connector).startPortConnection();
		}

		@Override
		public void stopSendingRequestToOneAVM() throws Exception {
			((RequestDispatcherManagementI)this.connector).stopSendingRequestToOneAVM();
			
		}


}
