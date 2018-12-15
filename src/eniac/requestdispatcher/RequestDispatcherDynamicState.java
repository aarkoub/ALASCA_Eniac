package eniac.requestdispatcher;

import java.util.Date;
import java.util.Map;

import eniac.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;
import fr.sorbonne_u.datacenter.data.AbstractTimeStampedData;

public class RequestDispatcherDynamicState extends AbstractTimeStampedData implements RequestDispatcherDynamicStateI {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private double average;

	public RequestDispatcherDynamicState(Map<String, Date> t1, Map<String, Date> t2) {
				
		long average=0 ;
		
		int size = t1.size();
		
		for(String reqUri : t1.keySet()){
			
			Date r1 = t1.get(reqUri);
			Date r2 = t2.get(reqUri);
			
			average += (r2.getTime()-r1.getTime());
			
		}
		
		this.average = average/t1.size();
		
	}
	
	@Override
	public double getAverageRequestTime() {
		return average;
		
	}

}
