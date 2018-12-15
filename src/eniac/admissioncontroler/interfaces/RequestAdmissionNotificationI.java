package eniac.admissioncontroler.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface RequestAdmissionNotificationI extends OfferedI, RequiredI {
	public void	acceptRequestTerminationNotification(RequestAdmissionI requestAdmission) throws Exception ;
}
