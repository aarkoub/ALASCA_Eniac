package etape1.cvm;

import etape1.requestdistributor.connectors.RequestDistributorManagementConnector;
import etape1.requestdistributor.ports.RequestDistributorManagementOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenterclient.requestgenerator.connectors.RequestGeneratorManagementConnector;
import fr.sorbonne_u.datacenterclient.requestgenerator.ports.RequestGeneratorManagementOutboundPort;

public class Integrator extends AbstractComponent {
	
	protected RequestDistributorManagementOutboundPort distribOutboundPort;
	protected RequestGeneratorManagementOutboundPort generatorOutboundPort;
	private String distribInPortURI;
	private String generatorInPortURI; 
	
	
	public Integrator(String requestDistributorManagementInboundPortURI, String requestGeneratorManagementInboundPortURI) throws Exception {
		super(1,0);
		
		assert requestDistributorManagementInboundPortURI !=null;
		assert requestGeneratorManagementInboundPortURI != null;
		
		distribInPortURI = requestDistributorManagementInboundPortURI;
		generatorInPortURI = requestGeneratorManagementInboundPortURI;
		
		distribOutboundPort = new RequestDistributorManagementOutboundPort(this);
		addPort(distribOutboundPort);
		distribOutboundPort.publishPort();
		
		generatorOutboundPort = new RequestGeneratorManagementOutboundPort(this);
		addPort(generatorOutboundPort);
		generatorOutboundPort.publishPort();
		
		
				
	}
	
	
	@Override
	public void start() throws ComponentStartException {
		
		super.start();
		
		try {
			doPortConnection(generatorOutboundPort.getPortURI(), generatorInPortURI, RequestGeneratorManagementConnector.class.getCanonicalName());
			doPortConnection(distribOutboundPort.getPortURI(), distribInPortURI, RequestDistributorManagementConnector.class.getCanonicalName()) ;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	@Override
	public void			execute() throws Exception
	{
		super.execute() ;
		System.out.println(generatorOutboundPort);
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
		this.doPortDisconnection(this.distribOutboundPort.getPortURI()) ;
		super.finalise();
	}
	
	
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			this.generatorOutboundPort.unpublishPort() ;
			this.distribOutboundPort.unpublishPort() ;
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
			this.distribOutboundPort.unpublishPort() ;
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdownNow();
	}

}
