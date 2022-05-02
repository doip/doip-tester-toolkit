package doip.tester.toolkit.event;

import doip.library.message.DoipUdpHeaderNegAck;

public class DoipEventUdpHeaderNegAck extends DoipEventUdpMessage {

	public DoipEventUdpHeaderNegAck(long timestamp, DoipUdpHeaderNegAck doipUdpHeaderNegAck) {
		super(timestamp, doipUdpHeaderNegAck);
	}
}
