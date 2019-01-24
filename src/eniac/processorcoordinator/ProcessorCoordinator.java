package eniac.processorcoordinator;

import java.util.ArrayList;
import java.util.HashMap;
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
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.datacenter.hardware.processors.connectors.ProcessorManagementConnector;
import fr.sorbonne_u.datacenter.hardware.processors.ports.ProcessorManagementOutboundPort;

public class ProcessorCoordinator extends AbstractComponent
implements ProcessorCoordinatorManagementI,
ProcessorCoordinatorFreqI{

	protected String coordinatorURI;
	protected String processorManagementInboundPortURI;
	protected String procURI;
	protected ProcessorManagementOutboundPort processorManagementOutboundPort;
	protected Map<String, ProcessorCoordinatorOrderOutboundPort> procCoordinatorOrderPortMap;
	protected ProcessorCoordinatorManagementInboundPort management_inport;
	
	protected int number = 0;
	protected Map<String, ProcessorCoordinatorFreqInboundPort> proc_cord_freq_map = new HashMap<>();
	
	
	
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
		
		management_inport = new ProcessorCoordinatorManagementInboundPort(
				processorCoordinatorManagementInboundPortURI, this);
		addPort(management_inport);
		management_inport.publishPort();
		
		procCoordinatorOrderPortMap = new HashMap<>();
		
		
	}
	
	@Override
	public void start() throws ComponentStartException{
		super.start();
		
		try {
			doPortConnection(processorManagementOutboundPort.getPortURI(),
					processorManagementInboundPortURI,
					ProcessorManagementConnector.class.getCanonicalName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
			System.out.println(automaticHandlerURI);
			procCoordinatorOrderPortMap.put(automaticHandlerURI, proc_order_outport);
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
	@Override
	public void setCoreFrequency(String handler_uri, int coreNo, int frequency) {
		try {
			processorManagementOutboundPort.setCoreFrequency(coreNo, frequency);
			
			/*
			 * Miss : if ecart trop grand, alors order les autres
			 * get dynamic state from proc
			 */
			for(String hand_uri : procCoordinatorOrderPortMap.keySet()){
				
				procCoordinatorOrderPortMap.get(hand_uri).setCoreFreqNextTime(procURI, coreNo, frequency);

			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
			// TODO Auto-generated catch block
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
			
			proc_cord_freq_map.put(inbound_port_uri, proc_freq_inport);
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		return inbound_port_uri;
	}

}
