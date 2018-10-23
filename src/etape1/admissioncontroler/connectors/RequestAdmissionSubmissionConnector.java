package etape1.admissioncontroler.connectors;

import etape1.admissioncontroler.interfaces.RequestAdmissionI;
import etape1.admissioncontroler.interfaces.RequestAdmissionSubmissionI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class RequestAdmissionSubmissionConnector extends AbstractConnector implements RequestAdmissionSubmissionI {

	@Override
	public String getSubmissionInboundPortURI(RequestAdmissionI requestAdmission) throws Exception {
		return ((RequestAdmissionSubmissionI)this.offering).getSubmissionInboundPortURI(requestAdmission);
	}

	/*@Override
	public void acceptRequestAdmissionSubmission(RequestAdmissionI requestai) throws Exception {
		((RequestAdmissionSubmissionI)this.offering).acceptRequestAdmissionSubmission(requestai);

	}

	@Override
	public void acceptRequestAdmissionSubmissionAndNotify(RequestAdmissionI requestai) throws Exception {
		((RequestAdmissionSubmissionI)this.offering).acceptRequestAdmissionSubmissionAndNotify(requestai);

	}*/

}
