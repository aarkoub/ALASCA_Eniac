package eniac.requestdispatcher.interfaces;

import java.util.Map;

import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.datacenter.interfaces.TimeStampingI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMDynamicStateI;

public interface RequestDispatcherDynamicStateI extends		DataOfferedI.DataI,
DataRequiredI.DataI,
TimeStampingI{
	
	public int getAverageRequestTime();

	public Map<String, ApplicationVMDynamicStateI> getAVMDynamicStateMap();
	
	public Map<String, Double> getScoresMap();
}
