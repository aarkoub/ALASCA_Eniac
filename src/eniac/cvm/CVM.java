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
import eniac.processorcoordinator.ProcessorCoordinator;
import eniac.requestgenarator.RequestGenerator;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.processors.Processor;
import fr.sorbonne_u.datacenter.hardware.processors.Processor.ProcessorPortTypes;
import fr.sorbonne_u.datacenter.hardware.tests.ComputerMonitor;

/**
 * La classe CVM correspond à la classe permettant de démarrer le projet en Mono-JVM, c'est-à-dire de lancer le centre de calcul et des demandes de d'hébergement d'applications.
 * Cela correspond donc à la création de tout les composants nécessaire c'est-à-dire les Computer, le contrôleur d'admission, les contrôleurs de performances et les RequestGenerator.
 * La classe a été configuré ici afin de lancer 2 applications.
 * @author L-C
 *
 */

public class CVM extends AbstractCVM {
	
	
	/**
	 * URI pour le contrôle des RequestGenerator
	 */
	protected static final String	RequestGeneratorManagementInboundPortURI = "requestGenerator_in_port" ;
		

	/**
	 * URI pour les RequestGenerator
	 */
	protected static final String URI_RequestGenerator = "uri_requestGenerator";
	
	/**
	 * URI pour les soumissions de requêtes
	 */
	protected static final String requestSubmissionInboundPortURI = "request_sub_inbound_port";
	
	/**
	 * URI pour les notifications de requêtes
	 */
	protected static final String requestNotificationInboundPortURI = "request_notification_inbound_port";
	
	/**
	 * URI du contrôleur d'admission
	 */
	protected static final String admissionControlerURI = "admission_controler";
	
	/**
	 * URI pour le contrôle du controlleur d'admission
	 */
	protected static final String admissionControlerManagementInboundURI = "admission_controler_management_inbound_uri";
	
	/**
	 * RequestGenerator
	 */
	protected RequestGenerator requestGenerator ;
	/**
	 * Intégrateur pour le RequestGenerator
	 */
	protected Integrator integrator;
	/**
	 * Moniteur pour les ordinateurs
	 */
	protected ComputerMonitor computerMonitor;
	
	/**
	 * Controlleur d'admission
	 */
	protected AdmissionControler admissionControler;
	/**
	 * Controlleur de performance
	 */
	protected AutomaticHandler automaticHandler;
	
	
	/**
	 * Liste des URIs pour la soumission de demandes d'hébergements
	 */
	protected static final List<String> requestAdmissionSubmissionInboundPortURIS = new ArrayList<>();
	
	/**
	 * Liste des URIs pour la notifications de demandes d'hébergements
	 */
	protected static final List<String> requestAdmissionNotificationInboundPortURIS = new ArrayList<>();
	
	/**
	 * Ordinateurs du centre de calcul
	 */
	protected List<Computer> computers = new ArrayList<>();
	/**
	 * Moniteur d'ordinateurs du centre de calcul
	 */
	protected List<ComputerMonitor> computerMonitors = new ArrayList<>();
	/**
	 * Liste des URIs pour les moniteurs d'ordinateurs
	 */
	protected List<String> computerMonitorsURI = new ArrayList<>();
	/**
	 * Liste des URIs des ordinateurs
	 */
	protected List<String> computersURI = new ArrayList<>();
	/**
	 * Liste des Générateur de requêtes
	 */
	protected List<RequestGenerator> requestGenerators = new ArrayList<>();
	/**
	 * Liste des intégrateurs des générateurs de requêtes
	 */
	protected List<Integrator> integrators = new ArrayList<>();


	/**
	 * URI de soumission pour l'admission d'un générateur de requêtes
	 */
	protected final String requestAdmissionSubmissionInboundPortURI ="request_admission_submission_inbound_port_uri";
	/**
	 * URI de notification pour l'admission d'un générateur de requêtes
	 */
	protected final String requestAdmissionNotificationInboundPortURI = "request_admission_notification_inbound_port_uri";
	/**
	 * URI pour la création de composant dynamique
	 */
	protected static final String dynamicComponentCreationInboundPortURI = "dynamicComponentCreationInboundPortURI";
	
	/**
	 * URI pour les distributeurs de requêtes
	 */
	protected String requestDispatcherListenerInboundPortURI = "req_disp_listener_inport";
	/**
	 * URI du contrôleur de performance
	 */
	protected String automaticHandlerURI = "automatic_handler";
	/**
	 * URI de contrôme du contrôleur de performance
	 */
	protected String automaticHandlerManagementInboundPortURI = "automatic_handler_managament_inport";
	
	public CVM(boolean isDistributed) throws Exception {
		super(isDistributed);
	}
	
	public CVM() throws Exception {
		super();
	}
	
	
	/**
	 * On instancie tout les composants et on démarre l'exécution du scénario.
	 */
	@Override
	public void deploy() throws Exception{
		
		Processor.DEBUG = true ;
		
		assert	!this.deploymentDone() ;
		
		int max_ressources = 3;
		int freq_threshold = 1500;
		
		/*
		 * Création des computeurs (en ressources du contrôleur d'admission)
		 */
		
		List<ComputerURI> computeruris = new ArrayList<>();
		for(int i=0; i<8; i++) {
		
			String ComputerDynamicStateDataInboundPortURI = "computerDynamic_inport_uri_"+i;
			String ComputerStaticStateDataInboundPortURI = "computerStatic_inport_uri_"+i;
			String ComputerServicesInboundPortURI = "computer_in_port_"+i;
			String computerURI = "computer_"+i ;
			int numberOfProcessors = 2 ;
			int numberOfCores = 10 ;
			Set<Integer> admissibleFrequencies = new HashSet<Integer>() ;
			admissibleFrequencies.add(1500) ;	// Cores can run at 1,5 GHz
			admissibleFrequencies.add(3000) ;	// and at 3 GHz
			admissibleFrequencies.add(4500) ;
			Map<Integer,Integer> processingPower = new HashMap<Integer,Integer>() ;
			processingPower.put(1500, 1500000) ;	// 1,5 GHz executes 1,5 Mips
			processingPower.put(3000, 3000000) ;	// 3 GHz executes 3 Mips
			processingPower.put(4500, 4500000) ;
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
		
		Map<String, String > proc_coord_management_map = new HashMap<>();
		
		for (int i = 0; i < computers.size(); i++) {
			Computer c =  computers.get(i);
			
			for(String proc_uri : c.getStaticState().getProcessorPortMap().keySet() ){
				
				String coordinatorURI = "proc_coord_uri_"+proc_uri;
				String proc_coord_management_inport_uri = "proc_coord_management_inport_uri_"+proc_uri;
				String proc_management_inport_uri = c.getStaticState().getProcessorPortMap().get(proc_uri).get(ProcessorPortTypes.MANAGEMENT);
				
				ProcessorCoordinator pc = new ProcessorCoordinator(coordinatorURI,
						proc_uri, 
						proc_management_inport_uri,
						proc_coord_management_inport_uri);
				
				addDeployedComponent(pc);
				
				proc_coord_management_map.put(proc_uri, proc_coord_management_inport_uri);
								
			}
		}
		
		
		for(int i=0; i<max_ressources; i++){
			requestAdmissionSubmissionInboundPortURIS.add("request_admission_submission_inbound_port_uri_"+i);
			requestAdmissionNotificationInboundPortURIS.add("request_admission_notification_inbound_port_uri_"+i);
		}
		
		
		/*
		 * Création du controleur d'admission
		 */
		admissionControler = new AdmissionControler(admissionControlerURI,
				max_ressources, 
				admissionControlerManagementInboundURI, 
				dynamicComponentCreationInboundPortURI,
				requestAdmissionSubmissionInboundPortURI,
				requestAdmissionNotificationInboundPortURI,
				proc_coord_management_map,
				computers,
				computeruris,
				computerMonitors);
		
		admissionControler.toggleLogging();
		admissionControler.toggleTracing();
		
		
		for(int i=0; i<2; i++){
			

			/*
			 * Creation du générateur de requêtes
			 */
		
			requestGenerator = new RequestGenerator(URI_RequestGenerator+i, 100, 2000000000L, 
					RequestGeneratorManagementInboundPortURI+i, requestSubmissionInboundPortURI+i,
					requestNotificationInboundPortURI+i, requestAdmissionSubmissionInboundPortURI,
					requestAdmissionNotificationInboundPortURI,
					3000);
			
			
				
			requestGenerator.toggleTracing();
			requestGenerator.toggleLogging();
			
			requestGenerators.add(requestGenerator);
			
			/*
			 * Création de l'intégrateur de générateur de requêtes
			 */
			integrator = new Integrator(RequestGeneratorManagementInboundPortURI+i);
			
			integrators.add(integrator);
		}
				
		
		/*
		 * Déploiment
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
			a.startStandardLifeCycle(500000000L) ;
			// Give some time to see the traces (convenience).
			Thread.sleep(10000L) ;
			// Simplifies the termination (termination has yet to be treated
			// properly in BCM).
			System.exit(0) ;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
