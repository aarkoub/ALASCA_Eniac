package eniac.processorcoordinator;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import eniac.processorcoordinator.connectors.ProcessorCoordinatorOrderConnector;
import eniac.processorcoordinator.interfaces.ProcessorCoordinatorFreqI;
import eniac.processorcoordinator.interfaces.ProcessorCoordinatorManagementI;
import eniac.processorcoordinator.ports.ProcessorCoordinatorFreqInboundPort;
import eniac.processorcoordinator.ports.ProcessorCoordinatorManagementInboundPort;
import eniac.processorcoordinator.ports.ProcessorCoordinatorOrderOutboundPort;
import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.connectors.DataConnector;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.connectors.ControlledDataConnector;
import fr.sorbonne_u.datacenter.hardware.processors.UnacceptableFrequencyException;
import fr.sorbonne_u.datacenter.hardware.processors.connectors.ProcessorManagementConnector;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorDynamicStateI;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorStateDataConsumerI;
import fr.sorbonne_u.datacenter.hardware.processors.interfaces.ProcessorStaticStateI;
import fr.sorbonne_u.datacenter.hardware.processors.ports.ProcessorDynamicStateDataOutboundPort;
import fr.sorbonne_u.datacenter.hardware.processors.ports.ProcessorManagementOutboundPort;
import fr.sorbonne_u.datacenter.hardware.processors.ports.ProcessorStaticStateDataOutboundPort;

/**
 * Le ProccessorCoordinator correspond à un cordinateur de processeur. 
 * Chaque processeur a son coordinateur de sorte à ce que ses coeurs
 * ait la même fréquence quand un gestionnaire automatique veut
 * augmenter ou diminiuer la fréquence d'un des coeurs utilisés par
 * une ApplicationVM. 
 * 
 *
 */

public class ProcessorCoordinator extends AbstractComponent
implements ProcessorCoordinatorManagementI,
ProcessorCoordinatorFreqI,
ProcessorStateDataConsumerI{
	
	/**
	 * URI du ProcessorCoordinator
	 */
	protected String coordinatorURI;
	
	/**
	 * URI du port d'entrée du management du Processor
	 */
	protected String processorManagementInboundPortURI;
	
	/**
	 * URI du Processor
	 */
	protected String procURI;
	
	/**
	 * Port de sortie du management du Processor
	 */
	protected ProcessorManagementOutboundPort processorManagementOutboundPort;
	
	/**
	 * Map qui les ports de sorties pour donner des ordres au AutomaticHandler
	 */
	protected Map<String, ProcessorCoordinatorOrderOutboundPort> procCoordinatorOrderPortMap;
	
	/**
	 * Port pour recevoir les données statiques du Processor
	 */
	protected ProcessorStaticStateDataOutboundPort static_outport;
	
	/**
	 * Port pour recevoir les données dynamiques du Processor
	 */
	protected ProcessorDynamicStateDataOutboundPort dynamic_outport;
	
	/**
	 * Ensemble des coeurs pour qui le coordinateur doit être notifié pour
	 * modifier leur fréquences
	 */
	
	/**
	 * int pour donner des noms port de coordination de fréquence 
	 * différents
	 * 
	 */
	protected int number = 0;
	
	/**
	 * Ensemble des fréquences admissibles pour le Processor
	 */
	protected Set<Integer> admissibleFreqs;
	
	/**
	 * Tableau des fréquences courantes des coeurs
	 */
	protected int[] currentFreqs;
	
	/**
	 * Seuil qui détermine la différence de fréquence admissible
	 */
	protected int freq_threshold;
	
	/**
	 * Map pour connaitre les coeurs qui appartiennent à telle AutomaticHandler
	 */
	protected Map<String, Set<Integer>> corePerHandler = new HashMap<>();
	
	/**
	 * Booléen pour savoir si le coordinateur a recu de nouvelles données
	 * par le Processor
	 */
	private boolean isNew;
	
	/**
	 * Map qui sert à connaître à quelle fréquence un coeur doit
	 * être mis si on devait le mettre à une certaine fréquence 
	 * (par un précédant appel à la méthode)
	 */
	protected Map<Integer, Integer> previousFreqs = new HashMap<>();
	
	/**
	 * Temps pour déterminer l'intervalle de temps où la fréquence des coeurs sont en train
	 * de diminuer et pendant lequel on ne peut pas augmenter la fréquence
	 */
	protected Date t1, t2;
	
	/**
	 * Constructeur du ProcessorCoordinator
	 * @param coordinatorURI 	URI du ProcessorCoordinator
	 * @param procURI 	URI du Processor
	 * @param processorManagementInboundPortURI 	URI de l'inbound port du management du Processor
	 * @param processorCoordinatorManagementInboundPortURI 	URI de l'inbound port du management du ProcessorCoordinator
	 * @throws Exception
	 */
	public ProcessorCoordinator(String coordinatorURI,
			String procURI,
			String processorManagementInboundPortURI,
			String processorCoordinatorManagementInboundPortURI) throws Exception{
		super(1, 1) ;
		
		assert coordinatorURI != null;
		assert processorManagementInboundPortURI != null;
		assert procURI != null;
		
		this.coordinatorURI = coordinatorURI;
		this.processorManagementInboundPortURI = processorManagementInboundPortURI;
		this.procURI = procURI;
		
		processorManagementOutboundPort =
				new ProcessorManagementOutboundPort(this);
		addPort(processorManagementOutboundPort);
		processorManagementOutboundPort.publishPort();
		
		ProcessorCoordinatorManagementInboundPort management_inport = new ProcessorCoordinatorManagementInboundPort(
				processorCoordinatorManagementInboundPortURI, this);
		addPort(management_inport);
		management_inport.publishPort();
		
		procCoordinatorOrderPortMap = new HashMap<>();
		
		toggleLogging();
		toggleTracing();
		
		logMessage("Coordinator for "+procURI);
		
		
	}
	
	@Override
	public void start() throws ComponentStartException{
		super.start();
		
		try {
			doPortConnection(processorManagementOutboundPort.getPortURI(),
					processorManagementInboundPortURI,
					ProcessorManagementConnector.class.getCanonicalName());
			
			List<String> uris = processorManagementOutboundPort.getStateDataInportsForProcCoord();
			
			String static_inport_uri = uris.get(0);
			String dynamic_inport_uri = uris.get(1);
			
			static_outport = new ProcessorStaticStateDataOutboundPort(this, procURI);
			addPort(static_outport);
			static_outport.publishPort();
			
			doPortConnection(static_outport.getPortURI(),
					static_inport_uri,
					DataConnector.class.getCanonicalName());
			
			dynamic_outport = new ProcessorDynamicStateDataOutboundPort(this, procURI);
			addPort(dynamic_outport);
			dynamic_outport.publishPort();
			
			doPortConnection(dynamic_outport.getPortURI(),
					dynamic_inport_uri,
					ControlledDataConnector.class.getCanonicalName());
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void addProcessorCoordinatorOrderOutboundPort(
			String automaticHandlerURI,
			String processorCoordinatorOrderInboundPortURI) throws Exception{
		
		try {
			ProcessorCoordinatorOrderOutboundPort proc_order_outport =
					new ProcessorCoordinatorOrderOutboundPort(this);
			addPort(proc_order_outport);
			proc_order_outport.publishPort();
			
			doPortConnection(proc_order_outport.getPortURI(),
					processorCoordinatorOrderInboundPortURI, 
					ProcessorCoordinatorOrderConnector.class.getCanonicalName());
			procCoordinatorOrderPortMap.put(automaticHandlerURI, proc_order_outport);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
	
	@Override
	public boolean setCoreFrequency(String handler_uri, int coreNo, int frequency) {
		try {
			
			Integer prevFreq ;
			
			if( (prevFreq = previousFreqs.get(coreNo))!=null) {
				
					if(currentFreqs[coreNo] != prevFreq) {
						processorManagementOutboundPort.setCoreFrequency(coreNo, prevFreq);	
						logMessage("Set core frequency for "+coreNo+" : from "+currentFreqs[coreNo]+" to "+prevFreq);
						return true;
					}
					previousFreqs.remove(coreNo);
					return false;
			}		
			
			int freq = frequency ;
			
			if(freq==currentFreqs[coreNo]) return false;
			
			if(freq!=currentFreqs[coreNo]){
				
				if(currentFreqs[coreNo]-frequency>0){
					t1 = new Date();
				}
				else{
					t2 = new Date();
					if(t1!=null && t2.getTime()-t1.getTime()<1000){
						return false;
					}
					else
						t1 = null;
				}
				
				if(isFreqGapTooBig(coreNo, frequency)){
					if(currentFreqs[coreNo]-frequency>0){
						freq = getPreviousFreq(currentFreqs[coreNo], admissibleFreqs);
					}
					else
						freq = getNextFreq(currentFreqs[coreNo], admissibleFreqs);
				}
				
				try{
					processorManagementOutboundPort.setCoreFrequency(coreNo, freq);
					logMessage("Set core frequency for "+coreNo+" : from "+currentFreqs[coreNo]+" to "+freq);
				}
				catch(UnacceptableFrequencyException e){
				System.out.println("Warning Exception catched "+coreNo+" "+currentFreqs[coreNo]+" "+freq);
				}
			}
			

			if(isNew){
				isNew = false;
				
					Set<Integer> occupied_cores = new HashSet<>();
				
					for(String hand_uri : procCoordinatorOrderPortMap.keySet()){
	
						for(Integer core :  corePerHandler.get(handler_uri)){
							
							occupied_cores.add(core);
							
							if(currentFreqs[core]!=freq){
								int next ;
								if(currentFreqs[core]-frequency > 0){
									next = getPreviousFreq(currentFreqs[core], admissibleFreqs);
								}
								else
									next = getNextFreq(currentFreqs[core], admissibleFreqs);
								
								previousFreqs.put(core, next);
								procCoordinatorOrderPortMap.get(hand_uri).setCoreFreqNextTime(procURI, core, next);
							
							}
	
						}
					
					}
					
					for(int i=0 ; i<currentFreqs.length ; i++){
						if(!occupied_cores.contains(i)){
							if(currentFreqs[i]!=freq){
								
								int next ;
								if(currentFreqs[i]-frequency > 0){
									next = getPreviousFreq(currentFreqs[i], admissibleFreqs);
								}
								else
									next = getNextFreq(currentFreqs[i], admissibleFreqs);
								
								processorManagementOutboundPort.setCoreFrequency(i, next);							
								
							}
						}
					}
					return  true;
				
			}
			
			
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return false;
		
		
	}
	
	/**
	 * Regarde si la fréquence d'un coeur dépasse le seuil par rapport à 
	 * une fréquence donnée.
	 * @param coreNum	numéro du coeur
	 * @param frequency	fréquence à comparer
	 * @return true si l'intervalle est trop grand ou si la fréquence donnée
	 * n'est pas une fréquence admissible, false sinon.
	 */
	private boolean isFreqGapTooBig(int coreNum, int frequency) {
		
		int currentFreq = currentFreqs[coreNum];
		
		boolean isFreqAdmissible = false;
		for(Integer freq : admissibleFreqs){
			if(frequency==freq){
				isFreqAdmissible = true;
				break;
			}
		}
		
		if(isFreqAdmissible){
			if( Math.abs(frequency-currentFreq)>freq_threshold){
				return true;
			}
			return false;
		}
		
		return true;
	}

	@Override
	public void finalise() throws Exception{
		
		if(processorManagementOutboundPort.connected()){
			processorManagementOutboundPort.doDisconnection();
		}
		for(String hand_uri : procCoordinatorOrderPortMap.keySet()){
			ProcessorCoordinatorOrderOutboundPort outport = procCoordinatorOrderPortMap.get(hand_uri);
			if(outport.connected())
				outport.unpublishPort();
		}
		
		
		super.finalise();
	}
	
	
	@Override
	public void shutdown() throws ComponentShutdownException {
		
		try {
			processorManagementOutboundPort.unpublishPort();
			for(String hand_uri : procCoordinatorOrderPortMap.keySet()){
				procCoordinatorOrderPortMap.get(hand_uri).unpublishPort();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		super.shutdown();
	}

	@Override
	public String addCoordInboundPort() {
		
		String inbound_port_uri = coordinatorURI+number++;
		
		try {
			ProcessorCoordinatorFreqInboundPort proc_freq_inport =
					new ProcessorCoordinatorFreqInboundPort(inbound_port_uri, this);
			addPort(proc_freq_inport);
			proc_freq_inport.publishPort();
			
						
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		return inbound_port_uri;
	}

	@Override
	public void removeOrderOutport(String handler_uri) throws Exception {
		
		procCoordinatorOrderPortMap.get(handler_uri).removeFreqPort(this.procURI);
		procCoordinatorOrderPortMap.remove(handler_uri).unpublishPort();	
		
		logMessage("Remove ordre outbound port for "+handler_uri);
		
	}

	@Override
	public void acceptProcessorStaticData(String processorURI, ProcessorStaticStateI staticState) throws Exception {
		
		admissibleFreqs = staticState.getAdmissibleFrequencies();
		freq_threshold = staticState.getMaxFrequencyGap();
		
	}

	@Override
	public void acceptProcessorDynamicData(String processorURI, ProcessorDynamicStateI currentDynamicState)
			throws Exception {
		
		
		currentFreqs = currentDynamicState.getCurrentCoreFrequencies();		
		isNew = true;		
	}


	@Override
	public void notifyCorePossession(String handler_uri, int coreNum) throws Exception {
		Set<Integer> cores = corePerHandler.get(handler_uri);
		if(cores==null){
			cores = new HashSet<>();
			cores.add(coreNum);
			
			corePerHandler.put(handler_uri, cores);
		}
		else{
			cores.add(coreNum);
		}
				
	}
	
	@Override
	public void notifyCoreRestitution(String handler_uri, int coreNum) throws Exception{
		corePerHandler.get(handler_uri).remove(coreNum);
	
	}
	
	/**
	 * Calcule la fréquence possible qui est plus basse que l'actuelle
	 * @param currentFreq	fréquence actuelle
	 * @param freqs	ensemble des fréquences possibles
	 * @return la fréquence d'haut dessus
	 */
	public int getNextFreq(int currentFreq, Set<Integer> freqs) {
		
		int ret = currentFreq;
		
		
		for(Integer i : freqs){
			if(i>ret)
				ret = i;
		}
		
		for(Integer i : freqs) {
			if(i>currentFreq){
				if(i<ret){
					ret = i;
				}
			}
		}

		return ret;
	}
	
	/**
	 * Calcule la fréquence possible qui est plus basse que l'actuelle
	 * @param currentFreq	fréquence actuelle
	 * @param freqs	ensemble des fréquences possibles
	 * @return la fréquence d'en dessous
	 */
	public int getPreviousFreq(int currentFreq, Set<Integer> freqs) {
	
		int ret = currentFreq;
		
		for(Integer i : freqs) {
			
			if(i<ret)
				ret = i;			
		}
		
		for(Integer i : freqs){
			if(i<currentFreq){
				if(i>ret)
					ret = i;
			}
		}
		
		return ret;
		
	}

}
