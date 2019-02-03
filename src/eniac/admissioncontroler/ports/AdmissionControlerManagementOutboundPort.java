package eniac.admissioncontroler.ports;

import eniac.admissioncontroler.interfaces.AdmissionControlerManagementI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;

/**
 * La classe AdmissionControlerManagementOutboundPort est un port de sortie pour des appels de contrôle sur le contrôleur d'admission.
 * Cette classe a été créer pour préparer un éventuel besoin, cependant, nous n'en avons malheureusement pas eu le besoin d'où le fait qu'elle soit vide.
 * 
 *
 */

public class AdmissionControlerManagementOutboundPort extends AbstractOutboundPort
implements AdmissionControlerManagementI {

	
	public AdmissionControlerManagementOutboundPort(String uri, ComponentI owner ) throws Exception{
		super(uri, AdmissionControlerManagementI.class, owner);
		assert	uri != null && owner != null ;
	}
	
	public AdmissionControlerManagementOutboundPort(ComponentI owner) throws Exception {
		super(AdmissionControlerManagementI.class, owner);
		assert owner!=null;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;




}
