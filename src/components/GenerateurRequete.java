package components;

import fr.sorbonne_u.components.AbstractComponent;

public class GenerateurRequete extends AbstractComponent {

	public GenerateurRequete(String reflectionInboundPortURI, int nbThreads, int nbSchedulableThreads) {
		super(reflectionInboundPortURI, nbThreads, nbSchedulableThreads);
		// TODO Auto-generated constructor stub
	}
	
	public GenerateurRequete(int nbThreads, int nbSchedulableThreads) {
		super(nbThreads, nbSchedulableThreads);
		// TODO Auto-generated constructor stub
	}
	
	public GenerateurRequete(String uri, String portUri) {
		super(uri, 1, 0);
	}

}
