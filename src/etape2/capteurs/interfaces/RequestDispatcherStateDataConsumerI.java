package etape2.capteurs.interfaces;

import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMDynamicStateI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMStaticStateI;

public interface RequestDispatcherStateDataConsumerI {
	
	public void			acceptRequestDispatcherStaticData(
			String					requestDisptacherURI,
			RequestDispatcherStaticStateDataI	staticState
			) throws Exception ;
	
	public void			acceptRequestDispatcherDynamicData(
			String					requestDisptacherURI,
			RequestDispatcherDynamicStateDataI	dynamicState
			) throws Exception ;

}
