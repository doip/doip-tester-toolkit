package doip.tester.toolkit.event;

import doip.library.message.DoipUdpDiagnosticPowerModeResponse;

public class DoipEventUdpDiagnosticPowerModeResponse extends DoipEventUdpMessage {
	
	public DoipEventUdpDiagnosticPowerModeResponse(long timestamp, DoipUdpDiagnosticPowerModeResponse doipUdpDiagnosticPowerModeReesponse) {
		super(timestamp, doipUdpDiagnosticPowerModeReesponse);
	}
}
