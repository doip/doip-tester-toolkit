package doip.tester.event;

import doip.library.message.DoipUdpVehicleAnnouncementMessage;

public class DoipEventUdpVehicleAnnouncementMessage extends DoipEventUdpMessage {

	public DoipEventUdpVehicleAnnouncementMessage(long timestamp, DoipUdpVehicleAnnouncementMessage doipUdpVehicleAnnouncementMessage) {
		super(timestamp, doipUdpVehicleAnnouncementMessage);
	}
}
