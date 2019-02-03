package eniac.automatichandler.interfaces;

import java.util.List;
import java.util.Map;

/**
 * Interface qui correspond aux demandes de l'AutomaticHandler vers l'AdmissionControler
 * @author Amel^^
 *
 */
public interface AutomaticHandlerRequestI {
	
	/**
	 * Ajoute une AVM au RequestDispatcher
	 * @param handler_uri	URI de l'AutomaticHandler qui fait la demande
	 * @param requestDispatcherURI	URI du RequestDispatcher
	 * @return	true si l'AVM a été ajoutée, false sinon
	 * @throws Exception
	 */
	 public Map<String, String> addAVMToRequestDispatcher(String handler_uri, String requestDispatcherURI) throws Exception;
	 
	 /**
	  * Enlève une AVM donnée à un RequestDispatcher
	  * @param handler_uri	URI de l'AutomaticHAndler
	  * @param requestDispatcherURI	URI du RequestDispatcher
	  * @param avmURI	URI de l'AVM
	  * @return true si l'AVM a pu être enlevée, false sinon
	  * @throws Exception
	  */
	 public List<String> removeAVMFromRequestDispatcher(String handler_uri, String requestDispatcherURI, String avmURI) throws Exception;
	 
	 /**
	  * Ajoute des coeurs à une AVM
	  * @param handler_uri	URI de l'AutomaticHandler
	  * @param avm_uri	URI de l'AVM
	  * @param nbcores	Nombre de coeurs à ajouter
	  * @return Map de l'URI du Processor et de l'URI du ProcessorCoordinatorFreqInboundPort
	  * qui a été crée si c'est la première fois que le ResquestDispatcher a un coeur sur 
	  * ce Processor
	  * 
	  * @throws Exception
	  */
	 public Map<String, String> addCoreToAvm(String handler_uri, String avm_uri, int nbcores) throws Exception;
	 
	 /**
	  * Enlève un coeur à une AVM donnée
	  * @param handler_uri URI de l'AutomaticHandler
	  * @param avm_uri URI de l'AVM
	  * @return la liste des URIS des Processor pour enlever le ProcessorCoordinatorFreqOutboundPort
	  * @throws Exception
	  */
	 public List<String> removeCoreFromAvm(String handler_uri, String avm_uri) throws Exception;

}
