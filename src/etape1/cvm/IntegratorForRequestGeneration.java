package etape1.cvm;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;


public class IntegratorForRequestGeneration extends AbstractComponent {
	

	protected ApplicationVMManagementOutboundPort appliOutboundPort;
	protected ComputerServicesOutboundPort computerOutboundPort;
	
	private String appliInPortURI;
	private String computerInPortURI;

	
	
	
	public IntegratorForRequestGeneration(String applicationVMManagementInboundPortURI,
			String computerServiceInboundPortURI) throws Exception {
		super(0,0);
		
		assert applicationVMManagementInboundPortURI != null;
		assert computerServiceInboundPortURI != null;
		
		
		appliInPortURI = applicationVMManagementInboundPortURI;
		computerInPortURI = computerServiceInboundPortURI;
		

		appliOutboundPort = new ApplicationVMManagementOutboundPort(this);
		addPort(appliOutboundPort);
		appliOutboundPort.publishPort();
		
		computerOutboundPort = new ComputerServicesOutboundPort(this);
		addPort(computerOutboundPort);
		computerOutboundPort.publishPort();
		
		
				
	}
	
	
	@Override
	public void start() throws ComponentStartException {
		
		super.start();
		try {
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
		
	
	}
	

	@Override
	public void			finalise() throws Exception
	{
		
		
		doPortDisconnection(computerOutboundPort.getPortURI());
		doPortDisconnection(appliOutboundPort.getPortURI());
		
		super.finalise();
	}
	
	
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			
			computerOutboundPort.unpublishPort();
			appliOutboundPort.unpublishPort();
		
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdown();
	}
	
	
	@Override
	public void			shutdownNow() throws ComponentShutdownException
	{
		try {
	
			computerOutboundPort.unpublishPort();
			appliOutboundPort.unpublishPort();
		
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdownNow();
	}

}
