package eniac.processorcoordinator.interfaces;

/**
 * Interface qui définit ce que l'AutomaticHandler 
 * peut demander à au ProcessorCoordinator concernant la fréquence
 * des coeurs
 *
 */
public interface ProcessorCoordinatorFreqI {
	
	/**
	 * Demande au ProcessorCoordinator de changer la fréquence du coeur donné
	 * @param handler_uri URI du l'AutomaticHandler
	 * @param coreNo numéro du coeur
	 * @param frequency fréquence souahaitée
	 * @return true si la fréquence a été changée, false sinon
	 * @throws Exception exception
	 */
	public boolean setCoreFrequency(String handler_uri, final int coreNo, final int frequency) throws Exception;
	
	/**
	 * Demande au ProcessorCoordinator de créer des ports pour gérer les ordres d'un AutomaticHandler
	 * @param handler_uri URI de l'AutomaticHandler
	 * @param processorCoordinatorOrderInboundPortURI uri
	 * @throws Exception exception
	 */
	public void addProcessorCoordinatorOrderOutboundPort(String handler_uri, String processorCoordinatorOrderInboundPortURI) throws Exception;
		
}
