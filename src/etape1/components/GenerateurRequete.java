package etape1.components;

import java.util.Random;

import etape1.ports.GenerateurInboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.PreconditionException;
import fr.sorbonne_u.components.ports.PortI;

public class GenerateurRequete extends AbstractComponent {
	
	protected String[] requetes  = {"req1", "req2", "req3" } ;
	Random r = new Random();

	public GenerateurRequete(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads) {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
		// TODO Auto-generated constructor stub
	}
	
	public GenerateurRequete(int nbThreads, int nbSchedulableThreads) {
		super(nbThreads, nbSchedulableThreads);
		// TODO Auto-generated constructor stub
	}
	
	public GenerateurRequete(String uri, String portUri) throws Exception {
		super(uri, 1, 0);
		assert	uri != null :
			new PreconditionException("uri can't be null!") ;
		
		assert	portUri != null :
			new PreconditionException("port can't be null!") ;
		
		PortI p = new GenerateurInboundPort(portUri, this) ;
		p.publishPort() ;
		
		if (AbstractCVM.isDistributed) {
			this.executionLog.setDirectory(System.getProperty("user.dir")) ;
		} else {
			this.executionLog.setDirectory(System.getProperty("user.home")) ;
		}
		this.tracer.setTitle("Generateur de requetes") ;
		this.tracer.setRelativePosition(1, 0) ;
		
	}

	public String genererRequeteService() {

		String s = requetes[r.nextInt(3)];
		
		this.logMessage("Genère la requete "+ s) ;
		return s;
	}

}
