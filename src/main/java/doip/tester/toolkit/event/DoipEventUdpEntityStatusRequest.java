package doip.tester.toolkit.event;

import doip.library.message.DoipUdpEntityStatusRequest;

public class DoipEventUdpEntityStatusRequest extends DoipEventUdpMessage {

	public DoipEventUdpEntityStatusRequest(long timestamp, DoipUdpEntityStatusRequest doipUdpEntityStatusRequest) {
		super(timestamp, doipUdpEntityStatusRequest);
	}
}
