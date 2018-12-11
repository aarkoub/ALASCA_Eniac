package etape2.capteurs.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.sorbonne_u.datacenter.data.AbstractTimeStampedData;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;
import fr.sorbonne_u.datacenter.hardware.processors.Processor.ProcessorPortTypes;
import fr.sorbonne_u.datacenter.hardware.processors.ports.ProcessorServicesOutboundPort;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMStaticStateI;

public class ApplicationVMStaticState extends AbstractTimeStampedData implements ApplicationVMStaticStateI {

	private Map<Integer, Integer> coreProc;
	

	
	public ApplicationVMStaticState(Set<AllocatedCore> allocatedCores) {
		
		coreProc = new HashMap<>();
		
		for(AllocatedCore ac : allocatedCores){
			coreProc.put(ac.coreNo, ac.processorNo);			
		}
	}

	@Override
	public Map<Integer, Integer> getIdCores() {
		return coreProc;
	}

}
