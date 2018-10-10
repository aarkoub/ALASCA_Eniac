package etape1.cvm;

import etape1.requestdistributor.RequestDistributor;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;
import fr.sorbonne_u.datacenter.software.connectors.RequestSubmissionConnector;
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
	protected static final String RequestNotificationInboundPortURI = "iport_notification_requset";
	
	protected RequestDistributor requestDisbributor ;
	protected RequestGenerator requestGenerator ;
	protected ApplicationVM applicationVM ;
	
	public CVM(boolean isDistributed) throws Exception {
		super(isDistributed);
		// TODO Auto-generated constructor stub
	}
	
	public CVM() throws Exception {
		super();
	}
	
	@Override
	public void deploy() throws Exception{
		
		assert	!this.deploymentDone() ;
		
		requestDisbributor = new RequestDistributor(URI_RequestDistributor, RequestDistributorManagementOutboundPortURI);
		requestGenerator = new RequestGenerator(URI_RequestGenerator, 500, 10, RequestGeneratorManagementInboundPortURI, URIInboundPortConnectRequestProcess, URIInboundPortReceiveRequestNotification);
		applicationVM = new ApplicationVM(URI_ApplicationVM, ApplicationVMManagementInboundPortURI, RequestSubmissionInboundPortURI, RequestNotificationInboundPortURI);
		
		requestDisbributor.toggleTracing();
		requestDisbributor.toggleLogging();
		
		deployedComponents.add(requestDisbributor);
		
		requestGenerator.toggleTracing();
		requestGenerator.toggleLogging();
		
		deployedComponents.add(requestGenerator);
		
		/*applicationVM.toggleTracing();
		applicationVM.toggleLogging();
		
		deployedComponents.add(applicationVM);*/
		
		this.requestDisbributor.doPortConnection(
				RequestDistributorManagementOutboundPortURI,
				RequestGeneratorManagementInboundPortURI,
				RequestGeneratorManagementConnector.class.getCanonicalName()) ;
		
		/*this.applicationVM.doPortConnection(ApplicationVMManagementOutboundPortURI,
				RequestDistributorManagementInboundPortURI,
				RequestSubmissionConnector.class.getCanonicalName());*/
		
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
