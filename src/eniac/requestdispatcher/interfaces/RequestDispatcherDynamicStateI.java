package eniac.requestdispatcher.interfaces;

import java.util.Map;

import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.datacenter.interfaces.TimeStampingI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMDynamicStateI;

/**
 * Cette interface définit les méthodes permettant de récupérer les données dynamiques du requestDispatcher.
 * On récupère notamment la moyenne calculé à l'instant t, les scores de chaque AVM c'est-à-dire la charge de l'AVM 
 * par rapport aux requêtes mais aussi les données dynamiques sur les AVMs concernant les coeurs et ses fréquences.
 * @author L-C
 *
 */

public interface RequestDispatcherDynamicStateI extends		DataOfferedI.DataI,
DataRequiredI.DataI,
TimeStampingI{
	
	/**
	 * Retourne la moyenne avec le filtre exponentiel.
	 * @return la moyenne à l'instant t
	 */
	public int getAverageRequestTime();
	
	/**
	 * Retourne les données dynamiques de chaque AVM du requestDispatcher (les coeurs et les fréquences associées).
	 * @return les données dynamiques
	 */
	public Map<String, ApplicationVMDynamicStateI> getAVMDynamicStateMap();
	
	/**
	 * Retourne la charge quantifiées de chaque AVM du requestDispatcher.
	 * @return les scores de chaque AVM
	 */
	public Map<String, Double> getScoresMap();
}
