package etape1.cvm;


import etape1.admissioncontroler.AdmissionControler;
import etape1.requestGeneratorForAdmissionControler.RequestGenerator;
import etape1.requestdispatcher.RequestDispatcher;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.datacenter.hardware.processors.Processor;
import fr.sorbonne_u.datacenter.hardware.tests.ComputerMonitor;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;


public class CVM2 extends AbstractCVM {
	
	/** URI of the repartiteur outbound port (simplifies the connection).	*/
	protected static final String	RequestDistributorManagementOutboundPortURI = "requestDistributor_out_port" ;
	/** URI of the generateur inbound port (simplifies the connection).		*/
	protected static final String	RequestGeneratorManagementInboundPortURI = "requestGenerator_in_port" ;
	
	protected static final String ApplicationVMManagementOutboundPortURI = "applicationVM_out_port";
	
	protected static final String RequestDistributorManagementInboundPortURI = "requestDistributor_in_port";
	
	
	protected static final String URI_RequestDistributor = "uri_requestDistributor";
	protected static final String URI_RequestGenerator = "uri_requestGenerator";
	protected static final String URI_ApplicationVM = "uri_applicationVM";
	
	protected static final String URIInboundPortConnectRequestProcess = "uri-connection";
	protected static final String URIInboundPortReceiveRequestNotification = "uri-notification";
	
	protected static final String ApplicationVMManagementInboundPortURI = "applicationVM_in_port";
	protected static final String RequestSubmissionInboundPortURI = "iport_submission_request";
	protected static final String RequestNotificationInboundPortURI = "iport_notification_request";
	
	protected RequestDispatcher requestDisbributor ;
	protected RequestGenerator requestGenerator ;
	protected ApplicationVM applicationVM ;
	protected Integrator integrator;
	protected ComputerMonitor computerMonitor;
	protected AdmissionControler admissionControler;
	
	protected static final String RequestNotificationInboundPortURI_2 = "req_not_2";
	protected static final String RequestSubmissionInboundPortURI_2 = "req_sub_2";
	
	protected static final  String ComputerDynamicStateDataInboundPortURI = "computerDynamic_inport_uri";
	protected static final  String ComputerStaticStateDataInboundPortURI = "computerStatic_inport_uri";
	
	protected static final  String ComputerServicesInboundPortURI = "computer_in_port";
	
	protected static final String requestSubmissionInboundPortURI = "request_sub_inbound_port";
	protected static final String requestNotificationInboundPortURI = "request_notification_inbound_port";
	
	
	
	protected static final String admissionControlerURI = "admission_controler";
	protected static final String admissionControlerManagementInboundURI = "admission_controler_management_inbound_uri";
	protected static final String requestAdmissionSubmissionInboundPortURI = "request_admission_submission_inbound_port_uri";
	
	public CVM2(boolean isDistributed) throws Exception {
		super(isDistributed);
		// TODO Auto-generated constructor stub
	}
	
	public CVM2() throws Exception {
		super();
	}
	
	@Override
	public void deploy() throws Exception{
		
		Processor.DEBUG = true ;
		
		assert	!this.deploymentDone() ;
		
		
		admissionControler = new AdmissionControler(admissionControlerURI,
				2, 
				admissionControlerManagementInboundURI, 
				requestAdmissionSubmissionInboundPortURI, this);
		
		
		requestGenerator = new RequestGenerator(URI_RequestGenerator, 500, 10, 
				RequestGeneratorManagementInboundPortURI, requestSubmissionInboundPortURI,
				requestNotificationInboundPortURI, requestAdmissionSubmissionInboundPortURI);
		

		
		
		requestGenerator.toggleTracing();
		requestGenerator.toggleLogging();
		
		addDeployedComponent(requestGenerator);
		
		addDeployedComponent(admissionControler);
		
		Integrator2 integrator = new Integrator2(RequestGeneratorManagementInboundPortURI,
				admissionControlerManagementInboundURI);
		
		addDeployedComponent(this.integrator) ;
		
		
		
		super.deploy();
		
		assert this.deploymentDone();
		
		
		
	}
	
	
	public static void		main(String[] args)
	{
		try {
			// Create an instance of the defined component virtual machine.
			CVM2 a = new CVM2() ;
			// Execute the application.
			a.startStandardLifeCycle(15000L) ;
			// Give some time to see the traces (convenience).
			Thread.sleep(10000L) ;
			// Simplifies the termination (termination has yet to be treated
			// properly in BCM).
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
