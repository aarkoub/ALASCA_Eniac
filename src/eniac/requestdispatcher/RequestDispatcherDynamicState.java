package eniac.requestdispatcher;

import java.util.Map;


import eniac.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;
import fr.sorbonne_u.datacenter.data.AbstractTimeStampedData;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMDynamicStateI;

/**
 * Cette classe correspond aux données envoyées dynamiquement par le request Dispatcher à ceux qui le demandes, cela sert notamment à récupérer les
 * scores des AVMs liés au distributeur de requête, mais aussi la moyenne qui à été calculé localement  et ainsi que les données relatives aux AVMs.
 * @author L-C
 *
 */
public class RequestDispatcherDynamicState extends AbstractTimeStampedData implements RequestDispatcherDynamicStateI {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * la moyenne calculé
	 */
	private int average;
	/**
	 * Les données des AVMs
	 */
	private Map<String, ApplicationVMDynamicStateI> avmDynamicStateMap;
	/**
	 * Le score de chaque AVMs qui correspond à la division de la taille de la liste de requête de l'AVM par le nombre de coeur alloués
	 * à celui-ci.
	 */
	private Map<String, Double> scoresMap;

	public RequestDispatcherDynamicState(int average, 
			Map<String, ApplicationVMDynamicStateI> avmDynamicStateMap,
			Map<String, Double> scoresMap) {
				
		this.average = average;
		this.avmDynamicStateMap = avmDynamicStateMap;
		this.scoresMap = scoresMap;
		
	}
	
	/**
	 * Retourne le temps moyen prit pour traiter une requêtes.
	 * @return la moyenne
	 */
	@Override
	public int getAverageRequestTime() {
		return average;
		
	}
	
	/**
	 * Retourne les données dynamiques de chaque AVM du distributeur de requête.
	 * @return données dynamiques
	 */
	@Override
	public Map<String, ApplicationVMDynamicStateI> getAVMDynamicStateMap(){
		return avmDynamicStateMap;
	}
	
	/**
	 * Retournes le score de chaque AVM.
	 * @return map de scores
	 */
	@Override
	public Map<String, Double> getScoresMap(){
		return scoresMap;
	}

}
