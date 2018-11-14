package etape1.admissioncontroler.ports;

import etape1.admissioncontroler.AdmissionControler;
import etape1.admissioncontroler.interfaces.AdmissionControlerManagementI;
import etape1.requestdispatcher.components.RequestDispatcher;
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
