package eniac.requestdispatcher.data;

import eniac.applicationvm.ports.ApplicationVMDynamicStateDataOutboundPort;
import eniac.applicationvm.ports.ApplicationVMStaticStateDataOutboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestNotificationInboundPort;
import fr.sorbonne_u.datacenter.software.ports.RequestSubmissionOutboundPort;
/**
 * Cette classe est un conteneur de ports, elle contient tous les ports utilisés entre le distributeur de requête et l'AVM en question.
 * Cela permet de regrouper logiquement les données et y accéder plus facilement mais aussi de le rendre plus lisible.
 * Etant données que c'est un simple conteneur il est inutile d'indiquer les le rôles de chaque méthodes, ce ne sont que des setters et getters.
 * Les attributs en revanches indiquent à quoi ils correspondent. 
 * @author L-C
 *
 */
public class AVMPorts {
	/**
	 * Port permettant de soumettre une requête au distributeur de requête.
	 */
	protected RequestSubmissionOutboundPort rsopvm;
	/**
	 * Port permettant de recevoir une notification de terminaison de requête de la part de l'AVM.
	 */
	protected RequestNotificationInboundPort rnipvm;
	/**
	 * Port permettant de réceptionner des données statiques.
	 */
	private ApplicationVMDynamicStateDataOutboundPort applicationVMDynamicStateDataOutboundPort;
	/**
	 * Port permettant de réceptionner des données dynamiques.
	 */
	private ApplicationVMStaticStateDataOutboundPort applicationVMStaticStateDataOutboundPort;
	
	public AVMPorts(RequestSubmissionOutboundPort rsopvm, RequestNotificationInboundPort rnipvm) {
		this.rsopvm = rsopvm;
		this.rnipvm = rnipvm;
		
	}
	
	public AVMPorts(RequestSubmissionOutboundPort rsopvm, RequestNotificationInboundPort rnipvm,
			ApplicationVMDynamicStateDataOutboundPort applicationVMDynamicStateDataOutboundPort,
			ApplicationVMStaticStateDataOutboundPort applicationVMStaticStateDataOutboundPort) {
		this.rsopvm = rsopvm;
		this.rnipvm = rnipvm;
		this.applicationVMDynamicStateDataOutboundPort = applicationVMDynamicStateDataOutboundPort;
		this.applicationVMStaticStateDataOutboundPort = applicationVMStaticStateDataOutboundPort;
		
	}
	
	public RequestSubmissionOutboundPort getRequestSubmissionOutboundPort() {
		return rsopvm;
	}
	
	public RequestNotificationInboundPort getRequestNotificationInboundPort() {
		return rnipvm;
	}
	
	
	public void setAvmStaticStateDataOutboundPort(
			ApplicationVMStaticStateDataOutboundPort applicationVMStaticStateDataOutboundPort) {
		this.applicationVMStaticStateDataOutboundPort = applicationVMStaticStateDataOutboundPort;
		
	}
	
	public ApplicationVMStaticStateDataOutboundPort getAvmStaticStateDataOutboundPort(){
		return applicationVMStaticStateDataOutboundPort;
	}
	
	public void setAvmDynamicStateDataOutboundPort(
			ApplicationVMDynamicStateDataOutboundPort applicationVMDynamicStateDataOutboundPort){
		this.applicationVMDynamicStateDataOutboundPort = applicationVMDynamicStateDataOutboundPort;
		
	}
	
	public ApplicationVMDynamicStateDataOutboundPort getAvmDynamicStateDataOutboundPort(){
		return applicationVMDynamicStateDataOutboundPort;
	}
	
	

}