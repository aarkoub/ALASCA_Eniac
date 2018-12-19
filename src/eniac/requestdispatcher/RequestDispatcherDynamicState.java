package eniac.requestdispatcher;

import java.util.Date;
import java.util.Map;

import eniac.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;
import fr.sorbonne_u.datacenter.data.AbstractTimeStampedData;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMDynamicStateI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMStaticStateI;

public class RequestDispatcherDynamicState extends AbstractTimeStampedData implements RequestDispatcherDynamicStateI {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private double average;
	private Map<String, ApplicationVMDynamicStateI> avmDynamicStateMap;

	public RequestDispatcherDynamicState(Map<String, Date> t1, Map<String, Date> t2, 
			Map<String, ApplicationVMDynamicStateI> avmDynamicStateMap) {
				
		long average=0 ;
				
		for(String reqUri : t1.keySet()){
			
			Date r1 = t1.get(reqUri);
			Date r2 = t2.get(reqUri);
			
			average += (r2.getTime()-r1.getTime());
			
		}
		
		this.average = average/t1.size();
		
		this.avmDynamicStateMap = avmDynamicStateMap;
		
	}
	
	@Override
	public double getAverageRequestTime() {
		return average;
		
	}
	
	@Override
	public Map<String, ApplicationVMDynamicStateI> getAVMDynamicStateMap(){
		return avmDynamicStateMap;
	}

}
