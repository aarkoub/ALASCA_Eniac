package etape1.admissioncontroler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import etape1.admissioncontroler.interfaces.AdmissionControlerManagementI;
import etape1.admissioncontroler.interfaces.RequestAdmissionI;
import etape1.admissioncontroler.interfaces.RequestAdmissionNotificationHandlerI;
import etape1.admissioncontroler.interfaces.RequestAdmissionNotificationI;
import etape1.admissioncontroler.interfaces.RequestAdmissionSubmissionHandlerI;
import etape1.admissioncontroler.interfaces.RequestAdmissionSubmissionI;
import etape1.admissioncontroler.ports.AdmissionControlerManagementInboundPort;
import etape1.dynamiccomponentcreator.DynamicComponentCreationConnector;
import etape1.dynamiccomponentcreator.DynamicComponentCreationI;
import etape1.dynamiccomponentcreator.DynamicComponentCreationOutboundPort;
import etape1.requestadmission.ports.RequestAdmissionNotificationInboundPort;
import etape1.requestadmission.ports.RequestAdmissionSubmissionInboundPort;
import etape1.requestdispatcher.multi.components.RequestDispatcherMultiVM;
import etape1.requestdispatcher.multi.connectors.RequestDispatcherMultiVMManagementConnector;
import etape1.requestdispatcher.multi.data.AVMUris;
import etape1.requestdispatcher.multi.ports.RequestDispatcherMultiVMManagementOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
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
RequestAdmissionNotificationHandlerI{
	
	String uri;
	
	private int id=0;
	private AdmissionControlerManagementInboundPort admissionControlerManagementInboundPort;
	private DynamicComponentCreationOutboundPort dynamicComponentCreationOutboundPort;
	private String dynamicComponentCreationInboundPortURI;
	private RequestAdmissionSubmissionInboundPort requestAdmissionSubmissionInboundPort;
	private RequestAdmissionNotificationInboundPort requestAdmissionNotificationInboundPort;
	
	
	private static final int DEFAULT_AVM_SIZE = 2;
	private Map<String, RequestDispatcherMultiVMManagementOutboundPort> rdmanagementport;
	private Map<String, ComputerData> computerdata;
	private Map<String, ApplicationVMManagementOutboundPort> avmmanagementport;
	private Map<String, List<AllocationCore>> allocationVMCores;
	private static int id_avm = 0;
	private static final String AVMURI = "avm_uri_";
	private static final String AVMMANAGEMENTURI = "avm_muri_";
	private static final String AVMREQUESTSUBMISSIONURI = "avm_rsuri_";
	private static final String AVMREQUESTNOTIFICATIONURI = "avm_rnuri_";
	
	private Map<Integer, List<Computer>> nbCoresMap ;
	
	
	public AdmissionControler(String uri, 
			int nbComputers,
			String AdmissionControlerManagementInboundURI,
			String dynamicComponentCreationInboundPortURI,
			String RequestAdmissionSubmissionInboundPortURI,
			String RequestAdmissionNotificationInboundPortURI,
			List<Computer> computers,
			List<ComputerURI> computeruris,
			List<ComputerMonitor> computerMonitors) throws Exception{
		
		super(1,1);
		assert nbComputers > 0;
		assert uri != null;
		assert AdmissionControlerManagementInboundURI != null;
		assert RequestAdmissionSubmissionInboundPortURI != null;
		assert RequestAdmissionNotificationInboundPortURI != null;
		assert dynamicComponentCreationInboundPortURI != null;
		

		this.uri = uri;
		
		admissionControlerManagementInboundPort = new AdmissionControlerManagementInboundPort(AdmissionControlerManagementInboundURI, this);
		addPort(admissionControlerManagementInboundPort);
		admissionControlerManagementInboundPort.publishPort();
		
		
		addOfferedInterface(RequestAdmissionSubmissionI.class);
		requestAdmissionSubmissionInboundPort = new RequestAdmissionSubmissionInboundPort(RequestAdmissionSubmissionInboundPortURI, this);
		addPort(requestAdmissionSubmissionInboundPort);
		requestAdmissionSubmissionInboundPort.publishPort();
		//requestAdmissionSubmissionInboundPorts.add(sub_port);
		
		
		addOfferedInterface(RequestAdmissionNotificationI.class);
		requestAdmissionNotificationInboundPort = new RequestAdmissionNotificationInboundPort(RequestAdmissionNotificationInboundPortURI, this);
		addPort(requestAdmissionNotificationInboundPort);
		requestAdmissionNotificationInboundPort.publishPort();
		//requestAdmissionNotificationInboundPorts.add(notif_port);	
		

		
		addRequiredInterface(DynamicComponentCreationI.class);
		this.dynamicComponentCreationInboundPortURI = dynamicComponentCreationInboundPortURI;
		dynamicComponentCreationOutboundPort = new DynamicComponentCreationOutboundPort(this);
		addPort(dynamicComponentCreationOutboundPort);
		dynamicComponentCreationOutboundPort.publishPort();
		
		rdmanagementport = new HashMap<>();
		computerdata = new HashMap<>();
		avmmanagementport = new HashMap<>();
		allocationVMCores = new HashMap<>();
		nbCoresMap = new HashMap<>();
		
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
				doPortConnection(dynamicComponentCreationOutboundPort.getPortURI(), dynamicComponentCreationInboundPortURI, 
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

				this.dynamicComponentCreationOutboundPort.unpublishPort() ;
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
				this.dynamicComponentCreationOutboundPort.unpublishPort() ;
			} catch (Exception e) {
				throw new ComponentShutdownException("Error when shutdown admission controler");
			}
			

		super.shutdownNow();
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
	
		/*
		 * Si on a encore des ressources libres
		 */
		
		RequestAdmissionI newRequestAdmission = requestAdmission.copy();
		
		List<AllocationCore> allocation = allocateCoreFromComputers(1, DEFAULT_AVM_SIZE);
		if(allocation != null) {
			String rd_uri = "dispatcher_"+id;
			String distribInPortURI = "dispatcher_management_inbound_port_URI_"+id;
			String requestSubmissionInboundPortURI = "dispatcher_submission_inboud_port_URI_"+id;
			String requestNotificationInboundPortURI = requestAdmission.getRequestNotificationPortURI();
			newRequestAdmission.setRequestDispatcherURI(rd_uri);
		
			id++;
			List<AVMUris> uris = new ArrayList<>();
			for(int i = 0; i < DEFAULT_AVM_SIZE; i++) {
				String vmURI = AVMURI+id_avm;
				String appliInPortURI = AVMMANAGEMENTURI+id_avm;
				String requestSubmissionInboundPortURIVM = AVMREQUESTSUBMISSIONURI+id_avm;
				String requestNotificationInboundPortURIVM = AVMREQUESTNOTIFICATIONURI+id_avm;
				uris.add(new AVMUris(requestSubmissionInboundPortURIVM, requestNotificationInboundPortURIVM, appliInPortURI, vmURI));
				id_avm++;
			}
			
			//On fournit au generateur l'uri du port de submission de requete du dispatcher 
			newRequestAdmission.setRequestSubmissionPortURI(requestSubmissionInboundPortURI);
			
			/*on limite l'acces au dynamic component creator car il doit cr�er/d�marrer/ex�cuter
			 *  tous les composants n�cessaires pour un seul g�n�rateur d'un coup !
			 *  Donc pas d'acc�s concurrents
			*/
			synchronized (dynamicComponentCreationOutboundPort) {
					
				/*
				 * On cr�e le dispatcher via le dynamicComponentCreator
				 */
				Object[] argumentsDispatcher = {rd_uri,
						distribInPortURI, 
						requestSubmissionInboundPortURI, 
						requestNotificationInboundPortURI, 
						uris};
	
				dynamicComponentCreationOutboundPort.createComponent(RequestDispatcherMultiVM.class.getCanonicalName(),
						argumentsDispatcher);		
				
				RequestDispatcherMultiVMManagementOutboundPort rsmvmmop = new RequestDispatcherMultiVMManagementOutboundPort(this);
				addPort(rsmvmmop);
				rsmvmmop.publishPort();
				doPortConnection(rsmvmmop.getPortURI(), distribInPortURI, RequestDispatcherMultiVMManagementConnector.class.getCanonicalName());
				rdmanagementport.put(rd_uri, rsmvmmop);
				
				/*
				 * On cr�e l'application VM via le dynamicComponentCreator
				 */
				for(int i = 0; i < DEFAULT_AVM_SIZE; i++) {
					Object[] argumentsAppVM = {uris.get(i).getAVMUri(), 
							uris.get(i).getApplicationVMManagementInboundPortVM(),
							uris.get(i).getRequestSubmissionInboundPortVM(), 
							uris.get(i).getRequestNotificationInboundPortVM()};
		
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
				allocationVMCores.put(rd_uri, new ArrayList<>());
				for(int i = 0; i < uris.size(); i++) {
					AVMUris auri = uris.get(i);
					avmmop = new ApplicationVMManagementOutboundPort(this);
					addPort(avmmop);
					avmmop.publishPort();
					doPortConnection(avmmop.getPortURI(), auri.getApplicationVMManagementInboundPortVM(), ApplicationVMManagementConnector.class.getCanonicalName());
					avmmanagementport.put(auri.getAVMUri(), avmmop);
					
					allocation.get(i).setVMUri(auri.getAVMUri());
					avmmop.allocateCores(allocation.get(i).getCores());
					allocationVMCores.get(rd_uri).add(allocation.get(i));
				}
				dynamicComponentCreationOutboundPort.startComponents();
				dynamicComponentCreationOutboundPort.executeComponents();

				//createAndAddVMToRequestDispatcher(rdmanagementport.keySet().stream().findFirst().get());
				
				
			}
			
			logMessage("Controleur d'admission : Acceptation de la demande du g�n�rateur "+requestAdmission.getRequestGeneratorManagementInboundPortURI());
	
			/*
			 * On retourne l'uri du port de soumission de requetes
			 */
					
		}
		
		logMessage("Controleur d'admission : Refus de la demande du g�n�rateur "+requestAdmission.getRequestGeneratorManagementInboundPortURI());
		
		
		/*
		 * Sinon, si on n'a pas les ressources n�cessaires pour satisfaire 
		 * les besoins du g�n�rateur de requ�tes, on renvoie null
		 */
		return newRequestAdmission;
	}
	
	
	private boolean createAndAddVMToRequestDispatcher(String RequestDispatcherURI) throws Exception {
		Object[] args = {
				AVMURI+id_avm,
				AVMMANAGEMENTURI+id_avm,
				AVMREQUESTSUBMISSIONURI+id_avm,
				AVMREQUESTNOTIFICATIONURI+id_avm
		};
		
		AllocationCore alloc = allocateCoreFromComputers(1, AVMURI+id_avm);
		if(alloc == null) return false;
		AllocatedCore[] ac = alloc.getCores();
		
		AVMUris uri = new AVMUris(AVMREQUESTSUBMISSIONURI+id_avm, AVMREQUESTNOTIFICATIONURI+id_avm, AVMMANAGEMENTURI+id_avm, AVMURI+id_avm);
		
		RequestDispatcherMultiVMManagementOutboundPort rsmvmmop = rdmanagementport.get(RequestDispatcherURI);
		
		rsmvmmop.addAVM(uri);
		dynamicComponentCreationOutboundPort.createComponent(ApplicationVM.class.getCanonicalName(),
				args);
		
		
		id_avm++;
		
		
		ApplicationVMManagementOutboundPort avmmop = new ApplicationVMManagementOutboundPort(this);
		addPort(avmmop);
		avmmop.publishPort();
		doPortConnection(avmmop.getPortURI(), uri.getApplicationVMManagementInboundPortVM(), ApplicationVMManagementConnector.class.getCanonicalName());
		avmmanagementport.put(uri.getAVMUri(), avmmop);
		avmmop.allocateCores(ac);
		
		dynamicComponentCreationOutboundPort.startComponents();
		dynamicComponentCreationOutboundPort.executeComponents();
		
		rsmvmmop.connectAVM(uri.getAVMUri());
		return true;
	}


	@Override
	public void acceptRequestAdmissionTerminationNotification(RequestAdmissionI requestAdmission) throws Exception {
		allocationVMCores.get(requestAdmission.getRequestDispatcherURI()).stream().forEach(arg0 -> {
			try {
				//arg0.freeCores();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		allocationVMCores.remove(requestAdmission.getRequestDispatcherURI());
		logMessage("Controleur d'admission : Ressources libérées par le Request Generator "+requestAdmission.getRequestGeneratorManagementInboundPortURI());
		
	}


}
