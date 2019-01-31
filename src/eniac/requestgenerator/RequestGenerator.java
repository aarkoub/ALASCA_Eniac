package eniac.requestgenerator;

import java.lang.reflect.Method;
import java.util.HashMap;

//Copyright Jacques Malenfant, Sorbonne Universite.
//
//Jacques.Malenfant@lip6.fr
//
//This software is a computer program whose purpose is to provide a
//basic component programming model to program with components
//distributed applications in the Java programming language.
//
//This software is governed by the CeCILL-C license under French law and
//abiding by the rules of distribution of free software.  You can use,
//modify and/ or redistribute the software under the terms of the
//CeCILL-C license as circulated by CEA, CNRS and INRIA at the following
//URL "http://www.cecill.info".
//
//As a counterpart to the access to the source code and  rights to copy,
//modify and redistribute granted by the license, users are provided only
//with a limited warranty  and the software's author,  the holder of the
//economic rights,  and the successive licensors  have only  limited
//liability. 
//
//In this respect, the user's attention is drawn to the risks associated
//with loading,  using,  modifying and/or developing or reproducing the
//software by the user in light of its specific status of free software,
//that may mean  that it is complicated to manipulate,  and  that  also
//therefore means  that it is reserved for developers  and  experienced
//professionals having in-depth computer knowledge. Users are therefore
//encouraged to load and test the software's suitability as regards their
//requirements in conditions enabling the security of their systems and/or 
//data to be ensured and,  more generally, to use and operate it in the 
//same conditions as regards security. 
//
//The fact that you are presently reading this means that you have had
//knowledge of the CeCILL-C license and that you accept its terms.

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.commons.math3.random.RandomDataGenerator;

import eniac.admissioncontroler.RequestAdmission;
import eniac.admissioncontroler.connectors.RequestAdmissionNotificationConnector;
import eniac.admissioncontroler.connectors.RequestAdmissionSubmissionConnector;
import eniac.admissioncontroler.interfaces.RequestAdmissionI;
import eniac.admissioncontroler.interfaces.RequestAdmissionNotificationI;
import eniac.admissioncontroler.interfaces.RequestAdmissionSubmissionI;
import eniac.requestadmission.ports.RequestAdmissionNotificationOutboundPort;
import eniac.requestadmission.ports.RequestAdmissionSubmissionOutboundPort;
import eniac.requestgenerator.interfaces.RequestGeneratorManagementI;
import eniac.requestgenerator.interfaces.SubmissionISend;
import eniac.requestgenerator.ports.RequestGeneratorManagementInboundPort;
import eniac.requestgenerator.ports.SendSubmissionOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.TimeManagement;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationInboundPort;
import fr.sorbonne_u.datacenterclient.requestgenerator.Request;
import fr.sorbonne_u.datacenterclient.utils.TimeProcessing;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

/**
 * La classe RequestGenerator est une classe qui à été reprise BCM écrit par Mr. Jacques Malenfant
 * pour nos besoin, cette classe simule une application qui demande si un datacenter (controlleur d'admission) peut prendre en charge
 * ses requêtes si c'est le cas, il connecte son port de submission au requestDispatcher dont l'URI est donnée
 * lors de la réponse du controller d'admission et envoit des séries de correctes plus ou moins longue avec un délai qui peut varier.
 * Il est à noter que nous avons essayé d'utiliser Javassist afin de faire de la génération de code en exécution.
 * En effet, nous générons une classe de "connecteur" pour connecter les soumissions de requêtes entre le RequestGenerator et le RequestDispatcher car
 * l'envoit de requête du côté du RequestGenerator est différent de celui du RequestDispatcher et nous n'avons pas auparavant écrit une classe Connecteur qui permet de faire
 * la correspondance entre les deux, nous l'avons généré dynamiquement avec Javassist.
 */
public class				RequestGenerator
extends		AbstractComponent
implements	RequestNotificationHandlerI
{
	public static int	DEBUG_LEVEL = 2 ;

	// -------------------------------------------------------------------------
	// Constants and instance variables
	// -------------------------------------------------------------------------

	/** the URI of the component.											*/
	protected final String					rgURI ;
	/** a random number generator used to generate processing times.		*/
	protected RandomDataGenerator			rng ;
	/** a counter used to generate request URI.								*/
	protected int							counter ;
	/** the mean inter-arrival time of requests in ms.						*/
	protected double							meanInterArrivalTime ;
	/** the mean processing time of requests in ms.							*/
	protected long							meanNumberOfInstructions ;

	/** the inbound port provided to manage the component.					*/
	protected RequestGeneratorManagementInboundPort	rgmip ;
	/** the output port used to send requests to the service provider.		*/
	public SendSubmissionOutboundPort	rsop ;
	protected String							requestSubmissionInboundPortURI ;
	/** the inbound port receiving end of execution notifications.			*/
	protected RequestNotificationInboundPort	rnip ;
	/** a future pointing to the next request generation task.				*/
	protected Future<?>						nextRequestTaskFuture ;
	
	
	
	
	protected RequestAdmissionSubmissionOutboundPort requestAdmissionSubmissionOutboundPort;
	
	protected String requestAdmissionSubmissionInboundPortURI;
	
	protected RequestAdmissionNotificationOutboundPort requestAdmissionNotificationOutboundPort;
	
	protected String requestAdmissionNotificationInboundPortURI;
	
	protected String requestNotificationInboundPortURI;
	
	protected RequestAdmissionI requestAdmission;
	
	protected boolean isRsopPortConnected = false;
	
	
	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * create a request generator component.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	meanInterArrivalTime &gt; 0.0 and meanNumberOfInstructions &gt; 0
	 * pre	requestSubmissionOutboundPortURI != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param rgURI								URI of the request generator component.
	 * @param meanInterArrivalTime				mean inter-arrival time of the requests in ms.
	 * @param meanNumberOfInstructions			mean number of instructions of the requests in ms.
	 * @param managementInboundPortURI			URI of the management inbound port.
	 * @param requestSubmissionInboundPortURI	URI of the inbound port to connect to the request processor.
	 * @param requestNotificationInboundPortURI	URI of the inbound port to receive notifications of the request execution progress.
	 * @param requestAdmissionSubmissionInboundPortURI URI de soumission du controlleur d'admission
	 * @param requestAdmissionNotificationInboundPortURI URI de notification du controlleur d'admission
	 * @param averageResponseTime				Le temps de calcul moyen souhaité
	 * @throws Exception							<i>todo.</i>
	 */
	public				RequestGenerator(
		String rgURI,
		double meanInterArrivalTime,
		long meanNumberOfInstructions,
		String managementInboundPortURI,
		String requestSubmissionInboundPortURI,
		String requestNotificationInboundPortURI,
		String requestAdmissionSubmissionInboundPortURI,
		String requestAdmissionNotificationInboundPortURI,
		double averageResponseTime) throws Exception
	{
		super(rgURI, 1, 1) ;

		// preconditions check
		assert	meanInterArrivalTime > 0.0 && meanNumberOfInstructions > 0 ;
		assert	managementInboundPortURI != null ;
		assert	requestSubmissionInboundPortURI != null ;
		assert	requestNotificationInboundPortURI != null ;
		assert requestAdmissionSubmissionInboundPortURI != null;
		assert requestAdmissionNotificationInboundPortURI != null;

		// initialization
		this.rgURI = rgURI ;
		this.counter = 0 ;
		this.meanInterArrivalTime = meanInterArrivalTime ;
		this.meanNumberOfInstructions = meanNumberOfInstructions ;
		this.rng = new RandomDataGenerator() ;
		this.rng.reSeed() ;
		this.nextRequestTaskFuture = null ;
		this.requestSubmissionInboundPortURI =
										requestSubmissionInboundPortURI ;
		this.requestAdmissionSubmissionInboundPortURI = requestAdmissionSubmissionInboundPortURI;
		
		this.requestNotificationInboundPortURI = requestNotificationInboundPortURI;

		this.addOfferedInterface(RequestGeneratorManagementI.class) ;
		this.rgmip = new RequestGeneratorManagementInboundPort(
										managementInboundPortURI, this) ;
		this.addPort(this.rgmip) ;
		this.rgmip.publishPort() ;

		this.addRequiredInterface(RequestSubmissionI.class) ;
		this.rsop = new SendSubmissionOutboundPort(this) ;
		this.addPort(this.rsop) ;
		this.rsop.publishPort() ;

		this.addOfferedInterface(RequestNotificationI.class) ;
		this.rnip =
			new RequestNotificationInboundPort(
						requestNotificationInboundPortURI, this) ;
		this.addPort(this.rnip) ;
		this.rnip.publishPort() ;
		
		
		
		
		// Creation de l'objet contenant l'URI de notification du RequestGenerator
		requestAdmission = new RequestAdmission(requestNotificationInboundPortURI);
		requestAdmission.setAverageRequestResponseTime(averageResponseTime);
		// Ajout du port pour soumettre la demande d'hebergement au Controleur d'Admission
		addRequiredInterface(RequestAdmissionSubmissionI.class);
		requestAdmissionSubmissionOutboundPort = new RequestAdmissionSubmissionOutboundPort(this);
		addPort(requestAdmissionSubmissionOutboundPort);
		requestAdmissionSubmissionOutboundPort.publishPort();
		
		//Ajout du port pour notifier la terminaison de la requete d'admission
		
		this.requestAdmissionNotificationInboundPortURI = requestAdmissionNotificationInboundPortURI;
		
		addRequiredInterface(RequestAdmissionNotificationI.class);
		requestAdmissionNotificationOutboundPort = 
				new RequestAdmissionNotificationOutboundPort(this);
		addPort(requestAdmissionNotificationOutboundPort);
		requestAdmissionNotificationOutboundPort.publishPort();

		// post-conditions check
		assert	this.rng != null && this.counter >= 0 ;
		assert	this.meanInterArrivalTime > 0.0 ;
		assert	this.meanNumberOfInstructions > 0 ;
		assert	this.rsop != null && this.rsop instanceof RequestSubmissionI ;
		assert  this.requestAdmissionSubmissionOutboundPort!=null && 
				requestAdmissionSubmissionOutboundPort instanceof RequestAdmissionSubmissionI;
		assert requestAdmissionNotificationOutboundPort != null &&
				requestAdmissionNotificationOutboundPort instanceof RequestAdmissionNotificationI;
		
		
	}

	// -------------------------------------------------------------------------
	// Component life-cycle
	// -------------------------------------------------------------------------

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#start()
	 */
	@Override
	public void			start() throws ComponentStartException
	{
		super.start();
		
		// Connexion du port pour demander au controleur d'admission
		try {
			doPortConnection(requestAdmissionSubmissionOutboundPort.getPortURI(), 
				requestAdmissionSubmissionInboundPortURI,
					RequestAdmissionSubmissionConnector.class.getCanonicalName());
			
			doPortConnection(requestAdmissionNotificationOutboundPort.getPortURI(),
					requestAdmissionNotificationInboundPortURI,
					RequestAdmissionNotificationConnector.class.getCanonicalName());
			
		}catch(Exception e) {
			throw new ComponentStartException(e) ;
		}
			
	}
	
	/**
	 * Compteur permettant de créer un connecteur d'indice unique.
	 */
	public static int cpt = 0;
	
	/**
	 * Méthode permettant de générer une classe à l'aide de Javassist, plus particulièrement cette méthode
	 * génère une classe Connecteur afin de relier deux méthodes différentes.
	 *  
	 * @param connectorCanonicalClassName Le nom de classe à générer
	 * @param connectorSuperClass La superclasse
	 * @param connectorImplementedInterface L'interface qu'il doit implanter
	 * @param offeredInterface L'interface qu'il offre
	 * @param methodNamesMap La correspondance des appels de méthodes
	 * @return la classe
	 * @throws Exception exception
	 */
	public static Class<?> makeConnectorClassJavassist(String connectorCanonicalClassName,
			Class<?> connectorSuperClass,
			Class<?> connectorImplementedInterface,
			Class<?> offeredInterface,
			HashMap<String, String> methodNamesMap) throws Exception{
		ClassPool pool = ClassPool.getDefault() ;
		CtClass cs = pool.get(connectorSuperClass.getCanonicalName()) ;
		CtClass cii = pool.get(connectorImplementedInterface.getCanonicalName()) ;
		//CtClass oi = pool.get(offeredInterface.getCanonicalName()) ;
		CtClass connectorCtClass = pool.makeClass(connectorCanonicalClassName) ;
		
		connectorCtClass.setSuperclass(cs) ;
		Method[] methodsToImplement = connectorImplementedInterface.getDeclaredMethods() ;
		
		for (int i = 0 ; i < methodsToImplement.length ; i++) {
			String source = "public " ;
			source += methodsToImplement[i].getReturnType().getName() + " " ;
			source += methodsToImplement[i].getName() + "(" ;
			Class<?>[] pt = methodsToImplement[i].getParameterTypes() ;
			String callParam = "" ;
			for (int j = 0 ; j < pt.length ; j++) {
				String pName = "aaa" + j ;
				source += pt[j].getCanonicalName() + " " + pName ;
				callParam += pName ;
				if (j < pt.length - 1) {
					source += ", " ;
					callParam += ", " ;
				}
			}
			source += ")" ;
			Class<?>[] et = methodsToImplement[i].getExceptionTypes() ;
			if(et != null && et.length > 0) {
				source += " throws ";
				for (int z = 0 ; z < et.length ; z++) {
					source += et[z].getCanonicalName() ;
					if (z < et.length - 1) {
						source += "," ;
					}
				}
			}
			source += "\n{ return ((" ;
			source += offeredInterface.getCanonicalName() + ")this.offering)." ;
			source += methodNamesMap.get(methodsToImplement[i].getName()) ;
			source += "(" + callParam + ") ;\n}" ;
			CtMethod theCtMethod = CtMethod.make(source, connectorCtClass) ;
			connectorCtClass.addMethod(theCtMethod) ;
		}
		connectorCtClass.setInterfaces(new CtClass[]{cii}) ;
		//cii.detach() ;
		//cs.detach() ;
		//oi.detach() ;
		Class<?> ret = connectorCtClass.toClass() ;
		connectorCtClass.detach() ;
		return ret ;
	}

	/**
	 * @see fr.sorbonne_u.components.AbstractComponent#finalise()
	 */
	@Override
	public void			finalise() throws Exception
	{
		if (this.nextRequestTaskFuture != null &&
				!(this.nextRequestTaskFuture.isCancelled() ||
						this.nextRequestTaskFuture.isDone())) {
			this.nextRequestTaskFuture.cancel(true) ;
		}
		
		if(isRsopPortConnected)
			this.doPortDisconnection(this.rsop.getPortURI()) ;

		super.finalise() ;
	}

	/**
	 * shut down the component, first canceling any future request generation
	 * already scheduled.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true				// no more preconditions.
	 * post	true				// no more postconditions.
	 * </pre>
	 * 
	 * @see fr.sorbonne_u.components.AbstractComponent#shutdown()
	 */
	@Override
	public void			shutdown() throws ComponentShutdownException
	{

		try {
			this.rsop.unpublishPort() ;
			this.rnip.unpublishPort() ;
			this.rgmip.unpublishPort() ;
			requestAdmissionSubmissionOutboundPort.unpublishPort();
			requestAdmissionNotificationOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}

		super.shutdown();
	}
	
	
	@Override
	public void			shutdownNow() throws ComponentShutdownException
	{

		try {
			
			this.rsop.unpublishPort() ;
			this.rnip.unpublishPort() ;
			this.rgmip.unpublishPort() ;
			requestAdmissionSubmissionOutboundPort.unpublishPort();
			requestAdmissionNotificationOutboundPort.unpublishPort();
		} catch (Exception e) {
			throw new ComponentShutdownException(e) ;
		}

		super.shutdownNow();
	}

	// -------------------------------------------------------------------------
	// Component internal services
	// -------------------------------------------------------------------------

	/**
	 * start the generation and submission of requests.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception		<i>todo.</i>
	 */
	public void			startGeneration() throws Exception
	{
		if (RequestGenerator.DEBUG_LEVEL == 1) {
			this.logMessage("Request generator " + this.rgURI + " starting.") ;
		}
		
		this.generateNextRequest() ;
	}

	/**
	 * stop the generation and submission of requests.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception		<i>todo.</i>
	 */
	public void			stopGeneration() throws Exception
	{
		
		if (RequestGenerator.DEBUG_LEVEL == 1) {
			this.logMessage("Request generator " + this.rgURI + " stopping.") ;
		}
		if (this.nextRequestTaskFuture != null &&
						!(this.nextRequestTaskFuture.isCancelled() ||
										this.nextRequestTaskFuture.isDone())) {
			this.nextRequestTaskFuture.cancel(true) ;
		}
	}

	/**
	 * return the current value of the mean inter-arrival time used to generate
	 * requests.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @return	the current value of the mean inter-arrival time.
	 */
	public double		getMeanInterArrivalTime()
	{
		return this.meanInterArrivalTime ;
	}

	/**
	 * set the value of the mean inter-arrival time used to generate requests.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param miat	new value for the mean inter-arrival time.
	 */
	public void			setMeanInterArrivalTime(double miat)
	{
		assert	miat > 0.0 ;
		this.meanInterArrivalTime = miat ;
	}

	/**
	 * generate a new request with some processing time following an exponential
	 * distribution and then schedule the next request generation in a delay
	 * also following an exponential distribution.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	true			// no precondition.
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @throws Exception		<i>todo.</i>
	 */
	public void			generateNextRequest() throws Exception
	{
		// generate a random number of instructions for the request.
		long noi =
			(long) this.rng.nextExponential(this.meanNumberOfInstructions) ;
		Request r = new Request(this.rgURI + "-" + this.counter++, noi) ;
		// generate a random delay until the next request generation.
		long interArrivalDelay =
				(long) this.rng.nextExponential(this.meanInterArrivalTime) ;

		if (RequestGenerator.DEBUG_LEVEL == 2) {
			this.logMessage(
					"Request generator " + this.rgURI + 
					" submitting request " + r.getRequestURI() + " at " +
					TimeProcessing.toString(System.currentTimeMillis() +
														interArrivalDelay) +
			" with number of instructions " + noi) ;
		}
		
		// submit the current request.
		this.rsop.sendRequestAndNotify(r) ;
		// schedule the next request generation.
		this.nextRequestTaskFuture =
			this.scheduleTask(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						try {
						
							((RequestGenerator)this.getOwner()).
								generateNextRequest() ;
						} catch (Exception e) {
							throw new RuntimeException(e) ;
						}
					}
				},
				TimeManagement.acceleratedDelay(interArrivalDelay),
				TimeUnit.MILLISECONDS) ;
		
	}

	/**
	 * process an end of execution notification for a request r previously
	 * submitted. 
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	r != null
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param r	request that just terminated.
	 * @throws Exception		<i>todo.</i>
	 */
	@Override
	public void			acceptRequestTerminationNotification(RequestI r)
	throws Exception
	{
		assert	r != null ;

		if (RequestGenerator.DEBUG_LEVEL == 2) {
			this.logMessage("Request generator " + this.rgURI +
							" is notified that request "+ r.getRequestURI() +
							" has ended.") ;
		}
	}
	
	
	/**
	 * Cette méthode correspond à la demande de l'application au centre de calcul, d'héberger son application.
	 * En effet, le RequestGenerator envoit une demande d'hébergement au Controlleur d'admission via un port configuré dans le constructeur et y 
	 * envoit en même temps son port de notification lorsqu'il reçoit la réponse, le RequestGenerator connecte son port de soumission avec l'uri fournit
	 * par la réponse du Controlleur d'admission, ici nous faisont la connection avec un connecteur générer par Javassist, nous avons intentionnellement changé la 
	 * méthode soumission de requête afin d'utiliser Javassist.
	 * @return true si le controlleur d'admission accepte/ false sinon
	 * @throws Exception exception
	 */
	public boolean askAdmissionControler() throws Exception{

	
		requestAdmission.setRequestGeneratorManagementInboundPortURI(rgmip.getPortURI());
		// Soumission de la demande d'hebergement (recupere le port de soumission de Request Dispatcher)
		String reqSubInboundPortURI = null;
		try {
			requestAdmission = requestAdmissionSubmissionOutboundPort.getRequestAdmissionFromAdmissionController(requestAdmission);
			reqSubInboundPortURI=requestAdmission.getRequestSubmissionPortURI();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		// Desinscription du port de soumission du Controleur d'Admission
		try {
			requestAdmissionSubmissionOutboundPort.unpublishPort();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
		
		if(reqSubInboundPortURI==null) {
			logMessage("Request generator " + this.rgURI +" : Refus du controleur d'admission");
			return false;
			
		}
		
		
		logMessage("Request generator " + this.rgURI +" : Acceptation de la demande par le controleur d'admission");
		
		// Connexion du port de soumission vers le Request Dispatcher
		try {
			HashMap<String, String> methodmap = new HashMap<>();
			methodmap.put("sendRequest", "submitRequest");
			methodmap.put("sendRequestAndNotify", "submitRequestAndNotify");
			Class<?> connector = makeConnectorClassJavassist("etape1.requestGeneratorForAdmissionControler.connectorJavassist"+cpt++,
					AbstractConnector.class,
					SubmissionISend.class,
					RequestSubmissionI.class,
					methodmap);
			this.doPortConnection(
					this.rsop.getPortURI(),
					reqSubInboundPortURI,
					connector.getCanonicalName()) ;
			isRsopPortConnected=true;
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new ComponentStartException(e) ;
		}

			
		return true;
	}

	
	/**
	 * Cette méthode demande au controlleur d'admission de libérer les ressources qui ont été alloué pour ce RequestGenerator, car
	 * celui-ci a terminé ses demandes de tâches.
	 */
	public void freeAdmissionControlerRessources() {
		try {
		
			requestAdmissionNotificationOutboundPort.acceptRequestTerminationNotification(requestAdmission);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
