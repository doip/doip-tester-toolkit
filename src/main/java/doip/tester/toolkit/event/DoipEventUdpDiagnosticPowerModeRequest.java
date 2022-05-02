package doip.tester.toolkit.event;

import doip.library.message.DoipUdpDiagnosticPowerModeRequest;

public class DoipEventUdpDiagnosticPowerModeRequest extends DoipEventUdpMessage {
	
	public DoipEventUdpDiagnosticPowerModeRequest(long timestamp, DoipUdpDiagnosticPowerModeRequest doipUdpDiagnosticPowerModeRequest) {
		super(timestamp, doipUdpDiagnosticPowerModeRequest);
	}
}
