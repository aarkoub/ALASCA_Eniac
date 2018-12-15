package eniac.requestgenarator.interfaces;

import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;
import fr.sorbonne_u.datacenter.software.interfaces.RequestI;

public interface SubmissionISend extends OfferedI, RequiredI{
	public void	sendRequest(final RequestI r) throws Exception ;
	public void sendRequestAndNotify(final RequestI r) throws Exception ;
}
