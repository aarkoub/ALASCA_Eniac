package eniac.applicationvm;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.sorbonne_u.datacenter.data.AbstractTimeStampedData;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMStaticStateI;

public class ApplicationVMStaticState extends AbstractTimeStampedData implements ApplicationVMStaticStateI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<Integer, Integer> coreProc;
	private Map<String, Set<Integer>> admissibleFreqCores;
	

	
	public ApplicationVMStaticState(Set<AllocatedCore> allocatedCores, Map<String,Set<Integer>> admissibleFreqCores) {
		
		coreProc = new HashMap<>();
		
		for(AllocatedCore ac : allocatedCores){
			coreProc.put(ac.coreNo, ac.processorNo);			
		}
		
		this.admissibleFreqCores = admissibleFreqCores;
		
	}

	@Override
	public Map<Integer, Integer> getIdCores() {
		return coreProc;
	}
	
	@Override
	public Map<String, Set<Integer>> getAdmissibleFreqCores(){
		return admissibleFreqCores;
	}

}
