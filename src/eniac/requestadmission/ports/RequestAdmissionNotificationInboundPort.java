package eniac.requestadmission.ports;

import eniac.admissioncontroler.AdmissionControler;
import eniac.admissioncontroler.interfaces.RequestAdmissionI;
import eniac.admissioncontroler.interfaces.RequestAdmissionNotificationI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class RequestAdmissionNotificationInboundPort extends AbstractInboundPort
		implements RequestAdmissionNotificationI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RequestAdmissionNotificationInboundPort(ComponentI owner) throws Exception {
		super(RequestAdmissionNotificationI.class, owner);
	}
	
	public RequestAdmissionNotificationInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RequestAdmissionNotificationI.class, owner);
	}

	@Override
	public void acceptRequestTerminationNotification(RequestAdmissionI requestAdmission) throws Exception {
		this.getOwner().handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((AdmissionControler)this.getOwner()).acceptRequestAdmissionTerminationNotification(requestAdmission);
						return null;
					}
				}) ;
	}

}
