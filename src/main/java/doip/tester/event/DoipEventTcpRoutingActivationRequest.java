package doip.tester.event;

import doip.library.message.DoipTcpRoutingActivationRequest;

public class DoipEventTcpRoutingActivationRequest extends DoipEventTcpMessage {

	public DoipEventTcpRoutingActivationRequest(
			long timestamp, 
			DoipTcpRoutingActivationRequest doipTcpRoutingActivationRequest) {
		super(timestamp, doipTcpRoutingActivationRequest);
	}
}
