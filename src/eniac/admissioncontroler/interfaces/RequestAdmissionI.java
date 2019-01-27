package eniac.admissioncontroler.interfaces;

import java.io.Serializable;

/**
 * Cette interface RequestAdmissionI correspond aux méthodes qu'une classe doit implanter pour pouvoir transmettre des données entre le RequestGenerator et le contrôleur d'admission 
 * (AdmissionController). Il s'agit de l'objet envoyé par le RequestGenerator lors de la soumission de demande d'hébergement d'application au
 * contrôleur d'admission et y contient le temps de réponse souhaitée, le port de notification du RequestGenerator.
 * L'objet est ensuite renvoyé par le contrôleur d'admission au RequestGenerator dans le cas où le contrôleur accepte la demande et dans cet objet contient l'URI du port de soumission 
 * du RequestDispatcher.
 * @author L-C
 *
 */

public interface RequestAdmissionI extends Serializable {
	
	/**
	 * Retourne l'URI de notification du RequestGenerator
	 * @return URI de notification du RequestGenerator
	 */
	public String getRequestNotificationPortURI();

	/**
	 * Change l'URI de notification du RequestGenerator
	 * @param uri URI de notification du RequestGenerator
	 */
	public void setRequestSubmissionPortURI(String uri);

	
	/**
	 * Retourne l'URI de soumission du RequestDispatcher
	 * @return URI de soumission du RequestDispatcher
	 */
	public String getRequestSubmissionPortURI();
	
	/**
	 * Retourne l'URI du RequestGenerator pour les contrôles
	 * @return URI du RequestGenerator pour les contrôles
	 */
	public String getRequestGeneratorManagementInboundPortURI();
	
	/**
	 * Change l'URI du RequestGenerator pour les contrôles
	 * @param uri URI du RequestGenerator pour les contrôles
	 */
	public void setRequestGeneratorManagementInboundPortURI(String uri);
	
	/**
	 * Retourne l'URI du RequestDispatcher alloué
	 * @return URI du RequestDispatcher alloué
	 */
	public String getRequestDispatcherURI();
	
	/**
	 * Change l'URI du RequestDispatcher alloué
	 * @param uri URI du RequestDispatcher alloué
	 */
	public void setRequestDispatcherURI(String uri);
	
	/**
	 * Retourne le temps de réponse souhaités par le demandeur d'hébergement
	 * @return temps de réponse souhaités par le demandeur d'hébergement
	 */
	public double getAverageRequestResponseTime();
	
	/**
	 * Change le temps de réponse souhaités par le demandeur d'hébergement
	 * @param d le temps de réponse souhaités par le demandeur d'hébergement
	 */
	public void setAverageRequestResponseTime(double d);

	/**
	 * Créer une copie de l'objet
	 * @return copie de l'objet
	 */
	public RequestAdmissionI copy();


	
}
