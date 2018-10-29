package etape1.admissioncontroler.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface RequestAdmissionSubmissionI extends OfferedI, RequiredI {
	
	public void setSubmissionInboundPortURI(RequestAdmissionI requestAdmission) throws Exception;
}
