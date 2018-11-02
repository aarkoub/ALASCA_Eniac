package etape1.requestdispatcher.multi;

import java.util.ArrayList;
import java.util.List;

import etape1.requestdispatcher.multi.interfaces.RequestDispatcherMultiVMManagementI;
import etape1.requestdispatcher.multi.ports.RequestDispatcherMultiVMManagementInboundPort;
import etape1.requestdistributor.interfaces.RequestDispatcherManagementI;
import etape1.requestdistributor.ports.RequestDispatcherManagementInboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
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
RequestNotificationHandlerI{

	private String rd_uri;
	private RequestDispatcherMultiVMManagementInboundPort requestDispatcherMultiVMManagementInboundPort;
	private String requestNotificationInboundPortURI;
	
	//connecteur pour le generateur
	private RequestSubmissionInboundPort requestSubmissionInboundPort;
	private RequestNotificationOutboundPort requestNotificationOutboundPort;
	
	//connecteur pour la VM
	private List<AVMData> avms;
	private int chooser;
	
	
	public RequestDispatcherMultiVM(String rd_uri,
			String managementInboundPortURI,
			String requestSubmissionInboundPortURI,
			String requestNotificationInboundPortURI,
			ArrayList<AVMUris> uris) throws Exception {
		

		super(1,1);
		
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
		
		for(int i = 0; i < uris.size(); i++) {
			requestNotificationInboundPortVM = new RequestNotificationInboundPort(uris.get(i).getRequestNotificationInboundPortVM(), this);
			addPort(requestNotificationInboundPortVM);
			requestNotificationInboundPortVM.publishPort();
			requestSubmissionOutboundPortVM = new RequestSubmissionOutboundPort(this);
			addPort(requestSubmissionOutboundPortVM);
			requestSubmissionOutboundPortVM.publishPort();
			data = new AVMData(uris.get(i), new AVMPorts(requestSubmissionOutboundPortVM, requestNotificationInboundPortVM));
			avms.add(data);
		}
		
		 
	}
	
	@Override
	public void start() throws ComponentStartException {
		
		super.start();
		
		try {
			doPortConnection(requestNotificationOutboundPort.getPortURI(), requestNotificationInboundPortURI,
					RequestNotificationConnector.class.getCanonicalName());
			
			for(int i = 0; i < avms.size(); i++) {
				doPortConnection(avms.get(i).getAvmports().getRequestSubmissionOutboundPort().getPortURI(),
						avms.get(i).getAvmuris().getRequestSubmissionInboundPortVM(),
						RequestSubmissionConnector.class.getCanonicalName());
			}
			
		} catch (Exception e) {
			throw new ComponentStartException(e) ;
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
			for(int i = 0; i < avms.size(); i++) {
				avms.get(i).getAvmports().getRequestSubmissionOutboundPort().unpublishPort();
				avms.get(i).getAvmports().getRequestNotificationInboundPort().unpublishPort();
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
			for(int i = 0; i < avms.size(); i++) {
				avms.get(i).getAvmports().getRequestSubmissionOutboundPort().unpublishPort();
				avms.get(i).getAvmports().getRequestNotificationInboundPort().unpublishPort();
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
		
	}

	@Override
	public void acceptRequestSubmissionAndNotify(RequestI r) throws Exception {
		logMessage("RequestDispatcher "+rd_uri+" requete reçue avec notification: "+r.getRequestURI());
		chooser =  chooser%avms.size();
		avms.get(chooser).getAvmports().getRequestSubmissionOutboundPort().submitRequestAndNotify(r);
		chooser++;
		
	}

	@Override
	public void acceptRequestTerminationNotification(RequestI r) throws Exception {
		logMessage("Requete terminé : "+r.getRequestURI());
		requestNotificationOutboundPort.notifyRequestTermination(r);
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
		doPortConnection(requestSubmissionOutboundPortVM.getPortURI(),
				avmuris.getRequestSubmissionInboundPortVM(),
				RequestSubmissionConnector.class.getCanonicalName());
	}
	

}