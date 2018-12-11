package etape1.requestdispatcher.multi.data;

import etape2.capteurs.ports.ApplicationVMDynamicStateDataOutboundPort;
import etape2.capteurs.ports.ApplicationVMStaticStateDataOutboundPort;

public class AVMData {
	private AVMPorts avmports;
	private AVMUris avmuris;
	private String avmDynamicStateDataInboundPortURI;
	private String avmStaticStateDataInboundPortURI;

	
	public AVMData(AVMUris avmuris, AVMPorts avmports) {
		this.avmports = avmports;
		this.avmuris = avmuris;
	}

	public AVMPorts getAvmports() {
		return avmports;
	}

	public AVMUris getAvmuris() {
		return avmuris;
	}

	public void setAvmports(AVMPorts avmports) {
		this.avmports = avmports;
	}

	public void setAvmuris(AVMUris avmuris) {
		this.avmuris = avmuris;
	}
	
	public void setAvmDynamicStateDataInboundPortURI(String uri){
		avmDynamicStateDataInboundPortURI = uri;
	}
	
	public String getAvmDynamicStateDataInboundPortURI(){
		return avmDynamicStateDataInboundPortURI;
	}
	
	public void setAvmStaticStateDataInboundPortURI(String uri){
		avmStaticStateDataInboundPortURI = uri;
	}
	
	public String getAvmStaticStateDataInboundPortURI(){
		return avmStaticStateDataInboundPortURI;
	}

	
	
	
}
