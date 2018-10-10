package etape1.requestdistributor;

import etape1.requestdistributor.interfaces.RequestDistributorManagementI;
import etape1.requestdistributor.ports.RequestDistributorManagementInboundPort;
import etape1.requestdistributor.ports.RequestDistributorManagementOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.exceptions.PreconditionException;
import fr.sorbonne_u.datacenter.software.connectors.RequestSubmissionConnector;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionOutboundPort;

public class RequestDistributor extends AbstractComponent implements RequestSubmissionHandlerI {
	
	
	protected RequestDistributorManagementOutboundPort uriOutboundPort;
	protected int counter = 0;
	private String requestSubmissionInboundPortURI;
	private RequestDistributorManagementInboundPort rgmip;
	private RequestSubmissionOutboundPort rsop;
	private RequestNotificationInboundPort rnip;
	private String uri;

	public RequestDistributor(int nbThreads, int nbSchedulableThreads) {
		super(nbThreads, nbSchedulableThreads);
		// TODO Auto-generated constructor stub
	}
	
	public RequestDistributor(String reflectionInboundPortURI,int nbThreads,int nbSchedulableThreads){
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
	}
	
	public RequestDistributor(String uri,
			String managementInboundPortURI,
			String requestSubmissionInboundPortURI,
			String requestNotificationInboundPortURI)
					throws Exception {
		super(uri, 0,1);
		
		assert	uri != null :
			new PreconditionException("uri can't be null!") ;
		
		assert	managementInboundPortURI != null ;
		assert	requestSubmissionInboundPortURI != null ;
		assert	requestNotificationInboundPortURI != null ;
		
		this.uri = uri;
		
		
		this.requestSubmissionInboundPortURI =
				requestSubmissionInboundPortURI ;

		this.addOfferedInterface(RequestDistributorManagementI.class) ;
		this.rgmip = new RequestDistributorManagementInboundPort(
						managementInboundPortURI, this) ;
		this.addPort(this.rgmip) ;
		this.rgmip.publishPort() ;
		
		this.addRequiredInterface(RequestSubmissionI.class) ;
		this.rsop = new RequestSubmissionOutboundPort(this) ;
		this.addPort(this.rsop) ;
		this.rsop.publishPort() ;
		
		this.addOfferedInterface(RequestNotificationI.class) ;
		this.rnip =
		new RequestNotificationInboundPort(
		requestNotificationInboundPortURI, this) ;
		this.addPort(this.rnip) ;
		this.rnip.publishPort() ;
		
		
		if (AbstractCVM.isDistributed) {
			this.executionLog.setDirectory(System.getProperty("user.dir")) ;
		} else {
			this.executionLog.setDirectory(System.getProperty("user.home")) ;
		}
		this.tracer.setTitle("Request distributor") ;
		this.tracer.setRelativePosition(1, 1) ;
		
	}

	
	@Override
	public void start() throws ComponentStartException{
		super.start() ;

		try {
			this.doPortConnection(
					this.rsop.getPortURI(),
					requestSubmissionInboundPortURI,
					RequestSubmissionConnector.class.getCanonicalName()) ;
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
		}
		
	}
	
	@Override
	public void			shutdown() throws ComponentShutdownException
	{

		try {
			this.rsop.unpublishPort() ;
			this.rnip.unpublishPort() ;
			this.rgmip.unpublishPort() ;
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}

		super.shutdown();
	}

	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {

				
			logMessage("Requete recue : "+r.getRequestURI());
		
	}

	@Override
	public void acceptRequestSubmissionAndNotify(RequestI r) throws Exception {
		// TODO Auto-generated method stub
		
	}

}