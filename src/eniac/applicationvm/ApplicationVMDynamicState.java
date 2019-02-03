package eniac.applicationvm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.sorbonne_u.datacenter.data.AbstractTimeStampedData;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMDynamicStateI;

/**
 * Cette classe correspond à l'implantation de ApplicationVMDynamicStateI, il s'agit d'un classe conteneur qui rassemble les données dynamiques 
 * d'une AVM et qui est transféré par la suite à requestDispatcher.
 * 
 *
 */

public class ApplicationVMDynamicState extends AbstractTimeStampedData implements ApplicationVMDynamicStateI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * URI de l'AVM.
	 */
	private String uri;
	/**
	 * Status des coeurs, s'ils sont utilisés.
	 */
	private Map<AllocatedCore, Boolean> allocatedCoresIdleStatus;
	/**
	 * Fréquences de chaque coeur.
	 */
	private Map<String, Map<Integer, Integer>> procCurrentFreqCoresMap;
	/**
	 * Nombre de coeurs alloués.
	 */
	private int nballocatedcore;
	/**
	 * Nombre de requêtes en cours dans l'AVM.
	 */
	private int nbrequest;
	
	public ApplicationVMDynamicState(String avmUri, Map<AllocatedCore, Boolean> allocatedCoresIdleStatus, Map<String, Map<Integer, Integer>> procCurrentFreqCoresMap,
			int nballocatedcore, int nbrequest) {
		this.uri = avmUri;
		this.allocatedCoresIdleStatus = allocatedCoresIdleStatus;
		this.procCurrentFreqCoresMap = procCurrentFreqCoresMap;
		this.nballocatedcore = nballocatedcore;
		this.nbrequest = nbrequest;
		
	}
	
	/**
	 * retourne l'URI de l'AVM.
	 * @return URI de l'AVM
	 */
	@Override
	public String getApplicationVMURI() {
		// TODO Auto-generated method stub
		return uri;
	}
	
	/**
	 * Retourne le status des coeurs, s'ils sont utilisés.
	 * @return Status des coeurs
	 */
	@Override
	public List<AllocatedCore> getIdleAllocatedCores(){
		
		List<AllocatedCore> idleCoresList = new ArrayList<>();
		
		for(AllocatedCore allocatedCore : allocatedCoresIdleStatus.keySet()){
			
			if(allocatedCoresIdleStatus.get(allocatedCore)==true){
				idleCoresList.add(allocatedCore);
			}
			
		}
		
		return idleCoresList; 
	}

	/**
	 * Indique si l'AVM est oisive.
	 * @return true s'il est oisive/false sinon
	 */
	@Override
	public boolean isIdle() {
		
		for(AllocatedCore allocatedCore : allocatedCoresIdleStatus.keySet()){
		
			if(allocatedCoresIdleStatus.get(allocatedCore)==true){
				return true;
			}
			
		}
		return false;
	}
	
	/**
	 * Retourne le nombre de coeurs.
	 * @return le nombre de coeurs
	 */
	@Override
	public int getTotalNumberOfCores(){
		return allocatedCoresIdleStatus.size();
	
	}
	
	/**
	 * Retourne les fréquences de chaque coeur.
	 * @return fréquences des coeurs
	 */
	@Override
	public Map<String, Map<Integer, Integer>> getProcCurrentFreqCoresMap() {
		return procCurrentFreqCoresMap;
	}
	
	/**
	 * Retourne le score de l'AVM, le score étant la charge de requêtes par rapport à son nombre de coeurs.
	 * @return le score de l'AVM
	 */
	@Override
	public double getScore() {
		return (double)nbrequest/(double)nballocatedcore;
	}
	



}
