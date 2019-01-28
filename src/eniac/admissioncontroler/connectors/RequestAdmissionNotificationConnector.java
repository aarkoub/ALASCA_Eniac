package eniac.admissioncontroler.connectors;

import eniac.admissioncontroler.interfaces.RequestAdmissionI;
import eniac.admissioncontroler.interfaces.RequestAdmissionNotificationI;
import fr.sorbonne_u.components.connectors.AbstractConnector;

/**
 * La classe RequestAdmissionNotificationConnector est un connecteur entre le port d'entrée et de sortie pour la notification de fin d'hébergement d'application.
 * Cela permet de demander au contrôleur d'admission de libérer les ressources.
 * @author L-C
 *
 */

public class RequestAdmissionNotificationConnector extends AbstractConnector
implements RequestAdmissionNotificationI {

	/**
	 * Effectue une demande auprès du contrôleur d'admission pour libérer les ressources utilisées pour servir les requêtes du RequestGenerator en question.
	 * @param requestAdmission données sur le RequestGenerator, notamment son URI pour l'identifier
	 */
	@Override
	public void acceptRequestTerminationNotification(RequestAdmissionI requestAdmission) throws Exception {
		((RequestAdmissionNotificationI)this.offering).acceptRequestTerminationNotification(requestAdmission);

	}

}
