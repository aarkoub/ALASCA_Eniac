package eniac.requestdispatcher.interfaces;


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
