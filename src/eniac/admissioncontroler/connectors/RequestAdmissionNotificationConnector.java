package eniac.admissioncontroler.connectors;

import eniac.admissioncontroler.interfaces.RequestAdmissionI;
import eniac.admissioncontroler.interfaces.RequestAdmissionNotificationI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class RequestAdmissionNotificationConnector extends AbstractConnector implements RequestAdmissionNotificationI {

	@Override
	public void acceptRequestTerminationNotification(RequestAdmissionI requestAdmission) throws Exception {
		((RequestAdmissionNotificationI)this.offering).acceptRequestTerminationNotification(requestAdmission);

	}

}
