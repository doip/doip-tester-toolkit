package doip.tester.utils;

import java.net.Socket;
import java.util.LinkedList;
import java.util.Vector;

import doip.library.comm.DoipTcpConnection;
import doip.library.comm.DoipTcpConnectionListener;
import doip.library.exception.DoipException;
import doip.library.message.DoipTcpAliveCheckRequest;
import doip.library.message.DoipTcpAliveCheckResponse;
import doip.library.message.DoipTcpDiagnosticMessage;
import doip.library.message.DoipTcpDiagnosticMessageNegAck;
import doip.library.message.DoipTcpDiagnosticMessagePosAck;
import doip.library.message.DoipTcpHeaderNegAck;
import doip.library.message.DoipTcpRoutingActivationRequest;
import doip.library.message.DoipTcpRoutingActivationResponse;
import doip.library.util.Helper;
import doip.logging.LogManager;
import doip.logging.Logger;
import doip.tester.event.DoipEvent;
import doip.tester.event.DoipEventConnectionClosed;
import doip.tester.event.DoipEventTcpAliveCheckRequest;
import doip.tester.event.DoipEventTcpAliveCheckResponse;
import doip.tester.event.DoipEventTcpDiagnosticMessage;
import doip.tester.event.DoipEventTcpDiagnosticMessageNegAck;
import doip.tester.event.DoipEventTcpDiagnosticMessagePosAck;
import doip.tester.event.DoipEventTcpHeaderNegAck;
import doip.tester.event.DoipEventTcpRoutingActivationRequest;
import doip.tester.event.DoipEventTcpRoutingActivationResponse;
import doip.tester.exception.DiagnosticServiceExecutionFailed;
import doip.tester.exception.RoutingActivationFailed;

/**
 * Class for testing a DoIP TCP connection. 
 */
public class DoipTcpConnectionWithEventCollection implements DoipTcpConnectionListener {

	/**
	 * log4j logger
	 */
	private static Logger logger = LogManager.getLogger(DoipTcpConnectionWithEventCollection.class);
	
	/**
	 * The DoIP TCP connection which is under test
	 */
	private DoipTcpConnection doipTcpConnection = null;
	
	/**
	 * contains the configuration for the tests. It
	 * needs to be passed in the constructor
	 */
	private TestConfig config = null;
	
	/**
	 * Instance counter to give each DoIP TCP connection a new name 
	 * for logging
	 */
	private static int instanceCounter = 1;
	
	/**
	 * Event queue for incoming events.
	 * Can be TCP messages or connection closed event.
	 */
	private volatile Vector<DoipEvent> events = new Vector<DoipEvent>();
	
	private volatile LinkedList<DoipTcpConnectionWithEventCollectionListener> listeners = 
			new LinkedList<DoipTcpConnectionWithEventCollectionListener>();
	
	/**
	 * Constructor
	 * @param config The test configuration for this TCP connection.-
	 */
	public DoipTcpConnectionWithEventCollection(TestConfig config) {
		this.config = config;
		doipTcpConnection = new DoipTcpConnection("TCP-CONN-TESTER-" + instanceCounter, config.getMaxByteArraySizeLogging());
	}
	
	/**
	 * Starts the thread of the DoIP TCP connection.
	 * @param socket The TCP socket.
	 */
	public void start(Socket socket) {
		if (logger.isTraceEnabled()) {
			logger.trace(">>> public void start(Socket socket)");
		}
		
		doipTcpConnection.addListener(this);
		doipTcpConnection.start(socket);
		
		if (logger.isTraceEnabled()) { 
			logger.trace("<<< public void start(Socket socket)");
		}
	}
	
	/**
	 * Stops the DoIP TCP connection thread.
	 */
	public void stop() {
		if (logger.isTraceEnabled()) {
			logger.trace(">>> public void stop()");
		}
		
		doipTcpConnection.stop();
		doipTcpConnection.removeListener(this);
		
		if (logger.isTraceEnabled()) {
			logger.trace("<<< public void stop()");
		}
	}
	
	public void addListener(DoipTcpConnectionWithEventCollectionListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(DoipTcpConnectionWithEventCollectionListener listener) {
		this.listeners.remove(listener);
	}
	
	public void onEvent(DoipEvent event) {
		for (DoipTcpConnectionWithEventCollectionListener listener : listeners) {
			listener.onEvent(this, event);
		}
	}
	
	
	/**
	 * Performs a routing activation. 
	 * @param activationType The activation type (see ISO 13400)
	 * @param expectedResponseCode The expected response code in the response.
	 *                             If the response code is different an AssertionError
	 *                             will be thrown.
	 * @return Returns true if routing activation was successful
	 * @throws InterruptedException 
	 * @throws RoutingActivationFailed 
	 */
	public boolean performRoutingActivation(int activationType, int expectedResponseCode) throws InterruptedException {
		
		// Clear the event queue 
		this.clearEvents();
		
		this.sendRoutingActivationRequest(config.getTesterAddress(), activationType, -1);
		
		// Wait for incoming TCP message
		boolean ret;
		try {
			ret = this.waitForEvents(1, config.getRoutingActivationTimeout());
		} catch (InterruptedException e) {
			logger.error(Helper.getExceptionAsString(e));
			throw e;
		}
		if (ret == false) {
			logger.error("No Routing Activation Response received");
			return false;
		}
		
		// Get the event out of the queue
		DoipEvent event = this.getEvent(0);
		if (!(event instanceof DoipEventTcpRoutingActivationResponse)) {
			logger.error("Received event is not type of DoipEventTcpRoutingActivationResponse");
			return false;
		}
		
		// Check the response code which shall match to the expected response code
		DoipEventTcpRoutingActivationResponse eventRoutingActivationResponse = (DoipEventTcpRoutingActivationResponse) event;
		DoipTcpRoutingActivationResponse  routingActivationResponse = (DoipTcpRoutingActivationResponse) eventRoutingActivationResponse.getDoipMessage();
		int responseCode = routingActivationResponse.getResponseCode();
		if (responseCode != expectedResponseCode) {
			logger.error("Response code does not match the expected response code");
			return false;
		}
		
		return true;
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
	
	
	/**
	 * Executes a diagnostic service
	 * @param request
	 * @param responseExpected
	 * @return
	 * @throws DiagnosticServiceExecutionFailed 
	 */
	public byte[] executeDiagnosticService(byte[] request, boolean responseExpected) throws DiagnosticServiceExecutionFailed {
	
		try {
			this.clearEvents();
			this.sendDiagnosticMessage(config.getTesterAddress(), config.getEcuAddressPhysical(), request);
			boolean ret = this.waitForEvents(1, config.get_A_DoIP_Diagnostic_Message());
			if (!ret) {
				DiagnosticServiceExecutionFailed ex = new DiagnosticServiceExecutionFailed("No event received after sending diagnostic request");
				logger.error(Helper.getExceptionAsString(ex));
				throw ex;
			}
			
			DoipEvent event = this.events.get(0);
			if (!(event instanceof DoipEventTcpDiagnosticMessagePosAck)) {
				DiagnosticServiceExecutionFailed ex = new DiagnosticServiceExecutionFailed("Received Event was not of type DoipEventTcpDiagnosticMessagePosAck");
				logger.error(Helper.getExceptionAsString(ex));
				throw ex;
			}
			
			DoipEventTcpDiagnosticMessagePosAck posAckEvent = (DoipEventTcpDiagnosticMessagePosAck) event;
			DoipTcpDiagnosticMessagePosAck posAckMsg = (DoipTcpDiagnosticMessagePosAck) posAckEvent.getDoipMessage();
			// TODO: assertNotNull(posAckMsg);
		
			// TODO: finish implementation
		} catch (InterruptedException e) {
			DiagnosticServiceExecutionFailed ex = new DiagnosticServiceExecutionFailed("Unexpected InterruptedException", e);
			logger.error(Helper.getExceptionAsString(ex));
			throw ex;
			
		}
		return null;
	}
	
	/** 
	 * Sends a routing activation request
	 * 
	 * @param sourceAddress Source address of tester which is asking for 
	 *                      routing activation
	 * @param activationType
	 * @param oemData OEM specific data
	 */
	public void sendRoutingActivationRequest(
			int sourceAddress, int activationType, long oemData) {
		
		if (logger.isTraceEnabled()) {
			logger.trace(">>> public void sendRoutingActivationRequest(int sourceAddress, int activationType, long oemData)");
		}
		
		DoipTcpRoutingActivationRequest request = new DoipTcpRoutingActivationRequest(sourceAddress, activationType, oemData);
		logger.info("Send routing activation request");
		doipTcpConnection.send(request);
		
		if (logger.isTraceEnabled()) {
			logger.trace("<<< public void sendRoutingActivationRequest(int sourceAddress, int activationType, long oemData)");
		}
	}
	
	public void sendDiagnosticMessage(int sourceAddress, int targetAddress, byte[] message) {
		if (logger.isTraceEnabled()) {
			logger.trace(">>> public void sendDiagnosticMessage()");
		}
		
		DoipTcpDiagnosticMessage request = new DoipTcpDiagnosticMessage(sourceAddress, targetAddress, message);
		doipTcpConnection.send(request);
		
		if (logger.isTraceEnabled()) {
			logger.trace("<<< public void sendDiagnosticMessage()");
		}
	}
	
	/*
	public void sendDiagnosticMessagePosAck(int sourceAddress, int targetAddress, byte[] message) {
		if (logger.isTraceEnabled()) {
			logger.trace(">>> public void sendDiagnosticMessagePosAck(int sourceAddress, int targetAddress, byte[] message)");
		}
		
		DoipTcpDiagnosticMessagePosAck msg = new DoipTcpDiagnosticMessagePosAck(sourceAddress, targetAddress, 0x00, message);
		doipTcpConnection.send(msg);
	
		if (logger.isTraceEnabled()) {
			logger.trace("<<< public void sendDiagnosticMessagePosAck(int sourceAddress, int targetAddress, byte[] message)");
		}
	}*/
	
//-----------------------------------------------------------------------------
// Callback functions
//-----------------------------------------------------------------------------

	@Override
	public void onConnectionClosed(DoipTcpConnection doipTcpConnection) {
		DoipEventConnectionClosed event = 
				new DoipEventConnectionClosed(System.nanoTime());
		this.events.add(event);
		this.onEvent(event);
	}

	@Override
	public void onDoipTcpDiagnosticMessage(DoipTcpConnection doipTcpConnection,
			DoipTcpDiagnosticMessage doipMessage) {
		DoipEventTcpDiagnosticMessage event = 
				new DoipEventTcpDiagnosticMessage(System.nanoTime(), doipMessage);
		this.events.add(event);
		this.onEvent(event);
	}

	@Override
	public void onDoipTcpDiagnosticMessageNegAck(
			DoipTcpConnection doipTcpConnection,
			DoipTcpDiagnosticMessageNegAck doipMessage) {
		DoipEventTcpDiagnosticMessageNegAck event =
				new DoipEventTcpDiagnosticMessageNegAck(System.nanoTime(), doipMessage);
		this.events.add(event);
		this.onEvent(event);
	}

	@Override
	public void onDoipTcpDiagnosticMessagePosAck(
			DoipTcpConnection doipTcpConnection,
			DoipTcpDiagnosticMessagePosAck doipMessage) {
		DoipEventTcpDiagnosticMessagePosAck event =
				new DoipEventTcpDiagnosticMessagePosAck(System.nanoTime(), doipMessage);
		this.events.add(event);
		this.onEvent(event);
	}

	@Override
	public void onDoipTcpRoutingActivationRequest(
			DoipTcpConnection doipTcpConnection,
			DoipTcpRoutingActivationRequest doipMessage) {
		DoipEventTcpRoutingActivationRequest event =
				new DoipEventTcpRoutingActivationRequest(System.nanoTime(), doipMessage);
		this.events.add(event);
		this.onEvent(event);
	}

	@Override
	public void onDoipTcpRoutingActivationResponse(
			DoipTcpConnection doipTcpConnection,
			DoipTcpRoutingActivationResponse doipMessage) {
		DoipEventTcpRoutingActivationResponse event = 
				new DoipEventTcpRoutingActivationResponse(System.nanoTime(), doipMessage);
		this.events.add(event);
		this.onEvent(event);
	}

	@Override
	public void onDoipTcpAliveCheckRequest(DoipTcpConnection doipTcpConnection,
			DoipTcpAliveCheckRequest doipMessage) {
		DoipEventTcpAliveCheckRequest event =
				new DoipEventTcpAliveCheckRequest(System.nanoTime(), doipMessage);
		this.events.add(event);
		this.onEvent(event);
	}

	@Override
	public void onDoipTcpAliveCheckResponse(DoipTcpConnection doipTcpConnection,
			DoipTcpAliveCheckResponse doipMessage) {
		DoipEventTcpAliveCheckResponse event =
				new DoipEventTcpAliveCheckResponse(System.nanoTime(), doipMessage);
		this.events.add(event);
		this.onEvent(event);
	}

	@Override
	public void onDoipTcpHeaderNegAck(DoipTcpConnection doipTcpConnection,
			DoipTcpHeaderNegAck doipMessage) {
		DoipEventTcpHeaderNegAck event =
				new DoipEventTcpHeaderNegAck(System.nanoTime(), doipMessage);
		this.events.add(event);
		this.onEvent(event);
	}
	
//-----------------------------------------------------------------------------
// Getter & Setter
//-----------------------------------------------------------------------------

	public DoipEvent getEvent(int index) {
		return this.events.get(index);
	}

	public void clearEvents() {
		logger.info("Clear event queue");
		this.events.clear();
	}
}