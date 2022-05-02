package doip.tester.toolkit.event;

import doip.library.message.DoipMessage;

public class DoipEventMessage extends DoipEvent {

	private DoipMessage doipMessage = null;
	
	public DoipEventMessage(long timestamp, DoipMessage doipMessage) {
		super(timestamp);
		this.doipMessage = doipMessage;
	}
	
	public DoipMessage getDoipMessage() {
		return this.doipMessage;
	}
	
}
