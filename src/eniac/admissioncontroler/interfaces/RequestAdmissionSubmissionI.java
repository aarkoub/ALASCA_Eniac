package eniac.admissioncontroler.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface RequestAdmissionSubmissionI extends OfferedI, RequiredI {
	
	public RequestAdmissionI getRequestAdmissionFromAdmissionController(RequestAdmissionI requestAdmission) throws Exception;
}
