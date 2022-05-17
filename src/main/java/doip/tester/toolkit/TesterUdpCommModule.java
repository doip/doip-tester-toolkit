package doip.tester.toolkit;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import doip.library.message.DoipUdpVehicleIdentRequest;
import doip.library.message.DoipUdpVehicleIdentRequestWithEid;
import doip.library.message.DoipUdpVehicleIdentRequestWithVin;
import doip.logging.LogManager;
import doip.logging.Logger;

public class TesterUdpCommModule extends DoipUdpMessageHandlerWithEventCollection {
	
	private static Logger logger = LogManager.getLogger(TesterUdpCommModule.class);
	
	private TestConfig config = null;

	public TesterUdpCommModule(TestConfig config) {
		super(config);
		this.config = config;
	}
	/**
	 * Sends a UDP message to the address which is given as argument.
	 * The target port will be taken from the configuration file.
	 * @param data The data to send.
	 * 
	 * @param address The target address to which the UDP message shall
	 *                be send
	 *                
	 * @throws IOException Throws an IOException if there was a problem 
	 *                     to send the data
	 */
	public void send(byte[] data, InetAddress address) throws IOException {
		logger.trace(">>> public void send(byte[] data, InetAddress address) throws IOException");
		this.sendDatagramPacket(data, data.length,
				address, config.getTargetPort());
		logger.trace("<<< public void send(byte[] data, InetAddress address) throws IOException");
	}

	/**
	 * Sends a DoIP vehicle identification request message.
	 * 
	 * @param address The target address to which the message shall be send to.
	 * 
	 * @throws IOException Throws an IOException if there was a problem 
	 *                     to send the data.
	 */
	public void sendDoipUdpVehicleIdentRequest(InetAddress address) throws IOException {
		logger.trace(">>> public void sendDoipUdpVehicleIdentRequest(InetAddress address) throws IOException");
		DoipUdpVehicleIdentRequest request = new DoipUdpVehicleIdentRequest();
		this.send(request, address,
				config.getTargetPort());
		logger.trace("<<< public void sendDoipUdpVehicleIdentRequest(InetAddress address) throws IOException");
	}

	/**
	 * Sends a DoIP vehicle identification request message with EID.
	 * 
	 * @param eid The EID which shall be set in the message. 
	 * 
	 * @param address The target address to which the message shall be send to.
	 *
	 * @throws IOException Throws an IOException if there was a problem 
	 *                     to send the data.
	 */
	public void sendDoipUdpVehicleIdentRequestWithEid(byte[] eid, InetAddress address) throws IOException {
		logger.trace(">>> public void sendDoipUdpVehicleIdentRequestWithEid(byte[] eid, InetAddress address) throws IOException");
		DoipUdpVehicleIdentRequestWithEid request = new DoipUdpVehicleIdentRequestWithEid(
				eid);
		this.send(request, address,
				config.getTargetPort());
		logger.trace("<<< public void sendDoipUdpVehicleIdentRequestWithEid(byte[] eid, InetAddress address) throws IOException");
	}

	/**
	 * Sends a DoIP vehicle identification request message with VIN
	 * 
	 * @param vin the VIN which shall be set in the message.
	 * 
	 * @param address The target address to which the message shall be send to.
	 * 
	 * @throws IOException Throws an IOException if there was a problem 
	 *                     to send the data.
	 */
	public void sendDoipUdpVehicleIdentRequestWithVin(byte[] vin, InetAddress address) throws IOException {
		logger.trace(">>> public void sendDoipUdpVehicleIdentRequestWithVin(byte[] vin, InetAddress address)");
		try {
			DoipUdpVehicleIdentRequestWithVin request = new DoipUdpVehicleIdentRequestWithVin(
					vin);
			this.send(request, address,
					config.getTargetPort());
		} finally {
			logger.trace("<<< public void sendDoipUdpVehicleIdentRequestWithVin(byte[] vin, InetAddress address)");
		}
	} 
	
	@Override
	public void onHeaderTooShort(DatagramPacket packet) {
		String function = "public void onHeaderTooShort(DatagramPacket packet)";
		logger.trace(">>> " + function);
		try {
			logger.info("Received UDP message which was too short, but there is nothing to do " +
				"because a diagnostic tester shall not send a negative acknowledge message.");
		} finally {
			logger.trace("<<< " + function);
		}
	}
	
}
