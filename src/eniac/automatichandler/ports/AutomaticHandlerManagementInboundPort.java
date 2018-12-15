package eniac.automatichandler.ports;

import eniac.admissioncontroler.AdmissionControler;
import eniac.admissioncontroler.interfaces.AdmissionControlerManagementI;
import eniac.automatichandler.AutomaticHandler;
import eniac.automatichandler.interfaces.AutomaticHandlerManagementI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class AutomaticHandlerManagementInboundPort extends AbstractInboundPort
implements AutomaticHandlerManagementI{


	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public	AutomaticHandlerManagementInboundPort(
			ComponentI owner
			) throws Exception
		{
			super(AutomaticHandlerManagementI.class, owner) ;

			assert	owner != null && owner instanceof AutomaticHandler ;
		}

		public	AutomaticHandlerManagementInboundPort(
			String uri,
			ComponentI owner
			) throws Exception
		{
			super(uri, AutomaticHandlerManagementI.class, owner);

			assert	owner != null && owner instanceof AutomaticHandler ;
		}

}
