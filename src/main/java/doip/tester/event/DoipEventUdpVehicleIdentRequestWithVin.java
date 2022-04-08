package doip.tester.event;

import doip.library.message.DoipUdpVehicleIdentRequest;
import doip.library.message.DoipUdpVehicleIdentRequestWithVin;

public class DoipEventUdpVehicleIdentRequestWithVin extends DoipEventUdpMessage {
	
	public DoipEventUdpVehicleIdentRequestWithVin(long timestamp, DoipUdpVehicleIdentRequestWithVin doipUdpVehicleIdentRequestWithVin) {
		super(timestamp, doipUdpVehicleIdentRequestWithVin);
	}

}
