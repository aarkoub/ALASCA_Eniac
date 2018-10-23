package etape1.admissioncontroler.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface RequestAdmissionSubmissionI extends OfferedI, RequiredI {
	
	public String getSubmissionInboundPortURI(RequestAdmissionI requestAdmission) throws Exception;
}
