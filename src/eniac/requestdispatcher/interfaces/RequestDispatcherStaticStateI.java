package eniac.requestdispatcher.interfaces;

import java.util.Map;

import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMStaticStateI;

/**
 * Interface définissant la méthode pour récupérer les données statiques de chaque AVM lié au distributeur de requête.
 * @author L-C
 *
 */

public interface RequestDispatcherStaticStateI extends		DataOfferedI.DataI,
DataRequiredI.DataI{

	/**
	 * Retourne les données statiques de chaque AVM (coeurs et fréquences)
	 * @return
	 */
	public Map<String, ApplicationVMStaticStateI> getAVMStaticStateMap();
	
	// The data interface is defined as an external interface
		// RequestDispatcherStaticStateI

}
