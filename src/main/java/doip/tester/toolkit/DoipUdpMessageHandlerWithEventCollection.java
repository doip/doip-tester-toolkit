package doip.tester.toolkit;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;
import java.util.Vector;

import doip.library.comm.DoipUdpMessageHandler;
import doip.library.comm.DoipUdpMessageHandlerListener;
import doip.library.message.DoipUdpDiagnosticPowerModeRequest;
import doip.library.message.DoipUdpDiagnosticPowerModeResponse;
import doip.library.message.DoipUdpEntityStatusRequest;
import doip.library.message.DoipUdpEntityStatusResponse;
import doip.library.message.DoipUdpHeaderNegAck;
import doip.library.message.DoipUdpMessage;
import doip.library.message.DoipUdpVehicleAnnouncementMessage;
import doip.library.message.DoipUdpVehicleIdentRequest;
import doip.library.message.DoipUdpVehicleIdentRequestWithEid;
import doip.library.message.DoipUdpVehicleIdentRequestWithVin;
import doip.library.timer.NanoTimer;
import doip.logging.LogManager;
import doip.logging.Logger;
import doip.tester.toolkit.event.DoipEvent;
import doip.tester.toolkit.event.DoipEventUdpDiagnosticPowerModeRequest;
import doip.tester.toolkit.event.DoipEventUdpDiagnosticPowerModeResponse;
import doip.tester.toolkit.event.DoipEventUdpEntityStatusRequest;
import doip.tester.toolkit.event.DoipEventUdpEntityStatusResponse;
import doip.tester.toolkit.event.DoipEventUdpHeaderNegAck;
import doip.tester.toolkit.event.DoipEventUdpVehicleAnnouncementMessage;
import doip.tester.toolkit.event.DoipEventUdpVehicleIdentRequest;
import doip.tester.toolkit.event.DoipEventUdpVehicleIdentRequestWithEid;
import doip.tester.toolkit.event.DoipEventUdpVehicleIdentRequestWithVin;

/**
 * Implements features to perform tests on a DoipUdpMessageHandler.
 */
public class DoipUdpMessageHandlerWithEventCollection extends DoipUdpMessageHandler
		implements DoipUdpMessageHandlerListener {

	/**
	 * log4j logger
	 */
	private static Logger logger = LogManager
			.getLogger(DoipUdpMessageHandlerWithEventCollection.class);

	/**
	 * List where the incoming events will be stored.
	 * 
	 */
	private volatile Vector<DoipEvent> events = new Vector<DoipEvent>();

	/**
	 * Configuration for the tests
	 */
	private TestConfig config = null;

//-----------------------------------------------------------------------------
// Constructors
//-----------------------------------------------------------------------------
	
	/**
	 * Constructor with parameter config.
	 * 
	 * @param config The test configuration
	 */
	public DoipUdpMessageHandlerWithEventCollection(TestConfig config) {
		super("UDP-RECV", null);
		this.config = config;
	}
	
//-----------------------------------------------------------------------------	
// Member Functions
//-----------------------------------------------------------------------------	
	
	/**
	 * Starts the UDP message handler which means to start the receiver 
	 * thread. Additional it adds himself to the UDP message handler listeners
	 * to receive the UDP messages.
	 * 
	 * @param socket The UDP socket for sending and receive UDP messages
	 */
	public void start(DatagramSocket socket) {
		logger.trace(">>> public void start(DatagramSocket socket)");
		this.addListener(this);
		super.start(socket);
		logger.trace("<<< public void start(DatagramSocket socket)");
	}


	/**
	 * Stops the UDP message handler which means also to close the socket.
	 * It also removes itself from the UDP message handler listeners.
	 */
	public void stop() {
		logger.trace(">>> public void stop()");
		super.stop();
		this.removeListener(this);
		logger.trace("<<< public void stop()");
	}


	public void clearEvents() {
		this.events.clear();
	}

	public DoipEvent getEvent(int index) {
		return this.events.get(index);
	}

	/**
	 * Waits for incoming events
	 * @param numberOfEvents The number of events which shall be in the event 
	 *                       queue
	 *                       
	 * @param timeout        Maximum time to wait until the number of events
	 *                       in the event queue has been reached.
	 *                       
	 * @return Returns true if the number of events has been reached with in the
	 *         timeout time.
	 * @throws InterruptedException 
	 */
	public boolean waitForEvents(int numberOfEvents, long timeout) throws InterruptedException {
		return  Wait.waitForEvents(events, numberOfEvents, timeout);
	}

//-----------------------------------------------------------------------------	
// Callback functions
//-----------------------------------------------------------------------------

	@Override
	public void onDoipUdpVehicleIdentRequest(
			DoipUdpVehicleIdentRequest doipMessage, DatagramPacket packet) {

		DoipEventUdpVehicleIdentRequest event = new DoipEventUdpVehicleIdentRequest(
				System.nanoTime(), doipMessage);
		this.events.add(event);
	}

	@Override
	public void onDoipUdpVehicleIdentRequestWithEid(
			DoipUdpVehicleIdentRequestWithEid doipMessage,
			DatagramPacket packet) {
		DoipEventUdpVehicleIdentRequestWithEid event = new DoipEventUdpVehicleIdentRequestWithEid(
				System.nanoTime(), doipMessage);
		this.events.add(event);
	}

	@Override
	public void onDoipUdpVehicleIdentRequestWithVin(
			DoipUdpVehicleIdentRequestWithVin doipMessage,
			DatagramPacket packet) {
		DoipEventUdpVehicleIdentRequestWithVin event = new DoipEventUdpVehicleIdentRequestWithVin(
				System.nanoTime(), doipMessage);
		this.events.add(event);
	}

	@Override
	public void onDoipUdpVehicleAnnouncementMessage(
			DoipUdpVehicleAnnouncementMessage doipMessage,
			DatagramPacket packet) {
		DoipEventUdpVehicleAnnouncementMessage event = new DoipEventUdpVehicleAnnouncementMessage(
				System.nanoTime(), doipMessage);
		this.events.add(event);
	}

	@Override
	public void onDoipUdpDiagnosticPowerModeRequest(
			DoipUdpDiagnosticPowerModeRequest doipMessage,
			DatagramPacket packet) {
		DoipEventUdpDiagnosticPowerModeRequest event = new DoipEventUdpDiagnosticPowerModeRequest(
				System.nanoTime(), doipMessage);
		this.events.add(event);
	}

	@Override
	public void onDoipUdpDiagnosticPowerModeResponse(
			DoipUdpDiagnosticPowerModeResponse doipMessage,
			DatagramPacket packet) {
		DoipEventUdpDiagnosticPowerModeResponse event = new DoipEventUdpDiagnosticPowerModeResponse(
				System.nanoTime(), doipMessage);
		this.events.add(event);
	}

	@Override
	public void onDoipUdpEntityStatusRequest(
			DoipUdpEntityStatusRequest doipMessage, DatagramPacket packet) {
		DoipEventUdpEntityStatusRequest event = new DoipEventUdpEntityStatusRequest(
				System.nanoTime(), doipMessage);
		this.events.add(event);
	}

	@Override
	public void onDoipUdpEntityStatusResponse(
			DoipUdpEntityStatusResponse doipMessage, DatagramPacket packet) {
		DoipEventUdpEntityStatusResponse event = new DoipEventUdpEntityStatusResponse(
				System.nanoTime(), doipMessage);
		this.events.add(event);
	}

	@Override
	public void onDoipUdpHeaderNegAck(DoipUdpHeaderNegAck doipMessage,
			DatagramPacket packet) {
		DoipEventUdpHeaderNegAck event = new DoipEventUdpHeaderNegAck(
				System.nanoTime(), doipMessage);
		this.events.add(event);
	}

//-----------------------------------------------------------------------------
// Getter & Setter
//-----------------------------------------------------------------------------
	
}