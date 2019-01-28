package eniac.requestdispatcher.ports;

import eniac.requestdispatcher.RequestDispatcher;
import eniac.requestdispatcher.interfaces.RequestDispatcherStaticStateDataI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.datacenter.ports.AbstractControlledDataInboundPort;
/**
 * La classe RequestDispatcherStaticStateDataInboundPort correspond au port d'entrée afin d'envoyer les données statiques relatives du RequestDispatcher
 * @author L-C
 *
 */
public class RequestDispatcherStaticStateDataInboundPort 
extends AbstractControlledDataInboundPort
implements RequestDispatcherStaticStateDataI{

	public RequestDispatcherStaticStateDataInboundPort(ComponentI owner) throws Exception {
		super(owner);
		// TODO Auto-generated constructor stub
	}

	public RequestDispatcherStaticStateDataInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, owner);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public DataOfferedI.DataI get() throws Exception {
		return this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<DataOfferedI.DataI>() {
					@Override
					public DataOfferedI.DataI call() throws Exception {
						return ((RequestDispatcher)this.getOwner()).
										getStaticState() ;
					}
				}) ;
	}

	
}
