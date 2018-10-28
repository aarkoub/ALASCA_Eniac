package etape1.cvm;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import etape1.requestdispatcher.RequestDispatcher;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.processors.Processor;
import fr.sorbonne_u.datacenter.hardware.tests.ComputerMonitor;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;
import fr.sorbonne_u.datacenterclient.requestgenerator.RequestGenerator;
import fr.sorbonne_u.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;


public class CVM extends AbstractCVM {
	
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
	protected static final String RequestNotificationInboundPortURI_2 = "req_not_2";
	protected static final String RequestSubmissionInboundPortURI_2 = "req_sub_2";
	
	protected static final  String ComputerDynamicStateDataInboundPortURI = "computerDynamic_inport_uri";
	protected static final  String ComputerStaticStateDataInboundPortURI = "computerStatic_inport_uri";
	
	protected static final  String ComputerServicesInboundPortURI = "computer_in_port";
	
	protected static final String requestSubmissionInboundPortURI = "request_sub_inbound_port";
	protected static final String requestNotificationInboundPortURI = "request_notification_inbound_port";
	
	public CVM(boolean isDistributed) throws Exception {
		super(isDistributed);
		// TODO Auto-generated constructor stub
	}
	
	public CVM() throws Exception {
		super();
	}
	
	@Override
	public void deploy() throws Exception{
		
		Processor.DEBUG = true ;
		
		assert	!this.deploymentDone() ;
		
		// --------------------------------------------------------------------
				// Create and deploy a computer component with its 2 processors and
				// each with 2 cores.
				// --------------------------------------------------------------------
				String computerURI = "computer0" ;
				int numberOfProcessors = 2 ;
				int numberOfCores = 2 ;
				Set<Integer> admissibleFrequencies = new HashSet<Integer>() ;
				admissibleFrequencies.add(1500) ;	// Cores can run at 1,5 GHz
				admissibleFrequencies.add(3000) ;	// and at 3 GHz
				Map<Integer,Integer> processingPower = new HashMap<Integer,Integer>() ;
				processingPower.put(1500, 1500000) ;	// 1,5 GHz executes 1,5 Mips
				processingPower.put(3000, 3000000) ;	// 3 GHz executes 3 Mips
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
									ComputerDynamicStateDataInboundPortURI) ;
				this.addDeployedComponent(c) ;
				c.toggleLogging() ;
				c.toggleTracing() ;
				// --------------------------------------------------------------------

				// --------------------------------------------------------------------
				// Create the computer monitor component and connect its to ports
				// with the computer component.
				// --------------------------------------------------------------------
				this.computerMonitor = new ComputerMonitor(computerURI,
											 true,
											 ComputerStaticStateDataInboundPortURI,
											 ComputerDynamicStateDataInboundPortURI) ;
				this.addDeployedComponent(this.computerMonitor) ;
				// --------------------------------------------------------------------

		
		requestGenerator = new RequestGenerator(URI_RequestGenerator, 500, 50, RequestGeneratorManagementInboundPortURI, requestSubmissionInboundPortURI, requestNotificationInboundPortURI);
		requestDisbributor = new RequestDispatcher(URI_RequestDistributor, RequestDistributorManagementInboundPortURI, requestSubmissionInboundPortURI,
								requestNotificationInboundPortURI,
								RequestSubmissionInboundPortURI_2, RequestNotificationInboundPortURI_2);
		
		applicationVM = new ApplicationVM(URI_ApplicationVM, ApplicationVMManagementInboundPortURI, RequestSubmissionInboundPortURI_2, RequestNotificationInboundPortURI_2);
		
		
		requestDisbributor.toggleTracing();
		requestDisbributor.toggleLogging();
		
		addDeployedComponent(requestDisbributor);
		
		requestGenerator.toggleTracing();
		requestGenerator.toggleLogging();
		
		addDeployedComponent(requestGenerator);
		
		applicationVM.toggleTracing();
		applicationVM.toggleLogging();
		
		addDeployedComponent(applicationVM);
		
		
		/*this.applicationVM.doPortConnection(ApplicationVMManagementOutboundPortURI,
				RequestDistributorManagementInboundPortURI,
				RequestSubmissionConnector.class.getCanonicalName());*/
		
		this.integrator = new Integrator(RequestDistributorManagementInboundPortURI,
				RequestGeneratorManagementInboundPortURI,
				ApplicationVMManagementInboundPortURI,
				ComputerServicesInboundPortURI ) ;
		addDeployedComponent(this.integrator) ;
		
		
		super.deploy();
		
		assert this.deploymentDone();
		
		
		
	}
	
	
	public static void		main(String[] args)
	{
		try {
			// Create an instance of the defined component virtual machine.
			CVM a = new CVM() ;
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
