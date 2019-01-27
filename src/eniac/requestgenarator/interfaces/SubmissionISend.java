package eniac.requestgenarator.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;

/**
 * Interface de soumission utilisé par le RequestGenerator (pour pouvoir utiliser Javassist)
 * @author L-C
 *
 */

public interface SubmissionISend extends OfferedI, RequiredI{
	/**
	 * Soumission simple d'une requête
	 * @param r la requête à soumettre
	 * @throws Exception exception
	 */
	public void	sendRequest(final RequestI r) throws Exception ;
	
	/**
	 * Soumission et demande de notification lorsque la requête termine
	 * @param r la requête à soumettre
	 * @throws Exception exception
	 */
	public void sendRequestAndNotify(final RequestI r) throws Exception ;
}
