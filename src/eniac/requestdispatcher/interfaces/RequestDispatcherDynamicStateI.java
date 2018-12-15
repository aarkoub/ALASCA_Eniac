package eniac.requestdispatcher.interfaces;

import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.datacenter.interfaces.TimeStampingI;

public interface RequestDispatcherDynamicStateI extends		DataOfferedI.DataI,
DataRequiredI.DataI,
TimeStampingI{
	
	public double getAverageRequestTime();

}
