package etape1.admissioncontroler.interfaces;

public interface RequestAdmissionSubmissionHandlerI {
	
	/*public void acceptRequestAdmissionSubmission(final RequestAdmissionI requestai) throws Exception;
	
	public void acceptRequestAdmissionSubmissionAndNotify(final RequestAdmissionI requestai) throws Exception;*/
	
	
	public String getSubmissionInboundPortURI(RequestAdmissionI requestAdmission) throws Exception;
	
}
