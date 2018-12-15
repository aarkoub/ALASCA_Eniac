package etape2.capteurs.ports;

import etape2.capteurs.interfaces.RequestDispatcherStateDataConsumerI;
import etape2.capteurs.interfaces.RequestDispatcherStaticStateDataI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataRequiredI.DataI;
import fr.sorbonne_u.datacenter.ports.AbstractControlledDataOutboundPort;

public class RequestDispatcherStaticStateDataOutboundPort extends	
AbstractControlledDataOutboundPort
implements RequestDispatcherStaticStateDataI{
	
	private String reqDispUri ;

	public RequestDispatcherStaticStateDataOutboundPort(ComponentI owner, String reqDispUri) throws Exception {
		super(owner);
		this.reqDispUri = reqDispUri;
		assert owner instanceof RequestDispatcherStateDataConsumerI;
	}

	
	public RequestDispatcherStaticStateDataOutboundPort(String uri, ComponentI owner, String reqDispUri) throws Exception {
		super(uri, owner);
		this.reqDispUri = reqDispUri;
		
		assert owner instanceof RequestDispatcherStateDataConsumerI;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void receive(DataI d) throws Exception {
		((RequestDispatcherStateDataConsumerI)this.owner).
		acceptRequestDispatcherStaticData(reqDispUri,
								  ((RequestDispatcherStaticStateDataI)d)) ;
		
	}

}
