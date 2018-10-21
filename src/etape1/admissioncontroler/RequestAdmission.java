package etape1.admissioncontroler;

import etape1.admissioncontroler.interfaces.RequestAdmissionI;

public class RequestAdmission implements RequestAdmissionI {
	private static final long serialVersionUID = 1L;
	
	private String RequestNotificationPortURI;
	
	
	public RequestAdmission(String RequestNotificationPortURI) {
		this.RequestNotificationPortURI = RequestNotificationPortURI;
	}
	
	@Override
	public String getRequestNotificationPortURI() {
		return RequestNotificationPortURI;
	}

}
