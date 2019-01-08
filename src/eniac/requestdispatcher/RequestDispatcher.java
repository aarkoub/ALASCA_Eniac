package eniac.requestdispatcher;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import eniac.applicationvm.interfaces.ApplicationVMStateDataConsumerI;
import eniac.applicationvm.ports.ApplicationVMDynamicStateDataOutboundPort;
import eniac.applicationvm.ports.ApplicationVMStaticStateDataOutboundPort;
import eniac.requestdispatcher.data.AVMData;
import eniac.requestdispatcher.data.AVMPorts;
import eniac.requestdispatcher.data.AVMUris;
import eniac.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;
import eniac.requestdispatcher.interfaces.RequestDispatcherManagementI;
import eniac.requestdispatcher.interfaces.RequestDispatcherStaticStateI;
import eniac.requestdispatcher.ports.RequestDispatcherDynamicStateDataInboundPort;
import eniac.requestdispatcher.ports.RequestDispatcherManagementInboundPort;
import eniac.requestdispatcher.ports.RequestDispatcherStaticStateDataInboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.connectors.DataConnector;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.TimeManagement;
import fr.sorbonne_u.datacenter.connectors.ControlledDataConnector;
import fr.sorbonne_u.datacenter.interfaces.PushModeControllingI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMDynamicStateI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMStaticStateI;
import fr.sorbonne_u.datacenter.software.connectors.RequestNotificationConnector;
import fr.sorbonne_u.datacenter.software.connectors.RequestSubmissionConnector;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionHandlerI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionOutboundPort;




public class RequestDispatcher extends AbstractComponent implements RequestDispatcherManagementI,
RequestSubmissionHandlerI,
RequestNotificationHandlerI,
ApplicationVMStateDataConsumerI,
PushModeControllingI{
	public static final int AVG_NUMBER_RQ = 50;
	protected String rd_uri;
	protected RequestDispatcherManagementInboundPort requestDispatcherMultiVMManagementInboundPort;
	protected String requestNotificationInboundPortURI;
	
	//connecteur pour le generateur
	protected RequestSubmissionInboundPort requestSubmissionInboundPort;
	protected RequestNotificationOutboundPort requestNotificationOutboundPort;
	
	//connecteur pour la VM
	protected Map<String, AVMData> avms;

	protected ScheduledFuture<?> pushingFuture;
	
	protected RequestDispatcherDynamicStateDataInboundPort requestDispatcherDynamicStateDataInboundPort;
	protected RequestDispatcherStaticStateDataInboundPort requestDispatcherStaticStateDataInboundPort;
	
	protected Map<String, ApplicationVMDynamicStateI> avmDynamicStateMap;
	protected Map<String, ApplicationVMStaticStateI> avmStaticStateMap;
	
	
	protected AverageCompute avgcompute;
	
	protected Map<String, Double> avmScores;
	
	public RequestDispatcher(String rd_uri,
			String managementInboundPortURI,
			String requestSubmissionInboundPortURI,
			String requestNotificationInboundPortURI,
			String requestDispatcherDynamicStateDataInboundPortURI,
			String requestDispatcherStaticStateDataInboundPortURI,
			ArrayList<AVMUris> uris) throws Exception {
		

		super(rd_uri,1,1);
		
		assert rd_uri != null;
		assert	managementInboundPortURI != null ;
		assert	requestSubmissionInboundPortURI != null ;
		assert	requestNotificationInboundPortURI != null ;
		assert uris != null;
		assert requestDispatcherDynamicStateDataInboundPortURI!=null;
		assert requestDispatcherStaticStateDataInboundPortURI!=null;

		
		this.rd_uri = rd_uri;
		
		this.requestNotificationInboundPortURI =
				requestNotificationInboundPortURI ;
		
		
		addOfferedInterface(RequestDispatcherManagementI.class);
		
		requestDispatcherMultiVMManagementInboundPort = new RequestDispatcherManagementInboundPort(managementInboundPortURI, this);
		addPort(requestDispatcherMultiVMManagementInboundPort);
		requestDispatcherMultiVMManagementInboundPort.publishPort();
		
		addOfferedInterface(RequestSubmissionI.class);
		requestSubmissionInboundPort = new RequestSubmissionInboundPort(requestSubmissionInboundPortURI, this);
		addPort(requestSubmissionInboundPort);
		requestSubmissionInboundPort.publishPort();
		
		addRequiredInterface(RequestNotificationI.class);
		requestNotificationOutboundPort = new RequestNotificationOutboundPort(this);
		addPort(requestNotificationOutboundPort);
		requestNotificationOutboundPort.publishPort();
		
		
		avms = new HashMap<>();
		addOfferedInterface(RequestNotificationI.class);
		addRequiredInterface(RequestSubmissionI.class);
		RequestSubmissionOutboundPort requestSubmissionOutboundPortVM;
		RequestNotificationInboundPort requestNotificationInboundPortVM;
		AVMData data;
		

		for(AVMUris uri : uris) {
			
			requestNotificationInboundPortVM = new RequestNotificationInboundPort(uri.getRequestNotificationInboundPortVM(), this);
			addPort(requestNotificationInboundPortVM);
			requestNotificationInboundPortVM.publishPort();
			
			requestSubmissionOutboundPortVM = new RequestSubmissionOutboundPort(this);
			addPort(requestSubmissionOutboundPortVM);
			requestSubmissionOutboundPortVM.publishPort();
			
			data = new AVMData(uri, new AVMPorts(requestSubmissionOutboundPortVM, requestNotificationInboundPortVM));
			avms.put(uri.getAVMUri(), data);
			
			String avmDynamicStateDataInboundPortURI = data.getAvmuris().getAVMUri() + "-avmdsdibp" ; 
			String avmStaticStateDataInboundPortURI = data.getAvmuris().getAVMUri() + "-avmssdibp" ; 
						
			ApplicationVMStaticStateDataOutboundPort vmStaticOutports = new ApplicationVMStaticStateDataOutboundPort(this, data.getAvmuris().getAVMUri());
			addPort(vmStaticOutports);
			vmStaticOutports.publishPort();
		
			
			
			ApplicationVMDynamicStateDataOutboundPort vmDynamicOutports = new ApplicationVMDynamicStateDataOutboundPort(this, data.getAvmuris().getAVMUri());
			addPort(vmDynamicOutports);
			vmDynamicOutports.publishPort();
			
			
			data.setAvmDynamicStateDataInboundPortURI(avmDynamicStateDataInboundPortURI);
			data.setAvmStaticStateDataInboundPortURI(avmStaticStateDataInboundPortURI);
			
			data.getAvmports().setAvmDynamicStateDataOutboundPort(vmDynamicOutports);
			data.getAvmports().setAvmStaticStateDataOutboundPort(vmStaticOutports);
			
		}
		
		this.toggleLogging();
		this.toggleTracing();
		
		addOfferedInterface(RequestDispatcherStaticStateI.class);
		requestDispatcherStaticStateDataInboundPort = new RequestDispatcherStaticStateDataInboundPort(
				requestDispatcherStaticStateDataInboundPortURI, this);
		requestDispatcherStaticStateDataInboundPort.publishPort();
		addPort(requestDispatcherStaticStateDataInboundPort);
		
		
		addOfferedInterface(RequestDispatcherDynamicStateI.class);
		requestDispatcherDynamicStateDataInboundPort = new RequestDispatcherDynamicStateDataInboundPort(
				requestDispatcherDynamicStateDataInboundPortURI, this);
		requestDispatcherDynamicStateDataInboundPort.publishPort();
		addPort(requestDispatcherDynamicStateDataInboundPort);
		
		
		avmDynamicStateMap = new HashMap<>();
		avmStaticStateMap = new HashMap<>();
		
		avgcompute = new AverageCompute(AVG_NUMBER_RQ);
		
		avmScores = new HashMap<>();
		
		for(AVMUris uri : uris) {
			avmScores.put(uri.getAVMUri(), (double)0);
		}
	}
	
	
 
	public String chooseAVMToCompute() {
		String avm = avms.keySet().stream().findFirst().get();
		double score = avmScores.get(avm);
		for(Map.Entry<String, Double> entry: avmScores.entrySet()) {
			if(entry.getValue() < score && avms.get(entry.getKey()) != null) {
				score = entry.getValue();
				avm = entry.getKey();
			}
		}
		return avm;
	}
	
	
	@Override
	public void start() throws ComponentStartException {
		super.start();
		
	}
	
	public void startPortConnection() {
		try {
			doPortConnection(requestNotificationOutboundPort.getPortURI(), requestNotificationInboundPortURI,
					RequestNotificationConnector.class.getCanonicalName());
			
			for(AVMData data  : avms.values()) {
				doPortConnection(data.getAvmports().getRequestSubmissionOutboundPort().getPortURI(),
						data.getAvmuris().getRequestSubmissionInboundPortVM(),
						RequestSubmissionConnector.class.getCanonicalName());
			
				doPortConnection(data.getAvmports().getAvmStaticStateDataOutboundPort().getPortURI(), data.getAvmuris().getApplicationVMStaticStateDataInboundPortURI(), DataConnector.class.getCanonicalName());
				doPortConnection(data.getAvmports().getAvmDynamicStateDataOutboundPort().getPortURI(),data.getAvmuris().getApplicationVMDynamicStateDataInboundPortURI(), ControlledDataConnector.class.getCanonicalName());
				
				data.getAvmports().getAvmDynamicStateDataOutboundPort().startUnlimitedPushing(500);
			
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	
	@Override
	public void			finalise() throws Exception
	{
		this.doPortDisconnection(
							this.requestNotificationOutboundPort.getPortURI()) ;
		for(Map.Entry<String, AVMData> d: avms.entrySet()) {
			doPortDisconnection(
					d.getValue().getAvmports().getRequestSubmissionOutboundPort().getPortURI());
		}
		
		super.finalise() ;
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException {
	
		try {
			requestSubmissionInboundPort.unpublishPort();
			requestDispatcherMultiVMManagementInboundPort.unpublishPort();
			requestNotificationOutboundPort.unpublishPort();
			for(AVMData data : avms.values()) {
				
				data.getAvmports().getRequestSubmissionOutboundPort().unpublishPort();
				data.getAvmports().getRequestNotificationInboundPort().unpublishPort();
				data.getAvmports().getAvmStaticStateDataOutboundPort().unpublishPort();
				data.getAvmports().getAvmDynamicStateDataOutboundPort().unpublishPort();
			}
			
		} catch (Exception e) {
			throw new ComponentShutdownException(
					"processor services outbound port disconnection"
					+ " error", e) ;
		}
		
		
		super.shutdown();
		
		
		
	}
	
	@Override
	public void shutdownNow() throws ComponentShutdownException {
	
		try {
			requestSubmissionInboundPort.unpublishPort();
			requestDispatcherMultiVMManagementInboundPort.unpublishPort();
			requestNotificationOutboundPort.unpublishPort();
			for(AVMData data : avms.values()) {
				data.getAvmports().getRequestSubmissionOutboundPort().unpublishPort();
				data.getAvmports().getRequestNotificationInboundPort().unpublishPort();
				data.getAvmports().getAvmStaticStateDataOutboundPort().unpublishPort();
				data.getAvmports().getAvmDynamicStateDataOutboundPort().unpublishPort();
			}
			
		} catch (Exception e) {
			throw new ComponentShutdownException(
					"processor services outbound port disconnection"
					+ " error", e) ;
		}
		
		
		super.shutdownNow();
		
		
		
	}



	@Override
	public void acceptRequestSubmission(RequestI r) throws Exception {
		logMessage("RequestDispatcher "+rd_uri+" requete reçue "+r.getRequestURI());
		String choice =  chooseAVMToCompute();
		avms.get(choice).getAvmports().getRequestSubmissionOutboundPort().submitRequest(r);
		
		Date r1 = new Date();
		
		avgcompute.addStartTime(r.getRequestURI(), r1);
		
	}

	@Override
	public void acceptRequestSubmissionAndNotify(RequestI r) throws Exception {
		logMessage("RequestDispatcher "+rd_uri+" requete reçue avec notification: "+r.getRequestURI());
		String choice =  chooseAVMToCompute();
		avms.get(choice).getAvmports().getRequestSubmissionOutboundPort().submitRequestAndNotify(r);
		
		Date r1 = new Date();
		
		avgcompute.addStartTime(r.getRequestURI(), r1);
		
	}

	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		Date r2 = new Date();
		avgcompute.addEndTime(r.getRequestURI(), r2);
	
		logMessage("Requete terminée : "+r.getRequestURI());
		requestNotificationOutboundPort.notifyRequestTermination(r);
		
		
	}


	@Override
	public boolean removeAVM(String uri) {
		AVMData data = null;
		for(AVMData tmp: avms.values()) {
			if(tmp.getAvmuris().getAVMUri() == uri) {
				data = tmp;
				break;
			}
		}
		
		if(data == null) return false;
		try {
			doPortDisconnection(data.getAvmports().getRequestSubmissionOutboundPort().getPortURI());
			data.getAvmports().getRequestSubmissionOutboundPort().unpublishPort();
			
			doPortDisconnection(data.getAvmports().getAvmDynamicStateDataOutboundPort().getPortURI());
			data.getAvmports().getAvmDynamicStateDataOutboundPort().unpublishPort();
			
			doPortDisconnection(data.getAvmports().getAvmStaticStateDataOutboundPort().getPortURI());
			data.getAvmports().getAvmStaticStateDataOutboundPort().unpublishPort();
			
			avms.remove(uri);
			avmDynamicStateMap.remove(uri);
			avmStaticStateMap.remove(uri);
			avmScores.remove(uri);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return false;
	}

	@Override
	public int getNbAvm() {
		return avms.size();
	}

	@Override
	public void addAVM(AVMUris avmuris) throws Exception {
		RequestSubmissionOutboundPort requestSubmissionOutboundPortVM;
		RequestNotificationInboundPort requestNotificationInboundPortVM;
		AVMData data;
		
		requestNotificationInboundPortVM = new RequestNotificationInboundPort(avmuris.getRequestNotificationInboundPortVM(), this);
		addPort(requestNotificationInboundPortVM);
		requestNotificationInboundPortVM.publishPort();
		requestSubmissionOutboundPortVM = new RequestSubmissionOutboundPort(this);
		addPort(requestSubmissionOutboundPortVM);
		requestSubmissionOutboundPortVM.publishPort();
		
		ApplicationVMDynamicStateDataOutboundPort app_dynamic_outport =
				new ApplicationVMDynamicStateDataOutboundPort(this, avmuris.getAVMUri());
		addPort(app_dynamic_outport);
		app_dynamic_outport.publishPort();
		
		ApplicationVMStaticStateDataOutboundPort app_static_outport = 
				new ApplicationVMStaticStateDataOutboundPort(this, avmuris.getAVMUri());
		addPort(app_static_outport);
		app_static_outport.publishPort();
		
		AVMPorts ports=new AVMPorts(requestSubmissionOutboundPortVM, requestNotificationInboundPortVM,
				app_dynamic_outport, app_static_outport);
		data = new AVMData(avmuris, ports);
		avms.put(avmuris.getAVMUri(), data);
		
		
	}

	@Override
	public void connectAVM(String uri) throws Exception {
		for(AVMData data : avms.values()) {
			if(data.getAvmuris().getAVMUri() == uri) {
				doPortConnection(data.getAvmports().getRequestSubmissionOutboundPort().getPortURI(),
						data.getAvmuris().getRequestSubmissionInboundPortVM(),
						RequestSubmissionConnector.class.getCanonicalName());
				
				doPortConnection(data.getAvmports().getAvmStaticStateDataOutboundPort().getPortURI(), 
						data.getAvmuris().getApplicationVMStaticStateDataInboundPortURI(),
						DataConnector.class.getCanonicalName());
				doPortConnection(data.getAvmports().getAvmDynamicStateDataOutboundPort().getPortURI(),
						data.getAvmuris().getApplicationVMDynamicStateDataInboundPortURI(), 
						ControlledDataConnector.class.getCanonicalName());
				
				data.getAvmports().getAvmDynamicStateDataOutboundPort().startUnlimitedPushing(500);
				
				avmScores.put(uri, (double)0);
				
				return;
			}
		}
		
	}
	
	

	@Override
	public void acceptApplicationVMStaticData(String avmURI, ApplicationVMStaticStateI staticState) throws Exception {
		avmStaticStateMap.put(avmURI, staticState);
		logMessage("staticState : "+avmURI);
		
		for(Integer idCore : staticState.getIdCores().keySet()){
			
			logMessage("core_"+idCore+" on processor_"+staticState.getIdCores().get(idCore));
			
		}

		
	}

	@Override
	public void acceptApplicationVMDynamicData(String avmURI, ApplicationVMDynamicStateI dynamicState)
			throws Exception {
		
		avmDynamicStateMap.put(avmURI, dynamicState);
		avmScores.put(avmURI, dynamicState.getScore());
		logMessage("dynamicState : "+avmURI);
		logMessage("isIdle : "+dynamicState.isIdle());
		
	}

	public RequestDispatcherDynamicStateI getDynamicState() {
		
		return new RequestDispatcherDynamicState(avgcompute.getAverage(), avmDynamicStateMap, avmScores);
	}
	
	public RequestDispatcherStaticStateI getStaticState() {
		return new RequestDispatcherStaticState(avmStaticStateMap);
	}

	@Override
	public void startUnlimitedPushing(int interval) throws Exception {

		// first, send the static state if the corresponding port is connected
		this.sendStaticState() ;

		this.pushingFuture =
			this.scheduleTaskAtFixedRate(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								((RequestDispatcher)this.getOwner()).
											sendDynamicState() ;
							} catch (Exception e) {
								throw new RuntimeException(e) ;
							}
						}
					},
					TimeManagement.acceleratedDelay(interval),
					TimeManagement.acceleratedDelay(interval),
					TimeUnit.MILLISECONDS) ;
		
	}

	public void sendDynamicState() throws Exception {
		if (this.requestDispatcherDynamicStateDataInboundPort.connected()) {
			try {
				RequestDispatcherDynamicStateI rdds = this.getDynamicState() ;
				this.requestDispatcherDynamicStateDataInboundPort.send(rdds) ;
			}catch (Exception e) {
				e.printStackTrace();
			}
			
			
		}
		
	}

	@Override
	public void startLimitedPushing(int interval, int n) throws Exception {
		assert	n > 0 ;

		this.logMessage(this.rd_uri + " startLimitedPushing with interval "
									+ interval + " ms for " + n + " times.") ;

		// first, send the static state if the corresponding port is connected
		this.sendStaticState() ;

		this.pushingFuture =
			this.scheduleTask(
					new AbstractComponent.AbstractTask() {
						@Override
						public void run() {
							try {
								((RequestDispatcher)this.getOwner()).
									sendDynamicState(interval, n) ;
							} catch (Exception e) {
								throw new RuntimeException(e) ;
							}
						}
					},
					TimeManagement.acceleratedDelay(interval),
					TimeUnit.MILLISECONDS) ;
		
	}

	public void sendDynamicState(int interval, final int numberOfRemainingPushes
			) throws Exception
		{
	
			this.sendStaticState() ;
			final int fNumberOfRemainingPushes = numberOfRemainingPushes - 1 ;
			if (fNumberOfRemainingPushes > 0) {
				this.pushingFuture =
						this.scheduleTask(
								new AbstractComponent.AbstractTask() {
									@Override
									public void run() {
										try {
											((RequestDispatcher)this.getOwner()).
												sendDynamicState(
													interval,
													fNumberOfRemainingPushes) ;
										} catch (Exception e) {
											throw new RuntimeException(e) ;
										}
									}
								},
								TimeManagement.acceleratedDelay(interval),
								TimeUnit.MILLISECONDS) ;
			}
		
	}

	public void sendStaticState() throws Exception {
		if (this.requestDispatcherStaticStateDataInboundPort.connected()) {
			RequestDispatcherStaticStateI rdds = this.getStaticState() ;
			this.requestDispatcherStaticStateDataInboundPort.send(rdds) ;
		}
		
	}

	@Override
	public void stopPushing() throws Exception {
		if (this.pushingFuture != null &&
				!(this.pushingFuture.isCancelled() ||
									this.pushingFuture.isDone())) {
			this.pushingFuture.cancel(false) ;
		}
		
	}


	

}