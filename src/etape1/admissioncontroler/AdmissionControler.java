package etape1.admissioncontroler;

import java.util.ArrayList;
import java.util.List;

import etape1.admissioncontroler.interfaces.AdmissionControlerManagementI;
import etape1.admissioncontroler.interfaces.RequestAdmissionI;
import etape1.admissioncontroler.interfaces.RequestAdmissionSubmissionHandlerI;
import etape1.admissioncontroler.ports.AdmissionControlerManagementInboundPort;
import etape1.admissioncontroler.ports.RequestAdmissionSubmissionInboundPort;
import etape1.cvm.IntegratorForRequestGeneration;
import etape1.dynamiccomponentcreator.DynamicComponentCreationConnector;
import etape1.dynamiccomponentcreator.DynamicComponentCreationI;
import etape1.dynamiccomponentcreator.DynamicComponentCreationOutboundPort;
import etape1.requestdispatcher.RequestDispatcher;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.tests.ComputerMonitor;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI;

public class AdmissionControler extends AbstractComponent implements AdmissionControlerManagementI, 
RequestAdmissionSubmissionHandlerI{
	
	String uri;
	int ressources_libres = 0;
	int max_ressources ;
	
	List<RequestDispatcher> available = new ArrayList<>();
	List<RequestDispatcher> used = new ArrayList<>();
	List<ApplicationVM> usedVM = new ArrayList<>();
	List<String> computersURI;
	List<Computer> computers ;
	List<ComputerMonitor> computerMonitors ;

	
	RequestAdmissionSubmissionInboundPort reqSubInPort;
	
	private int id=0;
	private AdmissionControlerManagementInboundPort managementInboundPort;
	private DynamicComponentCreationOutboundPort dynamicComponentCreationOutboundPort;
	private String dynamicComponentCreationInboundPortURI;
	
	
	public AdmissionControler(String uri, 
			int nbComputers,
			String AdmissionControlerManagementInboundURI,
			String RequestAdmissionSubmissionInboundPortURI,
			String dynamicComponentCreationInboundPortURI,
			List<Computer> computers,
			List<ComputerMonitor> computerMonitors,
			List<String> computersURI) throws Exception{
		
		super(1,1);
		
		assert nbComputers > 0;
		assert uri != null;
		assert AdmissionControlerManagementInboundURI != null;
		assert RequestAdmissionSubmissionInboundPortURI != null;
		assert dynamicComponentCreationInboundPortURI != null;
		
		this.computers = computers;
		this.computerMonitors = computerMonitors ;
		this.computersURI = computersURI;
		
		max_ressources = nbComputers;
		this.uri = uri;
		
		managementInboundPort = new AdmissionControlerManagementInboundPort(AdmissionControlerManagementInboundURI, this);
		addPort(managementInboundPort);
		managementInboundPort.publishPort();
		
		addOfferedInterface(RequestSubmissionI.class);
		reqSubInPort = new RequestAdmissionSubmissionInboundPort(RequestAdmissionSubmissionInboundPortURI, this);
		addPort(reqSubInPort);
		reqSubInPort.publishPort();
		
		addRequiredInterface(DynamicComponentCreationI.class);
		this.dynamicComponentCreationInboundPortURI = dynamicComponentCreationInboundPortURI;
		dynamicComponentCreationOutboundPort = new DynamicComponentCreationOutboundPort(this);
		addPort(dynamicComponentCreationOutboundPort);
		dynamicComponentCreationOutboundPort.publishPort();
		
		
		
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
				this.dynamicComponentCreationOutboundPort.unpublishPort() ;
			} catch (Exception e) {
				throw new ComponentShutdownException("Error when shutdown admission controler");
			}
			

		super.shutdown();
	}

	@Override
	public String getSubmissionInboundPortURI(RequestAdmissionI requestAdmission) throws Exception {
		
		/*
		 * Si on a encore des ressources libres
		 */
		if(ressources_libres !=max_ressources) {
			
			String rd_uri = "dispatcher_"+id;
			String distribInPortURI = "dispatcher_management_inbound_port_URI_"+id;
			String requestSubmissionInboundPortURI = "dispatcher_submission_inboud_port_URI_"+id;
			String requestNotificationInboundPortURI = requestAdmission.getRequestNotificationPortURI();
		
			
			String vmURI = "appli_vm_"+id;
			String appliInPortURI = "appli_vm_management_inbound_port_URI_"+id;
			String requestSubmissionInboundPortURIVM = "request_sub_inbound_port_uri_"+id;
			String requestNotificationInboundPortURIVM = "request_notif_inbound_port_uri_"+id;
			
			String computerOutPortURI = computersURI.get(ressources_libres);
			
			
			//On fournit au generateur l'uri du port de submission de requete du dispatcher 
			requestAdmission.setRequestSubmissionPortURI(requestSubmissionInboundPortURI);
			

			/*
			 * On cr�e le dispatcher via le dynamicComponentCreator
			 */
			Object[] argumentsDispatcher = {rd_uri,
					distribInPortURI, 
					requestSubmissionInboundPortURI, 
					requestNotificationInboundPortURI, 
					requestSubmissionInboundPortURIVM,
					requestNotificationInboundPortURIVM};

			dynamicComponentCreationOutboundPort.createComponent(RequestDispatcher.class.getCanonicalName(),
					argumentsDispatcher);		
			
			/*
			 * On cr�e l'application VM via le dynamicComponentCreator
			 */
			Object[] argumentsAppVM = {vmURI, 
								appliInPortURI,
								requestSubmissionInboundPortURIVM, 
								requestNotificationInboundPortURIVM};
			
			dynamicComponentCreationOutboundPort.createComponent(ApplicationVM.class.getCanonicalName(),
					argumentsAppVM);		

				

			/*
			 * On cr�e l'integrateur qui va g�rer la g�n�ration de requete
			 *  via le dynamicComponentCreator :
			 * on r�cup�re l'uri du port de management du g�n�rateur de requ�te dans l'objet
			 * requete d'admission, fourni en argument de la methode
			 */
			
			Object[] argumentsIntegrator = {requestAdmission.getRequestGeneratorManagementInboundPortURI(),
					appliInPortURI,
					computerOutPortURI };
			
			dynamicComponentCreationOutboundPort.createComponent(IntegratorForRequestGeneration.class.getCanonicalName(),
					argumentsIntegrator);

			dynamicComponentCreationOutboundPort.startComponents();
			
			
			/*
			 * On retourne l'uri du port de soumission de requetes
			 */
			
			return requestSubmissionInboundPortURI;
		}
		
		/*
		 * Sinon, si on n'a pas les ressources n�cessaires pour satisfaire 
		 * les besoins du g�n�rateur de requ�tes, on renvoie null
		 */
		return null;
	}


}
