package eniac.requestdispatcher;

import java.util.Map;

import eniac.requestdispatcher.interfaces.RequestDispatcherStaticStateI;
import fr.sorbonne_u.datacenter.data.AbstractTimeStampedData;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMStaticStateI;

/**
 * Cette classe correspond au données envoyés statiquement, celui-ci n'est effectué qu'une fois car ce sont des données qui ne sont pas
 * susceptible de changé au cours de l'exécution.
 * @author L-C
 *
 */
public class RequestDispatcherStaticState extends AbstractTimeStampedData implements RequestDispatcherStaticStateI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Données statiques
	 */
	private Map<String, ApplicationVMStaticStateI> avmStaticStateMap;

	public RequestDispatcherStaticState(Map<String, ApplicationVMStaticStateI> avmStaticStateMap){
		this.avmStaticStateMap = avmStaticStateMap;
		
	}
	
	/**
	 * Retourne les données statiques de chaque AVM lié au distributeur de requête.
	 * @return les données statiques
	 */
	@Override
	public Map<String, ApplicationVMStaticStateI> getAVMStaticStateMap(){
		return avmStaticStateMap;
	}
	
}
