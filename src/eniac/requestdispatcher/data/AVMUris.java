package eniac.requestdispatcher.data;

public class AVMUris {
	private String RequestSubmissionInboundPortVM;
	private String RequestNotificationInboundPortVM;
	private String ApplicationVMManagementInboundPortVM;
	private String AVMUri;
	private String applicationVMDynamicStateDataInboundPortURI;
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
