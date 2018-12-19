package eniac.requestdispatcher.interfaces;

import java.util.Map;

import fr.sorbonne_u.components.interfaces.DataOfferedI;
import fr.sorbonne_u.components.interfaces.DataRequiredI;
import fr.sorbonne_u.datacenter.interfaces.TimeStampingI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMStaticStateI;

public interface RequestDispatcherStaticStateI extends		DataOfferedI.DataI,
DataRequiredI.DataI{

	public Map<String, ApplicationVMStaticStateI> getAVMStaticStateMap();
	
	// The data interface is defined as an external interface
		// RequestDispatcherStaticStateI

}
