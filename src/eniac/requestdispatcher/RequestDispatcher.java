package eniac.requestdispatcher;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import eniac.applicationvm.interfaces.ApplicationVMStateDataConsumerI;
import eniac.applicationvm.ports.ApplicationVMDynamicStateDataOutboundPort;
import eniac.applicationvm.ports.ApplicationVMStaticStateDataOutboundPort;
import eniac.requestdispatcher.data.AVMData;
import eniac.requestdispatcher.data.AVMPorts;
import eniac.requestdispatcher.data.AVMUris;
import eniac.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;
import eniac.requestdispatcher.interfaces.RequestDispatcherManagementI;
import eniac.requestdispatcher.interfaces.RequestDispatcherStaticStateI;
import eniac.requestdispatcher.ports.RequestDispatcherDynamicStateDataInboundPort;
import eniac.requestdispatcher.ports.RequestDispatcherManagementInboundPort;
import eniac.requestdispatcher.ports.RequestDispatcherStaticStateDataInboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.connectors.DataConnector;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.TimeManagement;
import fr.sorbonne_u.datacenter.connectors.ControlledDataConnector;
import fr.sorbonne_u.datacenter.interfaces.PushModeControllingI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMDynamicStateI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMStaticStateI;
import fr.sorbonne_u.datacenter.software.connectors.RequestNotificationConnector;
import fr.sorbonne_u.datacenter.software.connectors.RequestSubmissionConnector;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionOutboundPort;

/**
 * Ce composant correspond au répartiteur de requêtes, c'est-à-dire que ce composant reçoit des requêtes envoyés
 * par le RequestGenerator via un port dédié et choisit à quelle AVM transmettre la requête pour son traitement.
 * Le choix de l'AVM est fait par un calcul simple, on divise la taille de la file de chaque AVM par son nombre de coeurs
 * alloués ce qui donne un score et on sélectionne le plus petit score.
 * Pour plus de détail, les requêtes sont reçut dans la méthode acceptRequestSubmissionAndNotify où l'on informe 
 * AverageCompute (permet le calcul de la moyenne) de l'arrivée d'une requête, ensuite ont choisit comme explicité
 * précédemment quel AVM transmettre la requête et on la transmet.
 * Lorsqu'une AVM a terminé le traitement d'un requête, celui-ci informe le répartiteur de requête via un port et appel
 * la méthode acceptRequestTerminationNotification, dans cette méthode on informe AverageCompute de la date de fin
 * de la requête et on retransmet le fait que la requête à été traité au RequestGenerator.
 * 
 * @author lc-laptop
 *
 */


public class RequestDispatcher extends AbstractComponent implements RequestDispatcherManagementI,
RequestSubmissionHandlerI,
RequestNotificationHandlerI,
ApplicationVMStateDataConsumerI,
PushModeControllingI{
	/**
	 * URI du répartiteur de requête
	 */
	protected String rd_uri;
	/**
	 * URI du port de notification à prévenir lorsqu'une requête termine
	 */
	protected String requestNotificationInboundPortURI;
	
	
	
	/**
	 * Port permettant de recevoir des appels pour notamment ajouter/retrancher des AVMs (Controleur d'admission)
	 */
	protected RequestDispatcherManagementInboundPort requestDispatcherMultiVMManagementInboundPort;
	/**
	 * Port permettant de recevoir des requêtes du RequestGenerator
	 */
	protected RequestSubmissionInboundPort requestSubmissionInboundPort;
	
	/**
	 * Port permettant de prévenir qu'une requête est terminé
	 */
	protected RequestNotificationOutboundPort requestNotificationOutboundPort;
	
	
	
	/**
	 * Données liant l'uri d'une AVM à tous ses ports et URIs
	 */
	protected Map<String, AVMData> avms;
	
	/**
	 * Attribut qui permet de garder en mémoire une tâche prévu (pour envoyer des données)
	 */
	protected ScheduledFuture<?> pushingFuture;
	
	/**
	 * Port qui sert à envoyer les données dynamiques du répartiteur de requête
	 */
	protected RequestDispatcherDynamicStateDataInboundPort requestDispatcherDynamicStateDataInboundPort;
	/**
	 * Port qui sert à envoyer les données statiques du répartiteur de requête
	 */
	protected RequestDispatcherStaticStateDataInboundPort requestDispatcherStaticStateDataInboundPort;
	
	/**
	 * Contient les données dynamiques de chaque AVM
	 */
	protected Map<String, ApplicationVMDynamicStateI> avmDynamicStateMap;
	/**
	 * Contient les données statiques de chaque AVM
	 */
	protected Map<String, ApplicationVMStaticStateI> avmStaticStateMap;
	
	/**
	 * Calcul des moyennes des temps de requêtes
	 */
	protected AverageCompute avgcompute;
	
	/**
	 * Tableau des scores des AVMs (charge de requêtes de chaque AVM)
	 */
	protected Map<String, Double> avmScores;
	private String notToChoose;
	
	
	public RequestDispatcher(String rd_uri,
			String managementInboundPortURI,
			String requestSubmissionInboundPortURI,
			String requestNotificationInboundPortURI,
			String requestDispatcherDynamicStateDataInboundPortURI,
			String requestDispatcherStaticStateDataInboundPortURI,
			ArrayList<AVMUris> uris) throws Exception {
		

		super(rd_uri,1,1);
		
		assert rd_uri != null;
		assert	managementInboundPortURI != null ;
		assert	requestSubmissionInboundPortURI != null ;
		assert	requestNotificationInboundPortURI != null ;
		assert uris != null;
		assert requestDispatcherDynamicStateDataInboundPortURI!=null;
		assert requestDispatcherStaticStateDataInboundPortURI!=null;

		
		this.rd_uri = rd_uri;
		
		this.requestNotificationInboundPortURI =
				requestNotificationInboundPortURI ;
		
		
		addOfferedInterface(RequestDispatcherManagementI.class);
		
		requestDispatcherMultiVMManagementInboundPort = new RequestDispatcherManagementInboundPort(managementInboundPortURI, this);
		addPort(requestDispatcherMultiVMManagementInboundPort);
		requestDispatcherMultiVMManagementInboundPort.publishPort();
		
		addOfferedInterface(RequestSubmissionI.class);
		requestSubmissionInboundPort = new RequestSubmissionInboundPort(requestSubmissionInboundPortURI, this);
		addPort(requestSubmissionInboundPort);
		requestSubmissionInboundPort.publishPort();
		
		addRequiredInterface(RequestNotificationI.class);
		requestNotificationOutboundPort = new RequestNotificationOutboundPort(this);
		addPort(requestNotificationOutboundPort);
		requestNotificationOutboundPort.publishPort();
		
		
		avms = new HashMap<>();
		addOfferedInterface(RequestNotificationI.class);
		addRequiredInterface(RequestSubmissionI.class);
		RequestSubmissionOutboundPort requestSubmissionOutboundPortVM;
		RequestNotificationInboundPort requestNotificationInboundPortVM;
		AVMData data;
		
		/** on connecte chaque port des AVM, c'est-à-dire les ports pour envoyer les données,
		 * les ports pour envoyer les requêtes et pour recevoir les notifications de 
		 * terminaisons.
		 */
		for(AVMUris uri : uris) {
			
			requestNotificationInboundPortVM = new RequestNotificationInboundPort(uri.getRequestNotificationInboundPortVM(), this);
			addPort(requestNotificationInboundPortVM);
			requestNotificationInboundPortVM.publishPort();
			
			requestSubmissionOutboundPortVM = new RequestSubmissionOutboundPort(this);
			addPort(requestSubmissionOutboundPortVM);
			requestSubmissionOutboundPortVM.publishPort();
			
			data = new AVMData(uri, new AVMPorts(requestSubmissionOutboundPortVM, requestNotificationInboundPortVM));
			avms.put(uri.getAVMUri(), data);
			
			String avmDynamicStateDataInboundPortURI = data.getAvmuris().getAVMUri() + "-avmdsdibp" ; 
			String avmStaticStateDataInboundPortURI = data.getAvmuris().getAVMUri() + "-avmssdibp" ; 
						
			ApplicationVMStaticStateDataOutboundPort vmStaticOutports = new ApplicationVMStaticStateDataOutboundPort(this, data.getAvmuris().getAVMUri());
			addPort(vmStaticOutports);
			vmStaticOutports.publishPort();
		
			
			
			ApplicationVMDynamicStateDataOutboundPort vmDynamicOutports = new ApplicationVMDynamicStateDataOutboundPort(this, data.getAvmuris().getAVMUri());
			addPort(vmDynamicOutports);
			vmDynamicOutports.publishPort();
			
			
			data.setAvmDynamicStateDataInboundPortURI(avmDynamicStateDataInboundPortURI);
			data.setAvmStaticStateDataInboundPortURI(avmStaticStateDataInboundPortURI);
			
			data.getAvmports().setAvmDynamicStateDataOutboundPort(vmDynamicOutports);
			data.getAvmports().setAvmStaticStateDataOutboundPort(vmStaticOutports);
			
		}
		
		this.toggleLogging();
		this.toggleTracing();
		
		addOfferedInterface(RequestDispatcherStaticStateI.class);
		requestDispatcherStaticStateDataInboundPort = new RequestDispatcherStaticStateDataInboundPort(
				requestDispatcherStaticStateDataInboundPortURI, this);
		requestDispatcherStaticStateDataInboundPort.publishPort();
		addPort(requestDispatcherStaticStateDataInboundPort);
		
		
		addOfferedInterface(RequestDispatcherDynamicStateI.class);
		requestDispatcherDynamicStateDataInboundPort = new RequestDispatcherDynamicStateDataInboundPort(
				requestDispatcherDynamicStateDataInboundPortURI, this);
		requestDispatcherDynamicStateDataInboundPort.publishPort();
		addPort(requestDispatcherDynamicStateDataInboundPort);
		
		
		avmDynamicStateMap = new HashMap<>();
		avmStaticStateMap = new HashMap<>();
		
		avgcompute = new AverageCompute();
		
		avmScores = new HashMap<>();
		
		for(AVMUris uri : uris) {
			avmScores.put(uri.getAVMUri(), (double)0);
		}
	}
	
	
	/**
	 * Calcul et retourne l'URI d'une AVM qui sera choisit pour traiter la requête
	 * A partir des données dynamiques des AVMs, on récupère le score, c'est-à-dire 
	 * le rapport de la taille de la liste de traitement par le nombre de coeurs et on
	 * prend le plus petit
	 * @return URI de l'AVM choisit
	 */
	public String chooseAVMToCompute() {
		String avm = avms.keySet().stream().findFirst().get();
		double score = avmScores.get(avm);
		for(Map.Entry<String, Double> entry: avmScores.entrySet()) {
			
			if(entry.getKey().equals(notToChoose))
				continue;
			
			if(entry.getValue() < score && avms.get(entry.getKey()) != null) {
				score = entry.getValue();
				avm = entry.getKey();
			}
		}
		return avm;
	}
	
	/**
	 * Arrete d'envoyer des requetes à une AVM
	 */
	public void stopSendingRequestToOneAVM(){

		if(avms.size() != 1){
			if(notToChoose!=null){
				for(String avmURI : avms.keySet()){
					notToChoose = avmURI;
					break;
				}
			}
		}
		
	}
	

	
	/**
	 * Reçoit et transfère les requêtes reçut du requestGenerator vers les AVMs pour le traitement, 
	 * l'AVM est choisit en fonction d'un score calculé. En même temps, on ajoute le temps de début
	 * de la requête pour effectuer le calcul de la moyenne.
	 * @param r la requête à traiter
	 */
	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {
		logMessage("RequestDispatcher "+rd_uri+" requete reçue "+r.getRequestURI());
		String choice =  chooseAVMToCompute();
		avms.get(choice).getAvmports().getRequestSubmissionOutboundPort().submitRequest(r);
		
		Date r1 = new Date();
		
		avgcompute.addStartTime(r.getRequestURI(), r1);
		
	}

	
	/**
	 * Reçoit et transfère les requêtes reçut du requestGenerator vers les AVMs pour le traitement, 
	 * l'AVM est choisit en fonction d'un score calculé. En même temps, on ajoute le temps de début
	 * de la requête pour effectuer le calcul de la moyenne.
	 * A la différence de acceptRequestSubmission, on demande à l'AVM de notifier de la terminaison 
	 * de la requête.
	 * @param r la requête à traiter
	 */
	@Override
	public void acceptRequestSubmissionAndNotify(RequestI r) throws Exception {
		logMessage("RequestDispatcher "+rd_uri+" requete reçue avec notification: "+r.getRequestURI());
		String choice =  chooseAVMToCompute();
		avms.get(choice).getAvmports().getRequestSubmissionOutboundPort().submitRequestAndNotify(r);
		
		Date r1 = new Date();
		
		avgcompute.addStartTime(r.getRequestURI(), r1);
		
	}

	/**
	 * Cette méthode permet de recevoir les notifications des AVMs sur la terminaison
	 * de la requêtes qu'ils ont traités, on retransfère cette notification au demandeur
	 * de traitement c'est-à-dire le requestGenerator, dans le même temps cela nous permet
	 * d'obtenir le temps de traitement de la requête depuis sa soumission et donc de 
	 * calculer la moyenne.
	 * @param r la requête terminé
	 */
	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		Date r2 = new Date();
		avgcompute.addEndTime(r.getRequestURI(), r2);
	
		logMessage("Requete terminée : "+r.getRequestURI());
		requestNotificationOutboundPort.notifyRequestTermination(r);
		
		
	}


	/**
	 * Retire une avm du request dispatcher, c'est-à-dire,
	 * on l'a supprime de la pool et on la déconnecte.
	 * @param uri URI de l'AVM a retirer
	 * @return true si elle à été retiré/false sinon
	 */
	@Override
	public boolean removeAVM(String uri) {
		
		notToChoose = null;
		
		AVMData data = null;
		for(AVMData tmp: avms.values()) {
			if(tmp.getAvmuris().getAVMUri() == uri) {
				data = tmp;
				break;
			}
		}
		
		if(data == null) return false;
		try {
			
			doPortDisconnection(data.getAvmports().getRequestSubmissionOutboundPort().getPortURI());
			data.getAvmports().getRequestSubmissionOutboundPort().unpublishPort();
			
			doPortDisconnection(data.getAvmports().getAvmDynamicStateDataOutboundPort().getPortURI());
			data.getAvmports().getAvmDynamicStateDataOutboundPort().unpublishPort();
			
			doPortDisconnection(data.getAvmports().getAvmStaticStateDataOutboundPort().getPortURI());
			data.getAvmports().getAvmStaticStateDataOutboundPort().unpublishPort();
			
			avms.remove(uri);
			avmDynamicStateMap.remove(uri);
			avmStaticStateMap.remove(uri);
			avmScores.remove(uri);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return false;
	}

	/**
	 * Retourne le nombre d'AVMs du répartiteur de requêtes
	 * @return le nombre d'AVM
	 */
	@Override
	public int getNbAvm() {
		return avms.size();
	}

	
	/**
	 * Ajoute et créer les différents ports pour l'AVM, c'est-à-dire 
	 * le port de notification, le port de soumission, les ports d'envoit d'informations statiques et dynamiques
	 * mais ne les connectes pas encore.
	 * @param avmuris les URIs des ports de l'AVM
	 */
	@Override
	public void addAVM(AVMUris avmuris) throws Exception {
		RequestSubmissionOutboundPort requestSubmissionOutboundPortVM;
		RequestNotificationInboundPort requestNotificationInboundPortVM;
		AVMData data;
		
		//instanciation des ports
		requestNotificationInboundPortVM = new RequestNotificationInboundPort(avmuris.getRequestNotificationInboundPortVM(), this);
		addPort(requestNotificationInboundPortVM);
		requestNotificationInboundPortVM.publishPort();
		requestSubmissionOutboundPortVM = new RequestSubmissionOutboundPort(this);
		addPort(requestSubmissionOutboundPortVM);
		requestSubmissionOutboundPortVM.publishPort();
		
		ApplicationVMDynamicStateDataOutboundPort app_dynamic_outport =
				new ApplicationVMDynamicStateDataOutboundPort(this, avmuris.getAVMUri());
		addPort(app_dynamic_outport);
		app_dynamic_outport.publishPort();
		
		ApplicationVMStaticStateDataOutboundPort app_static_outport = 
				new ApplicationVMStaticStateDataOutboundPort(this, avmuris.getAVMUri());
		addPort(app_static_outport);
		app_static_outport.publishPort();
		
		//ajout de l'AVM dans la pool
		AVMPorts ports=new AVMPorts(requestSubmissionOutboundPortVM, requestNotificationInboundPortVM,
				app_dynamic_outport, app_static_outport);
		data = new AVMData(avmuris, ports);
		avms.put(avmuris.getAVMUri(), data);
		
		
	}

	/**
	 * Dans cette méthode, on effectue les connections entre ports pour démarrer l'AVM, 
	 * ceci n'est pas fait dans l'ajout car il faut vérifier que l'Objet AVM est instancier avant
	 * toute connection
	 * @param uri URI de l'AVM
	 */
	@Override
	public void connectAVM(String uri) throws Exception {
		//boucle pour chercher les uris de l'AVM
		for(AVMData data : avms.values()) {
			//connection des ports
			if(data.getAvmuris().getAVMUri() == uri) {
				doPortConnection(data.getAvmports().getRequestSubmissionOutboundPort().getPortURI(),
						data.getAvmuris().getRequestSubmissionInboundPortVM(),
						RequestSubmissionConnector.class.getCanonicalName());
				
				doPortConnection(data.getAvmports().getAvmStaticStateDataOutboundPort().getPortURI(), 
						data.getAvmuris().getApplicationVMStaticStateDataInboundPortURI(),
						DataConnector.class.getCanonicalName());
				doPortConnection(data.getAvmports().getAvmDynamicStateDataOutboundPort().getPortURI(),
						data.getAvmuris().getApplicationVMDynamicStateDataInboundPortURI(), 
						ControlledDataConnector.class.getCanonicalName());
				
				data.getAvmports().getAvmDynamicStateDataOutboundPort().startUnlimitedPushing(500);
				
				//on débute avec un score à 0 pour qu'ils soient choisit rapidement
				avmScores.put(uri, (double)0);
				return;
			}
		}
		
	}
	
	
	/**
	 * Méthode permettant de recevoir les données statiques des AVMs, ce sont notamment
	 * des données sur les coeurs et les fréquences.
	 * @param avmURI l'uri de l'AVM
	 * @param staticState les données
	 */
	@Override
	public void acceptApplicationVMStaticData(String avmURI, ApplicationVMStaticStateI staticState) throws Exception {
		avmStaticStateMap.put(avmURI, staticState);
				
		logMessage("staticState : "+avmURI);
		for(Integer idCore : staticState.getIdCores().keySet()){
			
			logMessage("core_"+idCore+" on processor_"+staticState.getIdCores().get(idCore));
			
		}
		
		sendStaticState();

		
	}
	
	/**
	 * Méthode permettant de recevoir les données dynamiques des AVMs, ce sont notamment
	 * des données sur les coeurs et les fréquences mais aussi les données des scores, c'est-à-dire
	 * le nombre de requêtes par coeur.
	 * @param avmURI l'uri de l'AVM
	 * @param dynamicState les données
	 */
	@Override
	public void acceptApplicationVMDynamicData(String avmURI, ApplicationVMDynamicStateI dynamicState)
			throws Exception {
		
		avmDynamicStateMap.put(avmURI, dynamicState);
		avmScores.put(avmURI, dynamicState.getScore());
		logMessage("dynamicState : "+avmURI);
		logMessage("isIdle : "+dynamicState.isIdle());
		
	}
	
	/**
	 * Methode permettant de récupérer l'objet contenant les données dynamiques du requestDispatcher
	 * @return données dynamiques du requestDispatcher
	 */
	public RequestDispatcherDynamicStateI getDynamicState() {
		
		return new RequestDispatcherDynamicState(avgcompute.getAverage(), avmDynamicStateMap, avmScores);
	}
	
	/**
	 * Methode permettant de récupérer l'objet contenant les données statiques du requestDispatcher
	 * @return données dynamiques du requestDispatcher
	 */
	public RequestDispatcherStaticStateI getStaticState() {
		return new RequestDispatcherStaticState(avmStaticStateMap);
	}

	@Override
	public void startUnlimitedPushing(int interval) throws Exception {

		// first, send the static state if the corresponding port is connected
		this.sendStaticState() ;

		this.pushingFuture =
			this.scheduleTaskAtFixedRate(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								((RequestDispatcher)this.getOwner()).
											sendDynamicState() ;
							} catch (Exception e) {
								throw new RuntimeException(e) ;
							}
						}
					},
					TimeManagement.acceleratedDelay(interval),
					TimeManagement.acceleratedDelay(interval),
					TimeUnit.MILLISECONDS) ;
		
	}
	
	/**
	 * Envoit des données dynamique du répartiteur de requêtes
	 * @throws Exception exception
	 */
	public void sendDynamicState() throws Exception {
		if (this.requestDispatcherDynamicStateDataInboundPort.connected()) {
			try {
				RequestDispatcherDynamicStateI rdds = this.getDynamicState() ;
				this.requestDispatcherDynamicStateDataInboundPort.send(rdds) ;
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			
		}
		
	}

	@Override
	public void startLimitedPushing(int interval, int n) throws Exception {
		assert	n > 0 ;

		this.logMessage(this.rd_uri + " startLimitedPushing with interval "
									+ interval + " ms for " + n + " times.") ;

		// first, send the static state if the corresponding port is connected
		this.sendStaticState() ;

		this.pushingFuture =
			this.scheduleTask(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								((RequestDispatcher)this.getOwner()).
									sendDynamicState(interval, n) ;
							} catch (Exception e) {
								throw new RuntimeException(e) ;
							}
						}
					},
					TimeManagement.acceleratedDelay(interval),
					TimeUnit.MILLISECONDS) ;
		
	}

	public void sendDynamicState(int interval, final int numberOfRemainingPushes
			) throws Exception
		{
	
			this.sendStaticState() ;
			final int fNumberOfRemainingPushes = numberOfRemainingPushes - 1 ;
			if (fNumberOfRemainingPushes > 0) {
				this.pushingFuture =
						this.scheduleTask(
								new AbstractComponent.AbstractTask() {
									@Override
									public void run() {
										try {
											((RequestDispatcher)this.getOwner()).
												sendDynamicState(
													interval,
													fNumberOfRemainingPushes) ;
										} catch (Exception e) {
											throw new RuntimeException(e) ;
										}
									}
								},
								TimeManagement.acceleratedDelay(interval),
								TimeUnit.MILLISECONDS) ;
			}
		
	}

	public void sendStaticState() throws Exception {
		if (this.requestDispatcherStaticStateDataInboundPort.connected()) {
			RequestDispatcherStaticStateI rdds = this.getStaticState() ;
			this.requestDispatcherStaticStateDataInboundPort.send(rdds) ;
		}
		
	}

	@Override
	public void stopPushing() throws Exception {
		if (this.pushingFuture != null &&
				!(this.pushingFuture.isCancelled() ||
									this.pushingFuture.isDone())) {
			this.pushingFuture.cancel(false) ;
		}
		
	}
	
	
	
	
	
	
	
	
	
	


	@Override
	public void start() throws ComponentStartException {
		super.start();
		
	}
	
	@Override
	public void startPortConnection() {
		try {
			doPortConnection(requestNotificationOutboundPort.getPortURI(), requestNotificationInboundPortURI,
					RequestNotificationConnector.class.getCanonicalName());
			
			for(AVMData data  : avms.values()) {
				doPortConnection(data.getAvmports().getRequestSubmissionOutboundPort().getPortURI(),
						data.getAvmuris().getRequestSubmissionInboundPortVM(),
						RequestSubmissionConnector.class.getCanonicalName());
			
				doPortConnection(data.getAvmports().getAvmStaticStateDataOutboundPort().getPortURI(), data.getAvmuris().getApplicationVMStaticStateDataInboundPortURI(), DataConnector.class.getCanonicalName());
				doPortConnection(data.getAvmports().getAvmDynamicStateDataOutboundPort().getPortURI(),data.getAvmuris().getApplicationVMDynamicStateDataInboundPortURI(), ControlledDataConnector.class.getCanonicalName());
				
				data.getAvmports().getAvmDynamicStateDataOutboundPort().startUnlimitedPushing(500);
			
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	
	@Override
	public void			finalise() throws Exception
	{
		this.doPortDisconnection(
							this.requestNotificationOutboundPort.getPortURI()) ;
		for(Map.Entry<String, AVMData> d: avms.entrySet()) {
			doPortDisconnection(
					d.getValue().getAvmports().getRequestSubmissionOutboundPort().getPortURI());
		}
		
		super.finalise() ;
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException {
	
		try {
			requestSubmissionInboundPort.unpublishPort();
			requestDispatcherMultiVMManagementInboundPort.unpublishPort();
			requestNotificationOutboundPort.unpublishPort();
			for(AVMData data : avms.values()) {
				
				data.getAvmports().getRequestSubmissionOutboundPort().unpublishPort();
				data.getAvmports().getRequestNotificationInboundPort().unpublishPort();
				data.getAvmports().getAvmStaticStateDataOutboundPort().unpublishPort();
				data.getAvmports().getAvmDynamicStateDataOutboundPort().unpublishPort();
			}
			
		} catch (Exception e) {
			throw new ComponentShutdownException(
					"processor services outbound port disconnection"
					+ " error", e) ;
		}
		
		
		super.shutdown();
		
		
		
	}
	
	@Override
	public void shutdownNow() throws ComponentShutdownException {
	
		try {
			requestSubmissionInboundPort.unpublishPort();
			requestDispatcherMultiVMManagementInboundPort.unpublishPort();
			requestNotificationOutboundPort.unpublishPort();
			for(AVMData data : avms.values()) {
				data.getAvmports().getRequestSubmissionOutboundPort().unpublishPort();
				data.getAvmports().getRequestNotificationInboundPort().unpublishPort();
				data.getAvmports().getAvmStaticStateDataOutboundPort().unpublishPort();
				data.getAvmports().getAvmDynamicStateDataOutboundPort().unpublishPort();
			}
			
		} catch (Exception e) {
			throw new ComponentShutdownException(
					"processor services outbound port disconnection"
					+ " error", e) ;
		}
		
		
		super.shutdownNow();
		
		
		
	}

}