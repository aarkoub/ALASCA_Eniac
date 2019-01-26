package eniac.requestdispatcherhandler.ports;

import java.util.List;
import java.util.Map;

import eniac.admissioncontroler.AdmissionControler;
import eniac.requestdispatcherhandler.interfaces.RequestDispatcherHandlerI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.processors.UnacceptableFrequencyException;
import fr.sorbonne_u.datacenter.hardware.processors.UnavailableFrequencyException;

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

		@Override
		public void setCoreFrequency(String processor_uri, int coreNo, int frequency)
				throws UnavailableFrequencyException,
				UnacceptableFrequencyException, Exception {
			this.getOwner().handleRequestAsync(
					new AbstractComponent.AbstractService<Void>() {
						@Override
						public Void call() throws Exception {
							((AdmissionControler)this.getOwner()).
									setCoreFrequency(processor_uri,coreNo, frequency) ;
							return null;
						}
					});			
		}

}
