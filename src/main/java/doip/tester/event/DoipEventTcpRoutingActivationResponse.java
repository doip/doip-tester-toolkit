package doip.tester.event;

import doip.library.message.DoipTcpRoutingActivationResponse;

public class DoipEventTcpRoutingActivationResponse extends DoipEventTcpMessage {
	
	public DoipEventTcpRoutingActivationResponse(
			long timestamp, 
			DoipTcpRoutingActivationResponse doipTcpRoutingActivationResponse) {
		
		super(timestamp, doipTcpRoutingActivationResponse);
	}
}
