package eniac.admissioncontroler;

import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.computers.ports.ComputerServicesOutboundPort;

/**
 * La classe ComputerData est un conteneur de données relatif au Computer.
 * Celui-ci garde donc en mémoire l'instance Colputer, un port permettant d'effectuer des demandes sur celui-ci mais aussi les différents
 * URIs qui sont utilisés pour les connections avec le Computer.
 * @author L-C
 *
 */

public class ComputerData {
	/**
	 * Instance de Computer
	 */
	private Computer computer;
	/**
	 * Pour de demande de services
	 */
	private ComputerServicesOutboundPort csop;
	/**
	 * URIs pour les connections.
	 */
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
