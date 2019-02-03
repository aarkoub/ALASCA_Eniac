package eniac.admissioncontroler.interfaces;


/**
 * L'interface RequestAdmissionNotificationI définit le service du composant qui aura pour charge la notification de fin d'hébergement d'application.
 * Cela permet de demander au contrôleur d'admission de libérer les ressources.
 * 
 *
 */
public interface RequestAdmissionNotificationHandlerI {
	/**
	 * Effectue une demande auprès du contrôleur d'admission pour libérer les ressources utilisées pour servir les requêtes du RequestGenerator en question.
	 * @param requestAdmission données sur le RequestGenerator, notamment son URI pour l'identifier
	 * @throws Exception exception
	 */
	public void	acceptRequestAdmissionTerminationNotification(RequestAdmissionI requestAdmission) throws Exception ;
}
