package doip.tester.toolkit.event;

import doip.library.message.DoipTcpAliveCheckResponse;

public class DoipEventTcpAliveCheckResponse extends DoipEventTcpMessage {

	public DoipEventTcpAliveCheckResponse(
			long timestamp, 
			DoipTcpAliveCheckResponse doipTcpAliveCheckResponse) {
		super(timestamp, doipTcpAliveCheckResponse);
	}
}
