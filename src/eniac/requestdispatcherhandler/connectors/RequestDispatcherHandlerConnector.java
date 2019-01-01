package eniac.requestdispatcherhandler.connectors;

import eniac.requestdispatcherhandler.interfaces.RequestDispatcherHandlerI;
import fr.sorbonne_u.components.connectors.AbstractConnector;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;

public class RequestDispatcherHandlerConnector
extends AbstractConnector
implements RequestDispatcherHandlerI{


	@Override
	public String addAVMToRequestDispatcher(String requestDispatcherURI)throws Exception {
		return ((RequestDispatcherHandlerI)this.offering).addAVMToRequestDispatcher(requestDispatcherURI);
		
	}

	@Override
	public String removeAVMFromRequestDispatcher(String requestDispatcherURI) throws Exception{
		return ((RequestDispatcherHandlerI)this.offering).removeAVMFromRequestDispatcher(requestDispatcherURI);
		
	}

	@Override
	public boolean addCoreToAvm(String avm_uri, int nbcores) throws Exception {
		return ((RequestDispatcherHandlerI)this.offering).addCoreToAvm(avm_uri, nbcores);
	}

	@Override
	public boolean removeCoreFromAvm(String avm_uri, AllocatedCore allocatedCore) throws Exception {
		return ((RequestDispatcherHandlerI)this.offering).removeCoreFromAvm(avm_uri, allocatedCore);
	}

}
