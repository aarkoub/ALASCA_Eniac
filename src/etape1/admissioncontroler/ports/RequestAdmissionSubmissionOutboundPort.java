package etape1.admissioncontroler.ports;

import etape1.admissioncontroler.interfaces.RequestAdmissionI;
import etape1.admissioncontroler.interfaces.RequestAdmissionSubmissionI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class RequestAdmissionSubmissionOutboundPort extends AbstractOutboundPort
		implements RequestAdmissionSubmissionI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RequestAdmissionSubmissionOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, RequestAdmissionSubmissionI.class, owner);
	}
	
	public RequestAdmissionSubmissionOutboundPort(ComponentI owner)
			throws Exception {
		super(RequestAdmissionSubmissionI.class, owner);
	}

	@Override
	public String getSubmissionInboundPortURI(RequestAdmissionI requestAdmission) throws Exception {
		
		return ((RequestAdmissionSubmissionI)this.connector).getSubmissionInboundPortURI(requestAdmission);
	}

	/*@Override
	public void acceptRequestAdmissionSubmission(RequestAdmissionI requestai) throws Exception {
		((RequestAdmissionSubmissionI)this.connector).acceptRequestAdmissionSubmission(requestai);

	}

	@Override
	public void acceptRequestAdmissionSubmissionAndNotify(RequestAdmissionI requestai) throws Exception {
		((RequestAdmissionSubmissionI)this.connector).acceptRequestAdmissionSubmissionAndNotify(requestai);

	}*/

}
