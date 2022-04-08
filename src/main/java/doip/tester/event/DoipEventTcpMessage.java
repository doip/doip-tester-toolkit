package doip.tester.event;

import doip.library.message.DoipTcpMessage;

public class DoipEventTcpMessage extends DoipEventMessage {
	
	public DoipEventTcpMessage(long timestamp, DoipTcpMessage doipTcpMessage) {
		super(timestamp, doipTcpMessage);
	}
}
