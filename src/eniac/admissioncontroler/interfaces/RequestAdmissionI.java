package eniac.admissioncontroler.interfaces;

import java.io.Serializable;

public interface RequestAdmissionI extends Serializable {
	
	public String getRequestNotificationPortURI();

	public void setRequestSubmissionPortURI(String uri);

	public String getRequestSubmissionPortURI();
	
	public String getRequestGeneratorManagementInboundPortURI();
	
	public void setRequestGeneratorManagementInboundPortURI(String uri);
	
	public String getRequestDispatcherURI();
	
	public void setRequestDispatcherURI(String uri);
	
	public double getAverageRequestResponseTime();
	
	public void setAverageRequestResponseTime(double d);

	public RequestAdmissionI copy();


	
}
