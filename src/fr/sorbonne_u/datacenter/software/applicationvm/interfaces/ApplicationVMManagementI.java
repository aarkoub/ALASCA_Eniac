package fr.sorbonne_u.datacenter.software.applicationvm.interfaces;


import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;

/**
 * Cette interface est une modification de l'interface ApplicationVMManagementI fournit dans BCM par Mr. Jacques Malenfant,
 * nous y rajoutons des méthodes par nécessité.
 */
public interface			ApplicationVMManagementI
extends		OfferedI,
			RequiredI
{
	/**
	 * allocate cores to the application virtual machine.
	 * 
	 * <p><strong>Contract</strong></p>
	 * 
	 * <pre>
	 * pre	allocatedCores != null and allocatedCores.length != 0
	 * post	true			// no postcondition.
	 * </pre>
	 *
	 * @param allocatedCores	array of cores already reserved provided to the VM.
	 * @throws Exception	<i>todo.</i>
	 */
	public void			allocateCores(AllocatedCore[] allocatedCores)
	throws Exception ;
	
		
	/**
	 * Retire le port entre l'AVM et le Processeur
	 * @param processorUri processeur à retirer
	 * @throws Exception Exception
	 */
	public void removeProcDataStatePorts(String processorUri) throws Exception;


}
