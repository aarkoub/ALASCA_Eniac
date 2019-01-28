package eniac.admissioncontroler.connectors;

import eniac.admissioncontroler.interfaces.RequestAdmissionI;
import eniac.admissioncontroler.interfaces.RequestAdmissionSubmissionI;
import fr.sorbonne_u.components.connectors.AbstractConnector;


/**
 * La classe RequestAdmissionSubmissionConnector est un connecteur entre le port d'entrée et de sortie pour les demandes du RequestGenerator d'hébergement vers le Contrôleur d'admission.
 * @author L-C
 *
 */
public class RequestAdmissionSubmissionConnector extends AbstractConnector implements RequestAdmissionSubmissionI {

	
	/**
	 * Ceci est l'unique méthode d'appel de ce connecteur, il s'agit de la demande d'hébergement d'application où l'on ajoute en paramètre les données relatifs pour un hébergement notamment
	 * l'URI de notification du RequestGenerator pour se faire notifier la fin des requêtes mais aussi par exemple le temps moyens souhaitées pour une requête.
	 * En retour on retourne un objet qui peut contenir l'URI du port de soumission du RequestDispatcher chargé de redistribué les requêtes du RequestGenerator, si le contrôle d'admission 
	 * accepte la demande.
	 * @param requestAdmission données sur la demande d'hébergement
	 * @return l'uri du port de soumission du RequestDispatcher si l'hébergement à été accepté
	 */
	@Override
	public RequestAdmissionI getRequestAdmissionFromAdmissionController(RequestAdmissionI requestAdmission) throws Exception {
		return ((RequestAdmissionSubmissionI)this.offering).getRequestAdmissionFromAdmissionController(requestAdmission);
	}

}
