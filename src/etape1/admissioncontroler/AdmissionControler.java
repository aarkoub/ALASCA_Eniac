package etape1.admissioncontroler;

import java.util.ArrayList;
import java.util.List;

import etape1.admissioncontroler.interfaces.AdmissionControlerManagementI;
import etape1.admissioncontroler.interfaces.RequestAdmissionI;
import etape1.admissioncontroler.interfaces.RequestAdmissionSubmissionHandlerI;
import etape1.admissioncontroler.ports.AdmissionControlerManagementInboundPort;
import etape1.admissioncontroler.ports.RequestAdmissionSubmissionInboundPort;
import etape1.cvm.CVM4DynamicPurpose;
import etape1.requestdispatcher.RequestDispatcher;
import etape1.requestdistributor.ports.RequestDistributorManagementOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.sorbonne_u.datacenter.hardware.tests.ComputerMonitor;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
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
	
	
	public AdmissionControler(String uri, 
			int nbComputers,
			String AdmissionControlerManagementInboundURI,
			String RequestAdmissionSubmissionInboundPortURI,
			List<Computer> computers,
			List<ComputerMonitor> computerMonitors,
			List<String> computersURI) throws Exception{
		
		super(1,1);
		
		assert nbComputers > 0;
		assert uri != null;
		assert AdmissionControlerManagementInboundURI != null;
		assert RequestAdmissionSubmissionInboundPortURI != null;
		
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
		
		
	}
	
	@Override
	public void start(){
		
	}

	@Override
	public String getSubmissionInboundPortURI(RequestAdmissionI requestAdmission) throws Exception {
		
		if(ressources_libres !=max_ressources) {
			
			RequestDistributorManagementOutboundPort distribOutboundPort;
			ApplicationVMManagementOutboundPort appliOutboundPort;
			ComputerServicesOutboundPort computerOutboundPort;
			
			String rd_uri = "dispatcher_"+id;
			String distribInPortURI = "dispatcher_management_inbound_port_URI_"+id;
			String requestSubmissionInboundPortURI = "dispatcher_submission_inboud_port_URI_"+id;
			String requestNotificationInboundPortURI = requestAdmission.getRequestNotificationPortURI();
			String distribOutPortURI = "dispatcher_management_outbound_port_URI_"+id;
			
			String vmURI = "appli_vm_"+id;
			String appliInPortURI = "appli_vm_management_inbound_port_URI_"+id;
			String requestSubmissionInboundPortURIVM = "request_sub_inbound_port_uri_"+id;
			String requestNotificationInboundPortURIVM = "request_notif_inbound_port_uri_"+id;
			String appliOutPortURI = "appli_vm_management_outbound_port_URI_"+id;
	
			
			
			ApplicationVM appliVM = new ApplicationVM(vmURI, 
					appliInPortURI,
					requestSubmissionInboundPortURIVM, 
					requestNotificationInboundPortURIVM);
			
			RequestDispatcher dispatcher = new RequestDispatcher(rd_uri,
					distribInPortURI, 
					requestSubmissionInboundPortURI, 
					requestNotificationInboundPortURI, 
					requestSubmissionInboundPortURIVM,
					requestNotificationInboundPortURIVM);
			
			
			used.add(dispatcher);
			usedVM.add(appliVM);
			
			requestAdmission.setRequestSubmissionPortURI(requestSubmissionInboundPortURI);
			
			String ComputerServicesInboundPortURI = "computer_services_outbound_"+ressources_libres;
			
			CVM4DynamicPurpose cvm = new CVM4DynamicPurpose(dispatcher, 
					appliVM, computers.get(ressources_libres), computerMonitors.get(ressources_libres),
					distribInPortURI, appliInPortURI, computersURI.get(ressources_libres));
			
			cvm.deploy();
			cvm.getIntegrator().start();
			cvm.getIntegrator().execute();
			
			AbstractCVM currentCVM = AbstractCVM.getCVM();
			
			currentCVM.addDeployedComponent(computers.get(ressources_libres));
			currentCVM.addDeployedComponent(computerMonitors.get(ressources_libres));
			currentCVM.addDeployedComponent(appliVM);
			currentCVM.addDeployedComponent(dispatcher);
			
			computers.get(ressources_libres).start();
			computerMonitors.get(ressources_libres).start();
			appliVM.start();
			dispatcher.start();
			

			return requestSubmissionInboundPortURI;
		}
		return null;
	}

	/*@Override
	public void acceptRequestAdmissionSubmission(RequestAdmissionI requestai) throws Exception {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void acceptRequestAdmissionSubmissionAndNotify(RequestAdmissionI requestai) throws Exception {
		// TODO Auto-generated method stub
		
	}*/

	
}
