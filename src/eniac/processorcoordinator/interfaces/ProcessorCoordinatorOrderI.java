package eniac.processorcoordinator.interfaces;

/**
 * Interface qui permet au ProcessorCoordinator d'envoyer des ordres
 * à l'AutomaticHandler
 * @author Amel^^
 *
 */
public interface ProcessorCoordinatorOrderI {
	
	/**
	 * Demande à l'AutomaticHandler d'augmenter la fréquence du coeur
	 * @param procURI	URI du Processor
	 * @param coreNo	Numéro du coeur concerné
	 * @param frequency	Fréquence à laquelle le coeur doit être changé
	 * @throws Exception exception
	 */
	public void setCoreFreqNextTime(String procURI, int coreNo, final int frequency) throws Exception;
	
	/**
	 * Demande à l'AutomaticHandler d'enlever le port ProcessorCoordinatorFreqOutboundPort
	 * @param procURI	URI du Processor
	 * @throws Exception exception
	 */
	public void removeFreqPort(String procURI) throws Exception;

}

