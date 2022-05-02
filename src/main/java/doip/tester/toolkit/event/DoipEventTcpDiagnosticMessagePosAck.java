package doip.tester.toolkit.event;

import doip.library.message.DoipTcpDiagnosticMessagePosAck;

public class DoipEventTcpDiagnosticMessagePosAck extends DoipEventTcpMessage {

	public DoipEventTcpDiagnosticMessagePosAck(
			long timestamp, 
			DoipTcpDiagnosticMessagePosAck doipTcpDiagnosticMessagePosAck) {
		super(timestamp, doipTcpDiagnosticMessagePosAck);
	}
}
