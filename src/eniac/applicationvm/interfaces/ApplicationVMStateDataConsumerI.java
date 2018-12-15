package eniac.applicationvm.interfaces;

import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMDynamicStateI;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMStaticStateI;

public interface ApplicationVMStateDataConsumerI {
	
	public void			acceptApplicationVMStaticData(
			String					avmURI,
			ApplicationVMStaticStateI	staticState
			) throws Exception ;
	
	public void			acceptApplicationVMDynamicData(
			String					avmURI,
			ApplicationVMDynamicStateI	dynamicState
			) throws Exception ;

}
