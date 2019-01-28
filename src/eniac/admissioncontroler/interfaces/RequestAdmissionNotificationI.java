package eniac.admissioncontroler.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;


/**
 * L'interface RequestAdmissionNotificationI définit le service du composant pour la notification de fin d'hébergement d'application.
 * Cela permet de demander au contrôleur d'admission de libérer les ressources.
 * @author L-C
 *
 */
public interface RequestAdmissionNotificationI extends OfferedI, RequiredI {
	
	/**
	 * Effectue une demande auprès du contrôleur d'admission pour libérer les ressources utilisées pour servir les requêtes du RequestGenerator en question.
	 * @param requestAdmission données sur le RequestGenerator, notamment son URI pour l'identifier
	 * @throws Exception exception
	 */
	public void	acceptRequestTerminationNotification(RequestAdmissionI requestAdmission) throws Exception ;
}
