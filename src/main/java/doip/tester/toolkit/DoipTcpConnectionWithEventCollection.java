package doip.tester.toolkit;

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
import doip.tester.toolkit.event.DoipEvent;
import doip.tester.toolkit.event.DoipEventConnectionClosed;
import doip.tester.toolkit.event.DoipEventTcpAliveCheckRequest;
import doip.tester.toolkit.event.DoipEventTcpAliveCheckResponse;
import doip.tester.toolkit.event.DoipEventTcpDiagnosticMessage;
import doip.tester.toolkit.event.DoipEventTcpDiagnosticMessageNegAck;
import doip.tester.toolkit.event.DoipEventTcpDiagnosticMessagePosAck;
import doip.tester.toolkit.event.DoipEventTcpHeaderNegAck;
import doip.tester.toolkit.event.DoipEventTcpRoutingActivationRequest;
import doip.tester.toolkit.event.DoipEventTcpRoutingActivationResponse;
import doip.tester.toolkit.exception.DiagnosticServiceExecutionFailed;
import doip.tester.toolkit.exception.RoutingActivationFailed;

/**
 * Class for testing a DoIP TCP connection. 
 */
//public class DoipTcpConnectionWithEventCollection extends DoipTcpConnection {
public class DoipTcpConnectionWithEventCollection extends DoipTcpConnection implements DoipTcpConnectionListener {

	/**
	 * log4j logger
	 */
	private static Logger logger = LogManager.getLogger(DoipTcpConnectionWithEventCollection.class);
	
	/**
	 * Event queue for incoming events.
	 * Can be TCP messages or connection closed event.
	 */
	private volatile Vector<DoipEvent> events = new Vector<DoipEvent>();

	public DoipTcpConnectionWithEventCollection(String tcpReceiverThreadName, int maxByteArraySizeLogging) {
		super(tcpReceiverThreadName, maxByteArraySizeLogging);
		this.addListener(this);
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
	}

	
	@Override
	public void onDoipTcpDiagnosticMessage(DoipTcpConnection doipTcpConnection,
			DoipTcpDiagnosticMessage doipMessage) {
		DoipEventTcpDiagnosticMessage event = 
				new DoipEventTcpDiagnosticMessage(System.nanoTime(), doipMessage);
		this.events.add(event);
	}

	@Override
	public void onDoipTcpDiagnosticMessageNegAck(
			DoipTcpConnection doipTcpConnection,
			DoipTcpDiagnosticMessageNegAck doipMessage) {
		DoipEventTcpDiagnosticMessageNegAck event =
				new DoipEventTcpDiagnosticMessageNegAck(System.nanoTime(), doipMessage);
		this.events.add(event);
	}

	@Override
	public void onDoipTcpDiagnosticMessagePosAck(
			DoipTcpConnection doipTcpConnection,
			DoipTcpDiagnosticMessagePosAck doipMessage) {
		DoipEventTcpDiagnosticMessagePosAck event =
				new DoipEventTcpDiagnosticMessagePosAck(System.nanoTime(), doipMessage);
		this.events.add(event);
	}

	@Override
	public void onDoipTcpRoutingActivationRequest(
			DoipTcpConnection doipTcpConnection,
			DoipTcpRoutingActivationRequest doipMessage) {
		DoipEventTcpRoutingActivationRequest event =
				new DoipEventTcpRoutingActivationRequest(System.nanoTime(), doipMessage);
		this.events.add(event);
	}

	@Override
	public void onDoipTcpRoutingActivationResponse(
			DoipTcpConnection doipTcpConnection,
			DoipTcpRoutingActivationResponse doipMessage) {
		DoipEventTcpRoutingActivationResponse event = 
				new DoipEventTcpRoutingActivationResponse(System.nanoTime(), doipMessage);
		this.events.add(event);
	}

	@Override
	public void onDoipTcpAliveCheckRequest(DoipTcpConnection doipTcpConnection,
			DoipTcpAliveCheckRequest doipMessage) {
		DoipEventTcpAliveCheckRequest event =
				new DoipEventTcpAliveCheckRequest(System.nanoTime(), doipMessage);
		this.events.add(event);
	}

	@Override
	public void onDoipTcpAliveCheckResponse(DoipTcpConnection doipTcpConnection,
			DoipTcpAliveCheckResponse doipMessage) {
		DoipEventTcpAliveCheckResponse event =
				new DoipEventTcpAliveCheckResponse(System.nanoTime(), doipMessage);
		this.events.add(event);
	}

	@Override
	public void onDoipTcpHeaderNegAck(DoipTcpConnection doipTcpConnection,
			DoipTcpHeaderNegAck doipMessage) {
		DoipEventTcpHeaderNegAck event =
				new DoipEventTcpHeaderNegAck(System.nanoTime(), doipMessage);
		this.events.add(event);
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