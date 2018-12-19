package eniac.automatichandler.ports;

import eniac.admissioncontroler.AdmissionControler;
import eniac.automatichandler.AutomaticHandler;
import eniac.automatichandler.interfaces.RequestDispatcherListenerI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class RequestDispatcherListenerInboundPort
extends AbstractInboundPort
implements RequestDispatcherListenerI{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public	RequestDispatcherListenerInboundPort(
			ComponentI owner
			) throws Exception
		{
			super(RequestDispatcherListenerI.class, owner) ;

		}

		public	RequestDispatcherListenerInboundPort(
			String uri,
			ComponentI owner
			) throws Exception
		{
			super(uri, RequestDispatcherListenerI.class, owner);

			
		}

		@Override
		public void receiveNewRequestDispatcherURI(String rd_uri, String requestDispatcherDynamicStateDataInboundPortURI,
				String requestDispatcherStaticStateDataInboundPortURI)throws Exception {
			/*this.getOwner().handleRequestAsync(
					new AbstractComponent.AbstractService<Void>() {
						@Override
						public Void call() throws Exception {
						
							((AutomaticHandler)this.getOwner()).receiveNewRequestDispatcherURI(rd_uri, requestDispatcherDynamicStateDataInboundPortURI,
									requestDispatcherStaticStateDataInboundPortURI);
							return null;
						}
					}) ;*/
			
		}

}
