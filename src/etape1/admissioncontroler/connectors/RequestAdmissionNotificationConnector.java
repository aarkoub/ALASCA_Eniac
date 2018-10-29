package etape1.admissioncontroler.connectors;

import etape1.admissioncontroler.interfaces.RequestAdmissionI;
import etape1.admissioncontroler.interfaces.RequestAdmissionNotificationI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class RequestAdmissionNotificationConnector extends AbstractConnector implements RequestAdmissionNotificationI {

	@Override
	public void acceptRequestTerminationNotification(RequestAdmissionI requestAdmission) throws Exception {
		((RequestAdmissionNotificationI)this.offering).acceptRequestTerminationNotification(requestAdmission);

	}

}
