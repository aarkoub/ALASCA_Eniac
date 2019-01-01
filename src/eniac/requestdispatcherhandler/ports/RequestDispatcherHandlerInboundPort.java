package eniac.requestdispatcherhandler.ports;

import eniac.admissioncontroler.AdmissionControler;
import eniac.automatichandler.AutomaticHandler;
import eniac.requestdispatcherhandler.interfaces.RequestDispatcherHandlerI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;

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

		@Override
		public boolean addCoreToAvm(String avm_uri, int nbcores) throws Exception {
			return this.getOwner().handleRequestSync(
					new AbstractComponent.AbstractService<Boolean>() {
						@Override
						public Boolean call() throws Exception {
						
							return ((AdmissionControler)this.getOwner()).addCoreToAvm(avm_uri, nbcores);
						}
					}) ;
		}

		@Override
		public boolean removeCoreFromAvm(String avm_uri, AllocatedCore allocatedCore) throws Exception {
			return this.getOwner().handleRequestSync(
					new AbstractComponent.AbstractService<Boolean>() {
						@Override
						public Boolean call() throws Exception {
						
							return ((AdmissionControler)this.getOwner()).removeCoreFromAvm(avm_uri, allocatedCore);
						}
					}) ;
		}

}
