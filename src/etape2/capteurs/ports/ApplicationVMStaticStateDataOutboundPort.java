package etape2.capteurs.ports;

import etape2.capteurs.interfaces.ApplicationVMStateDataConsumerI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.components.ports.AbstractDataOutboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMStaticStateDataI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMStaticStateI;

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
