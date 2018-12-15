package eniac.requestadmission.ports;

import eniac.admissioncontroler.interfaces.RequestAdmissionI;
import eniac.admissioncontroler.interfaces.RequestAdmissionNotificationI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class RequestAdmissionNotificationOutboundPort extends AbstractOutboundPort
		implements RequestAdmissionNotificationI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RequestAdmissionNotificationOutboundPort(ComponentI owner)
			throws Exception {
		super(RequestAdmissionNotificationI.class, owner);
	}
	

	public RequestAdmissionNotificationOutboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, RequestAdmissionNotificationI.class, owner);
	}

	@Override
	public void acceptRequestTerminationNotification(RequestAdmissionI requestAdmission) throws Exception {
		((RequestAdmissionNotificationI)this.connector).acceptRequestTerminationNotification(requestAdmission);

	}

}
