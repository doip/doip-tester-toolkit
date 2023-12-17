package doip.tester.toolkit;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import doip.library.message.DoipUdpEntityStatusRequest;
import doip.library.message.DoipUdpVehicleIdentRequest;
import doip.library.message.DoipUdpVehicleIdentRequestWithEid;
import doip.library.message.DoipUdpVehicleIdentRequestWithVin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class TesterUdpCommModule extends DoipUdpMessageHandlerWithEventCollection {
	
	private static Logger logger = LogManager.getLogger(TesterUdpCommModule.class);
	
	private TestConfig config = null;

	/** Log4j marker for function entry */
	private static Marker enter = MarkerManager.getMarker("ENTER");
	
	/** Log4j marker for function exit */
	private static Marker exit = MarkerManager.getMarker("EXIT");
	
	
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
	 *                be send. The target address can be a unicast,
	 *                multicast or broadcast address.
	 *                
	 * @throws IOException Throws an IOException if there was a problem 
	 *                     to send the data
	 */
	public void send(byte[] data, InetAddress address) throws IOException {
		logger.trace(enter, ">>> public void send(byte[] data, InetAddress address)");
		this.sendDatagramPacket(data, data.length,
				address, config.getTargetPort());
		logger.trace(exit, "<<< public void send(byte[] data, InetAddress address)");
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
		logger.trace(enter, ">>> public void sendDoipUdpVehicleIdentRequest(InetAddress address)");
		DoipUdpVehicleIdentRequest request = new DoipUdpVehicleIdentRequest();
		logger.info(TextBuilder.sendMessage(request.getMessageName()));
		this.send(request, address,
				config.getTargetPort());
		logger.trace(exit, "<<< public void sendDoipUdpVehicleIdentRequest(InetAddress address)");
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
		logger.trace(enter, ">>> public void sendDoipUdpVehicleIdentRequestWithEid(byte[] eid, InetAddress address) throws IOException");
		DoipUdpVehicleIdentRequestWithEid request = new DoipUdpVehicleIdentRequestWithEid(
				eid);
		this.send(request, address,
				config.getTargetPort());
		logger.trace(exit, "<<< public void sendDoipUdpVehicleIdentRequestWithEid(byte[] eid, InetAddress address) throws IOException");
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
		logger.trace(enter, ">>> public void sendDoipUdpVehicleIdentRequestWithVin(byte[] vin, InetAddress address)");
		try {
			DoipUdpVehicleIdentRequestWithVin request = new DoipUdpVehicleIdentRequestWithVin(
					vin);
			this.send(request, address,
					config.getTargetPort());
		} finally {
			logger.trace(exit, "<<< public void sendDoipUdpVehicleIdentRequestWithVin(byte[] vin, InetAddress address)");
		}
	} 
	
	public void sendDoipUdpEntityStatusRequest(InetAddress address) throws IOException {
		String method = "public void sendDoipEntityStatusRequest()";
		try {
			logger.trace(enter, ">>> {}", method);
			DoipUdpEntityStatusRequest request = new DoipUdpEntityStatusRequest();
			this.send(request, address, config.getTargetPort());
					
		} finally {
			logger.trace(exit, "<<< {}", method);
		}
	}
	
	@Override
	public void onHeaderTooShort(DatagramPacket packet) {
		logger.trace(enter, ">>> public void onHeaderTooShort(DatagramPacket packet)");
		logger.info("Received UDP message which was too short, but there is nothing to do " +
				"because a diagnostic tester shall not send a negative acknowledge message.");
		logger.trace(exit, "<<< public void onHeaderTooShort(DatagramPacket packet)");
	}
	
	@Override
	public void onInvalidPayloadLength(DatagramPacket packet) {
		logger.trace(enter, ">>> public void onInvalidPayloadLength(DatagramPacket packet)");
		logger.info("Received UPD message with invalid payload length, but there is nothing to do " +
				"because a diagnostic tester shall not send a negative acknowledge message.");
		logger.trace(exit, "<<< public void onInvalidPayloadLength(DatagramPacket packet)");
	}

	@Override
	public void onInvalidPayloadType(DatagramPacket packet) {
		logger.trace(enter, ">>> public void onInvalidPayloadType(DatagramPacket packet)");
		logger.info("Received UPD message with invalid payload type, but there is nothing to do " +
				"because a diagnostic tester shall not send a negative acknowledge message.");
		logger.trace(exit, "<<< public void onInvalidPayloadType(DatagramPacket packet)");
	}
	
	@Override
	public void onHeaderIncorrectPatternFormat(DatagramPacket packet) {
		logger.trace(enter, ">>> 	public void onHeaderIncorrectPatternFormat(DatagramPacket packet)");
		logger.info("Received UPD message with incorrect pattern format, but there is nothing to do " +
				"because a diagnostic tester shall not send a negative acknowledge message.");
		logger.trace(exit, "<<< 	public void onHeaderIncorrectPatternFormat(DatagramPacket packet)");
	}
	
}
