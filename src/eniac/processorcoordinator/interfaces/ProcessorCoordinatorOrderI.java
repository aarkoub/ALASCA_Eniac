package eniac.processorcoordinator.interfaces;

public interface ProcessorCoordinatorOrderI {
	
	public void setCoreFreqNextTime(String procURI, int coreNo, final int frequency) throws Exception;

	public void removeFreq(String procURI) throws Exception;

}

