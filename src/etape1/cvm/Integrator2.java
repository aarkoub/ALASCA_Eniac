package etape1.cvm;

import etape1.admissioncontroler.connectors.AdmissionControlerManagementConnector;
import etape1.admissioncontroler.ports.AdmissionControlerManagementOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.sorbonne_u.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

public class Integrator2 extends AbstractComponent {
	
	
	protected RequestGeneratorManagementOutboundPort generatorOutboundPort;
	protected AdmissionControlerManagementOutboundPort admissionControlerOutboundPort;
	private String generatorInPortURI; 
	private String admissionControlerInPortURI;
	
	
	
	public Integrator2(String requestGeneratorManagementInboundPortURI,
			String admissionControlerManagementInboundPortURI) throws Exception {
		super(1,0);
		
		assert requestGeneratorManagementInboundPortURI != null;
		assert admissionControlerManagementInboundPortURI != null;
		
		
		generatorInPortURI = requestGeneratorManagementInboundPortURI;
		admissionControlerInPortURI = admissionControlerManagementInboundPortURI;
		
		
		generatorOutboundPort = new RequestGeneratorManagementOutboundPort(this);
		addPort(generatorOutboundPort);
		generatorOutboundPort.publishPort();
		
		admissionControlerOutboundPort = new AdmissionControlerManagementOutboundPort(this);
		addPort(admissionControlerOutboundPort);
		admissionControlerOutboundPort.publishPort();
		
				
	}
	
	
	@Override
	public void start() throws ComponentStartException {
		
		super.start();
		
		try {
			doPortConnection(generatorOutboundPort.getPortURI(), generatorInPortURI, RequestGeneratorManagementConnector.class.getCanonicalName());
			doPortConnection(admissionControlerOutboundPort.getPortURI(), admissionControlerInPortURI, AdmissionControlerManagementConnector.class.getCanonicalName());
			
			
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
		
	}
	
	
	@Override
	public void			execute() throws Exception
	{
		super.execute() ;
	
		
		this.generatorOutboundPort.startGeneration() ;
		
		// wait 20 seconds
		Thread.sleep(2000L) ;
		// then stop the generation.
		this.generatorOutboundPort.stopGeneration() ;
	}
	

	@Override
	public void			finalise() throws Exception
	{
		
		this.doPortDisconnection(this.generatorOutboundPort.getPortURI()) ;

		doPortDisconnection(admissionControlerOutboundPort.getPortURI());
		super.finalise();
	}
	
	
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			this.generatorOutboundPort.unpublishPort() ;

			admissionControlerOutboundPort.unpublishPort();
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

			admissionControlerOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdownNow();
	}

}
