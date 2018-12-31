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
		public String addAVMToRequestDispatcher(String requestDispatcherURI) throws Exception {
			return this.getOwner().handleRequestSync(
			new AbstractComponent.AbstractService<String>() {
				@Override
				public String call() throws Exception {
				
					return ((AdmissionControler)this.getOwner()).addAVMToRequestDispatcher(requestDispatcherURI);
			
				}
			}) ;
			
		}

		@Override
		public String removeAVMFromRequestDispatcher(String requestDispatcherURI) throws Exception {
			
			return this.getOwner().handleRequestSync(
					new AbstractComponent.AbstractService<String>() {
						@Override
						public String call() throws Exception {
						
							return ((AdmissionControler)this.getOwner()).removeAVMFromRequestDispatcher(requestDispatcherURI);
						}
					}) ;
			
		}

}
