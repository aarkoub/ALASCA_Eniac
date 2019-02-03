package eniac.admissioncontroler.ports;

import eniac.admissioncontroler.AdmissionControler;
import eniac.admissioncontroler.interfaces.AdmissionControlerManagementI;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;


/**
 * La classe AdmissionControlerManagementInboundPort est un port d'entrée pour des appels de contrôle sur le contrôleur d'admission.
 * Cette classe a été créer pour préparer un éventuel besoin, cependant, nous n'en avons malheureusement pas eu le besoin d'où le fait qu'elle soit vide.
 * 
 *
 */
public class AdmissionControlerManagementInboundPort extends 	AbstractInboundPort
implements	AdmissionControlerManagementI {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public	AdmissionControlerManagementInboundPort(
		ComponentI owner
		) throws Exception
	{
		super(AdmissionControlerManagementI.class, owner) ;

		assert	owner != null && owner instanceof AdmissionControler ;
	}

	public	AdmissionControlerManagementInboundPort(
		String uri,
		ComponentI owner
		) throws Exception
	{
		super(uri, AdmissionControlerManagementI.class, owner);

		assert	owner != null && owner instanceof AdmissionControler ;
	}


}
