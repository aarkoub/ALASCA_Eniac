package components;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.exceptions.PreconditionException;
import ports.RepartiteurOutboundPort;

public class Repartiteur extends AbstractComponent {
	
	
	protected RepartiteurOutboundPort uriOutboundPort;

	public Repartiteur(int nbThreads, int nbSchedulableThreads) {
		super(nbThreads, nbSchedulableThreads);
		// TODO Auto-generated constructor stub
	}
	
	public Repartiteur(String reflectionInboundPortURI,int nbThreads,int nbSchedulableThreads){
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
	}
	
	public Repartiteur(String uri, String outboundPort) throws Exception {
		super(uri, 0,1);
		
		assert	uri != null :
			new PreconditionException("uri can't be null!") ;
		
		assert	outboundPort != null :
			new PreconditionException("port can't be null!") ;
		
		uriOutboundPort = new RepartiteurOutboundPort(uri, this);
		
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
	}

}