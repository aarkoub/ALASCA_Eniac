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
import eniac.automatichandler.interfaces.RequestDispatcherHandlerI;
import eniac.automatichandler.ports.RequestDispatcherHandlerInboundPort;
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
	protected Map<String, RequestDispatcherManagementOutboundPort> rdmanagementport;
	protected Map<String, ComputerData> computerdata;
	protected Map<String, ApplicationVMManagementOutboundPort> avmmanagementport;
	protected Map<String, AllocationCore> allocationVMCores;
	protected Map<String, List<String>> reqDispAvms;
	protected static int id_avm = 0;
	protected static final String AVMURI = "avm_uri_";
	protected static final String AVMMANAGEMENTURI = "avm_muri_";
	protected static final String AVMREQUESTSUBMISSIONURI = "avm_rsuri_";
	protected static final String AVMREQUESTNOTIFICATIONURI = "avm_rnuri_";
	protected static final String AVM_DYNAMIC_STATE = "avm_dynamic_state";
	protected static final String AVM_STATIC_STATE = "avm_static_state";
	
	protected Map<Integer, List<Computer>> nbCoresMap ;

	
	
	
	public AdmissionControler(String uri, 
			int nbComputers,
			String admissionControlerManagementInboundURI,
			String dynamicComponentCreationInboundPortURI,
			String requestAdmissionSubmissionInboundPortURI,
			String requestAdmissionNotificationInboundPortURI,
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
	
		
		rdmanagementport = new HashMap<>();
		computerdata = new HashMap<>();
		avmmanagementport = new HashMap<>();
		allocationVMCores = new HashMap<>();
		nbCoresMap = new HashMap<>();
		reqDispAvms = new HashMap<>();
		requestDispatcherHandlerInboundPortMap = new HashMap<>();
		
		
		ComputerServicesOutboundPort csop;
		for (int i = 0; i < computers.size(); i++) {
			Computer c =  computers.get(i);
			ComputerURI cUri = computeruris.get(i);
			csop = new ComputerServicesOutboundPort(this);
			addPort(csop);
			csop.publishPort();
			ComputerData computerData =  new ComputerData(cUri,c, csop);
			computerdata.put(cUri.getComputerUri(), computerData);
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
	
	
	
	public boolean removeCoreFromAvm(String avm_uri, AllocatedCore allocatedCore) {
		AllocationCore alloc = allocationVMCores.get(avm_uri);
		if(alloc == null) return false;
		Computer computer = alloc.getComputer();
		try {
			
			int core = -1;
			for(int i = 0; i < alloc.getCores().length; i++) {
				if(alloc.getCores()[i].coreNo == allocatedCore.coreNo &&
						alloc.getCores()[i].processorNo == allocatedCore.processorNo &&
						alloc.getCores()[i].processorInboundPortURI == allocatedCore.processorInboundPortURI &&
						alloc.getCores()[i].processorURI == allocatedCore.processorURI) {
					core = i;
					break;
				}
			}
			if(core == -1) return false;
			computer.releaseCore(allocatedCore);
			AllocatedCore[] newAlloc = new AllocatedCore[alloc.getCores().length-1];
			int j = 0;
			for(int i = 0; i < alloc.getCores().length; i++) {
				if(i != core) {
					newAlloc[j] = alloc.getCores()[i];
					j++;
				}
			}
			alloc.setCores(newAlloc);
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean addCoreToAvm(String avm_uri, int nbcores) {
		AllocationCore alloc = allocationVMCores.get(avm_uri);
		if(alloc == null) return false;
		Computer computer = alloc.getComputer();
		try {
			AllocatedCore[] cores = computer.allocateCores(nbcores);
			if(cores.length != nbcores) {
				computer.releaseCores(cores);
				return false;
			}
			
			AllocatedCore[] alloccores = new AllocatedCore[cores.length+alloc.getCores().length];
			for(int i = 0; i < alloc.getCores().length; i++) {
				alloccores[i] = alloc.getCores()[i];
			}
			for(int i = alloc.getCores().length; i < alloccores.length; i++) {
				alloccores[i] = cores[i-alloc.getCores().length];
			}
			alloc.setCores(alloccores);
			avmmanagementport.get(avm_uri).allocateCores(cores);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	private List<AllocationCore> allocateCoreFromComputers(int nbcores, int nbvm) throws Exception {
		List<AllocationCore> allocores = new ArrayList<>();
		AllocatedCore[] cores;
		for(int k = 0; k < nbvm; k++) {
			for(ComputerData cd: computerdata.values()) {
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
	
	private AllocationCore allocateCoreFromComputers(int nbcores) throws Exception {
		AllocatedCore[] cores;
		for(ComputerData cd: computerdata.values()) {
			cores = cd.getCsop().allocateCores(nbcores);
			if(cores.length == nbcores) return new AllocationCore(cd.getComputer(), cores, "");
			for(AllocatedCore alloc: cores) {
				cd.getComputer().releaseCore(alloc);
			}
		}
		return null;
	}
	
	private AllocationCore allocateCoreFromComputers(int nbcores, String VMuri) throws Exception {
		AllocatedCore[] cores;
		for(ComputerData cd: computerdata.values()) {
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
		
		List<AllocationCore> allocation = allocateCoreFromComputers(1, DEFAULT_AVM_SIZE);
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

		reqDispAvms.put(rd_uri, avms_uri);
		
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
		
		rdmanagementport.put(rd_uri, rsmvmmop);		
		
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
			avmmanagementport.put(auri.getAVMUri(), avmmop);
			
			allocation.get(i).setVMUri(auri.getAVMUri());
			avmmop.allocateCores(allocation.get(i).getCores());
			allocationVMCores.put(auri.getAVMUri(), allocation.get(i));
			
		}
		
		rsmvmmop.startPortConnection();
		
		String ah_uri = "automatic_handler_uri"+id;
		String ah_management_inport_uri = "automatic_handler_management_inport_uri";
		
		Object[] argumentsAutomaticHandler = {ah_uri,
				ah_management_inport_uri, 
				rd_uri,
				requestDispatcherHandlerInboundPortURI,
				requestDispatcherDynamicStateDataInboundPortURI,
				requestDispatcherStaticStateDataInboundPortURI
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
	
	
	
	
	public boolean removeAVMFromRequestDispatcher(String RequestDispatcherURI, String avmURI) {
		List<String> l = reqDispAvms.get(RequestDispatcherURI);
		if(l == null) return false;
		l.remove(avmURI);
		RequestDispatcherManagementOutboundPort rqout = rdmanagementport.get(RequestDispatcherURI);
		try {
			rqout.removeAVM(avmURI);
			allocationVMCores.get(avmURI).freeCores();
			allocationVMCores.remove(avmURI);
			avmmanagementport.remove(avmURI).doDisconnection();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	@Override
	public void acceptRequestAdmissionTerminationNotification(RequestAdmissionI requestAdmission) throws Exception {
		String rqdispURI = requestAdmission.getRequestDispatcherURI();
		
		rdmanagementport.remove(rqdispURI).doDisconnection();
		logMessage("Controleur d'admission : Ressources libérées par le Request Generator "+requestAdmission.getRequestGeneratorManagementInboundPortURI());
		
	}




	@Override
	public void addAVMToRequestDispatcher(String requestDispatcherURI) throws Exception {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void removeAVMFromRequestDispatcher(String requestDispatcherURI) throws Exception {
		System.out.println(requestDispatcherURI);
		List<String> l = reqDispAvms.get(requestDispatcherURI);
		if(l == null || l.size()<=1) return ;
		String avmURI = l.remove(0);
		
		RequestDispatcherManagementOutboundPort rqout = rdmanagementport.get(requestDispatcherURI);
		try {
			rqout.removeAVM(avmURI);
			allocationVMCores.get(avmURI).freeCores();
			allocationVMCores.remove(avmURI);
			avmmanagementport.remove(avmURI).doDisconnection();
		} catch (Exception e) {
			e.printStackTrace();
		
		}

		
	}




}