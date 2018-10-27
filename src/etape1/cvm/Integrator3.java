package etape1.cvm;

import etape1.requestGeneratorForAdmissionControler.RequestGeneratorManagementConnector;
import etape1.requestGeneratorForAdmissionControler.RequestGeneratorManagementOutboundPort;
import etape1.requestdistributor.connectors.RequestDistributorManagementConnector;
import etape1.requestdistributor.ports.RequestDistributorManagementOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;


public class Integrator3 extends AbstractComponent {
	
	protected RequestDistributorManagementOutboundPort distribOutboundPort;

	protected ApplicationVMManagementOutboundPort appliOutboundPort;
	protected ComputerServicesOutboundPort computerOutboundPort;
	private String distribInPortURI; 
	private String appliInPortURI;
	private String computerInPortURI;
	private String genInPortURI;

	private RequestGeneratorManagementOutboundPort generatorOutboundPort;
	
	
	
	public Integrator3(String requestGeneratorManagementInboundPortURI,
			String requestDistributorManagementInboundPortURI,  String applicationVMManagementInboundPortURI,
			String computerServiceInboundPortURI) throws Exception {
		super(0,0);
		
		assert requestDistributorManagementInboundPortURI !=null;
		assert requestGeneratorManagementInboundPortURI != null;
		assert applicationVMManagementInboundPortURI != null;
		assert computerServiceInboundPortURI != null;
		
		
		distribInPortURI = requestDistributorManagementInboundPortURI;
	
		appliInPortURI = applicationVMManagementInboundPortURI;
		computerInPortURI = computerServiceInboundPortURI;
		
		genInPortURI = requestGeneratorManagementInboundPortURI;
		
		
		distribOutboundPort = new RequestDistributorManagementOutboundPort(this);
		addPort(distribOutboundPort);
		distribOutboundPort.publishPort();

		appliOutboundPort = new ApplicationVMManagementOutboundPort(this);
		addPort(appliOutboundPort);
		appliOutboundPort.publishPort();
		
		computerOutboundPort = new ComputerServicesOutboundPort(this);
		addPort(computerOutboundPort);
		computerOutboundPort.publishPort();
		
		generatorOutboundPort = new RequestGeneratorManagementOutboundPort(this);
		addPort(generatorOutboundPort);
		generatorOutboundPort.publishPort();
		
				
	}
	
	
	@Override
	public void start() throws ComponentStartException {
		
		super.start();
		try {
			doPortConnection(generatorOutboundPort.getPortURI(), genInPortURI, RequestGeneratorManagementConnector.class.getCanonicalName());
			doPortConnection(distribOutboundPort.getPortURI(), distribInPortURI, RequestDistributorManagementConnector.class.getCanonicalName()) ;
			doPortConnection(computerOutboundPort.getPortURI(), computerInPortURI, ComputerServicesConnector.class.getCanonicalName());
			doPortConnection(appliOutboundPort.getPortURI(), appliInPortURI, ApplicationVMManagementConnector.class.getCanonicalName());
			
			
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
		
	}
	
	
	@Override
	public void			execute() throws Exception
	{
		super.execute() ;
		
		AllocatedCore[] ac = this.computerOutboundPort.allocateCores(4) ;
		this.appliOutboundPort.allocateCores(ac) ;

		
		this.generatorOutboundPort.startGeneration() ;
		// wait 20 seconds
		Thread.sleep(2000L) ;
		// then stop the generation.
		this.generatorOutboundPort.stopGeneration() ;
	
	}
	

	@Override
	public void			finalise() throws Exception
	{
		
		
		this.doPortDisconnection(this.distribOutboundPort.getPortURI()) ;
		doPortDisconnection(computerOutboundPort.getPortURI());
		doPortDisconnection(appliOutboundPort.getPortURI());
		doPortDisconnection(generatorOutboundPort.getPortURI());
		super.finalise();
	}
	
	
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			
			this.distribOutboundPort.unpublishPort() ;
			computerOutboundPort.unpublishPort();
			appliOutboundPort.unpublishPort();
			generatorOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
	
	
	@Override
	public void			shutdownNow() throws ComponentShutdownException
	{
		try {
	
			this.distribOutboundPort.unpublishPort() ;
			computerOutboundPort.unpublishPort();
			appliOutboundPort.unpublishPort();
			generatorOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdownNow();
	}

}
