package doip.tester.event;

import doip.library.message.DoipUdpMessage;

public class DoipEventUdpMessage extends DoipEventMessage {

	public DoipEventUdpMessage(long timestamp, DoipUdpMessage doipUdpMessage) {
		super(timestamp, doipUdpMessage);
	}
}
