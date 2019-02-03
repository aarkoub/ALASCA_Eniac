package eniac.processorcoordinator.interfaces;

/**
 * Interface de management du ProcessorCoordinator
 * @author Amel^^
 *
 */
public interface ProcessorCoordinatorManagementI {

	/**
	 * Ajout d'un port d'entrée pour ProcessorCoordinatorFreqInboundPort
	 * @return l'URI correspondante
	 * @throws Exception exception
	 */
	public String addCoordInboundPort() throws Exception;
	
	/**
	 * Supprimer le outport d'order pour l'AutomaticHandler donnée
	 * @param handler_uri URI de l'AutomaticHandler
	 * @throws Exception exception
	 */
	public void removeOrderOutport(String handler_uri) throws Exception;
	
	/**
	 * Notifie le ProcessorCoordinator qu'un coeur a été attribué
	 * et qu'il est géré par l'AutomaticHandler
	 * @param handler_uri	URI de l'AutomaticHandler
	 * @param coreNum	numéro du coeur concerné
	 * @throws Exception exception
	 */
	public void notifyCorePossession(String handler_uri, int coreNum) throws Exception;
	
	/**
	 * Notifie le ProcessorCoordinator qu'un coeur a été enlevé
	 * de la gestion du AutomaticHandler
	 * @param handler_uri	URI de l'AutomaticHandler
	 * @param coreNum	numéro du coeur concerné
	 * @throws Exception exception
	 */
	public void notifyCoreRestitution(String handler_uri, int coreNum) throws Exception;
}
