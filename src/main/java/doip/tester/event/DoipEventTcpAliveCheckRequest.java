package doip.tester.event;

import doip.library.message.DoipTcpAliveCheckRequest;

public class DoipEventTcpAliveCheckRequest extends DoipEventTcpMessage {
	
	public DoipEventTcpAliveCheckRequest(long timestamp, DoipTcpAliveCheckRequest doipTcpAliveCheckRequest) {
		super(timestamp, doipTcpAliveCheckRequest);
	}
}
