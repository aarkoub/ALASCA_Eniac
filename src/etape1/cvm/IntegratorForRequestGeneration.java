package etape1.cvm;

import java.util.ArrayList;
import java.util.List;

import etape1.requestdispatcher.multi.data.AVMUris;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.computers.connectors.ComputerServicesConnector;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.connectors.ApplicationVMManagementConnector;
import fr.sorbonne_u.datacenter.software.applicationvm.ports.ApplicationVMManagementOutboundPort;


public class IntegratorForRequestGeneration extends AbstractComponent {
	

	protected List<ApplicationVMManagementOutboundPort> appliOutboundPorts;
	protected List<AVMUris> uris;
	protected ComputerServicesOutboundPort computerOutboundPort;
	private String computerInPortURI;

	
	
	
	public IntegratorForRequestGeneration(ArrayList<AVMUris> uris,
			String computerServiceInboundPortURI) throws Exception {
		super(0,0);
		
		assert uris != null;
		assert computerServiceInboundPortURI != null;
		
		computerInPortURI = computerServiceInboundPortURI;
		
		this.uris = uris;
		appliOutboundPorts = new ArrayList<>();
		for(int i = 0; i < uris.size(); i++) {
			ApplicationVMManagementOutboundPort appliOutboundPort = new ApplicationVMManagementOutboundPort(this);
			addPort(appliOutboundPort);
			appliOutboundPort.publishPort();
			appliOutboundPorts.add(appliOutboundPort);
		}
		
		computerOutboundPort = new ComputerServicesOutboundPort(this);
		addPort(computerOutboundPort);
		computerOutboundPort.publishPort();
		
		
		
				
	}
	
	
	@Override
	public void start() throws ComponentStartException {
		
		super.start();
		try {
			doPortConnection(computerOutboundPort.getPortURI(), computerInPortURI, ComputerServicesConnector.class.getCanonicalName());
			for(int i = 0; i < appliOutboundPorts.size(); i++) {
				doPortConnection(appliOutboundPorts.get(i).getPortURI(), uris.get(i).getApplicationVMManagementInboundPortVM(),
						ApplicationVMManagementConnector.class.getCanonicalName());
			}
			
			
		} catch (Exception e) {
			throw new ComponentStartException(e);
		}
		
	}
	
	
	@Override
	public void			execute() throws Exception
	{
		super.execute() ;
		
		for(int i = 0; i < appliOutboundPorts.size(); i++) {
			AllocatedCore[] ac = this.computerOutboundPort.allocateCores(2) ;
			appliOutboundPorts.get(i).allocateCores(ac) ;
		}
		
	
	}
	

	@Override
	public void			finalise() throws Exception
	{
		
		
		doPortDisconnection(computerOutboundPort.getPortURI());
		for(int i = 0; i < appliOutboundPorts.size(); i++) {
			doPortDisconnection(appliOutboundPorts.get(i).getPortURI());;
		}
		
		super.finalise();
	}
	
	
	@Override
	public void			shutdown() throws ComponentShutdownException
	{
		try {
			
			computerOutboundPort.unpublishPort();
			for(int i = 0; i < appliOutboundPorts.size(); i++) {
				appliOutboundPorts.get(i).unpublishPort();
			}
		
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
			for(int i = 0; i < appliOutboundPorts.size(); i++) {
				appliOutboundPorts.get(i).unpublishPort();
			}
		
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}
		super.shutdownNow();
	}

}
