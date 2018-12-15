package eniac.automatichandler;

import eniac.automatichandler.ports.AutomaticHandlerManagementInboundPort;
import fr.sorbonne_u.components.AbstractComponent;

public class AutomaticHandler extends AbstractComponent{
	
	protected String autoHand_uri;
	protected AutomaticHandlerManagementInboundPort automaticHandlerManagementInboundPort;
	
	
	
	public AutomaticHandler(String autoHand_uri,
			String managementInboundPortURI) throws Exception{
		super(autoHand_uri,1,1);
		
		assert autoHand_uri!=null;
		assert managementInboundPortURI!=null;
		
		automaticHandlerManagementInboundPort = new AutomaticHandlerManagementInboundPort(autoHand_uri, this);
		
		
		
		
	}

}
