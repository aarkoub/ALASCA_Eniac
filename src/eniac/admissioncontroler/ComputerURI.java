package eniac.admissioncontroler;

/**
 * La classe ComputerURI regroupe toutes les URIs qui sont utilisés par une instance de Computer, on y retrouve notamment son URI et
 * les URIs des ports utilisés.
 * 
 *
 */


public class ComputerURI {
	/**
	 * URI du Computer
	 */
	private String ComputerUri;
	/**
	 * URI pour les services
	 */
	private String ComputerServicesInboundPortURI;
	/**
	 * URI pour les envoit de données statiques
	 */
	private String ComputerStaticStateDataInboundPortURI;
	/**
	 * URI pour les envoit de données dynamiques
	 */
	private String ComputerDynamicStateDataInboundPortURI;
	
	public ComputerURI(String ComputerUri, String ComputerServicesInboundPortURI, String ComputerStaticStateDataInboundPortURI, String ComputerDynamicStateDataInboundPortURI) {
		this.ComputerUri = ComputerUri;
		this.ComputerServicesInboundPortURI = ComputerServicesInboundPortURI;
		this.ComputerStaticStateDataInboundPortURI = ComputerStaticStateDataInboundPortURI;
		this.ComputerDynamicStateDataInboundPortURI = ComputerDynamicStateDataInboundPortURI;
	}

	public String getComputerUri() {
		return ComputerUri;
	}

	public String getComputerServicesInboundPortURI() {
		return ComputerServicesInboundPortURI;
	}

	public String getComputerStaticStateDataInboundPortURI() {
		return ComputerStaticStateDataInboundPortURI;
	}

	public String getComputerDynamicStateDataInboundPortURI() {
		return ComputerDynamicStateDataInboundPortURI;
	}

	public void setComputerUri(String computerUri) {
		ComputerUri = computerUri;
	}

	public void setComputerServicesInboundPortURI(String computerServicesInboundPortURI) {
		ComputerServicesInboundPortURI = computerServicesInboundPortURI;
	}

	public void setComputerStaticStateDataInboundPortURI(String computerStaticStateDataInboundPortURI) {
		ComputerStaticStateDataInboundPortURI = computerStaticStateDataInboundPortURI;
	}

	public void setComputerDynamicStateDataInboundPortURI(String computerDynamicStateDataInboundPortURI) {
		ComputerDynamicStateDataInboundPortURI = computerDynamicStateDataInboundPortURI;
	}
}
