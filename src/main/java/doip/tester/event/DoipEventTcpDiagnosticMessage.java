package doip.tester.event;

import doip.library.message.DoipTcpDiagnosticMessage;

public class DoipEventTcpDiagnosticMessage extends DoipEventTcpMessage {
	
	public DoipEventTcpDiagnosticMessage(
			long timestamp, 
			DoipTcpDiagnosticMessage doipTcpDiagnosticMessage) {
		super(timestamp, doipTcpDiagnosticMessage);
	}
}
