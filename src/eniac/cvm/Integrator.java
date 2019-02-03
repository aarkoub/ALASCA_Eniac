package eniac.cvm;

import eniac.requestgenerator.connector.RequestGeneratorManagementConnector;
import eniac.requestgenerator.ports.RequestGeneratorManagementOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;

/**
 * Cette classe est un composant intégrateur permettant de démarrer le RequestGenerator.
 * Elle sert à faire la demande d'hébergement d'application et génère les requêtes.
 * Cette classe peut être modifier afin de changer les délais d'envois de requêtes.
 * 
 *
 */

public class Integrator extends AbstractComponent {
	
	/**
	 * Port permettant d'appliquer des contrôles sur le RequestGenerator
	 */
	protected RequestGeneratorManagementOutboundPort generatorOutboundPort;

	/**
	 * URI du port de contrôle du RequestGenerator
	 */
	private String generatorInPortURI; 

	
	
	
	public Integrator(String requestGeneratorManagementInboundPortURI) throws Exception {
		super(1,0);
		
		assert requestGeneratorManagementInboundPortURI != null;
		
		
		
		generatorInPortURI = requestGeneratorManagementInboundPortURI;

		
		
		generatorOutboundPort = new RequestGeneratorManagementOutboundPort(this);
		addPort(generatorOutboundPort);
		generatorOutboundPort.publishPort();
		

						
	}
	
	
	@Override
	public void start() throws ComponentStartException {
		
		super.start();
		
		try {
			doPortConnection(generatorOutboundPort.getPortURI(), generatorInPortURI, RequestGeneratorManagementConnector.class.getCanonicalName());
			
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
		
	}
	
	/**
	 * Coeur du composant Integrator, on y fait tout le traitement en rapport avec le RequestGenerator, c'est-à-dire la demande d'hébergement d'application mais 
	 * aussi la génération des requêtes, l'arrêt et la désallocation des ressources.
	 */
	@Override
	public void execute() throws Exception{
		super.execute();
		
		/*generateur de requetes demande si le controleur d'admission
		 * a les ressources necessaires
		 * */
		if(generatorOutboundPort.askAdmissionControler()){
			
			/*
			 * Tous les composants sont pr�ts,
			 * On peut commencer la generation de requ�tes
			 */
			
			generatorOutboundPort.startGeneration();
			Thread.sleep(500000000L) ;
			// then stop the generation.

			generatorOutboundPort.stopGeneration() ;
			
			
			generatorOutboundPort.freeAdmissionControlerRessources();
			
		}
		
		
		
	}
	
	
	@Override
	public void			finalise() throws Exception
	{
		
		this.doPortDisconnection(this.generatorOutboundPort.getPortURI()) ;

		super.finalise();
	}
	
	
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			this.generatorOutboundPort.unpublishPort() ;
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
	
	
	@Override
	public void			shutdownNow() throws ComponentShutdownException
	{
		try {
			this.generatorOutboundPort.unpublishPort() ;
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdownNow();
	}

}
