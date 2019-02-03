package eniac.admissioncontroler;

import fr.sorbonne_u.datacenter.hardware.computers.Computer;
import fr.sorbonne_u.datacenter.hardware.computers.Computer.AllocatedCore;

/**
 * La classe AllocationCore est un conteneur afin de garder en mémoire quelles sont les coeurs alloués à une AVM.
 * Cela permet par la suite, d'allouer des coeurs en plus, car une AVM doit avoir ses coeurs sur le même ordinateur, cela permet aussi de désallouer plus facilement.
 * 
 *
 */

public class AllocationCore {
	/**
	 * Ordinateur où se trouve les coeurs
	 */
	private Computer computer;
	/**
	 * L'ensemble de coeurs alloués
	 */
	private AllocatedCore[] cores;
	/**
	 * URI de l'AVM auquel les coeurs sont attribués
	 */
	private String VMUri;

	
	public AllocationCore(Computer computer, AllocatedCore[] cores, String VMUri) {
		this.computer = computer;
		this.cores = cores;
		this.VMUri = VMUri;
	}

	public String getVMUri() {
		return VMUri;
	}

	public void setVMUri(String vMUri) {
		VMUri = vMUri;
	}

	public Computer getComputer() {
		return computer;
	}

	public void setComputer(Computer computer) {
		this.computer = computer;
	}

	public AllocatedCore[] getCores() {
		return cores;
	}

	public void setCores(AllocatedCore[] cores) {
		this.cores = cores;
	}
	
	public void freeCores() throws Exception {
		computer.releaseCores(cores);
	}
	
}
