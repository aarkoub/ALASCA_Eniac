package eniac.requestdispatcher.interfaces;

import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMDynamicStateI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMStaticStateI;

public interface RequestDispatcherStateDataConsumerI {
	
	public void			acceptRequestDispatcherStaticData(
			String					requestDisptacherURI,
			RequestDispatcherStaticStateI	staticState
			) throws Exception ;
	
	public void			acceptRequestDispatcherDynamicData(
			String					requestDisptacherURI,
			RequestDispatcherDynamicStateI	dynamicState
			) throws Exception ;

}
