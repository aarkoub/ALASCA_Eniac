package eniac.admissioncontroler;

public class ComputerURI {
	private String ComputerUri;
	private String ComputerServicesInboundPortURI;
	private String ComputerStaticStateDataInboundPortURI;
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
