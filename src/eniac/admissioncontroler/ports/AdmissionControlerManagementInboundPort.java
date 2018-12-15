package eniac.admissioncontroler.ports;

import eniac.admissioncontroler.AdmissionControler;
import eniac.admissioncontroler.interfaces.AdmissionControlerManagementI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class AdmissionControlerManagementInboundPort extends 	AbstractInboundPort
implements	AdmissionControlerManagementI {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public	AdmissionControlerManagementInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(AdmissionControlerManagementI.class, owner) ;

		assert	owner != null && owner instanceof AdmissionControler ;
	}

	public	AdmissionControlerManagementInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, AdmissionControlerManagementI.class, owner);

		assert	owner != null && owner instanceof AdmissionControler ;
	}


}
