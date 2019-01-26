package eniac.admissioncontroler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eniac.admissioncontroler.interfaces.AdmissionControlerManagementI;
import eniac.admissioncontroler.interfaces.RequestAdmissionI;
import eniac.admissioncontroler.interfaces.RequestAdmissionNotificationHandlerI;
import eniac.admissioncontroler.interfaces.RequestAdmissionNotificationI;
import eniac.admissioncontroler.interfaces.RequestAdmissionSubmissionHandlerI;
import eniac.admissioncontroler.interfaces.RequestAdmissionSubmissionI;
import eniac.admissioncontroler.ports.AdmissionControlerManagementInboundPort;
import eniac.automatichandler.AutomaticHandler;
import eniac.processorcoordinator.connectors.ProcessorCoordinatorManagementConnector;
import eniac.processorcoordinator.ports.ProcessorCoordinatorManagementOutboundPort;
import eniac.requestadmission.ports.RequestAdmissionNotificationInboundPort;
import eniac.requestadmission.ports.RequestAdmissionSubmissionInboundPort;
import eniac.requestdispatcher.RequestDispatcher;
import eniac.requestdispatcher.connectors.RequestDispatcherManagementConnector;
import eniac.requestdispatcher.data.AVMUris;
import eniac.requestdispatcher.interfaces.RequestDispatcherManagementI;
import eniac.requestdispatcher.ports.RequestDispatcherManagementOutboundPort;
import eniac.requestdispatcherhandler.interfaces.RequestDispatcherHandlerI;
import eniac.requestdispatcherhandler.ports.RequestDispatcherHandlerInboundPort;
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
import fr.sorbonne_u.datacenter.hardware.processors.UnacceptableFrequencyException;
import fr.sorbonne_u.datacenter.hardware.processors.UnavailableFrequencyException;
import fr.sorbonne_u.datacenter.hardware.processors.ports.ProcessorManagementOutboundPort;
import fr.sorbonne_u.datacenter.hardware.tests.ComputerMonitor;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;
import fr.sorbonne_u.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;


public class AdmissionControler extends AbstractComponent implements AdmissionControlerManagementI, 
RequestAdmissionSubmissionHandlerI,
RequestAdmissionNotificationHandlerI,
RequestDispatcherHandlerI{
	
	protected String uri;
	
	protected int id=0;
	protected AdmissionControlerManagementInboundPort admissionControlerManagementInboundPort;
	protected DynamicComponentCreationOutboundPort dynamicComponentCreationOutboundPort;
	protected RequestAdmissionSubmissionInboundPort requestAdmissionSubmissionInboundPort;
	protected RequestAdmissionNotificationInboundPort requestAdmissionNotificationInboundPort;
	protected Map<String,RequestDispatcherHandlerInboundPort> requestDispatcherHandlerInboundPortMap;
	
	protected static final int DEFAULT_AVM_SIZE = 2;
	protected Map<String, RequestDispatcherManagementOutboundPort> rd_management_port_map;
	protected Map<String, String> rd_notification_inport_map;
	protected Map<String, ComputerData> computerdata_map;
	protected Map<String, ApplicationVMManagementOutboundPort> avm_management_port_map;
	protected Map<String, AllocationCore> allocationVMCores_map;
	protected Map<String, List<String>> reqDispAvms_map;
	protected static int id_avm = 0;
	protected static final String AVMURI = "avm_uri_";
	protected static final String AVMMANAGEMENTURI = "avm_muri_";
	protected static final String AVMREQUESTSUBMISSIONURI = "avm_rsuri_";
	protected static final String AVMREQUESTNOTIFICATIONURI = "avm_rnuri_";
	protected static final String AVM_DYNAMIC_STATE = "avm_dynamic_state";
	protected static final String AVM_STATIC_STATE = "avm_static_state";
	
	protected Map<Integer, List<Computer>> nbCoresMap ;

	protected Map<String, ProcessorCoordinatorManagementOutboundPort> proc_coord_map;

	protected Map<String, String> proc_coord_management_inport_map;
	
	protected Map<String, Map<String, Integer>> current_cores_handlers_map ;
	
	
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
		admissionControlerManagementInboundPort = new AdmissionControlerManagementInboundPort(admissionControlerManagementInboundURI, this);
		addPort(admissionControlerManagementInboundPort);
		admissionControlerManagementInboundPort.publishPort();
		
		
		addOfferedInterface(RequestAdmissionSubmissionI.class);
		requestAdmissionSubmissionInboundPort = new RequestAdmissionSubmissionInboundPort(requestAdmissionSubmissionInboundPortURI, this);
		addPort(requestAdmissionSubmissionInboundPort);
		requestAdmissionSubmissionInboundPort.publishPort();
		
		
		addOfferedInterface(RequestAdmissionNotificationI.class);
		requestAdmissionNotificationInboundPort = new RequestAdmissionNotificationInboundPort(requestAdmissionNotificationInboundPortURI, this);
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
		nbCoresMap = new HashMap<>();
		reqDispAvms_map = new HashMap<>();
		requestDispatcherHandlerInboundPortMap = new HashMap<>();
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
				admissionControlerManagementInboundPort.unpublishPort();
				requestAdmissionSubmissionInboundPort.unpublishPort();
				requestAdmissionNotificationInboundPort.unpublishPort();
				dynamicComponentCreationOutboundPort.unpublishPort() ;
				
				for(String uri : requestDispatcherHandlerInboundPortMap.keySet()) {
					requestDispatcherHandlerInboundPortMap.get(uri).unpublishPort();
				}
				
				
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
				admissionControlerManagementInboundPort.unpublishPort();
				requestAdmissionSubmissionInboundPort.unpublishPort();
				requestAdmissionNotificationInboundPort.unpublishPort();
				dynamicComponentCreationOutboundPort.unpublishPort() ;
				for(String uri : requestDispatcherHandlerInboundPortMap.keySet()) {
					requestDispatcherHandlerInboundPortMap.get(uri).unpublishPort();
				}
			} catch (Exception e) {
				throw new ComponentShutdownException("Error when shutdown admission controler");
			}
			

		super.shutdownNow();
	}
	
	
	@Override
	
	public void			setCoreFrequency(String processor_uri, int coreNo, int frequency)
			throws	UnavailableFrequencyException,
					UnacceptableFrequencyException,
					Exception
			{
			/*ProcessorManagementOutboundPort p = proc_management.get(processor_uri);
			p.setCoreFrequency(coreNo, frequency);*/
		 
			}
	
	
	@Override
	public boolean removeCoreFromAvm(String handler_uri, String avm_uri) {
		AllocationCore alloc = allocationVMCores_map.get(avm_uri);
		if(alloc == null) return false;
		Computer computer = alloc.getComputer();
		try {
			
			if(alloc.getCores().length <= 1) return false;
			
			AllocatedCore c = alloc.getCores()[0];
			
			for(AllocatedCore core : alloc.getCores()){
				System.out.println("\t"+c.processorURI);
			}
			
			computer.releaseCore(c);
			AllocatedCore[] newAlloc = new AllocatedCore[alloc.getCores().length-1];
			int j = 0;
			for(int i = 1; i < alloc.getCores().length; i++) {
				newAlloc[j] = alloc.getCores()[i];
				j++;
			}
			alloc.setCores(newAlloc);
			avm_management_port_map.get(avm_uri).removeProcDataStatePorts(c.processorURI);
			
			Map<String, Integer> cores_map = current_cores_handlers_map.get(handler_uri);
			System.out.println("removing "+handler_uri+" "+c.processorURI);
			int nb_cores = cores_map.get(c.processorURI);
			if(nb_cores==1){
				cores_map.remove(c.processorURI);
				ProcessorCoordinatorManagementOutboundPort outport = proc_coord_map.get(c.processorURI);
				outport.removeOrderOutport(handler_uri);
			}
			else{
				cores_map.put(c.processorURI, nb_cores-1);
				System.out.println("removed "+handler_uri+" "+c.processorURI+cores_map.get(c.processorURI));
			}
					
				
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
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
			
			for(String proc_uri : proc_coord_manage_inport_map.keySet()){
				System.out.println(proc_uri+" "+proc_coord_manage_inport_map.get(proc_uri));
			}
			
			avm_management_port_map.get(avm_uri).allocateCores(cores);
			return proc_coord_manage_inport_map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
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
	
	@SuppressWarnings("unused")
	private AllocationCore allocateCoreFromComputers(int nbcores) throws Exception {
		AllocatedCore[] cores;
		for(ComputerData cd: computerdata_map.values()) {
			cores = cd.getCsop().allocateCores(nbcores);
			if(cores.length == nbcores) return new AllocationCore(cd.getComputer(), cores, "");
			for(AllocatedCore alloc: cores) {
				cd.getComputer().releaseCore(alloc);
			}
		}
		return null;
	}
	
	@SuppressWarnings("unused")
	private AllocationCore allocateCoreFromComputers(int nbcores, String VMuri) throws Exception {
		AllocatedCore[] cores;
		for(ComputerData cd: computerdata_map.values()) {
			cores = cd.getCsop().allocateCores(nbcores);
			if(cores.length == nbcores) return new AllocationCore(cd.getComputer(), cores, VMuri);
			for(AllocatedCore alloc: cores) {
				cd.getComputer().releaseCore(alloc);
			}
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
		
		for(String proc_uri : proc_uri_cores_list.keySet()){
			System.out.println("proc_uri="+proc_uri+" proc_freq="+ proc_uri_cores_list.get(proc_uri));
		}
		
		
		Object[] argumentsAutomaticHandler = {ah_uri,
				ah_management_inport_uri, 
				rd_uri,
				requestDispatcherHandlerInboundPortURI,
				requestDispatcherDynamicStateDataInboundPortURI,
				requestDispatcherStaticStateDataInboundPortURI,
				requestAdmission.getAverageRequestResponseTime(),
				proc_uri_cores_list
				};
		
		addOfferedInterface(RequestDispatcherHandlerI.class);
		RequestDispatcherHandlerInboundPort req_disp_hand_inport = new RequestDispatcherHandlerInboundPort(requestDispatcherHandlerInboundPortURI, this);
		addPort(req_disp_hand_inport);
		req_disp_hand_inport.publishPort();
		
		requestDispatcherHandlerInboundPortMap.put(requestDispatcherDynamicStateDataInboundPortURI, req_disp_hand_inport);
		

		dynamicComponentCreationOutboundPort.createComponent(AutomaticHandler.class.getCanonicalName(),
				argumentsAutomaticHandler);
		
		
				
		logMessage("Controleur d'admission : Acceptation de la demande du générateur "+requestAdmission.getRequestGeneratorManagementInboundPortURI());

		
		/*
		 * On retourne l'uri du port de soumission de requetes
		 */
					
		return newRequestAdmission;
		
		
	}
	
	
	
	private void getProcessorCoordinatorFreqURIS(String handler_uri, AllocatedCore[] allocatedCores, Map<String, String> proc_coord_manage_inport_map) {

		Map<String, Integer> cores_map = current_cores_handlers_map.get(handler_uri);
		
		System.out.println("allocated Cores "+handler_uri);
		
		for(int i = 0; i < allocatedCores.length; i++) {
			
			System.out.println("allocatedCores "+handler_uri+" "+allocatedCores[i].coreNo+" "+allocatedCores[i].processorURI);
			
			if(cores_map!=null){
				if(cores_map.get(allocatedCores[i].processorURI)!=null){
					System.out.println("ICI "+cores_map.get(allocatedCores[i].processorURI));
					cores_map.put(allocatedCores[i].processorURI,
							cores_map.get(allocatedCores[i].processorURI)+1);
					
					continue;
				}
			}
			else{
				cores_map = new HashMap<>();
				current_cores_handlers_map.put(handler_uri, cores_map);
			}
			
			
			cores_map.put(allocatedCores[i].processorURI, 1);
			
			ProcessorCoordinatorManagementOutboundPort outport = proc_coord_map.get(allocatedCores[i].processorURI);
		
			try {
				proc_coord_manage_inport_map.put(allocatedCores[i].processorURI, outport.addCoordInboundPort());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
							
		}
		
	}




	@Override
	public boolean removeAVMFromRequestDispatcher(String RequestDispatcherURI, String avmURI) {
		List<String> l = reqDispAvms_map.get(RequestDispatcherURI);
		if(l == null) return false;
		l.remove(avmURI);
		RequestDispatcherManagementOutboundPort rqout = rd_management_port_map.get(RequestDispatcherURI);
		try {
			rqout.removeAVM(avmURI);
			allocationVMCores_map.get(avmURI).freeCores();
			allocationVMCores_map.remove(avmURI);
			avm_management_port_map.remove(avmURI).doDisconnection();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
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
		Map<String, String> proc_coord_manage_inport_map = new HashMap<>();
		getProcessorCoordinatorFreqURIS(handler_uri, cores.get(0).getCores(), proc_coord_manage_inport_map );
		allocationVMCores_map.put(vmURI, cores.get(0));
		return proc_coord_manage_inport_map;
	}




	@Override
	public String removeAVMFromRequestDispatcher(String requestDispatcherURI) throws Exception {
		
		List<String> l = reqDispAvms_map.get(requestDispatcherURI);
		if(l == null || l.size()<=1) return null;
		String avmURI = l.remove(0);
		
		RequestDispatcherManagementOutboundPort rqout = rd_management_port_map.get(requestDispatcherURI);
		try {
			rqout.removeAVM(avmURI);
			allocationVMCores_map.get(avmURI).freeCores();
			allocationVMCores_map.remove(avmURI);
			avm_management_port_map.remove(avmURI).doDisconnection();
		} catch (Exception e) {
			e.printStackTrace();
		
		}
		return avmURI;

		
	}




}
