package eniac.requestadmission.ports;

import eniac.admissioncontroler.AdmissionControler;
import eniac.admissioncontroler.interfaces.RequestAdmissionI;
import eniac.admissioncontroler.interfaces.RequestAdmissionSubmissionI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
/**
 * La classe RequestAdmissionSubmissionInboundPort est un port d'entrée pour les demandes du RequestGenerator d'hébergement vers le Contrôleur d'admission.
 * @author L-C
 *
 */
public class RequestAdmissionSubmissionInboundPort extends AbstractInboundPort implements RequestAdmissionSubmissionI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RequestAdmissionSubmissionInboundPort(String uri, ComponentI owner)
			throws Exception {
		super(uri, RequestAdmissionSubmissionI.class, owner);
	}
	
	public RequestAdmissionSubmissionInboundPort(ComponentI owner)
			throws Exception {
		super(RequestAdmissionSubmissionI.class, owner);
	}

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
		return this.getOwner().handleRequestSync(
				new AbstractComponent.AbstractService<RequestAdmissionI>() {
					@Override
					public RequestAdmissionI call() throws Exception {
						return ((AdmissionControler)this.getOwner()).getNewRequestAdmission(requestAdmission);
					
					}
				}) ;
			
		
	}


}
