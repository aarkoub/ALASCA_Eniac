package etape1.requestdistributor;

import etape1.requestdistributor.interfaces.RequestDistributorManagementI;
import etape1.requestdistributor.ports.RequestDistributorManagementInboundPort;
import etape1.requestdistributor.ports.RequestDistributorManagementOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.hardware.processors.ports.ProcessorServicesOutboundPort;
import fr.sorbonne_u.datacenter.software.connectors.RequestNotificationConnector;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionInboundPort;




public class RequestDistributor extends AbstractComponent implements RequestDistributorManagementI, RequestSubmissionHandlerI {

	private String rd_uri;
	private RequestDistributorManagementInboundPort managementInboundPort;
	private String requestNotificationInboundPortURI;
	private RequestSubmissionInboundPort requestSubmissionInboundPort;
	private RequestNotificationOutboundPort requestNotificationOutboundPort;
	
	
	
	public RequestDistributor(String rd_uri, String managementInboundPortURI,
			String requestSubmissionInboundPortURI,
			String requestNotificationInboundPortURI) throws Exception {
		

		super(1,1);
		
		assert rd_uri != null;
		assert	managementInboundPortURI != null ;
		assert	requestSubmissionInboundPortURI != null ;
		assert	requestNotificationInboundPortURI != null ;

		
		this.rd_uri = rd_uri;
		
		this.requestNotificationInboundPortURI =
				requestNotificationInboundPortURI ;
		
		addOfferedInterface(RequestDistributorManagementI.class);
		
		
		managementInboundPort = new RequestDistributorManagementInboundPort(managementInboundPortURI, this);
		addPort(managementInboundPort);
		managementInboundPort.publishPort();
		
		addOfferedInterface(RequestSubmissionI.class);
		requestSubmissionInboundPort = new RequestSubmissionInboundPort(requestSubmissionInboundPortURI, this);
		addPort(requestSubmissionInboundPort);
		requestSubmissionInboundPort.publishPort();
		
		addRequiredInterface(RequestNotificationI.class);
		requestNotificationOutboundPort = new RequestNotificationOutboundPort(this);
		addPort(requestNotificationOutboundPort);
		requestNotificationOutboundPort.publishPort();
		

		
		
		 
	}
	
	@Override
	public void start() throws ComponentStartException {
		
		super.start();
		
		try {
			doPortConnection(requestNotificationOutboundPort.getPortURI(), requestNotificationInboundPortURI,
					RequestNotificationConnector.class.getCanonicalName());
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}
	
	
	@Override
	public void			finalise() throws Exception
	{
		this.doPortDisconnection(
							this.requestNotificationOutboundPort.getPortURI()) ;
		
		super.finalise() ;
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException {
	
		try {
			requestSubmissionInboundPort.unpublishPort();
			managementInboundPort.unpublishPort();
			requestNotificationOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(
					"processor services outbound port disconnection"
					+ " error", e) ;
		}
		
		
		super.shutdown();
		
		
		
	}


	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {
		System.out.println("Requete recue : "+r.getRequestURI());
		
	}

	@Override
	public void acceptRequestSubmissionAndNotify(RequestI r) throws Exception {
		// TODO Auto-generated method stub
		
	}
	

}