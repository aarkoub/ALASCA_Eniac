package eniac.admissioncontroler;

import eniac.admissioncontroler.interfaces.RequestAdmissionI;

/**
 * La classe RequestAdmission est un implantation de RequestAdmissionI, il s'agit de l'objet envoyé par le RequestGenerator lors de la soumission de demande d'hébergement d'application au
 * contrôleur d'admission et y contient le temps de réponse souhaitée, le port de notification du RequestGenerator.
 * L'objet est ensuite renvoyé par le contrôleur d'admission au RequestGenerator dans le cas où le contrôleur accepte la demande et dans cet objet contient l'URI du port de soumission 
 * du RequestDispatcher.
 * @author L-C
 *
 */
public class RequestAdmission implements RequestAdmissionI {
	private static final long serialVersionUID = 1L;
	
	/**
	 * URI de notification du RequestGenerator
	 */
	private String requestNotificationPortURI;
	/**
	 * URI de soumission du RequestDispatcher
	 */
	private String requestSubmissionPortURI;
	/**
	 * URI du RequestDispatcher alloué
	 */
	private String requestDispatcherURI;
	/**
	 * URI du RequestGenerator pour les contrôles
	 */
	private String requestGeneratorManagementInboundPortURI;
	/**
	 * Temps de réponse souhaités par le demandeur d'hébergement
	 */
	private double requestAverageResponseTime = 0;
	
	
	public RequestAdmission(String requestNotificationPortURI) {
		this.requestNotificationPortURI = requestNotificationPortURI;
	}
	
	@Override
	public String getRequestNotificationPortURI() {
		return requestNotificationPortURI;
	}
	
	@Override
	public void setRequestSubmissionPortURI(String uri) {
		requestSubmissionPortURI = uri;
	}
	
	@Override
	public String getRequestSubmissionPortURI() {
		return requestSubmissionPortURI;
	}

	@Override
	public String getRequestGeneratorManagementInboundPortURI() {
		return requestGeneratorManagementInboundPortURI;
	}

	@Override
	public void setRequestGeneratorManagementInboundPortURI(String uri) {
		requestGeneratorManagementInboundPortURI = uri;
	}

	@Override
	public String getRequestDispatcherURI() {
		return requestDispatcherURI;
	}

	@Override
	public void setRequestDispatcherURI(String uri) {
		requestDispatcherURI = uri;
		
	}

	@Override
	public RequestAdmissionI copy() {
		RequestAdmission newRequestAdmission = new RequestAdmission(requestNotificationPortURI);
		newRequestAdmission.requestGeneratorManagementInboundPortURI = this.requestGeneratorManagementInboundPortURI;
		newRequestAdmission.requestNotificationPortURI = this.requestNotificationPortURI;
		newRequestAdmission.requestSubmissionPortURI = this.requestSubmissionPortURI;
		newRequestAdmission.requestDispatcherURI = this.requestDispatcherURI;
		newRequestAdmission.requestAverageResponseTime = this.requestAverageResponseTime;
		return newRequestAdmission;
	}

	@Override
	public double getAverageRequestResponseTime() {
		return requestAverageResponseTime;
	}

	@Override
	public void setAverageRequestResponseTime(double d) {
		requestAverageResponseTime = d;
	}
	

}
