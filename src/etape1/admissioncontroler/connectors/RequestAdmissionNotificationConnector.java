package etape1.admissioncontroler.connectors;

import etape1.admissioncontroler.interfaces.RequestAdmissionNotificationI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

public class RequestAdmissionNotificationConnector extends AbstractConnector implements RequestAdmissionNotificationI {

	@Override
	public void acceptRequestTerminationNotification(boolean b) throws Exception {
		((RequestAdmissionNotificationI)this.offering).acceptRequestTerminationNotification(b);

	}

}
