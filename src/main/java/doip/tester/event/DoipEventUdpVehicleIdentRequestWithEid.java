package doip.tester.event;

import doip.library.message.DoipUdpVehicleIdentRequestWithEid;

public class DoipEventUdpVehicleIdentRequestWithEid extends DoipEventUdpMessage {

	public DoipEventUdpVehicleIdentRequestWithEid(long timestamp, DoipUdpVehicleIdentRequestWithEid doipUdpVehicleIdentRequestWithEid) {
		super(timestamp, doipUdpVehicleIdentRequestWithEid);
	}
}
