package eniac.requestdispatcher.data;

/**
 * Cette classe correspond à un conteneur de données, plus précisément nous gardons l'ensemble des ports et des URIs d'une AVM,
 * afin d'y accéder plus facilement et en terme de structuration.
 * Etant données que c'est un simple conteneur il est inutile d'indiquer les le rôles de chaque méthodes, ce ne sont que des setters et getters.
 * Les attributs en revanches indiquent à quoi ils correspondent. 
 * @author L-C
 *
 */

public class AVMData {
	/**
	 * L'ensemble des ports utilisés par le distributeur de requêtes pour une AVM.
	 */
	private AVMPorts avmports;
	/**
	 * L'ensemble des Uris utilisés entre le distributeur de requêtes et une AVM.
	 */
	private AVMUris avmuris;
	/**
	 * URI utilisées pour échanger les données statiques.
	 */
	private String avmDynamicStateDataInboundPortURI;
	/**
	 * URI utilisées pour échanger les données dynamiques.
	 */
	private String avmStaticStateDataInboundPortURI;

	/**
	 * Unique constructeur
	 * @param avmuris Uris
	 * @param avmports Ports
	 */
	public AVMData(AVMUris avmuris, AVMPorts avmports) {
		this.avmports = avmports;
		this.avmuris = avmuris;
	}
	
	
	public AVMPorts getAvmports() {
		return avmports;
	}

	public AVMUris getAvmuris() {
		return avmuris;
	}

	public void setAvmports(AVMPorts avmports) {
		this.avmports = avmports;
	}

	public void setAvmuris(AVMUris avmuris) {
		this.avmuris = avmuris;
	}
	
	public void setAvmDynamicStateDataInboundPortURI(String uri){
		avmDynamicStateDataInboundPortURI = uri;
	}
	
	public String getAvmDynamicStateDataInboundPortURI(){
		return avmDynamicStateDataInboundPortURI;
	}
	
	public void setAvmStaticStateDataInboundPortURI(String uri){
		avmStaticStateDataInboundPortURI = uri;
	}
	
	public String getAvmStaticStateDataInboundPortURI(){
		return avmStaticStateDataInboundPortURI;
	}

	
	
	
}
