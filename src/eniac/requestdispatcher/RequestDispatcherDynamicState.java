package eniac.requestdispatcher;

import java.util.Map;

import eniac.requestdispatcher.interfaces.RequestDispatcherDynamicStateI;
import fr.sorbonne_u.datacenter.data.AbstractTimeStampedData;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMDynamicStateI;

public class RequestDispatcherDynamicState extends AbstractTimeStampedData implements RequestDispatcherDynamicStateI {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int average;
	private Map<String, ApplicationVMDynamicStateI> avmDynamicStateMap;
	private Map<String, Double> scoresMap;

	public RequestDispatcherDynamicState(int average, 
			Map<String, ApplicationVMDynamicStateI> avmDynamicStateMap,
			Map<String, Double> scoresMap) {
				
		this.average = average;
		this.avmDynamicStateMap = avmDynamicStateMap;
		this.scoresMap = scoresMap;
		
	}
	
	@Override
	public int getAverageRequestTime() {
		return average;
		
	}
	
	@Override
	public Map<String, ApplicationVMDynamicStateI> getAVMDynamicStateMap(){
		return avmDynamicStateMap;
	}
	
	@Override
	public Map<String, Double> getScoresMap(){
		return scoresMap;
	}

}
