package eniac.admissioncontroler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eniac.admissioncontroler.interfaces.AdmissionControlerManagementI;
import eniac.admissioncontroler.interfaces.RequestAdmissionI;
import eniac.admissioncontroler.interfaces.RequestAdmissionNotificationHandlerI;
import eniac.admissioncontroler.interfaces.RequestAdmissionNotificationI;
import eniac.admissioncontroler.interfaces.RequestAdmissionSubmissionHandlerI;
import eniac.admissioncontroler.interfaces.RequestAdmissionSubmissionI;
import eniac.admissioncontroler.ports.AdmissionControlerManagementInboundPort;
import eniac.automatichandler.AutomaticHandler;
import eniac.automatichandler.interfaces.AutomaticHandlerRequestI;
import eniac.automatichandler.ports.AutomaticHandlerRequestInboundPort;
import eniac.processorcoordinator.connectors.ProcessorCoordinatorManagementConnector;
import eniac.processorcoordinator.ports.ProcessorCoordinatorManagementOutboundPort;
import eniac.requestadmission.ports.RequestAdmissionNotificationInboundPort;
import eniac.requestadmission.ports.RequestAdmissionSubmissionInboundPort;
import eniac.requestdispatcher.RequestDispatcher;
import eniac.requestdispatcher.connectors.RequestDispatcherManagementConnector;
import eniac.requestdispatcher.data.AVMUris;
import eniac.requestdispatcher.interfaces.RequestDispatcherManagementI;
import eniac.requestdispatcher.ports.RequestDispatcherManagementOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.pre.dcc.connectors.DynamicComponentCreationConnector;
import fr.sorbonne_u.components.pre.dcc.interfaces.DynamicComponentCreationI;
import fr.sorbonne_u.components.pre.dcc.ports.DynamicComponentCreationOutboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.sorbonne_u.datacenter.hardware.tests.ComputerMonitor;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;
import fr.sorbonne_u.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;

/**
 *Le Controleur d'admission va gérer les ressources. 
 *C'est à lui que les RequestGenerator vont demander les ressources nécessaires pour
 *le calcul de leurs requêtes. En fonction de la disponibilité, le contrôleur d'admission
 *va créer les RequestDispatcher et les ApplicationVM qui leur sont associés.
 *Le controleur d'admission est notifié quand une ressource n'est plus utilisée, pour
 *ensuite le mettre à la disponibilité des demandes ultérieures.
 *
 */
public class AdmissionControler extends AbstractComponent implements AdmissionControlerManagementI, 
RequestAdmissionSubmissionHandlerI,
RequestAdmissionNotificationHandlerI,
AutomaticHandlerRequestI{
	
	/**
	 * URI de l'AdmissionControler
	 */
	protected String uri;
	
	/**
	 * Id qui permet de créer des uris dynamiques différentes 
	 */
	protected int id=0;
	
	/**
	 * Outbound port du DynamicComponentCreator
	 */
	protected DynamicComponentCreationOutboundPort dynamicComponentCreationOutboundPort;
	
	/**
	 * Taille des avm par défaut
	 */
	protected static final int DEFAULT_AVM_SIZE = 2;
	
	/**
	 * Map des RequestDispatcherManagementOutboundPort par RequestDispatcher
	 */
	protected Map<String, RequestDispatcherManagementOutboundPort> rd_management_port_map;
	
	/**
	 * Map des RequestNotificationInboundPortURIVM qui appartiennent aux RequestDispatcher
	 */
	protected Map<String, String> rd_notification_inport_map;
	
	/**
	 * Map contenant les datas par computer
	 */
	protected Map<String, ComputerData> computerdata_map;
	
	/**
	 * Map des ApplicationVMManagementOutboundPort des avms créées
	 */
	protected Map<String, ApplicationVMManagementOutboundPort> avm_management_port_map;
	
	/**
	 * Map des coeurs alloués pour les VM
	 */
	protected Map<String, AllocationCore> allocationVMCores_map;
	
	/**
	 * Map des AMVS des RequestDispatcher
	 */
	protected Map<String, List<String>> reqDispAvms_map;
	
	/**
	 * Entier pour donner des URIs dynamiques différents aux AVMS
	 */
	protected static int id_avm = 0;
	
	/**
	 * Partie de l'URI d'une AVM
	 */
	protected static final String AVMURI = "avm_uri_";
	
	/**
	 * Partie de l'URI de l'inbound port de management d'une AVM
	 */
	protected static final String AVMMANAGEMENTURI = "avm_muri_";
	
	/**
	 * Partie de l'URI de l'inbound port de RequestSubmission d'une AVM
	 */
	protected static final String AVMREQUESTSUBMISSIONURI = "avm_rsuri_";
	
	/**
	 * Partie de l'URI de l'inbound port de RequestNotification d'une AVM
	 */
	protected static final String AVMREQUESTNOTIFICATIONURI = "avm_rnuri_";
	
	/**
	 * Partie de l'URI de l'inbound port de données statiques d'une AVM
	 */
	protected static final String AVM_DYNAMIC_STATE = "avm_dynamic_state";
	
	/**
	 * Partie de l'URI de l'inbound port de données dynamiques  d'une AVM
	 */
	protected static final String AVM_STATIC_STATE = "avm_static_state";
	
	/**
	 * Map des ProcessorCoordinatorManagementOutboundPort par ProcessorCoordinator
	 */
	protected Map<String, ProcessorCoordinatorManagementOutboundPort> proc_coord_map;

	/**
	 * Map qui contient les URI des ProcessorCoordinatorManagementInboundPortURI 
	 * par ProcessorCoordinator
	 */
	protected Map<String, String> proc_coord_management_inport_map;
	
	/**
	 * Map qui contient les ensembles de coeurs que possède les
	 * dispatcher des AutomaticHandler sur chaque Processor
	 */
	protected Map<String, Map<String, Set<Integer>>> current_cores_handlers_map ;
	
	/**
	 * Contruction de AdmissionControler
	 * @param uri	URI du AdmissionControler
	 * @param nbComputers	nombre de Computer qu'il a en ressources
	 * @param admissionControlerManagementInboundURI	URI de AdmissionControlerManagementInboundPort
	 * @param dynamicComponentCreationInboundPortURI	URI de dynamicComponentCreationInboundPort
	 * @param requestAdmissionSubmissionInboundPortURI	URI de requestAdmissionSubmissionInboundPort
	 * @param requestAdmissionNotificationInboundPortURI	URI de requestAdmissionNotificationInboundPortU
	 * @param processorCoordinatorManagementInboundPortURIS	URIS des processorCoordinatorManagementInboundPort
	 * @param computers	Liste de Computers qu'il a en ressources
	 * @param computeruris	Liste des URIS associés au Computer
	 * @param computerMonitors	Liste des ComputerMonitor associés
	 * @throws Exception exception
	 */
	public AdmissionControler(String uri, 
			int nbComputers,
			String admissionControlerManagementInboundURI,
			String dynamicComponentCreationInboundPortURI,
			String requestAdmissionSubmissionInboundPortURI,
			String requestAdmissionNotificationInboundPortURI,
			Map<String, String> processorCoordinatorManagementInboundPortURIS,
			List<Computer> computers,
			List<ComputerURI> computeruris,
			List<ComputerMonitor> computerMonitors) throws Exception{
		
		super(uri, 1, 1);
		assert nbComputers > 0;
		assert uri != null;
		assert admissionControlerManagementInboundURI != null;
		assert requestAdmissionSubmissionInboundPortURI != null;
		assert requestAdmissionNotificationInboundPortURI != null;
		assert dynamicComponentCreationInboundPortURI != null;
		
		
		this.uri = uri;
		
		addOfferedInterface(AdmissionControlerManagementI.class);
		AdmissionControlerManagementInboundPort admissionControlerManagementInboundPort = new AdmissionControlerManagementInboundPort(admissionControlerManagementInboundURI, this);
		addPort(admissionControlerManagementInboundPort);
		admissionControlerManagementInboundPort.publishPort();
		
		
		addOfferedInterface(RequestAdmissionSubmissionI.class);
		RequestAdmissionSubmissionInboundPort requestAdmissionSubmissionInboundPort = new RequestAdmissionSubmissionInboundPort(requestAdmissionSubmissionInboundPortURI, this);
		addPort(requestAdmissionSubmissionInboundPort);
		requestAdmissionSubmissionInboundPort.publishPort();
		
		
		addOfferedInterface(RequestAdmissionNotificationI.class);
		RequestAdmissionNotificationInboundPort requestAdmissionNotificationInboundPort = new RequestAdmissionNotificationInboundPort(requestAdmissionNotificationInboundPortURI, this);
		addPort(requestAdmissionNotificationInboundPort);
		requestAdmissionNotificationInboundPort.publishPort();
		
		addRequiredInterface(DynamicComponentCreationI.class);
		dynamicComponentCreationOutboundPort = new DynamicComponentCreationOutboundPort(this);
		addPort(dynamicComponentCreationOutboundPort);
		dynamicComponentCreationOutboundPort.publishPort();
	
		
		rd_management_port_map = new HashMap<>();
		computerdata_map = new HashMap<>();
		avm_management_port_map = new HashMap<>();
		allocationVMCores_map = new HashMap<>();
		
		reqDispAvms_map = new HashMap<>();
		rd_notification_inport_map = new HashMap<>();
	
		
		proc_coord_map = new HashMap<>();
		current_cores_handlers_map = new HashMap<>();
		
		proc_coord_management_inport_map = processorCoordinatorManagementInboundPortURIS;
		
		ComputerServicesOutboundPort csop;
		for (int i = 0; i < computers.size(); i++) {
			Computer c =  computers.get(i);
						
			ComputerURI cUri = computeruris.get(i);
			csop = new ComputerServicesOutboundPort(this);
			addPort(csop);
			csop.publishPort();
			ComputerData computerData =  new ComputerData(cUri,c, csop);
			computerdata_map.put(cUri.getComputerUri(), computerData);
			doPortConnection(csop.getPortURI(),cUri.getComputerServicesInboundPortURI(), ComputerServicesConnector.class.getCanonicalName());
			
		}
		
				
		
	}
	
	
	
	
	@Override
	public void start() throws ComponentStartException{
		super.start();
		
			try {
				doPortConnection(dynamicComponentCreationOutboundPort.getPortURI(), AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX, 
						DynamicComponentCreationConnector.class.getCanonicalName());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for(String proc_uri : proc_coord_management_inport_map.keySet()){
				
				ProcessorCoordinatorManagementOutboundPort outport;
				try {
					outport = new ProcessorCoordinatorManagementOutboundPort(this);
					addPort(outport);
					outport.publishPort();
					
					doPortConnection(outport.getPortURI(),
							proc_coord_management_inport_map.get(proc_uri),
							ProcessorCoordinatorManagementConnector.class.getCanonicalName());
					
					this.proc_coord_map.put(proc_uri, outport);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
							
			}
					
		
	}
	

	
	@Override
	public void			finalise() throws Exception
	{
		this.doPortDisconnection(
							dynamicComponentCreationOutboundPort.getPortURI()) ;
		
		
		super.finalise() ;
	}
	
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		// Disconnect ports to the request emitter and to the processors owning
		// the allocated cores.
	
			try {
					
				dynamicComponentCreationOutboundPort.doDisconnection();
				
			} catch (Exception e) {
				throw new ComponentShutdownException("Error when shutdown admission controler");
			}
			
		

		super.shutdown();
	}
	
	
	@Override
	public void			shutdownNow() throws ComponentShutdownException
	{
		// Disconnect ports to the request emitter and to the processors owning
		// the allocated cores.
	
			try {

				dynamicComponentCreationOutboundPort.unpublishPort() ;

			} catch (Exception e) {
				throw new ComponentShutdownException("Error when shutdown admission controler");
			}
			

		super.shutdownNow();
	}
		
	
	@Override
	public List<String> removeCoreFromAvm(String handler_uri, String avm_uri) {
		AllocationCore alloc = allocationVMCores_map.get(avm_uri);
		if(alloc == null) return null;
		Computer computer = alloc.getComputer();
		try {
			
			if(alloc.getCores().length <= 1) return null;
			
			AllocatedCore c = alloc.getCores()[0];

			computer.releaseCore(c);
			AllocatedCore[] newAlloc = new AllocatedCore[alloc.getCores().length-1];
			int j = 0;
			for(int i = 1; i < alloc.getCores().length; i++) {
				newAlloc[j] = alloc.getCores()[i];
				j++;
			}
			alloc.setCores(newAlloc);
			avm_management_port_map.get(avm_uri).removeProcDataStatePorts(c.processorURI);
			List<String> res = new ArrayList<>();
			 removeCoresMap(handler_uri, c.processorURI, res, c.coreNo);
			 return res;
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		
	}
	
	@Override
	public Map<String, String> addCoreToAvm(String handler_uri, String avm_uri, int nbcores) {
		
		AllocationCore alloc = allocationVMCores_map.get(avm_uri);
		if(alloc == null) return null;
		Computer computer = alloc.getComputer();
		
		Map<String, String> proc_coord_manage_inport_map = null;
		
		try {
			
			AllocatedCore[] cores = computer.allocateCores(nbcores);
			if(cores.length != nbcores) {
				computer.releaseCores(cores);
				return null;
			}
			
			AllocatedCore[] alloccores = new AllocatedCore[cores.length+alloc.getCores().length];
			
			proc_coord_manage_inport_map = new HashMap<>();
			
			
			
			for(int i = 0; i < alloc.getCores().length; i++) {
				
				alloccores[i] = alloc.getCores()[i];
								
			}
			
			
			
			for(int i = alloc.getCores().length; i < alloccores.length; i++) {
				alloccores[i] = cores[i-alloc.getCores().length];
			}
			
			alloc.setCores(alloccores);
					
			getProcessorCoordinatorFreqURIS(handler_uri, alloccores, proc_coord_manage_inport_map);
			
			avm_management_port_map.get(avm_uri).allocateCores(cores);
			
			
			return proc_coord_manage_inport_map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Alloue des coeurs des Computer pour des AVM
	 * @param nbcores	nombre de coeurs à allouer par AVM
	 * @param nbvm	nombre d'AVM 
	 * @return la liste des AllocationCore correspondants
	 * @throws Exception exception
	 */
	private List<AllocationCore> allocateCoreFromComputers(int nbcores, int nbvm) throws Exception {
		List<AllocationCore> allocores = new ArrayList<>();
		AllocatedCore[] cores;
		for(int k = 0; k < nbvm; k++) {
			for(ComputerData cd: computerdata_map.values()) {
				cores = cd.getCsop().allocateCores(nbcores);
				if(cores.length == nbcores) {
					allocores.add(new AllocationCore(cd.getComputer(), cores, ""));
									
					break;
				}
				for(AllocatedCore alloc: cores) {
					cd.getComputer().releaseCore(alloc);
				}
			}
		}
		if(nbvm == allocores.size()) {
			return allocores;
		}
		
		for(AllocationCore c: allocores) {
			c.freeCores();
		}
		
		return null;
		
	}
	
	
	@Override
	public RequestAdmissionI getNewRequestAdmission(RequestAdmissionI requestAdmission) throws Exception {
		
		RequestAdmissionI newRequestAdmission = requestAdmission.copy();
		
		List<AllocationCore> allocation = allocateCoreFromComputers(2, DEFAULT_AVM_SIZE);
		
		
		/* Il n'y a pas assez de ressources pour satisfaire les besoins du générateur de requête */
		if(allocation == null) {
			logMessage("Controleur d'admission : Refus de la demande du générateur "+requestAdmission.getRequestGeneratorManagementInboundPortURI());
			return newRequestAdmission;
		}		
		
		String rd_uri = "dispatcher_"+id;
		String distribInPortURI = "dispatcher_management_inbound_port_URI_"+id;
		String requestSubmissionInboundPortURI = "dispatcher_submission_inboud_port_URI_"+id;
		String requestNotificationInboundPortURI = requestAdmission.getRequestNotificationPortURI();
		newRequestAdmission.setRequestDispatcherURI(rd_uri);
		String requestDispatcherDynamicStateDataInboundPortURI = "dispatcher_dynamic_uri_"+id;
		String requestDispatcherStaticStateDataInboundPortURI = "dispatcher_static_uri_"+id;
		String requestDispatcherHandlerInboundPortURI = "dispatcher_handler_uri_"+id;
		
		rd_notification_inport_map.put(rd_uri, requestNotificationInboundPortURI);
		
		id++;
		List<AVMUris> uris = new ArrayList<>();
		List<String> avms_uri = new ArrayList<>();
		for(int i = 0; i < DEFAULT_AVM_SIZE; i++) {
			String vmURI = AVMURI+id_avm;
			String appliInPortURI = AVMMANAGEMENTURI+id_avm;
			String requestSubmissionInboundPortURIVM = AVMREQUESTSUBMISSIONURI+id_avm;
			String requestNotificationInboundPortURIVM = AVMREQUESTNOTIFICATIONURI+id_avm;
			String applicationVMDynamicStateDataInboundPortURI = AVM_DYNAMIC_STATE + id_avm;
			String applicationVMStaticStateDataInboundPortURI = AVM_STATIC_STATE + id_avm;
			
			uris.add(new AVMUris(requestSubmissionInboundPortURIVM, requestNotificationInboundPortURIVM, appliInPortURI, vmURI,
					applicationVMDynamicStateDataInboundPortURI, applicationVMStaticStateDataInboundPortURI));
			id_avm++;
			avms_uri.add(vmURI);
			
		}

		reqDispAvms_map.put(rd_uri, avms_uri);
		
		//On fournit au generateur l'uri du port de submission de requete du dispatcher 
		newRequestAdmission.setRequestSubmissionPortURI(requestSubmissionInboundPortURI);
		
		/*on limite l'acces au dynamic component creator car il doit cr�er/d�marrer/ex�cuter
		 *  tous les composants n�cessaires pour un seul g�n�rateur d'un coup !
		 *  Donc pas d'acc�s concurrents
		*/
			
		/*
		 * On cr�e le dispatcher via le dynamicComponentCreator
		 */
		Object[] argumentsDispatcher = {rd_uri,
				distribInPortURI, 
				requestSubmissionInboundPortURI, 
				requestNotificationInboundPortURI,
				requestDispatcherDynamicStateDataInboundPortURI,
				requestDispatcherStaticStateDataInboundPortURI,
				uris};


		dynamicComponentCreationOutboundPort.createComponent(RequestDispatcher.class.getCanonicalName(),
					argumentsDispatcher);
		
		
		
		addRequiredInterface(RequestDispatcherManagementI.class);
		RequestDispatcherManagementOutboundPort rsmvmmop = new RequestDispatcherManagementOutboundPort(this);
		addPort(rsmvmmop);
		rsmvmmop.publishPort();
		try {
			doPortConnection(rsmvmmop.getPortURI(), distribInPortURI, RequestDispatcherManagementConnector.class.getCanonicalName());
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		rd_management_port_map.put(rd_uri, rsmvmmop);		
		
		/*
		 * On cr�e l'application VM via le dynamicComponentCreator
		 */
		for(int i = 0; i < DEFAULT_AVM_SIZE; i++) {
			AVMUris avmURIS = uris.get(i); 
			Object[] argumentsAppVM = {avmURIS.getAVMUri(), 
					avmURIS.getApplicationVMManagementInboundPortVM(),
					avmURIS.getRequestSubmissionInboundPortVM(), 
					avmURIS.getRequestNotificationInboundPortVM(),
					avmURIS.getApplicationVMDynamicStateDataInboundPortURI(),
					avmURIS.getApplicationVMStaticStateDataInboundPortURI()};

			dynamicComponentCreationOutboundPort.createComponent(ApplicationVM.class.getCanonicalName(),
					argumentsAppVM);	
		}

			

		/*
		 * On cr�e l'integrateur qui va g�rer la g�n�ration de requete
		 *  via le dynamicComponentCreator :
		 * on r�cup�re l'uri du port de management du g�n�rateur de requ�te dans l'objet
		 * requete d'admission, fourni en argument de la methode
		 */
		
		
		ApplicationVMManagementOutboundPort avmmop;
		
		
		for(int i = 0; i < uris.size(); i++) {
			AVMUris auri = uris.get(i);
			avmmop = new ApplicationVMManagementOutboundPort(this);
			addPort(avmmop);
			avmmop.publishPort();
			doPortConnection(avmmop.getPortURI(), auri.getApplicationVMManagementInboundPortVM(), ApplicationVMManagementConnector.class.getCanonicalName());
			avm_management_port_map.put(auri.getAVMUri(), avmmop);
			
			allocation.get(i).setVMUri(auri.getAVMUri());
			avmmop.allocateCores(allocation.get(i).getCores());
			allocationVMCores_map.put(auri.getAVMUri(), allocation.get(i));
			
		}
		
		rsmvmmop.startPortConnection();
		
		String ah_uri = "automatic_handler_uri"+id;
		String ah_management_inport_uri = "automatic_handler_management_inport_uri";
		
		Map<String, String> proc_uri_cores_list = new HashMap<>();
				
		for(AllocationCore ac : allocation){
			getProcessorCoordinatorFreqURIS(ah_uri,ac.getCores(), proc_uri_cores_list );
		}
		

		
		Object[] argumentsAutomaticHandler = {ah_uri,
				ah_management_inport_uri, 
				rd_uri,
				distribInPortURI,
				requestDispatcherHandlerInboundPortURI,
				requestDispatcherDynamicStateDataInboundPortURI,
				requestDispatcherStaticStateDataInboundPortURI,
				requestAdmission.getAverageRequestResponseTime(),
				proc_uri_cores_list
				};
		
		addOfferedInterface(AutomaticHandlerRequestI.class);
		AutomaticHandlerRequestInboundPort req_disp_hand_inport = new AutomaticHandlerRequestInboundPort(requestDispatcherHandlerInboundPortURI, this);
		addPort(req_disp_hand_inport);
		req_disp_hand_inport.publishPort();
		
		

		dynamicComponentCreationOutboundPort.createComponent(AutomaticHandler.class.getCanonicalName(),
				argumentsAutomaticHandler);
		
		
				
		logMessage("Controleur d'admission : Acceptation de la demande du générateur "+requestAdmission.getRequestGeneratorManagementInboundPortURI());

		
		/*
		 * On retourne l'uri du port de soumission de requetes
		 */
					
		return newRequestAdmission;
		
		
	}
	
	
	/**
	 * Notifie le ProcessorCoordinator de l'acquisition de coeurs d'un RequestDispatcher.
	 * Demande la création des ProcessorCoordinatorFreqInboundPort si nécessaire, et met
	 * les URIS correspondantes dans proc_coord_freq_inport_map
	 * @param handler_uri	URI de l'AutomaticHandler
	 * @param allocatedCores	Coeurs fraichement alloués
	 * @param proc_coord_freq_inport_map URIS des ProcessorCoordinatorFreqInboundPort
	 */
	private void getProcessorCoordinatorFreqURIS(String handler_uri, AllocatedCore[] allocatedCores, Map<String, String> proc_coord_freq_inport_map) {

		Map<String, Set<Integer>> cores_map = current_cores_handlers_map.get(handler_uri);
		
		
		for(int i = 0; i < allocatedCores.length; i++) {
			
			ProcessorCoordinatorManagementOutboundPort outport = proc_coord_map.get(allocatedCores[i].processorURI);
			try {
				outport.notifyCorePossession(handler_uri, allocatedCores[i].coreNo);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			if(cores_map!=null){
				Set<Integer> cores ;
				if((cores=cores_map.get(allocatedCores[i].processorURI))!=null){
					cores.add(allocatedCores[i].coreNo);
					continue;
				}
			}
			else{
				cores_map = new HashMap<>();
				current_cores_handlers_map.put(handler_uri, cores_map);
			}
			
			Set<Integer> cores = new HashSet<>();
			cores.add(allocatedCores[i].coreNo);
 			cores_map.put(allocatedCores[i].processorURI,  cores);
			
			
		
			try {
				proc_coord_freq_inport_map.put(allocatedCores[i].processorURI, outport.addCoordInboundPort());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
							
		}
		
	}




	@Override
	public List<String> removeAVMFromRequestDispatcher(String handler_uri, String RequestDispatcherURI, String avmURI) {
		
		List<String> l = reqDispAvms_map.get(RequestDispatcherURI);
		if(l == null) return null;
		l.remove(avmURI);
		RequestDispatcherManagementOutboundPort rqout = rd_management_port_map.get(RequestDispatcherURI);
		try {
			rqout.removeAVM(avmURI);
		
			List<String> proc_freqs = new ArrayList<>();


			for(AllocatedCore core : allocationVMCores_map.get(avmURI).getCores() ){
				
				removeCoresMap(handler_uri, core.processorURI, proc_freqs, core.coreNo);
			}
			
			allocationVMCores_map.get(avmURI).freeCores();
			
			allocationVMCores_map.remove(avmURI);
			avm_management_port_map.remove(avmURI).doDisconnection();
			
			return proc_freqs;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	
		
	}

	@Override
	public void acceptRequestAdmissionTerminationNotification(RequestAdmissionI requestAdmission) throws Exception {
		String rqdispURI = requestAdmission.getRequestDispatcherURI();
		
		rd_management_port_map.remove(rqdispURI).doDisconnection();
		logMessage("Controleur d'admission : Ressources libérées par le Request Generator "+requestAdmission.getRequestGeneratorManagementInboundPortURI());
		
	}




	@Override
	public Map<String, String> addAVMToRequestDispatcher(String handler_uri, String requestDispatcherURI) throws Exception {
		List<AllocationCore> cores = allocateCoreFromComputers(2, 1);
		if(cores==null){
		
			return null;
		}
		
		String vmURI = AVMURI+id_avm;
		String appliInPortURI = AVMMANAGEMENTURI+id_avm;
		String requestSubmissionInboundPortURIVM = AVMREQUESTSUBMISSIONURI+id_avm;
		String requestNotificationInboundPortURIVM = rd_notification_inport_map.get(requestDispatcherURI);
		String applicationVMDynamicStateDataInboundPortURI = AVM_DYNAMIC_STATE + id_avm;
		String applicationVMStaticStateDataInboundPortURI = AVM_STATIC_STATE + id_avm;
		id_avm++;
		
		Object[] argumentsAppVM = {vmURI, 
				appliInPortURI,
				requestSubmissionInboundPortURIVM, 
				requestNotificationInboundPortURIVM,
				applicationVMDynamicStateDataInboundPortURI,
				applicationVMStaticStateDataInboundPortURI};

		
		dynamicComponentCreationOutboundPort.createComponent(ApplicationVM.class.getCanonicalName(),
				argumentsAppVM);
		
		AVMUris avmUris = new AVMUris(requestSubmissionInboundPortURIVM, 
				requestNotificationInboundPortURIVM,
				appliInPortURI,
				vmURI, 
				applicationVMDynamicStateDataInboundPortURI, 
				applicationVMStaticStateDataInboundPortURI);
		
		RequestDispatcherManagementOutboundPort req_disp_management_outport 
		= rd_management_port_map.get(requestDispatcherURI);
		
		req_disp_management_outport.addAVM(avmUris);
		req_disp_management_outport.connectAVM(vmURI);
		
		
		ApplicationVMManagementOutboundPort avmmop = new ApplicationVMManagementOutboundPort(this);
		addPort(avmmop);
		avmmop.publishPort();
		doPortConnection(avmmop.getPortURI(), appliInPortURI, ApplicationVMManagementConnector.class.getCanonicalName());
		avm_management_port_map.put(vmURI, avmmop);
		
		
		cores.get(0).setVMUri(vmURI);
		avmmop.allocateCores(cores.get(0).getCores());
		Map<String, String> proc_coord_freq_inport_map = new HashMap<>();
		getProcessorCoordinatorFreqURIS(handler_uri, cores.get(0).getCores(), proc_coord_freq_inport_map );
		allocationVMCores_map.put(vmURI, cores.get(0));
		return proc_coord_freq_inport_map;
	}



	/**
	 * Notifie le ProcessorCoordinator qu'un coeur a été enlevé à un RequestDispatcher
	 * Les URIS des ports ProcessorCoordinatorFreqOutport à oter par l'AutomaticHandler sont dans procURIS.
	 * @param handler_uri	URI de l'AutomaticHandler
	 * @param processorURI	URI du Processor
	 * @param procURIS	Liste des URIS des ports ProcessorCoordinatorFreqOutport à oter par l'AutomaticHandler
	 * @param coreNum numéro du coeur qui a été enlevé
	 */
	private void removeCoresMap(String handler_uri, String processorURI,
			List<String> procURIS, int coreNum){

		Map<String, Set<Integer>> cores_map = current_cores_handlers_map.get(handler_uri);
		
		int nb_cores = cores_map.get(processorURI).size();
		Set<Integer> cores = cores_map.get(processorURI);
		ProcessorCoordinatorManagementOutboundPort outport = proc_coord_map.get(processorURI);
		if(nb_cores==1){
			
			cores_map.remove(processorURI);
			
			try {
				
				outport.notifyCoreRestitution(handler_uri, coreNum);
				outport.removeOrderOutport(handler_uri);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			procURIS.add(processorURI);
		}
		else{
			try {
				outport.notifyCoreRestitution(handler_uri, coreNum);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			cores.remove(coreNum);
			
		}
				
	}




}
