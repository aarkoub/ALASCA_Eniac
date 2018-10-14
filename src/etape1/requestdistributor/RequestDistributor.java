package etape1.requestdistributor;

import etape1.requestdistributor.interfaces.RequestDistributorManagementI;
import etape1.requestdistributor.ports.RequestDistributorManagementInboundPort;
import etape1.requestdistributor.ports.RequestDistributorManagementOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.hardware.processors.ports.ProcessorServicesOutboundPort;
import fr.sorbonne_u.datacenter.software.connectors.RequestNotificationConnector;
import fr.sorbonne_u.datacenter.software.connectors.RequestSubmissionConnector;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionOutboundPort;




public class RequestDistributor extends AbstractComponent implements RequestDistributorManagementI,
RequestSubmissionHandlerI,
RequestNotificationHandlerI{

	private String rd_uri;
	private RequestDistributorManagementInboundPort managementInboundPort;
	private String requestNotificationInboundPortURI;
	
	
	private RequestSubmissionInboundPort requestSubmissionInboundPort;
	private RequestNotificationOutboundPort requestNotificationOutboundPort;
	
	private RequestSubmissionOutboundPort requestSubmissionOutboundPort;
	private RequestNotificationInboundPort requestNotificationInboundPort;
	private String requestSubmissionInboundPortURI_2;
	
	
	
	public RequestDistributor(String rd_uri, String managementInboundPortURI,
			String requestSubmissionInboundPortURI,
			String requestNotificationInboundPortURI,
			String requestSubmissionInboundPortURI_2,
			String requestNotificationInboundPortURI_2) throws Exception {
		

		super(1,1);
		
		assert rd_uri != null;
		assert	managementInboundPortURI != null ;
		assert	requestSubmissionInboundPortURI != null ;
		assert	requestNotificationInboundPortURI != null ;
		assert requestSubmissionInboundPortURI_2 != null;
		assert requestNotificationInboundPortURI_2 != null;

		
		this.rd_uri = rd_uri;
		
		this.requestNotificationInboundPortURI =
				requestNotificationInboundPortURI ;
		
		this.requestSubmissionInboundPortURI_2 = requestSubmissionInboundPortURI_2;
		
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
		

		addOfferedInterface(RequestNotificationI.class);
		requestNotificationInboundPort = new RequestNotificationInboundPort(requestNotificationInboundPortURI_2, this);
		addPort(requestNotificationInboundPort);
		requestNotificationInboundPort.publishPort();
		
		addRequiredInterface(RequestSubmissionI.class);
		requestSubmissionOutboundPort = new RequestSubmissionOutboundPort(this);
		addPort(requestSubmissionOutboundPort);
		requestSubmissionOutboundPort.publishPort();
		
		 
	}
	
	@Override
	public void start() throws ComponentStartException {
		
		super.start();
		
		try {
			doPortConnection(requestNotificationOutboundPort.getPortURI(), requestNotificationInboundPortURI,
					RequestNotificationConnector.class.getCanonicalName());
			
			doPortConnection(requestSubmissionOutboundPort.getPortURI(),
					requestSubmissionInboundPortURI_2,
					RequestSubmissionConnector.class.getCanonicalName());
			
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
	}
	
	
	@Override
	public void			finalise() throws Exception
	{
		this.doPortDisconnection(
							this.requestNotificationOutboundPort.getPortURI()) ;
		
		doPortDisconnection(requestSubmissionOutboundPort.getPortURI());
		
		super.finalise() ;
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException {
	
		try {
			requestSubmissionInboundPort.unpublishPort();
			managementInboundPort.unpublishPort();
			requestNotificationOutboundPort.unpublishPort();
			requestSubmissionOutboundPort.unpublishPort();
			requestNotificationInboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(
					"processor services outbound port disconnection"
					+ " error", e) ;
		}
		
		
		super.shutdown();
		
		
		
	}


	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {
		//logMessage("Requete recue : "+r.getRequestURI());
		//requestSubmissionOutboundPort.submitRequest(r);
		
	}

	@Override
	public void acceptRequestSubmissionAndNotify(RequestI r) throws Exception {
		logMessage("Requete recue : "+r.getRequestURI());
		requestSubmissionOutboundPort.submitRequestAndNotify(r);
		
	}

	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {

		logMessage("Requete finie : "+r.getRequestURI());
		requestNotificationOutboundPort.notifyRequestTermination(r);
	}
	

}