package eniac.applicationvm.ports;

import eniac.applicationvm.interfaces.ApplicationVMStateDataConsumerI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.components.ports.AbstractDataOutboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMStaticStateDataI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMStaticStateI;

/**
 * Cette classe correspond au port utilisé pour l'envoit des données statiques de l'AVM vers le RequestDispatcher.
 * Il a été prit exemple sur BCM.
 * @author L-C
 *
 */

public class ApplicationVMStaticStateDataOutboundPort extends	
AbstractDataOutboundPort
implements ApplicationVMStaticStateDataI {

	private String avmUri;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ApplicationVMStaticStateDataOutboundPort(ComponentI owner, String avmUri) throws Exception {
		super(DataRequiredI.PullI.class, DataRequiredI.PushI.class, owner) ;
		this.avmUri = avmUri;
		
		assert owner instanceof ApplicationVMStateDataConsumerI; 
		
	}
	
	public ApplicationVMStaticStateDataOutboundPort(String uri, ComponentI owner, String avmUri) throws Exception {
		super(DataRequiredI.PullI.class, DataRequiredI.PushI.class, owner) ;
		this.avmUri = avmUri;
		
		assert owner instanceof ApplicationVMStateDataConsumerI; 
		
	}
	

	@Override
	public void receive(fr.sorbonne_u.components.interfaces.DataRequiredI.DataI d) throws Exception {
		((ApplicationVMStateDataConsumerI)this.owner).
		acceptApplicationVMStaticData(avmUri,
								  ((ApplicationVMStaticStateI)d)) ;
		
	}

}
