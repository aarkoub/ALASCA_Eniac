package eniac.requestdispatcher.ports;

import eniac.requestdispatcher.RequestDispatcher;
import eniac.requestdispatcher.data.AVMUris;
import eniac.requestdispatcher.interfaces.RequestDispatcherManagementI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
/**
 * La classe RequestDispatcherManagementInboundPort est un port d'entrée pour contrôler l'ajout et le retranchement d'AVM du RequestDispatcher.
 * 
 *
 */
public class RequestDispatcherManagementInboundPort extends AbstractInboundPort
		implements RequestDispatcherManagementI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public				RequestDispatcherManagementInboundPort(
			ComponentI owner
			) throws Exception
		{
			super(RequestDispatcherManagementI.class, owner) ;

			assert	owner != null && owner instanceof RequestDispatcher ;
		}

		public				RequestDispatcherManagementInboundPort(
			String uri,
			ComponentI owner
			) throws Exception
		{
			super(uri, RequestDispatcherManagementI.class, owner);

			assert	owner != null && owner instanceof RequestDispatcher ;
		}
	
		/**
		 * Cette méthode demande d'ajouter des données d'une AVM dans le distributeur de requêtes,
		 * le but étant de rajouter une AVM dans le distributeur de requête.
		 * @param avmuris tout les uris relatif à l'AVM
		 * @throws Exception exception
		 */
		@Override
		public void addAVM(AVMUris avmuris) throws Exception {
			this.getOwner().handleRequestSync(
					new AbstractComponent.AbstractService<Void>() {
						@Override
						public Void call() throws Exception {
							((RequestDispatcher)this.getOwner()).
								addAVM(avmuris); ;
							return null;
						}
					}) ;
		}
	
		/**
		 * Cette méthode demande de supprimer une AVM particulière du distributeur de requête
		 * @param uri l'uri de l'AVM à supprimer
		 * @return true s'il a été supprimé/false sinon
		 * @throws Exception exception
		 */
		@Override
		public boolean removeAVM(String uri) throws Exception {
			return this.getOwner().handleRequestSync(
					new AbstractComponent.AbstractService<Boolean>() {
						@Override
						public Boolean call() throws Exception {
							return ((RequestDispatcher)this.getOwner()).removeAVM(uri);
						}
					}) ;
		}
	
		/**
		 * Cette méthode demande le nombre d'AVM que possède le distributeur de requête.
		 * @return le nombre d'AVM liées au distributeur de requête
		 * @throws Exception exception
		 */
		@Override
		public int getNbAvm() throws Exception {
			return this.getOwner().handleRequestSync(
					new AbstractComponent.AbstractService<Integer>() {
						@Override
						public Integer call() throws Exception {
							return ((RequestDispatcher)this.getOwner()).getNbAvm();
						}
					}) ;
		}
	
		/**
		 * Cette méthode demande d'initier les connexions des ports pour pouvoir mettre en service l'AVM, et donc
		 * de recevoir, traiter et notificer les requêtes transmit par le distributeur de requêtes.
		 * @param uri uri de l'AVM à connecter
		 * @throws Exception exception
		 */
		@Override
		public void connectAVM(String uri) throws Exception {
			this.getOwner().handleRequestSync(
					new AbstractComponent.AbstractService<Void>() {
						@Override
						public Void call() throws Exception {
							((RequestDispatcher)this.getOwner()).
								connectAVM(uri);
							return null;
						}
					}) ;
			
		}
	
		
		/**
		 * Cette méthode demande d'initier les connexions des ports des AVMs de bases du distributeur de requête afin de le 
		 * mettre en route.
		 * @throws Exception exception
		 */
		@Override
		public void startPortConnection() throws Exception {
			this.getOwner().handleRequestSync(
					new AbstractComponent.AbstractService<Void>() {
						@Override
						public Void call() throws Exception {
							((RequestDispatcher)this.getOwner()).
								startPortConnection();
							return null;
						}
					}) ;
			
			
		}

		@Override
		public void stopSendingRequestToOneAVM() throws Exception {
			this.getOwner().handleRequestAsync(
					new AbstractComponent.AbstractService<Void>() {
						@Override
						public Void call() throws Exception {
							((RequestDispatcher)this.getOwner()).
							stopSendingRequestToOneAVM();
							return null;
						}
					}) ;
			
		}


}
