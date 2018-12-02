package etape1.requestadmission.ports;

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
	public RequestAdmissionI getRequestAdmissionFromAdmissionController(RequestAdmissionI requestAdmission) throws Exception {
		return this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<RequestAdmissionI>() {
					@Override
					public RequestAdmissionI call() throws Exception {
						return ((AdmissionControler)this.getOwner()).getNewRequestAdmission(requestAdmission);
					
					}
				}) ;
			
		
	}


}
