package etape1.cvm;

import etape1.components.GenerateurRequete;
import etape1.components.Repartiteur;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.examples.basic_cs.connectors.URIServiceConnector;

public class CVM extends AbstractCVM {
	
	/** URI of the repartiteur outbound port (simplifies the connection).	*/
	protected static final String	RepartiteurOutboundPortURI = "oport" ;
	/** URI of the generateur inbound port (simplifies the connection).		*/
	protected static final String	GenerateurInboundPortURI = "iport" ;
	
	protected static final String URIRepartiteur = "uri-repartiteur";
	protected static final String URIGenerateur = "uri-generateur";
	
	protected Repartiteur rep ;
	protected GenerateurRequete genReq ;
	
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
		
		rep = new Repartiteur(URIRepartiteur, RepartiteurOutboundPortURI);
		genReq = new GenerateurRequete(URIGenerateur, GenerateurInboundPortURI);
		
		rep.toggleTracing();
		rep.toggleLogging();
		
		deployedComponents.add(rep);
		
		genReq.toggleTracing();
		genReq.toggleLogging();
		
		deployedComponents.add(genReq);
		
		this.rep.doPortConnection(
				RepartiteurOutboundPortURI,
				GenerateurInboundPortURI,
				URIServiceConnector.class.getCanonicalName()) ;
		
		
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
