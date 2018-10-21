package etape1.admissioncontroler.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface RequestAdmissionSubmissionI extends OfferedI, RequiredI {
	public void acceptRequestAdmissionSubmission(final RequestAdmissionI requestai) throws Exception;
	
	public void acceptRequestAdmissionSubmissionAndNotify(final RequestAdmissionI requestai) throws Exception;
}
