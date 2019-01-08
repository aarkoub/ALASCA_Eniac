package eniac.admissioncontroler;

import eniac.admissioncontroler.interfaces.RequestAdmissionI;

public class RequestAdmission implements RequestAdmissionI {
	private static final long serialVersionUID = 1L;
	
	private String requestNotificationPortURI;
	private String requestSubmissionPortURI;
	private String requestDispatcherURI;
	private String requestGeneratorManagementInboundPortURI;
	private double requestAverageResponseTime = 0;
	
	
	public RequestAdmission(String requestNotificationPortURI) {
		this.requestNotificationPortURI = requestNotificationPortURI;
	}
	
	@Override
	public String getRequestNotificationPortURI() {
		return requestNotificationPortURI;
	}
	
	@Override
	public void setRequestSubmissionPortURI(String uri) {
		requestSubmissionPortURI = uri;
	}
	
	@Override
	public String getRequestSubmissionPortURI() {
		return requestSubmissionPortURI;
	}

	@Override
	public String getRequestGeneratorManagementInboundPortURI() {
		return requestGeneratorManagementInboundPortURI;
	}

	@Override
	public void setRequestGeneratorManagementInboundPortURI(String uri) {
		requestGeneratorManagementInboundPortURI = uri;
	}

	@Override
	public String getRequestDispatcherURI() {
		return requestDispatcherURI;
	}

	@Override
	public void setRequestDispatcherURI(String uri) {
		requestDispatcherURI = uri;
		
	}

	@Override
	public RequestAdmissionI copy() {
		RequestAdmission newRequestAdmission = new RequestAdmission(requestNotificationPortURI);
		newRequestAdmission.requestGeneratorManagementInboundPortURI = this.requestGeneratorManagementInboundPortURI;
		newRequestAdmission.requestNotificationPortURI = this.requestNotificationPortURI;
		newRequestAdmission.requestSubmissionPortURI = this.requestSubmissionPortURI;
		newRequestAdmission.requestDispatcherURI = this.requestDispatcherURI;
		
		return newRequestAdmission;
	}

	@Override
	public double getAverageRequestResponseTime() {
		return requestAverageResponseTime;
	}

	@Override
	public void setAverageRequestResponseTime(double d) {
		requestAverageResponseTime = d;
	}
	

}
