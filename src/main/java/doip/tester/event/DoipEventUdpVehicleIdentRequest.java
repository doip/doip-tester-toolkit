package doip.tester.event;

import doip.library.message.DoipUdpVehicleIdentRequest;

public class DoipEventUdpVehicleIdentRequest extends DoipEventUdpMessage {
	
	public DoipEventUdpVehicleIdentRequest(long timestamp, DoipUdpVehicleIdentRequest doipUdpVehicleIdentRequest) {
		super(timestamp, doipUdpVehicleIdentRequest);
	}
}
