package eniac.automatichandler.ports;

import eniac.admissioncontroler.AdmissionControler;
import eniac.automatichandler.AutomaticHandler;
import eniac.automatichandler.interfaces.RequestDispatcherHandlerI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class RequestDispatcherHandlerInboundPort
extends AbstractInboundPort
implements RequestDispatcherHandlerI{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public	RequestDispatcherHandlerInboundPort(
			ComponentI owner
			) throws Exception
		{
			super(RequestDispatcherHandlerI.class, owner) ;

		}

		public	RequestDispatcherHandlerInboundPort(
			String uri,
			ComponentI owner
			) throws Exception
		{
			super(uri, RequestDispatcherHandlerI.class, owner);

			
		}



		@Override
		public void addAVMToRequestDispatcher(String requestDispatcherURI) throws Exception {
			this.getOwner().handleRequestAsync(
			new AbstractComponent.AbstractService<Void>() {
				@Override
				public Void call() throws Exception {
				
					((AdmissionControler)this.getOwner()).addAVMToRequestDispatcher(requestDispatcherURI);
					return null;
				}
			}) ;
			
		}

		@Override
		public void removeAVMFromRequestDispatcher(String requestDispatcherURI) throws Exception {
			
			this.getOwner().handleRequestAsync(
					new AbstractComponent.AbstractService<Void>() {
						@Override
						public Void call() throws Exception {
						
							((AdmissionControler)this.getOwner()).removeAVMFromRequestDispatcher(requestDispatcherURI);
							return null;
						}
					}) ;
			
		}

}
