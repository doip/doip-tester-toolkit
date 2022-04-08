package doip.tester.event;

import doip.library.message.DoipTcpHeaderNegAck;

public class DoipEventTcpHeaderNegAck extends DoipEventTcpMessage {
	
	public DoipEventTcpHeaderNegAck(long timestamp, DoipTcpHeaderNegAck doipTcpHeaderNegAck) {
		super(timestamp, doipTcpHeaderNegAck);
	}
}
