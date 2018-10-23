package etape1.admissioncontroler.ports;

import etape1.admissioncontroler.interfaces.AdmissionControlerManagementI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

public class AdmissionControlerManagementOutboundPort extends AbstractOutboundPort
implements AdmissionControlerManagementI {

	
	public AdmissionControlerManagementOutboundPort(String uri, ComponentI owner ) throws Exception{
		super(uri, AdmissionControlerManagementOutboundPort.class, owner);
		assert	uri != null && owner != null ;
	}
	
	public AdmissionControlerManagementOutboundPort(ComponentI owner) throws Exception {
		super(AdmissionControlerManagementOutboundPort.class, owner);
		assert owner!=null;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;




}
