package eniac.automatichandler.ports;

import java.util.List;
import java.util.Map;

import eniac.admissioncontroler.AdmissionControler;
import eniac.automatichandler.interfaces.AutomaticHandlerRequestI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;

public class AutomaticHandlerRequestInboundPort
extends AbstractInboundPort
implements AutomaticHandlerRequestI{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public	AutomaticHandlerRequestInboundPort(
			ComponentI owner
			) throws Exception
		{
			super(AutomaticHandlerRequestI.class, owner) ;

		}

		public	AutomaticHandlerRequestInboundPort(
			String uri,
			ComponentI owner
			) throws Exception
		{
			super(uri, AutomaticHandlerRequestI.class, owner);

			
		}



		@Override
		public Map<String, String> addAVMToRequestDispatcher(String handler_uri, String requestDispatcherURI) throws Exception {
			return this.getOwner().handleRequestSync(
			new AbstractComponent.AbstractService<Map<String, String>>() {
				@Override
				public Map<String, String> call() throws Exception {
				
					return ((AdmissionControler)this.getOwner()).addAVMToRequestDispatcher(handler_uri, requestDispatcherURI);
			
				}
			}) ;
			
		}

			
		
		@Override
		public List<String> removeAVMFromRequestDispatcher(String handler_uri, String requestDispatcherURI, String avmURI) throws Exception {
			
			return this.getOwner().handleRequestSync(
					new AbstractComponent.AbstractService< List<String> >() {
						@Override
						public  List<String>  call() throws Exception {
						
							return ((AdmissionControler)this.getOwner()).removeAVMFromRequestDispatcher(handler_uri, requestDispatcherURI, avmURI);
						}
					}) ;
			
		}

		@Override
		public Map<String, String> addCoreToAvm(String handler_uri, String avm_uri, int nbcores) throws Exception {
			return this.getOwner().handleRequestSync(
					new AbstractComponent.AbstractService<Map<String, String>>() {
						@Override
						public Map<String, String> call() throws Exception {
						
							return ((AdmissionControler)this.getOwner()).addCoreToAvm(handler_uri, avm_uri, nbcores);
						}
					}) ;
		}

		@Override
		public List<String> removeCoreFromAvm(String handler_uri, String avm_uri) throws Exception {
			return this.getOwner().handleRequestSync(
					new AbstractComponent.AbstractService< List<String> >() {
						@Override
						public  List<String>  call() throws Exception {
						
							return ((AdmissionControler)this.getOwner()).removeCoreFromAvm(handler_uri, avm_uri);
						}
					}) ;
		}

		

}
