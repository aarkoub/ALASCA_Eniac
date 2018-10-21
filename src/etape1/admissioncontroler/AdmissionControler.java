package etape1.admissioncontroler;

import etape1.admissioncontroler.interfaces.AdmissionControlerManagementI;
import etape1.admissioncontroler.interfaces.RequestAdmissionI;
import etape1.admissioncontroler.interfaces.RequestAdmissionSubmissionHandlerI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.datacenter.software.interfaces.RequestNotificationI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestSubmissionI;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationOutboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionInboundPort;

public class AdmissionControler extends AbstractComponent implements AdmissionControlerManagementI, RequestAdmissionSubmissionHandlerI{
	
	String uri;
	String RequestNotificationInboundPortURI;
	
	RequestSubmissionInboundPort reqSubInPort;
	RequestNotificationOutboundPort reqNotifOutboundPort;
	
	public AdmissionControler(String uri, 
			int nbComputers,
			String AdmissionControlerManagementInboundURI,
			String RequestSubmissionInboundPortURI,
			String RequestNotificationInboundPortURI) throws Exception{
		
		super(1,1);
		
		assert nbComputers > 0;
		assert uri != null;
		assert AdmissionControlerManagementInboundURI != null;
		assert RequestSubmissionInboundPortURI != null;
		assert RequestNotificationInboundPortURI != null;
		
		this.uri = uri;
		
		addOfferedInterface(RequestSubmissionI.class);
		reqSubInPort = new RequestSubmissionInboundPort(RequestSubmissionInboundPortURI, this);
		addPort(reqSubInPort);
		reqSubInPort.publishPort();
		
		addRequiredInterface(RequestNotificationI.class);
		reqNotifOutboundPort = new RequestNotificationOutboundPort(this);
		addPort(reqNotifOutboundPort);
		reqNotifOutboundPort.publishPort();
		
		
		
		
	}
	
	




	@Override
	public void acceptRequestAdmissionSubmission(RequestAdmissionI requestai) throws Exception {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void acceptRequestAdmissionSubmissionAndNotify(RequestAdmissionI requestai) throws Exception {
		// TODO Auto-generated method stub
		
	}



	
}
