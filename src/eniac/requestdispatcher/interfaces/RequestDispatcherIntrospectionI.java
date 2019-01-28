package eniac.requestdispatcher.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

/**
 * L'interface RequestDispatcherIntrospectionI définit les services du composant pour introspecter le RequestDispatcher.
 * @author L-C
 *
 */

public interface RequestDispatcherIntrospectionI
extends		OfferedI,
RequiredI{
	
	/**
	 * Méthode pour récupérer les données statiques du RequestDispatcher
	 * @return données statiques
	 * @throws Exception exception
	 */
	public RequestDispatcherStaticStateI getStaticState() throws Exception ;
	
	
	/**
	 * Méthode pour récupérer les données dynamiques du RequestDispatcher
	 * @return données dynamiques
	 * @throws Exception exception
	 */
	public RequestDispatcherDynamicStateI getDynamicState() throws Exception;
}
