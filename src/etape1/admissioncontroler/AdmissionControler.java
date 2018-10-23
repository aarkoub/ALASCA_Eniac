package etape1.admissioncontroler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import etape1.admissioncontroler.interfaces.AdmissionControlerManagementI;
import etape1.admissioncontroler.interfaces.RequestAdmissionI;
import etape1.admissioncontroler.interfaces.RequestAdmissionSubmissionHandlerI;
import etape1.admissioncontroler.ports.RequestAdmissionSubmissionInboundPort;
import etape1.cvm.CVM2;
import etape1.requestdispatcher.RequestDispatcher;
import etape1.requestdistributor.connectors.RequestDistributorManagementConnector;
import etape1.requestdistributor.ports.RequestDistributorManagementOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.sorbonne_u.datacenter.hardware.tests.ComputerMonitor;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;
import fr.sorbonne_u.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI;

public class AdmissionControler extends AbstractComponent implements AdmissionControlerManagementI, 
RequestAdmissionSubmissionHandlerI{
	
	String uri;
	int ressources_libres = 0;
	int max_ressources ;
	
	List<RequestDispatcher> available = new ArrayList<>();
	List<RequestDispatcher> used = new ArrayList<>();
	List<String> computersURI = new ArrayList<>();
	List<Computer> computers = new ArrayList<>();
	CVM2 cvm ;
	
	RequestAdmissionSubmissionInboundPort reqSubInPort;
	
	private int id=0;
	
	
	public AdmissionControler(String uri, 
			int nbComputers,
			String AdmissionControlerManagementInboundURI,
			String RequestAdmissionSubmissionInboundPortURI,
			CVM2 cvm) throws Exception{
		
		super(1,1);
		
		assert nbComputers > 0;
		assert uri != null;
		assert AdmissionControlerManagementInboundURI != null;
		assert RequestAdmissionSubmissionInboundPortURI != null;
		assert cvm != null;
		
		this.cvm = cvm;
		
		max_ressources = nbComputers;
		this.uri = uri;
		
		addOfferedInterface(RequestSubmissionI.class);
		reqSubInPort = new RequestAdmissionSubmissionInboundPort(RequestAdmissionSubmissionInboundPortURI, this);
		addPort(reqSubInPort);
		reqSubInPort.publishPort();
		
		
	}
	



	/*@Override
	public void acceptRequestAdmissionSubmission(RequestAdmissionI requestai) throws Exception {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void acceptRequestAdmissionSubmissionAndNotify(RequestAdmissionI requestai) throws Exception {
		// TODO Auto-generated method stub
		
	}*/
	
	@Override
	public void start(){
		initiateComputer();
	}


	private void initiateComputer() {
		
		
		for(int i=0; i<max_ressources; i++) {
			
			String ComputerDynamicStateDataInboundPortURI = "computerDynamic_inport_uri_"+i;
			String ComputerStaticStateDataInboundPortURI = "computerStatic_inport_uri_"+i;
			String ComputerServicesInboundPortURI = "computer_in_port_"+i;
			
			
			String computerURI = "computer_"+i ;
			int numberOfProcessors = 2 ;
			int numberOfCores = 2 ;
			Set<Integer> admissibleFrequencies = new HashSet<Integer>() ;
			admissibleFrequencies.add(1500) ;	// Cores can run at 1,5 GHz
			admissibleFrequencies.add(3000) ;	// and at 3 GHz
			Map<Integer,Integer> processingPower = new HashMap<Integer,Integer>() ;
			processingPower.put(1500, 1500000) ;	// 1,5 GHz executes 1,5 Mips
			processingPower.put(3000, 3000000) ;	// 3 GHz executes 3 Mips
			
			
			try {
				Computer c = new Computer(
									computerURI,
									admissibleFrequencies,
									processingPower,  
									1500,		// Test scenario 1, frequency = 1,5 GHz
									// 3000,	// Test scenario 2, frequency = 3 GHz
									1500,		// max frequency gap within a processor
									numberOfProcessors,
									numberOfCores,
									ComputerServicesInboundPortURI,
									ComputerStaticStateDataInboundPortURI,
									ComputerDynamicStateDataInboundPortURI);
				ComputerMonitor computerMonitor = new ComputerMonitor(computerURI,
						 true,
						 ComputerStaticStateDataInboundPortURI,
						 ComputerDynamicStateDataInboundPortURI) ;
				
				cvm.addDeployedComponent(computerMonitor);
				/*c.toggleLogging() ;
				c.toggleTracing() ;*/

				computersURI.add(ComputerServicesInboundPortURI);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		
		
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
			
			String vmURI = "appli_vm_"+id;
			String appliInPortURI = "appli_vm_management_inbound_port_URI_"+id;
			String requestSubmissionInboundPortURIVM = "request_sub_inbound_port_uri_"+id;
			String requestNotificationInboundPortURIVM = "request_notif_inbound_port_uri_"+id;
			
			

			distribOutboundPort = new RequestDistributorManagementOutboundPort(this);
			addPort(distribOutboundPort);
			distribOutboundPort.publishPort();
			
			/*appliOutboundPort = new ApplicationVMManagementOutboundPort(this);
			addPort(appliOutboundPort);
			appliOutboundPort.publishPort();
			
			computerOutboundPort = new ComputerServicesOutboundPort(this);
			addPort(computerOutboundPort);
			computerOutboundPort.publishPort();
			
			String computerInPort = computersURI.get(ressources_libres);
			
			
			
			AllocatedCore[] ac = computerOutboundPort.allocateCores(4) ;
			appliOutboundPort.allocateCores(ac) ;
			
			
			ApplicationVM appliVM = new ApplicationVM(vmURI, 
					appliInPortURI,
					requestSubmissionInboundPortURIVM, 
					requestNotificationInboundPortURIVM);*/
			
			RequestDispatcher dispatcher = new RequestDispatcher(rd_uri,
					distribInPortURI, 
					requestSubmissionInboundPortURI, 
					requestNotificationInboundPortURI, 
					requestSubmissionInboundPortURIVM,
					requestNotificationInboundPortURIVM);
			
			//cvm.addDeployedComponent(appliVM);
			cvm.addDeployedComponent(dispatcher);
			
			doPortConnection(distribOutboundPort.getPortURI(), distribInPortURI, RequestDistributorManagementConnector.class.getCanonicalName()) ;
			//doPortConnection(computerOutboundPort.getPortURI(), computerInPort, ComputerServicesConnector.class.getCanonicalName());
			//doPortConnection(appliOutboundPort.getPortURI(), appliInPortURI, ApplicationVMManagementConnector.class.getCanonicalName());
			
			used.add(dispatcher);
			
			requestAdmission.setRequestSubmissionPortURI(requestSubmissionInboundPortURI);
			
			System.out.println(requestAdmission.getRequestSubmissionPortURI());

			return requestSubmissionInboundPortURI;
		}
		return null;
	}



	
}
