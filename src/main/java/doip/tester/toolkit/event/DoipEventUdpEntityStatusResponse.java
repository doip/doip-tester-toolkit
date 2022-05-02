package doip.tester.toolkit.event;

import doip.library.message.DoipUdpEntityStatusResponse;

public class DoipEventUdpEntityStatusResponse extends DoipEventUdpMessage {

	public DoipEventUdpEntityStatusResponse(long timestamp, DoipUdpEntityStatusResponse doipUdpEntityStatusResponse) {
		super(timestamp, doipUdpEntityStatusResponse);
	}
}
