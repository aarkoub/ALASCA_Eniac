package etape1.cvm;

import etape1.admissioncontroler.ports.AdmissionControlerManagementOutboundPort;
import etape1.dynamiccomponentcreator.DynamicComponentCreationConnector;
import etape1.dynamiccomponentcreator.DynamicComponentCreationOutboundPort;
import etape1.requestGeneratorForAdmissionControler.RequestGeneratorManagementConnector;
import etape1.requestGeneratorForAdmissionControler.RequestGeneratorManagementOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;


public class Integrator2 extends AbstractComponent {
	
	
	protected RequestGeneratorManagementOutboundPort generatorOutboundPort;
	protected DynamicComponentCreationOutboundPort dynamic_outbound;
	private String generatorInPortURI; 
	private String dynamic_outbound_uri;
	
	
	
	public Integrator2(String requestGeneratorManagementInboundPortURI,
			String dynamicOutbound) throws Exception {
		super(1,0);
		
		assert requestGeneratorManagementInboundPortURI != null;
		assert dynamicOutbound != null;
		
		
		generatorInPortURI = requestGeneratorManagementInboundPortURI;
		this.dynamic_outbound_uri = dynamicOutbound;
		
		
		generatorOutboundPort = new RequestGeneratorManagementOutboundPort(this);
		addPort(generatorOutboundPort);
		generatorOutboundPort.publishPort();
		
		
		dynamic_outbound = new DynamicComponentCreationOutboundPort(this);
		addPort(dynamic_outbound);
		dynamic_outbound.publishPort();
						
	}
	
	
	@Override
	public void start() throws ComponentStartException {
		
		super.start();
		
		try {
			doPortConnection(generatorOutboundPort.getPortURI(), generatorInPortURI, RequestGeneratorManagementConnector.class.getCanonicalName());
			doPortConnection(dynamic_outbound.getPortURI(), dynamic_outbound_uri, DynamicComponentCreationConnector.class.getCanonicalName());
			
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
		
	}
	
	@Override
	public void execute() throws Exception{
		super.execute();
		
		/*generateur de requetes demande si le controleur d'admission
		 * a les ressources necessaires
		 * */
		if(!generatorOutboundPort.askAdmissionControler()){
			throw new Exception("Refus du controleur d'admission");
		};
		
		/*
		 * On peut exécuter les composants crées par le dynamic component creator
		 * et donc notamment on exécute l'integrateur 3 qui va allouer les coeurs
		 * et démarrer la génération de requêtes (et finalement la stopper)
		 */
		dynamic_outbound.executeComponents();
	}
	
	
	@Override
	public void			finalise() throws Exception
	{
		
		this.doPortDisconnection(this.generatorOutboundPort.getPortURI()) ;

		doPortDisconnection(dynamic_outbound.getPortURI());
		super.finalise();
	}
	
	
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			this.generatorOutboundPort.unpublishPort() ;
			dynamic_outbound.unpublishPort();
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
			dynamic_outbound.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdownNow();
	}

}
