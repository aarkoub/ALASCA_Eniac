package etape1.components;

public class RequestGenerator extends fr.sorbonne_u.datacenterclient.requestgenerator.RequestGenerator {

	public RequestGenerator(String rgURI, double meanInterArrivalTime, long meanNumberOfInstructions,
			String managementInboundPortURI, String requestSubmissionInboundPortURI,
			String requestNotificationInboundPortURI) throws Exception {
		
		super(rgURI, meanInterArrivalTime, meanNumberOfInstructions, managementInboundPortURI, requestSubmissionInboundPortURI,
				requestNotificationInboundPortURI);
		// TODO Auto-generated constructor stub
	}

}
