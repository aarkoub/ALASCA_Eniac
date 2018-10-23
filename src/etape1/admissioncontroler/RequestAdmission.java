package etape1.admissioncontroler;

import etape1.admissioncontroler.interfaces.RequestAdmissionI;

public class RequestAdmission implements RequestAdmissionI {
	private static final long serialVersionUID = 1L;
	
	private String RequestNotificationPortURI;
	private int nbResources = 1;
	private double freq;
	private String requestSubmissionPortURI;
	
	
	public RequestAdmission(String RequestNotificationPortURI) {
		this.RequestNotificationPortURI = RequestNotificationPortURI;
	}
	
	@Override
	public String getRequestNotificationPortURI() {
		return RequestNotificationPortURI;
	}
	
	@Override
	public void setRequestSubmissionPortURI(String uri) {
		requestSubmissionPortURI = uri;
	}
	
	@Override
	public String getRequestSubmissionPortURI() {
		return requestSubmissionPortURI;
	}
	

}
