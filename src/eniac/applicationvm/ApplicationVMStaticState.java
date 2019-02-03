package eniac.applicationvm;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.sorbonne_u.datacenter.data.AbstractTimeStampedData;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMStaticStateI;

/**
 * Cette classe correspond à l'implantation de ApplicationVMStaticStateI, il s'agit d'un classe conteneur qui rassemble les données statiques 
 * d'une AVM et qui est transféré par la suite à requestDispatcher.
 * 
 *
 */

public class ApplicationVMStaticState extends AbstractTimeStampedData implements ApplicationVMStaticStateI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Correpondance du coeur au processeur
	 */
	private Map<Integer, Integer> coreProc;
	/**
	 * Ensemble des fréquences possibles pour chaque coeur
	 */
	private Map<String, Set<Integer>> admissibleFreqCores;
	

	
	public ApplicationVMStaticState(Set<AllocatedCore> allocatedCores, Map<String,Set<Integer>> admissibleFreqCores) {
		
		coreProc = new HashMap<>();
		
		for(AllocatedCore ac : allocatedCores){
			coreProc.put(ac.coreNo, ac.processorNo);			
		}
		
		this.admissibleFreqCores = admissibleFreqCores;
		
	}

	/**
	 * Retourne la correpondance du coeur au processeur.
	 * @return correpondance du coeur au processeur
	 */
	@Override
	public Map<Integer, Integer> getIdCores() {
		return coreProc;
	}
	
	
	/**
	 * Retourne l'ensemble des fréquences possibles pour chaque coeur.
	 * @return Ensemble des fréquences possibles pour chaque coeur
	 */
	@Override
	public Map<String, Set<Integer>> getAdmissibleFreqCores(){
		return admissibleFreqCores;
	}

}
