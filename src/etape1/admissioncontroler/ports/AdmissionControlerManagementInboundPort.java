package etape1.admissioncontroler.ports;

import etape1.admissioncontroler.interfaces.AdmissionControlerManagementI;
import etape1.requestdispatcher.RequestDispatcher;
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
		super(AdmissionControlerManagementInboundPort.class, owner) ;

		assert	owner != null && owner instanceof RequestDispatcher ;
	}

	public	AdmissionControlerManagementInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, AdmissionControlerManagementInboundPort.class, owner);

		assert	owner != null && owner instanceof RequestDispatcher ;
	}


}
