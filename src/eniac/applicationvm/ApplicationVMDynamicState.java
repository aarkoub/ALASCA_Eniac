package eniac.applicationvm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.helpers.CVMDebugModes;
import fr.sorbonne_u.datacenter.data.AbstractTimeStampedData;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.processors.Processor;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMDynamicStateI;

public class ApplicationVMDynamicState extends AbstractTimeStampedData implements ApplicationVMDynamicStateI{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String uri;
	private Map<AllocatedCore, Boolean> allocatedCoresIdleStatus;
	private Map<String, Map<Integer, Integer>> procCurrentFreqCoresMap;
	
	public ApplicationVMDynamicState(String avmUri, Map<AllocatedCore, Boolean> allocatedCoresIdleStatus, Map<String, Map<Integer, Integer>> procCurrentFreqCoresMap) {
		this.uri = avmUri;
		this.allocatedCoresIdleStatus = allocatedCoresIdleStatus;
		this.procCurrentFreqCoresMap = procCurrentFreqCoresMap;
		
	}
	
	@Override
	public String getApplicationVMURI() {
		// TODO Auto-generated method stub
		return uri;
	}
	
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

	@Override
	public boolean isIdle() {
		
		for(AllocatedCore allocatedCore : allocatedCoresIdleStatus.keySet()){
		
			if(allocatedCoresIdleStatus.get(allocatedCore)==true){
				return true;
			}
			
		}
		return false;
	}
	
	@Override
	public int getTotalNumberOfCores(){
		return allocatedCoresIdleStatus.size();
	
	}
	
	
	@Override
	public Map<String, Map<Integer, Integer>> getProcCurrentFreqCoresMap() {
		return procCurrentFreqCoresMap;
	}
	


}
