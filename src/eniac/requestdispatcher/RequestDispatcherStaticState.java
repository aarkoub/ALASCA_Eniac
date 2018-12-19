package eniac.requestdispatcher;

import java.util.Map;

import eniac.requestdispatcher.interfaces.RequestDispatcherStaticStateI;
import fr.sorbonne_u.datacenter.data.AbstractTimeStampedData;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMStaticStateI;

public class RequestDispatcherStaticState extends AbstractTimeStampedData implements RequestDispatcherStaticStateI {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Map<String, ApplicationVMStaticStateI> avmStaticStateMap;

	public RequestDispatcherStaticState(Map<String, ApplicationVMStaticStateI> avmStaticStateMap){
		this.avmStaticStateMap = avmStaticStateMap;
		
	}
	
	@Override
	public Map<String, ApplicationVMStaticStateI> getAVMStaticStateMap(){
		return avmStaticStateMap;
	}
	
}
