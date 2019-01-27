package eniac.requestdispatcher.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface RequestDispatcherIntrospectionI
extends		OfferedI,
RequiredI{
	
	public RequestDispatcherStaticStateI getStaticState() throws Exception ;
	
	public RequestDispatcherDynamicStateI getDynamicState() throws Exception;
}
