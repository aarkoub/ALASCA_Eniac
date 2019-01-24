package eniac.requestdispatcherhandler.ports;

import java.util.Map;

import eniac.requestdispatcherhandler.interfaces.RequestDispatcherHandlerI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.processors.UnacceptableFrequencyException;
import fr.sorbonne_u.datacenter.hardware.processors.UnavailableFrequencyException;

public class RequestDispatcherHandlerOutboundPort
extends AbstractOutboundPort
implements RequestDispatcherHandlerI{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RequestDispatcherHandlerOutboundPort(String uri, ComponentI owner ) throws Exception{
		super(uri, RequestDispatcherHandlerI.class, owner);
		assert	uri != null && owner != null ;
	}
	
	public RequestDispatcherHandlerOutboundPort(ComponentI owner) throws Exception {
		super(RequestDispatcherHandlerI.class, owner);
		assert owner!=null;
	}



	@Override
	public String addAVMToRequestDispatcher(String requestDispatcherURI) throws Exception {
		return ((RequestDispatcherHandlerI)this.connector).addAVMToRequestDispatcher(requestDispatcherURI);
		
	}

	@Override
	public String removeAVMFromRequestDispatcher(String requestDispatcherURI) throws Exception {
		return ((RequestDispatcherHandlerI)this.connector).removeAVMFromRequestDispatcher(requestDispatcherURI);
		
	}
	
	@Override
	public boolean removeAVMFromRequestDispatcher(String requestDispatcherURI, String avmURI) throws Exception {
		return ((RequestDispatcherHandlerI)this.connector).removeAVMFromRequestDispatcher(requestDispatcherURI, avmURI);
		
	}

	@Override
	public Map<String, String> addCoreToAvm(String handler_uri, String avm_uri, int nbcores) throws Exception {
		return ((RequestDispatcherHandlerI)this.connector).addCoreToAvm(handler_uri, avm_uri, nbcores);
	}

	@Override
	public boolean removeCoreFromAvm(String handler_uri, String avm_uri) throws Exception {
		return ((RequestDispatcherHandlerI)this.connector).removeCoreFromAvm(handler_uri, avm_uri);
	}

	@Override
	public void setCoreFrequency(String processor_uri, int coreNo, int frequency)
			throws UnavailableFrequencyException,
			UnacceptableFrequencyException, Exception {
		((RequestDispatcherHandlerI)this.connector).setCoreFrequency(processor_uri, coreNo, frequency);		
	}

}
