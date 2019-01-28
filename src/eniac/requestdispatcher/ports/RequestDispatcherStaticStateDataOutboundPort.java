package eniac.requestdispatcher.ports;

import eniac.requestdispatcher.interfaces.RequestDispatcherStateDataConsumerI;
import eniac.requestdispatcher.interfaces.RequestDispatcherStaticStateDataI;
import eniac.requestdispatcher.interfaces.RequestDispatcherStaticStateI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.components.ports.AbstractDataOutboundPort;
/**
 * La classe RequestDispatcherStaticStateDataOutboundPort correspond au port de sortie afin d'envoyer les données statiques relatives du RequestDispatcher
 * @author L-C
 *
 */
public class RequestDispatcherStaticStateDataOutboundPort	
extends		AbstractDataOutboundPort
implements RequestDispatcherStaticStateDataI{
	
	private String reqDispUri ;

	public RequestDispatcherStaticStateDataOutboundPort(ComponentI owner, String reqDispUri) throws Exception {
		super(DataRequiredI.PullI.class, DataRequiredI.PushI.class, owner) ;
		this.reqDispUri = reqDispUri;
		
		
		assert owner instanceof RequestDispatcherStateDataConsumerI;
	}

	
	public RequestDispatcherStaticStateDataOutboundPort(String uri, ComponentI owner, String reqDispUri) throws Exception {
		super(uri, DataRequiredI.PullI.class, DataRequiredI.PushI.class, owner) ;
		this.reqDispUri = reqDispUri;
		
		assert owner instanceof RequestDispatcherStateDataConsumerI;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void receive(DataRequiredI.DataI d) throws Exception {
		((RequestDispatcherStateDataConsumerI)this.owner).
		acceptRequestDispatcherStaticData(reqDispUri,
								  ((RequestDispatcherStaticStateI)d)) ;
		
	}

}
