package etape2.capteurs.implementation;

import fr.sorbonne_u.datacenter.data.AbstractTimeStampedData;
import fr.sorbonne_u.datacenter.software.applicationvm.interfaces.ApplicationVMDynamicStateI;

public class ApplicationVMDynamicState extends AbstractTimeStampedData implements ApplicationVMDynamicStateI{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String uri;
	
	public ApplicationVMDynamicState(String avmUri) {
		this.uri = avmUri;
		
	}
	
	@Override
	public String getApplicationVMURI() {
		// TODO Auto-generated method stub
		return uri;
	}

	@Override
	public boolean isIdle() {
		// TODO Auto-generated method stub
		return false;
	}

}
