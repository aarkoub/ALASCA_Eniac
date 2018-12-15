package eniac.admissioncontroler.connectors;

import eniac.admissioncontroler.interfaces.RequestAdmissionI;
import eniac.admissioncontroler.interfaces.RequestAdmissionSubmissionI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class RequestAdmissionSubmissionConnector extends AbstractConnector implements RequestAdmissionSubmissionI {

	@Override
	public RequestAdmissionI getRequestAdmissionFromAdmissionController(RequestAdmissionI requestAdmission) throws Exception {
		return ((RequestAdmissionSubmissionI)this.offering).getRequestAdmissionFromAdmissionController(requestAdmission);
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
