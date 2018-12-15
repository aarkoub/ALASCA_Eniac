package etape1.requestdispatcher.multi.components;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import etape1.requestdispatcher.interfaces.RequestDispatcherManagementI;
import etape1.requestdispatcher.multi.data.AVMData;
import etape1.requestdispatcher.multi.data.AVMPorts;
import etape1.requestdispatcher.multi.data.AVMUris;
import etape1.requestdispatcher.multi.interfaces.RequestDispatcherMultiVMManagementI;
import etape1.requestdispatcher.multi.ports.RequestDispatcherMultiVMManagementInboundPort;
import etape2.capteurs.implementation.RequestDispatcherDynamicState;
import etape2.capteurs.implementation.RequestDispatcherStaticState;
import etape2.capteurs.interfaces.ApplicationVMStateDataConsumerI;
import etape2.capteurs.interfaces.RequestDispatcherDynamicStateDataI;
import etape2.capteurs.interfaces.RequestDispatcherStaticStateDataI;
import etape2.capteurs.ports.ApplicationVMDynamicStateDataOutboundPort;
import etape2.capteurs.ports.ApplicationVMStaticStateDataOutboundPort;
import etape2.capteurs.ports.RequestDispatcherDynamicStateDataInboundPort;
import etape2.capteurs.ports.RequestDispatcherStaticStateDataInboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.connectors.DataConnector;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.interfaces.DataOfferedI.DataI;
import fr.sorbonne_u.datacenter.TimeManagement;
import fr.sorbonne_u.datacenter.connectors.ControlledDataConnector;
import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerDynamicStateI;
import fr.sorbonne_u.datacenter.hardware.computers.interfaces.ComputerStaticStateI;
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




public class RequestDispatcherMultiVM extends AbstractComponent implements RequestDispatcherMultiVMManagementI,
RequestSubmissionHandlerI,
RequestNotificationHandlerI,
ApplicationVMStateDataConsumerI,
PushModeControllingI{

	protected String rd_uri;
	protected RequestDispatcherMultiVMManagementInboundPort requestDispatcherMultiVMManagementInboundPort;
	protected String requestNotificationInboundPortURI;
	
	//connecteur pour le generateur
	protected RequestSubmissionInboundPort requestSubmissionInboundPort;
	protected RequestNotificationOutboundPort requestNotificationOutboundPort;
	
	//connecteur pour la VM
	protected List<AVMData> avms;
	protected int chooser;

	protected Map<String,Date> t1,t2;
	protected ScheduledFuture<?> pushingFuture;
	
	protected RequestDispatcherDynamicStateDataInboundPort requestDispatcherDynamicStateDataInboundPort;
	protected RequestDispatcherStaticStateDataInboundPort requestDispatcherStaticStateDataInboundPort;
	
	
	
	
	public RequestDispatcherMultiVM(String rd_uri,
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
		
		requestDispatcherMultiVMManagementInboundPort = new RequestDispatcherMultiVMManagementInboundPort(managementInboundPortURI, this);
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
		
		
		avms = new ArrayList<>();
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
			avms.add(data);
			
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
		t1 = new HashMap<>();
		t2 = new HashMap<>();
		
		requestDispatcherStaticStateDataInboundPort = new RequestDispatcherStaticStateDataInboundPort(
				requestDispatcherStaticStateDataInboundPortURI, this);
		requestDispatcherStaticStateDataInboundPort.publishPort();
		addPort(requestDispatcherStaticStateDataInboundPort);
		
		requestDispatcherDynamicStateDataInboundPort = new RequestDispatcherDynamicStateDataInboundPort(
				requestDispatcherStaticStateDataInboundPortURI, this);
		requestDispatcherDynamicStateDataInboundPort.publishPort();
		addPort(requestDispatcherDynamicStateDataInboundPort);
		
		
		
		
		
	}
	
	
	
	@Override
	public void start() throws ComponentStartException {
		super.start();
		
	}
	
	public void startPortConnection() {
		try {
			doPortConnection(requestNotificationOutboundPort.getPortURI(), requestNotificationInboundPortURI,
					RequestNotificationConnector.class.getCanonicalName());
			
			for(AVMData data  : avms) {
				doPortConnection(data.getAvmports().getRequestSubmissionOutboundPort().getPortURI(),
						data.getAvmuris().getRequestSubmissionInboundPortVM(),
						RequestSubmissionConnector.class.getCanonicalName());
			
				doPortConnection(data.getAvmports().getAvmStaticStateDataOutboundPort().getPortURI(), data.getAvmuris().getApplicationVMStaticStateDataInboundPortURI(), DataConnector.class.getCanonicalName());
				doPortConnection(data.getAvmports().getAvmDynamicStateDataOutboundPort().getPortURI(),data.getAvmuris().getApplicationVMDynamicStateDataInboundPortURI(),ControlledDataConnector.class.getCanonicalName());
				
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
		
		for(int i = 0; i < avms.size(); i++) {
			doPortDisconnection(
					avms.get(i).getAvmports().getRequestSubmissionOutboundPort().getPortURI());
		}
		
		super.finalise() ;
	}
	
	@Override
	public void shutdown() throws ComponentShutdownException {
	
		try {
			requestSubmissionInboundPort.unpublishPort();
			requestDispatcherMultiVMManagementInboundPort.unpublishPort();
			requestNotificationOutboundPort.unpublishPort();
			for(AVMData data : avms) {
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
			for(AVMData data : avms) {
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
		chooser =  chooser%avms.size();
		avms.get(chooser).getAvmports().getRequestSubmissionOutboundPort().submitRequest(r);
		chooser++;
		
		Date r1 = new Date();
		
		t1.put(r.getRequestURI(), r1);
		
	}

	@Override
	public void acceptRequestSubmissionAndNotify(RequestI r) throws Exception {
		logMessage("RequestDispatcher "+rd_uri+" requete reçue avec notification: "+r.getRequestURI());
		chooser =  chooser%avms.size();
		avms.get(chooser).getAvmports().getRequestSubmissionOutboundPort().submitRequestAndNotify(r);
		chooser++;
		
		Date r1 = new Date();
		
		t1.put(r.getRequestURI(), r1);
		
	}

	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		Date r2 = new Date();
		t2.put(r.getRequestURI(), r2);
	
		logMessage("Requete terminée : "+r.getRequestURI());
		requestNotificationOutboundPort.notifyRequestTermination(r);
		
		System.out.println(getAverageRequestTime());
		
	}


	@Override
	public boolean removeAVM(String uri) {
		AVMData data = null;
		for(AVMData tmp: avms) {
			if(tmp.getAvmuris().getAVMUri() == uri) {
				data = tmp;
				break;
			}
		}
		
		if(data == null) return false;
		try {
			doPortDisconnection(data.getAvmports().getRequestSubmissionOutboundPort().getPortURI());
			avms.remove(data);
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
		data = new AVMData(avmuris, new AVMPorts(requestSubmissionOutboundPortVM, requestNotificationInboundPortVM));
		avms.add(data);
		
	}

	@Override
	public void connectAVM(String uri) throws Exception {
		for(AVMData data : avms) {
			if(data.getAvmuris().getAVMUri() == uri) {
				doPortConnection(data.getAvmports().getRequestSubmissionOutboundPort().getPortURI(),
						data.getAvmuris().getRequestSubmissionInboundPortVM(),
						RequestSubmissionConnector.class.getCanonicalName());
				return;
			}
		}
		
	}
	
	public long getAverageRequestTime(){
		
		long average=0 ;
		
		for(String reqUri : t1.keySet()){
			
			Date r1 = t1.get(reqUri);
			Date r2 = t2.get(reqUri);
			
			average += (r2.getTime()-r1.getTime());
			
		}
		
		return average/t1.size();
		
	}

	@Override
	public void acceptApplicationVMStaticData(String avmURI, ApplicationVMStaticStateI staticState) throws Exception {
		logMessage("staticState : "+avmURI);
		
		for(Integer idCore : staticState.getIdCores().keySet()){
			
			logMessage("core_"+idCore+" on processor_"+staticState.getIdCores().get(idCore));
			
		}

		
	}

	@Override
	public void acceptApplicationVMDynamicData(String avmURI, ApplicationVMDynamicStateI dynamicState)
			throws Exception {
		logMessage("dynamicState : "+avmURI);
		logMessage("isIdle : "+dynamicState.isIdle());
		
	}

	public RequestDispatcherDynamicStateDataI getDynamicState() {
		
		return new RequestDispatcherDynamicState();
	}
	
	public RequestDispatcherStaticStateDataI getStaticState() {
		return new RequestDispatcherStaticState();
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
								((RequestDispatcherMultiVM)this.getOwner()).
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

	protected void sendDynamicState() throws Exception {
		if (this.requestDispatcherDynamicStateDataInboundPort.connected()) {
			RequestDispatcherDynamicStateDataI rdds = this.getDynamicState() ;
			this.requestDispatcherDynamicStateDataInboundPort.send(rdds) ;
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
								((RequestDispatcherMultiVM)this.getOwner()).
									sendDynamicState(interval, n) ;
							} catch (Exception e) {
								throw new RuntimeException(e) ;
							}
						}
					},
					TimeManagement.acceleratedDelay(interval),
					TimeUnit.MILLISECONDS) ;
		
	}

	protected void sendDynamicState(int interval, int n) {
		// TODO Auto-generated method stub
		
	}

	private void sendStaticState() throws Exception {
		if (this.requestDispatcherStaticStateDataInboundPort.connected()) {
			RequestDispatcherStaticStateDataI rdds = this.getStaticState() ;
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