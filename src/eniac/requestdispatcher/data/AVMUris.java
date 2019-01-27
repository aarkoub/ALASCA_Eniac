package eniac.requestdispatcher.data;

/**
 * Cette classe est un conteneur d'URI, il regroupe l'enemble des URIs utilisés pour connecter cette AVM avec d'autres composants.
 * @author L-C
 *
 */

public class AVMUris {
	/**
	 * URI correspondant à l'adresse d'envoit de requête.
	 */
	private String RequestSubmissionInboundPortVM;
	/**
	 * URI correspondant à l'adresse de notification.
	 */
	private String RequestNotificationInboundPortVM;
	/**
	 * URI correspondant à l'adresse pour contrôler l'AVM depuis un autre composant
	 */
	private String ApplicationVMManagementInboundPortVM;
	/**
	 * URI de l'AVM
	 */
	private String AVMUri;
	/**
	 * URI correspondant à l'adresse d'envoit des données dynamiques
	 */
	private String applicationVMDynamicStateDataInboundPortURI;
	/**
	 * URI correspondant à l'adresse d'envoit des données statiques
	 */
	private String applicationVMStaticStateDataInboundPortURI;
	
	public AVMUris(String RequestSubmissionInboundPortVM, String RequestNotificationInboundPortVM, String ApplicationVMManagementInboundPortVM,
			String AVMUri, String applicationVMDynamicStateDataInboundPortURI, String applicationVMStaticStateDataInboundPortURI ) {
		this.RequestSubmissionInboundPortVM = RequestSubmissionInboundPortVM;
		this.RequestNotificationInboundPortVM = RequestNotificationInboundPortVM;
		this.ApplicationVMManagementInboundPortVM = ApplicationVMManagementInboundPortVM;
		this.AVMUri = AVMUri;
		this.applicationVMDynamicStateDataInboundPortURI = applicationVMDynamicStateDataInboundPortURI;
		this.applicationVMStaticStateDataInboundPortURI = applicationVMStaticStateDataInboundPortURI;
	}

	public String getApplicationVMDynamicStateDataInboundPortURI() {
		return applicationVMDynamicStateDataInboundPortURI;
	}

	public void setApplicationVMDynamicStateDataInboundPortURI(String applicationVMDynamicStateDataInboundPortURI) {
		this.applicationVMDynamicStateDataInboundPortURI = applicationVMDynamicStateDataInboundPortURI;
	}

	public String getApplicationVMStaticStateDataInboundPortURI() {
		return applicationVMStaticStateDataInboundPortURI;
	}

	public void setApplicationVMStaticStateDataInboundPortURI(String applicationVMStaticStateDataInboundPortURI) {
		this.applicationVMStaticStateDataInboundPortURI = applicationVMStaticStateDataInboundPortURI;
	}

	public String getAVMUri() {
		return AVMUri;
	}

	public void setAVMUri(String aVMUri) {
		AVMUri = aVMUri;
	}

	public String getRequestSubmissionInboundPortVM() {
		return RequestSubmissionInboundPortVM;
	}

	public String getRequestNotificationInboundPortVM() {
		return RequestNotificationInboundPortVM;
	}

	public String getApplicationVMManagementInboundPortVM() {
		return ApplicationVMManagementInboundPortVM;
	}

	public void setRequestSubmissionInboundPortVM(String requestSubmissionInboundPortVM) {
		RequestSubmissionInboundPortVM = requestSubmissionInboundPortVM;
	}

	public void setRequestNotificationInboundPortVM(String requestNotificationInboundPortVM) {
		RequestNotificationInboundPortVM = requestNotificationInboundPortVM;
	}

	public void setApplicationVMManagementInboundPortVM(String applicationVMManagementInboundPortVM) {
		ApplicationVMManagementInboundPortVM = applicationVMManagementInboundPortVM;
	}
}
