package etape1.requestdistributor;

import java.util.concurrent.TimeUnit;

import etape1.requestdistributor.ports.RequestDistributorManagementOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.exceptions.PreconditionException;
import fr.sorbonne_u.datacenterclient.requestgenerator.Request;

public class RequestDistributor extends AbstractComponent {
	
	
	protected RequestDistributorManagementOutboundPort uriOutboundPort;
	protected int counter = 0;
	protected Request request;

	public RequestDistributor(int nbThreads, int nbSchedulableThreads) {
		super(nbThreads, nbSchedulableThreads);
		// TODO Auto-generated constructor stub
	}
	
	public RequestDistributor(String reflectionInboundPortURI,int nbThreads,int nbSchedulableThreads){
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
	}
	
	public RequestDistributor(String uri, String outboundPort) throws Exception {
		super(uri, 0,1);
		
		assert	uri != null :
			new PreconditionException("uri can't be null!") ;
		
		assert	outboundPort != null :
			new PreconditionException("port can't be null!") ;
		
		uriOutboundPort = new RequestDistributorManagementOutboundPort(outboundPort, this);
		
		addPort(uriOutboundPort);
		
		uriOutboundPort.localPublishPort();
		
		if (AbstractCVM.isDistributed) {
			this.executionLog.setDirectory(System.getProperty("user.dir")) ;
		} else {
			this.executionLog.setDirectory(System.getProperty("user.home")) ;
		}
		this.tracer.setTitle("repartiteur") ;
		this.tracer.setRelativePosition(1, 1) ;
		
	}

	
	@Override
	public void start() throws ComponentStartException{
		super.start();
		
		
		scheduleTask(new AbstractComponent.AbstractTask() {
			
			@Override
			public void run() {
				try {
					((RequestDistributor) this.getOwner()).getRequestAndPrint();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}, 1000, TimeUnit.MILLISECONDS);
		
	}

	public void getRequestAndPrint() throws Exception {
		
		if(counter++<10){
		
			request = uriOutboundPort.getRequest();
			
			logMessage("Requete recue : "+request.getRequestURI());
			
			scheduleTask(new AbstractComponent.AbstractTask() {
				
				@Override
				public void run() {
					try {
						((RequestDistributor) this.getOwner()).getRequestAndPrint();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}, 1000, TimeUnit.MILLISECONDS);
			
		}
		
	}
	
	public Request getRequest(){
		return request;
	}

}