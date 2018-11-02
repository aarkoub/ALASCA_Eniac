package etape1.requestdispatcher.multi;

public class AVMData {
	private AVMPorts avmports;
	private AVMUris avmuris;
	
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
	
	
}
