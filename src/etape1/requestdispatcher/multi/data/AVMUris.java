package etape1.requestdispatcher.multi.data;

public class AVMUris {
	private String RequestSubmissionInboundPortVM;
	private String RequestNotificationInboundPortVM;
	private String ApplicationVMManagementInboundPortVM;
	private String AVMUri;
	
	public AVMUris(String RequestSubmissionInboundPortVM, String RequestNotificationInboundPortVM, String ApplicationVMManagementInboundPortVM, String AVMUri) {
		this.RequestSubmissionInboundPortVM = RequestSubmissionInboundPortVM;
		this.RequestNotificationInboundPortVM = RequestNotificationInboundPortVM;
		this.ApplicationVMManagementInboundPortVM = ApplicationVMManagementInboundPortVM;
		this.AVMUri = AVMUri;
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
