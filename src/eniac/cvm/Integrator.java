package eniac.cvm;

import eniac.admissioncontroler.ports.AdmissionControlerManagementOutboundPort;
import eniac.requestgenarator.connector.RequestGeneratorManagementConnector;
import eniac.requestgenarator.ports.RequestGeneratorManagementOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;


public class Integrator extends AbstractComponent {
	
	
	protected RequestGeneratorManagementOutboundPort generatorOutboundPort;

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
			Thread.sleep(2000L) ;
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
