package eniac.admissioncontroler.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * L'interface RequestAdmissionSubmissionI définit le service du composant pour les demandes du RequestGenerator d'hébergement vers le Contrôleur d'admission.
 * 
 *
 */

public interface RequestAdmissionSubmissionI extends OfferedI, RequiredI {
	
	/**
	 * Il s'agit de la demande d'hébergement d'application où l'on ajoute en paramètre les données relatifs pour un hébergement notamment
	 * l'URI de notification du RequestGenerator pour se faire notifier la fin des requêtes mais aussi par exemple le temps moyens souhaitées pour une requête.
	 * En retour on retourne un objet qui peut contenir l'URI du port de soumission du RequestDispatcher chargé de redistribué les requêtes du RequestGenerator, si le contrôle d'admission 
	 * accepte la demande.
	 * @param requestAdmission données sur la demande d'hébergement
	 * @return l'uri du port de soumission du RequestDispatcher si l'hébergement à été accepté
	 * @throws Exception exception
	 */
	public RequestAdmissionI getRequestAdmissionFromAdmissionController(RequestAdmissionI requestAdmission) throws Exception;
}
