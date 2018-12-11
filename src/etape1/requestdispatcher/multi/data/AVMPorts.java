package etape1.requestdispatcher.multi.data;

import etape2.capteurs.ports.ApplicationVMDynamicStateDataOutboundPort;
import etape2.capteurs.ports.ApplicationVMStaticStateDataOutboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionOutboundPort;

public class AVMPorts {
	protected RequestSubmissionOutboundPort rsopvm;
	protected RequestNotificationInboundPort rnipvm;
	private ApplicationVMDynamicStateDataOutboundPort applicationVMDynamicStateDataOutboundPort;
	private ApplicationVMStaticStateDataOutboundPort applicationVMStaticStateDataOutboundPort;
	
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
	
	
	public void setAvmStaticStateDataOutboundPort(
			ApplicationVMStaticStateDataOutboundPort applicationVMStaticStateDataOutboundPort) {
		this.applicationVMStaticStateDataOutboundPort = applicationVMStaticStateDataOutboundPort;
		
	}
	
	public ApplicationVMStaticStateDataOutboundPort getAvmStaticStateDataOutboundPort(){
		return applicationVMStaticStateDataOutboundPort;
	}
	
	public void setAvmDynamicStateDataOutboundPort(
			ApplicationVMDynamicStateDataOutboundPort applicationVMDynamicStateDataOutboundPort){
		this.applicationVMDynamicStateDataOutboundPort = applicationVMDynamicStateDataOutboundPort;
		
	}
	
	public ApplicationVMDynamicStateDataOutboundPort getAvmDynamicStateDataOutboundPort(){
		return applicationVMDynamicStateDataOutboundPort;
	}
	
	

}