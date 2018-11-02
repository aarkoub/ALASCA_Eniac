package etape1.admissioncontroler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;


import etape1.admissioncontroler.interfaces.AdmissionControlerManagementI;
import etape1.admissioncontroler.interfaces.RequestAdmissionI;
import etape1.admissioncontroler.interfaces.RequestAdmissionNotificationHandlerI;
import etape1.admissioncontroler.interfaces.RequestAdmissionNotificationI;
import etape1.admissioncontroler.interfaces.RequestAdmissionSubmissionHandlerI;
import etape1.admissioncontroler.interfaces.RequestAdmissionSubmissionI;
import etape1.admissioncontroler.ports.AdmissionControlerManagementInboundPort;
import etape1.cvm.IntegratorForRequestGeneration;
import etape1.dynamiccomponentcreator.DynamicComponentCreationConnector;
import etape1.dynamiccomponentcreator.DynamicComponentCreationI;
import etape1.dynamiccomponentcreator.DynamicComponentCreationOutboundPort;
import etape1.requestadmission.ports.RequestAdmissionNotificationInboundPort;
import etape1.requestadmission.ports.RequestAdmissionSubmissionInboundPort;
import etape1.requestdispatcher.RequestDispatcher;
import etape1.requestdispatcher.multi.AVMUris;
import etape1.requestdispatcher.multi.RequestDispatcherMultiVM;
import etape1.requestdispatcher.multi.connectors.RequestDispatcherMultiVMManagementConnector;
import etape1.requestdispatcher.multi.ports.RequestDispatcherMultiVMManagementOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.tests.ComputerMonitor;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;

public class AdmissionControler extends AbstractComponent implements AdmissionControlerManagementI, 
RequestAdmissionSubmissionHandlerI,
RequestAdmissionNotificationHandlerI{
	
	String uri;
	int ressources_libres = 0;
	int max_ressources ;
	
	List<RequestDispatcher> available = new ArrayList<>();
	List<RequestDispatcher> used = new ArrayList<>();
	List<ApplicationVM> usedVM = new ArrayList<>();
	List<String> computersURI;
	List<Computer> computers ;
	List<ComputerMonitor> computerMonitors ;
	
	Map<String, Integer> ressourcesPrises = new HashMap<>();
	Stack<Integer> ressourcesLibres = new Stack<Integer>();

	
	//protected List<RequestAdmissionSubmissionInboundPort> requestAdmissionSubmissionInboundPorts = new ArrayList<>();
	//protected List<RequestAdmissionNotificationInboundPort> requestAdmissionNotificationInboundPorts = new ArrayList<>();
	//protected List<String> requestAdmissionNotificationInboundPortURIS;
	//protected List<String> requestAdmissionSubmissionInboundPortURIS;
	
	private int id=0;
	private AdmissionControlerManagementInboundPort admissionControlerManagementInboundPort;
	private DynamicComponentCreationOutboundPort dynamicComponentCreationOutboundPort;
	private String dynamicComponentCreationInboundPortURI;
	private RequestAdmissionSubmissionInboundPort requestAdmissionSubmissionInboundPort;
	private RequestAdmissionNotificationInboundPort requestAdmissionNotificationInboundPort;
	
	
	private static final int DEFAULT_AVM_SIZE = 2;
	private Map<String, RequestDispatcherMultiVMManagementOutboundPort> rdmanagementport;
	
	
	public AdmissionControler(String uri, 
			int nbComputers,
			String AdmissionControlerManagementInboundURI,
			String dynamicComponentCreationInboundPortURI,
			String RequestAdmissionSubmissionInboundPortURI,
			String RequestAdmissionNotificationInboundPortURI,
			List<Computer> computers,
			List<ComputerMonitor> computerMonitors,
			List<String> computersURI) throws Exception{
		
		super(1,1);
		
		assert nbComputers > 0;
		assert uri != null;
		assert AdmissionControlerManagementInboundURI != null;
		assert RequestAdmissionSubmissionInboundPortURI != null;
		assert RequestAdmissionNotificationInboundPortURI != null;
		assert dynamicComponentCreationInboundPortURI != null;
		
		this.computers = computers;
		this.computerMonitors = computerMonitors ;
		this.computersURI = computersURI;
		
		max_ressources = nbComputers;
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
		
		
		for(int i=0; i<max_ressources; i++){
				
			ressourcesLibres.push(i);
			
		}
		
		addRequiredInterface(DynamicComponentCreationI.class);
		this.dynamicComponentCreationInboundPortURI = dynamicComponentCreationInboundPortURI;
		dynamicComponentCreationOutboundPort = new DynamicComponentCreationOutboundPort(this);
		addPort(dynamicComponentCreationOutboundPort);
		dynamicComponentCreationOutboundPort.publishPort();
		
		rdmanagementport = new HashMap<>();
						
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

	@Override
	public String getSubmissionInboundPortURI(RequestAdmissionI requestAdmission) throws Exception {
	
		/*
		 * Si on a encore des ressources libres
		 */
		if(ressourcesLibres.size() !=0) {
			
			id = ressourcesLibres.pop();
			
			ressourcesPrises.put(requestAdmission.getRequestGeneratorManagementInboundPortURI(), id);
			
			String rd_uri = "dispatcher_"+id;
			String distribInPortURI = "dispatcher_management_inbound_port_URI_"+id;
			String requestSubmissionInboundPortURI = "dispatcher_submission_inboud_port_URI_"+id;
			String requestNotificationInboundPortURI = requestAdmission.getRequestNotificationPortURI();
		
			
			List<AVMUris> uris = new ArrayList<>();
			for(int i = 0; i < DEFAULT_AVM_SIZE; i++) {
				String vmURI = "appli_vm_"+id+"_"+i;
				String appliInPortURI = "appli_vm_management_inbound_port_URI_"+id+"_"+i;
				String requestSubmissionInboundPortURIVM = "request_sub_inbound_port_uri_"+id+"_"+i;
				String requestNotificationInboundPortURIVM = "request_notif_inbound_port_uri_"+id+"_"+i;
				uris.add(new AVMUris(requestSubmissionInboundPortURIVM, requestNotificationInboundPortURIVM, appliInPortURI, vmURI));
			}
			
			
			String computerOutPortURI = computersURI.get(id);
			Computer c = computers.get(id);
			c.toggleLogging() ;
			c.toggleTracing() ;
			
			
			//On fournit au generateur l'uri du port de submission de requete du dispatcher 
			requestAdmission.setRequestSubmissionPortURI(requestSubmissionInboundPortURI);
			
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
				rdmanagementport.put(distribInPortURI, rsmvmmop);
				
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
				
				Object[] argumentsIntegrator = {
						uris,
						computerOutPortURI};
				
				dynamicComponentCreationOutboundPort.createComponent(IntegratorForRequestGeneration.class.getCanonicalName(),
						argumentsIntegrator);
				
				dynamicComponentCreationOutboundPort.startComponents();
				dynamicComponentCreationOutboundPort.executeComponents();
				
			}
			
			logMessage("Controleur d'admission : Acceptation de la demande du g�n�rateur "+requestAdmission.getRequestGeneratorManagementInboundPortURI());
			
			
			/*
			 * On retourne l'uri du port de soumission de requetes
			 */
					
			return requestSubmissionInboundPortURI;
		}
		
		logMessage("Controleur d'admission : Refus de la demande du g�n�rateur "+requestAdmission.getRequestGeneratorManagementInboundPortURI());
		/*
		 * Sinon, si on n'a pas les ressources n�cessaires pour satisfaire 
		 * les besoins du g�n�rateur de requ�tes, on renvoie null
		 */
		return null;
	}


	@Override
	public void acceptRequestAdmissionTerminationNotification(RequestAdmissionI requestAdmission) throws Exception {
		
		int id_libere = ressourcesPrises.remove(requestAdmission.getRequestGeneratorManagementInboundPortURI());

		ressourcesLibres.push(id_libere);
		
		logMessage("Controleur d'admission : Ressources lib�r�es par le Request Generator "+requestAdmission.getRequestGeneratorManagementInboundPortURI());
		
	}


}
