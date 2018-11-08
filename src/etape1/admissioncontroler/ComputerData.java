package etape1.admissioncontroler;

import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;

public class ComputerData {
	private Computer computer;
	private ComputerServicesOutboundPort csop;
	private ComputerURI uris;
	
	
	public ComputerServicesOutboundPort getCsop() {
		return csop;
	}


	public void setCsop(ComputerServicesOutboundPort csop) {
		this.csop = csop;
	}


	public ComputerData(ComputerURI uris, Computer computer, ComputerServicesOutboundPort csop) {
		this.computer = computer;
		this.uris = uris;
		this.csop = csop;
	}


	public Computer getComputer() {
		return computer;
	}


	public ComputerURI getUris() {
		return uris;
	}


	public void setComputer(Computer computer) {
		this.computer = computer;
	}


	public void setUris(ComputerURI uris) {
		this.uris = uris;
	}
	
	
}
