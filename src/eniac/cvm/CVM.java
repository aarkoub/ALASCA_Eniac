package eniac.cvm;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eniac.admissioncontroler.AdmissionControler;
import eniac.admissioncontroler.ComputerURI;
import eniac.automatichandler.AutomaticHandler;
import eniac.automatichandler.ports.AutomaticHandlerManagementOutboundPort;
import eniac.requestgenarator.RequestGenerator;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.processors.Processor;
import fr.sorbonne_u.datacenter.hardware.tests.ComputerMonitor;


public class CVM extends AbstractCVM {
	

	protected static final String	RequestGeneratorManagementInboundPortURI = "requestGenerator_in_port" ;
		

	protected static final String URI_RequestGenerator = "uri_requestGenerator";
	
	
	
	protected RequestGenerator requestGenerator ;
	protected Integrator integrator;
	protected ComputerMonitor computerMonitor;
	protected AdmissionControler admissionControler;
	protected AutomaticHandler automaticHandler;
	
	
	protected static final String requestSubmissionInboundPortURI = "request_sub_inbound_port";
	protected static final String requestNotificationInboundPortURI = "request_notification_inbound_port";
	
		
	protected static final String admissionControlerURI = "admission_controler";
	protected static final String admissionControlerManagementInboundURI = "admission_controler_management_inbound_uri";
	protected static final List<String> requestAdmissionSubmissionInboundPortURIS = new ArrayList<>();
	protected static final List<String> requestAdmissionNotificationInboundPortURIS = new ArrayList<>();
	
	
	protected List<Computer> computers = new ArrayList<>();
	protected List<ComputerMonitor> computerMonitors = new ArrayList<>();
	protected List<String> computerMonitorsURI = new ArrayList<>();
	protected List<String> computersURI = new ArrayList<>();
	protected List<RequestGenerator> requestGenerators = new ArrayList<>();
	protected List<Integrator> integrators = new ArrayList<>();


	protected final String requestAdmissionSubmissionInboundPortURI ="request_admission_submission_inbound_port_uri";
	protected final String requestAdmissionNotificationInboundPortURI = "request_admission_notification_inbound_port_uri";
	protected static final String dynamicComponentCreationInboundPortURI = "dynamicComponentCreationInboundPortURI";
	
	protected String requestDispatcherListenerInboundPortURI = "req_disp_listener_inport";
	
	protected String automaticHandlerURI = "automatic_handler";
	protected String automaticHandlerManagementInboundPortURI = "automatic_handler_managament_inport";
	
	public CVM(boolean isDistributed) throws Exception {
		super(isDistributed);
	}
	
	public CVM() throws Exception {
		super();
	}
	
	@Override
	public void deploy() throws Exception{
		
		Processor.DEBUG = true ;
		
		assert	!this.deploymentDone() ;
		
		int max_ressources = 2;
		
		/*
		 * Cr�ation des computeurs (en ressources du contr�leur d'admission)
		 */
		
		List<ComputerURI> computeruris = new ArrayList<>();
		for(int i=0; i<max_ressources; i++) {
		
			String ComputerDynamicStateDataInboundPortURI = "computerDynamic_inport_uri_"+i;
			String ComputerStaticStateDataInboundPortURI = "computerStatic_inport_uri_"+i;
			String ComputerServicesInboundPortURI = "computer_in_port_"+i;
			String computerURI = "computer_"+i ;
			int numberOfProcessors = 1 ;
			int numberOfCores = 2 ;
			Set<Integer> admissibleFrequencies = new HashSet<Integer>() ;
			admissibleFrequencies.add(1500) ;	// Cores can run at 1,5 GHz
			admissibleFrequencies.add(3000) ;	// and at 3 GHz
			Map<Integer,Integer> processingPower = new HashMap<Integer,Integer>() ;
			processingPower.put(1500, 1500000) ;	// 1,5 GHz executes 1,5 Mips
			processingPower.put(3000, 3000000) ;	// 3 GHz executes 3 Mips
			Computer c = new Computer(
								computerURI,
								admissibleFrequencies,
								processingPower,  
								1500,		// Test scenario 1, frequency = 1,5 GHz
								// 3000,	// Test scenario 2, frequency = 3 GHz
								1500,		// max frequency gap within a processor
								numberOfProcessors,
								numberOfCores,
								ComputerServicesInboundPortURI,
								ComputerStaticStateDataInboundPortURI,
								ComputerDynamicStateDataInboundPortURI) ;
			this.addDeployedComponent(c) ;
			
			// --------------------------------------------------------------------
	
			// --------------------------------------------------------------------
			// Create the computer monitor component and connect its to ports
			// with the computer component.
			// --------------------------------------------------------------------
			this.computerMonitor = new ComputerMonitor(computerURI,
										 true,
										 ComputerStaticStateDataInboundPortURI,
										 ComputerDynamicStateDataInboundPortURI) ;
			this.addDeployedComponent(this.computerMonitor) ;
			c.toggleLogging();
			c.toggleTracing();
			computers.add(c);
			computerMonitors.add(computerMonitor);
			computersURI.add(ComputerServicesInboundPortURI);
			computeruris.add(new ComputerURI(computerURI, ComputerServicesInboundPortURI, ComputerStaticStateDataInboundPortURI, ComputerDynamicStateDataInboundPortURI));
			computerMonitor.toggleLogging();
			computerMonitor.toggleTracing();
			
		}
		
		
		for(int i=0; i<max_ressources; i++){
			requestAdmissionSubmissionInboundPortURIS.add("request_admission_submission_inbound_port_uri_"+i);
			requestAdmissionNotificationInboundPortURIS.add("request_admission_notification_inbound_port_uri_"+i);
		}
		
		
		/*
		 * Creation du controleur d'admission
		 */
		admissionControler = new AdmissionControler(admissionControlerURI,
				max_ressources, 
				admissionControlerManagementInboundURI, 
				dynamicComponentCreationInboundPortURI,
				requestAdmissionSubmissionInboundPortURI,
				requestAdmissionNotificationInboundPortURI,
				computers,
				computeruris,
				computerMonitors);
		
		admissionControler.toggleLogging();
		admissionControler.toggleTracing();
		
		
		for(int i=0; i<1; i++){
			

			/*
			 * Creation du g�n�rateur de requetes
			 */
		
			requestGenerator = new RequestGenerator(URI_RequestGenerator+i, 100, 50, 
					RequestGeneratorManagementInboundPortURI+i, requestSubmissionInboundPortURI+i,
					requestNotificationInboundPortURI+i, requestAdmissionSubmissionInboundPortURI,
					requestAdmissionNotificationInboundPortURI);
			
			
				
			requestGenerator.toggleTracing();
			requestGenerator.toggleLogging();
			
			requestGenerators.add(requestGenerator);
			
			/*
			 * Creation du l'int�grateur 2
			 */
			integrator = new Integrator(RequestGeneratorManagementInboundPortURI+i);
			
			integrators.add(integrator);
		}
				
		
		/*
		 * D�ploiement
		 */
		
		
		addDeployedComponent(admissionControler);
		
		for(RequestGenerator reqGen : requestGenerators)
			addDeployedComponent(reqGen);
		
		
		for(Integrator inte : integrators)
			addDeployedComponent(inte) ;
		
			
		super.deploy();
		
		assert this.deploymentDone();
		
		
		
	}
	
	
	public static void		main(String[] args)
	{
		try {
			// Create an instance of the defined component virtual machine.
			CVM a = new CVM() ;
			// Execute the application.
			a.startStandardLifeCycle(18000L) ;
			// Give some time to see the traces (convenience).
			Thread.sleep(10000000L) ;
			// Simplifies the termination (termination has yet to be treated
			// properly in BCM).
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
