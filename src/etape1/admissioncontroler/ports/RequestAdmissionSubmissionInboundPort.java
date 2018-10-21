package etape1.admissioncontroler.ports;

import etape1.admissioncontroler.AdmissionControler;
import etape1.admissioncontroler.interfaces.RequestAdmissionI;
import etape1.admissioncontroler.interfaces.RequestAdmissionSubmissionI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class RequestAdmissionSubmissionInboundPort extends AbstractInboundPort implements RequestAdmissionSubmissionI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RequestAdmissionSubmissionInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, RequestAdmissionSubmissionI.class, owner);
	}
	
	public RequestAdmissionSubmissionInboundPort(ComponentI owner)
			throws Exception {
		super(RequestAdmissionSubmissionI.class, owner);
	}

	@Override
	public void acceptRequestAdmissionSubmission(RequestAdmissionI requestai) throws Exception {
		this.getOwner().handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((AdmissionControler)this.getOwner()).acceptRequestAdmissionSubmission(requestai);
						return null;
					}
				}) ;

	}

	@Override
	public void acceptRequestAdmissionSubmissionAndNotify(RequestAdmissionI requestai) throws Exception {
		this.getOwner().handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((AdmissionControler)this.getOwner()).acceptRequestAdmissionSubmissionAndNotify(requestai);
						return null;
					}
				}) ;

	}

}
