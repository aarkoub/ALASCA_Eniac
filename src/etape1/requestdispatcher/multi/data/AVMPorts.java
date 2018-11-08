package etape1.requestdispatcher.multi.data;

import fr.sorbonne_u.datacenter.software.ports.RequestNotificationInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionOutboundPort;

public class AVMPorts {
	protected RequestSubmissionOutboundPort rsopvm;
	protected RequestNotificationInboundPort rnipvm;
	
	public AVMPorts(RequestSubmissionOutboundPort rsopvm, RequestNotificationInboundPort rnipvm) {
		this.rsopvm = rsopvm;
		this.rnipvm = rnipvm;
		
	}
	
	public RequestSubmissionOutboundPort getRequestSubmissionOutboundPort() {
		return rsopvm;
	}
	
	public RequestNotificationInboundPort getRequestNotificationInboundPort() {
		return rnipvm;
	}
	

}