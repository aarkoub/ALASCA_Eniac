package etape1.cvm;

import etape1.requestdispatcher.RequestDispatcher;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.processors.Processor;
import fr.sorbonne_u.datacenter.hardware.tests.ComputerMonitor;
import fr.sorbonne_u.datacenter.software.applicationvm.ApplicationVM;
import fr.sorbonne_u.datacenterclient.requestgenerator.RequestGenerator;

public class CVM4DynamicPurpose extends AbstractCVM {


	private RequestDispatcher requestDisbributor;
	private Computer c;
	private ComputerMonitor computerMonitor;
	private ApplicationVM applicationVM;
	String RequestDistributorManagementInboundPortURI;

	String ApplicationVMManagementInboundPortURI;
	String ComputerServicesInboundPortURI;
	private IntegratorForRequestGeneration integrator;


	public CVM4DynamicPurpose(
			RequestDispatcher disp, 
			ApplicationVM appli,
			Computer c,
			ComputerMonitor monitor,
			
			String RequestDistributorManagementInboundPortURI,
			String ApplicationVMManagementInboundPortURI,
			String ComputerServicesInboundPortURI
			) throws Exception {
	
		
		super();
		
		this.requestDisbributor = disp;
		this.c = c;
		this.computerMonitor = monitor;
		this.applicationVM = appli;
		
		this.RequestDistributorManagementInboundPortURI = RequestDistributorManagementInboundPortURI;
		
		this.ApplicationVMManagementInboundPortURI = ApplicationVMManagementInboundPortURI;
		this.ComputerServicesInboundPortURI = ComputerServicesInboundPortURI;
		
	
	}
	
	@Override
	public void deploy() throws Exception{
		
		Processor.DEBUG = true ;
		
		assert	!this.deploymentDone() ;
		
		
				this.addDeployedComponent(c) ;
				c.toggleLogging() ;
				c.toggleTracing() ;
			
				this.addDeployedComponent(this.computerMonitor) ;
				// --------------------------------------------------------------------

		
		
		
		
		requestDisbributor.toggleTracing();
		requestDisbributor.toggleLogging();
		
		addDeployedComponent(requestDisbributor);

		
		applicationVM.toggleTracing();
		applicationVM.toggleLogging();
		
		addDeployedComponent(applicationVM);

		
		/*integrator = new Integrator3(RequestDistributorManagementInboundPortURI,
				
				ApplicationVMManagementInboundPortURI,
				ComputerServicesInboundPortURI ) ;
		addDeployedComponent(integrator) ;*/
		
		
		super.deploy();
		
		assert this.deploymentDone();
		
		
		
	}
	
	public IntegratorForRequestGeneration getIntegrator(){
		return integrator;
	}
	

	
}
