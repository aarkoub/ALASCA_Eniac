package eniac.requestadmission.ports;

import eniac.admissioncontroler.AdmissionControler;
import eniac.admissioncontroler.interfaces.RequestAdmissionI;
import eniac.admissioncontroler.interfaces.RequestAdmissionNotificationI;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
/**
 * La classe RequestAdmissionNotificationInboundPort est un port d'entrée pour la demande de libération de ressources.
 * Cette classe a été créer pour préparer un éventuel besoin, cependant, nous n'en avons malheureusement pas eu le besoin d'où le fait qu'elle soit vide.
 * @author L-C
 *
 */
public class RequestAdmissionNotificationInboundPort extends AbstractInboundPort
		implements RequestAdmissionNotificationI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RequestAdmissionNotificationInboundPort(ComponentI owner) throws Exception {
		super(RequestAdmissionNotificationI.class, owner);
	}
	
	public RequestAdmissionNotificationInboundPort(String uri, ComponentI owner) throws Exception {
		super(uri, RequestAdmissionNotificationI.class, owner);
	}

	/**
	 * Effectue une demande auprès du contrôleur d'admission pour libérer les ressources utilisées pour servir les requêtes du RequestGenerator en question.
	 * @param requestAdmission données sur le RequestGenerator, notamment son URI pour l'identifier
	 */
	@Override
	public void acceptRequestTerminationNotification(RequestAdmissionI requestAdmission) throws Exception {
		this.getOwner().handleRequestAsync(
				new AbstractComponent.AbstractService<Void>() {
					@Override
					public Void call() throws Exception {
						((AdmissionControler)this.getOwner()).acceptRequestAdmissionTerminationNotification(requestAdmission);
						return null;
					}
				}) ;
	}

}
