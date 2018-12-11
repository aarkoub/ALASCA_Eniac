package etape1.requestdispatcher.multi.components;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import etape1.requestdispatcher.interfaces.RequestDispatcherManagementI;
import etape1.requestdispatcher.multi.data.AVMData;
import etape1.requestdispatcher.multi.data.AVMPorts;
import etape1.requestdispatcher.multi.data.AVMUris;
import etape1.requestdispatcher.multi.interfaces.RequestDispatcherMultiVMManagementI;
import etape1.requestdispatcher.multi.ports.RequestDispatcherMultiVMManagementInboundPort;
import etape2.capteurs.interfaces.ApplicationVMStateDataConsumerI;
import etape2.capteurs.ports.ApplicationVMDynamicStateDataOutboundPort;
import etape2.capteurs.ports.ApplicationVMStaticStateDataOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.connectors.DataConnector;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.connectors.ControlledDataConnector;
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
ApplicationVMStateDataConsumerI{

	private String rd_uri;
	private RequestDispatcherMultiVMManagementInboundPort requestDispatcherMultiVMManagementInboundPort;
	private String requestNotificationInboundPortURI;
	
	//connecteur pour le generateur
	private RequestSubmissionInboundPort requestSubmissionInboundPort;
	private RequestNotificationOutboundPort requestNotificationOutboundPort;
	
	//connecteur pour la VM
	private List<AVMData> avms;
	private int chooser;
;
	private Map<String,Date> t1,t2;
	
	private ApplicationVMDynamicStateDataOutboundPort[] vmDynamicOutports;
	private ApplicationVMStaticStateDataOutboundPort[] vmStaticOutports;
	
	
	public RequestDispatcherMultiVM(String rd_uri,
			String managementInboundPortURI,
			String requestSubmissionInboundPortURI,
			String requestNotificationInboundPortURI,
			ArrayList<AVMUris> uris) throws Exception {
		

		super(rd_uri,1,1);
		
		assert rd_uri != null;
		assert	managementInboundPortURI != null ;
		assert	requestSubmissionInboundPortURI != null ;
		assert	requestNotificationInboundPortURI != null ;
		assert uris != null;

		
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
		
		vmDynamicOutports = new ApplicationVMDynamicStateDataOutboundPort[uris.size()];
		vmStaticOutports = new ApplicationVMStaticStateDataOutboundPort[uris.size()];
		
		for(int i = 0; i < uris.size(); i++) {
			
			
			
			requestNotificationInboundPortVM = new RequestNotificationInboundPort(uris.get(i).getRequestNotificationInboundPortVM(), this);
			addPort(requestNotificationInboundPortVM);
			requestNotificationInboundPortVM.publishPort();
			requestSubmissionOutboundPortVM = new RequestSubmissionOutboundPort(this);
			addPort(requestSubmissionOutboundPortVM);
			requestSubmissionOutboundPortVM.publishPort();
			data = new AVMData(uris.get(i), new AVMPorts(requestSubmissionOutboundPortVM, requestNotificationInboundPortVM));
			avms.add(data);
			
			String avmDynamicStateDataInboundPortURI = data.getAvmuris().getAVMUri() + "-avmdsdibp" ; 
			String avmStaticStateDataInboundPortURI = data.getAvmuris().getAVMUri() + "-avmssdibp" ; 
						
			vmStaticOutports[i] = new ApplicationVMStaticStateDataOutboundPort(this, data.getAvmuris().getAVMUri());
			addPort(vmStaticOutports[i]);
			vmStaticOutports[i].publishPort();
		
			
			
			vmDynamicOutports[i] = new ApplicationVMDynamicStateDataOutboundPort(this, data.getAvmuris().getAVMUri());
			addPort(vmDynamicOutports[i]);
			vmDynamicOutports[i].publishPort();
			
			
			data.setAvmDynamicStateDataInboundPortURI(avmDynamicStateDataInboundPortURI);
			data.setAvmStaticStateDataInboundPortURI(avmStaticStateDataInboundPortURI);
			
			data.getAvmports().setAvmDynamicStateDataOutboundPort(vmDynamicOutports[i]);
			data.getAvmports().setAvmStaticStateDataOutboundPort(vmStaticOutports[i]);
			
		}
		
		this.toggleLogging();
		this.toggleTracing();
		t1 = new HashMap<>();
		t2 = new HashMap<>();
		
		
		
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
		for(int i = 0; i < avms.size(); i++) {
			AVMData data = avms.get(i); 
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
		logMessage("staticState : "+avmURI+' '+staticState.getTimeStamp());
		
	}

	@Override
	public void acceptApplicationVMDynamicData(String avmURI, ApplicationVMDynamicStateI dynamicState)
			throws Exception {
		logMessage("dynamicState : "+avmURI+" "+dynamicState.getTimeStamp());
		
	}


	

}