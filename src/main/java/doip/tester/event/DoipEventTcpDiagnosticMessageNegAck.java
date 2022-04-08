package doip.tester.event;

import doip.library.message.DoipTcpDiagnosticMessageNegAck;

public class DoipEventTcpDiagnosticMessageNegAck extends DoipEventTcpMessage {
	
	public DoipEventTcpDiagnosticMessageNegAck(
			long timestamp, 
			DoipTcpDiagnosticMessageNegAck doipTcpDiagnosticMessageNegAck) {
		super(timestamp, doipTcpDiagnosticMessageNegAck);
	}
}
