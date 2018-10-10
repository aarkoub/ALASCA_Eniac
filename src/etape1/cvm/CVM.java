package etape1.cvm;

import etape1.requestdistributor.RequestDistributor;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;
import fr.sorbonne_u.datacenter.software.connectors.RequestSubmissionConnector;
import fr.sorbonne_u.datacenterclient.requestgenerator.RequestGenerator;
import fr.sorbonne_u.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;

public class CVM extends AbstractCVM {
	
	/** URI of the repartiteur outbound port (simplifies the connection).	*/
	protected static final String	RepartiteurOutboundPortURI = "oport" ;
	/** URI of the generateur inbound port (simplifies the connection).		*/
	protected static final String	GenerateurInboundPortURI = "iport" ;
	
	protected static final String ApplicationOutboundPortURI = "appli_oport";
	
	protected static final String RepartiteurInboundPortURI = "rep_iport";
	
	
	protected static final String URIRepartiteur = "uri-repartiteur";
	protected static final String URIGenerateur = "uri-generateur";
	protected static final String URIApplicationVM = "uri-applicationvm";
	
	protected static final String URIInboundPortConnectRequestProcess = "uri-connection";
	protected static final String URIInboundPortReceiveRequestNotification = "uri-notification";
	
	protected static final String applicationVMManagementInboundPortURI = "iport_application";
	protected static final String requestSubmissionInboundPortURI = "iport_submission_request";
	protected static final String requestNotificationInboundPortURI = "iport_notification_requset";
	
	protected RequestDistributor rep ;
	protected RequestGenerator genReq ;
	protected ApplicationVM appliVM ;
	
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
		
		rep = new RequestDistributor(URIRepartiteur, RepartiteurOutboundPortURI);
		genReq = new RequestGenerator(URIGenerateur, 500, 10, GenerateurInboundPortURI, URIInboundPortConnectRequestProcess, URIInboundPortReceiveRequestNotification);
		appliVM = new ApplicationVM(URIApplicationVM, applicationVMManagementInboundPortURI, requestSubmissionInboundPortURI, requestNotificationInboundPortURI);
		
		rep.toggleTracing();
		rep.toggleLogging();
		
		deployedComponents.add(rep);
		
		genReq.toggleTracing();
		genReq.toggleLogging();
		
		deployedComponents.add(genReq);
		
		appliVM.toggleTracing();
		appliVM.toggleLogging();
		
		deployedComponents.add(appliVM);
		
		this.rep.doPortConnection(
				RepartiteurOutboundPortURI,
				GenerateurInboundPortURI,
				RequestGeneratorManagementConnector.class.getCanonicalName()) ;
		
		this.appliVM.doPortConnection(ApplicationOutboundPortURI, RepartiteurInboundPortURI, RequestSubmissionConnector.class.getCanonicalName());
		
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
