package doip.tester.toolkit;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import doip.library.message.DoipMessage;
import doip.library.message.DoipTcpDiagnosticMessage;
import doip.library.message.DoipTcpDiagnosticMessagePosAck;
import doip.library.message.DoipTcpRoutingActivationRequest;
import doip.library.message.DoipTcpRoutingActivationResponse;
import doip.library.util.Helper;
import doip.tester.toolkit.event.DoipEvent;
import doip.tester.toolkit.event.DoipEventTcpDiagnosticMessage;
import doip.tester.toolkit.event.DoipEventTcpDiagnosticMessagePosAck;
import doip.tester.toolkit.event.DoipEventTcpRoutingActivationResponse;
import doip.tester.toolkit.exception.DiagnosticServiceExecutionFailed;
import doip.tester.toolkit.exception.RoutingActivationFailed;

public class TesterTcpConnection extends DoipTcpConnectionWithEventCollection {

	private static Logger logger = LogManager.getLogger(TesterTcpConnection.class);
	private static Marker enter = MarkerManager.getMarker("ENTER");
	private static Marker exit = MarkerManager.getMarker("EXIT");

	private static int connectionCounter = 1;

	private TestConfig config = null;

	public TesterTcpConnection(TestConfig config) {
		super("TCP-TESTER-" + connectionCounter, 64);
		connectionCounter++;
		this.config = config;
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

		try {
			logger.trace(enter, ">>> public void sendRoutingActivationRequest(int sourceAddress, int activationType, long oemData)");
	
			DoipTcpRoutingActivationRequest request = new DoipTcpRoutingActivationRequest(sourceAddress, activationType, oemData);
			logger.info("Send routing activation request");
			this.send(request);
		} finally {
			logger.trace(exit, "<<< public void sendRoutingActivationRequest(int sourceAddress, int activationType, long oemData)");
		}
	}

 	/**
	 * Performs a routing activation. It will send a routing activation request
	 * and waits for a routing activation response. If no response has been received
	 * then the function will throw a RoutingActivationFailed exception with
	 * error code NO_RESPONSE_RECEIVED. If a response other than a routing activation
	 * response has been received then a RoutingActivationFailed exception with error
	 * code WRONG_REPONSE_RECEIVED will be thrown.
	 * @param activationType The activation type (see ISO 13400)
	 * @param expectedResponseCode The expected response code in the response.
	 * @return Returns the DoipEventTcpRoutingActivationResponse
	 * @throws InterruptedException
	 * @throws RoutingActivationFailed
	 */
	public DoipEventTcpRoutingActivationResponse performRoutingActivation(int address, int activationType) throws InterruptedException, RoutingActivationFailed {
		String function = "public DoipEventTcpRoutingActivationResponse performRoutingActivation(int activationType, int expectedResponseCode)";
		try {
			logger.trace(enter, ">>> " + function);

			// Clear the event queue
			this.clearEvents();

			this.sendRoutingActivationRequest(address, activationType, -1);

			// Wait for incoming TCP message
			DoipEvent event = null;
			event = this.waitForEvents(1, config.getRoutingActivationTimeout());
			CheckResult result = EventChecker.checkEvent(event, DoipEventTcpRoutingActivationResponse.class);
			if (result.getCode() != CheckResult.NO_ERROR) {
				logger.error(result.getText());
			}
			if (event == null) {
				logger.error("No valid DoIP routing activation response received");
				throw new RoutingActivationFailed(
						RoutingActivationFailed.NO_RESPONSE_RECEIVED,
						"No valid DoIP routing activation response received");
			}

			if (!(event instanceof DoipEventTcpRoutingActivationResponse)) {
				logger.error("Received event is not type of DoipEventTcpRoutingActivationResponse");
				throw new RoutingActivationFailed(
						RoutingActivationFailed.WRONG_RESPONSE_RECEIVED,
						"No valid DoIP routing activation response received");
			}

			// TODO: Check the response code which shall match to the expected response code
			DoipEventTcpRoutingActivationResponse eventRoutingActivationResponse = 
					(DoipEventTcpRoutingActivationResponse) event;
			
			
			return eventRoutingActivationResponse;
		} finally {
			logger.trace(exit, "<<< " + function);
		}
	}

	/**
	 * Override function and do nothing in the function because implementation
	 * in base class send negative acknowledge message. But tester should
	 * not do that.
	 */
	@Override
	public void onHeaderIncorrectPatternFormat() {
		String function = "public void onHeaderIncorrectPatternFormat()";
			logger.trace(">>> " + function);
			logger.trace("<<< " + function);
	}
	
	/**
	 * Executes a diagnostic service and check for correct result.
	 * @param request
	 * @param responseExpected
	 * @return
	 * @throws DiagnosticServiceExecutionFailed
	 */
	public DoipEventTcpDiagnosticMessage executeDiagnosticServicePosAck(byte[] request) throws DiagnosticServiceExecutionFailed {

		try {
			logger.trace(enter, ">>> public byte[] executeDiagnosticService(byte[] request)");
			this.clearEvents();
			
			this.sendDiagnosticMessage(config.getTesterAddress(), config.getEcuAddressPhysical(), request);
			
			// It is expected to receive a positive acknowledge on the diagnostic request message
			DoipEvent event = this.waitForEvents(1, config.get_A_DoIP_Diagnostic_Message());
			CheckResult result = EventChecker.checkEvent(event, DoipEventTcpDiagnosticMessagePosAck.class);
			if (result.getCode() != CheckResult.NO_ERROR ) {
				logger.error(result.getText());
			}
			if (event == null) {
				DiagnosticServiceExecutionFailed ex =
						new DiagnosticServiceExecutionFailed(
								DiagnosticServiceExecutionFailed.NO_DIAG_MESSAGE_POS_ACK_RECEIVED,
								"No message of type '" + DoipTcpDiagnosticMessagePosAck.getMessageNameOfClass() + "' has been received.");
				throw logger.throwing(Level.INFO, ex);
			}

			if (!(event instanceof DoipEventTcpDiagnosticMessagePosAck)) {
				DiagnosticServiceExecutionFailed ex =
						new DiagnosticServiceExecutionFailed(
								DiagnosticServiceExecutionFailed.NO_DIAG_MESSAGE_POS_ACK_RECEIVED,
								"No message of type '" + DoipTcpDiagnosticMessagePosAck.getMessageNameOfClass() + "' has been received.");
				throw logger.throwing(Level.INFO, ex);
			}

			DoipEventTcpDiagnosticMessagePosAck posAckEvent = (DoipEventTcpDiagnosticMessagePosAck) event;
			DoipTcpDiagnosticMessagePosAck posAckMsg = (DoipTcpDiagnosticMessagePosAck) posAckEvent.getDoipMessage();

			event = this.waitForEvents(2, config.get_A_DoIP_Diagnostic_Message());
			EventChecker.checkEvent(event, DoipEventTcpDiagnosticMessage.class);
			if (result.getCode() != CheckResult.NO_ERROR ) {
				logger.error(result.getText());
			}
			if (event == null) {
				DiagnosticServiceExecutionFailed ex =
						new DiagnosticServiceExecutionFailed(
								DiagnosticServiceExecutionFailed.NO_DIAG_MESSAGE_RECEIVED,
								"No event received after receiving the event DoipEventTcpDiagnosticMessagePosAck");
				throw logger.throwing(Level.INFO, ex);
			}

			if (!(event instanceof DoipEventTcpDiagnosticMessage)) {
				DiagnosticServiceExecutionFailed ex =
						new DiagnosticServiceExecutionFailed(
								DiagnosticServiceExecutionFailed.NO_DIAG_MESSAGE_RECEIVED,
								"Received Event was not of type DoipEventTcpDiagnosticMessage");
				throw logger.throwing(Level.INFO, ex);
			}

			DoipEventTcpDiagnosticMessage doipEventTcpDiagnosticMessage = (DoipEventTcpDiagnosticMessage) event;
			return doipEventTcpDiagnosticMessage;

		} catch (InterruptedException e) {
			DiagnosticServiceExecutionFailed ex =
					new DiagnosticServiceExecutionFailed(
							DiagnosticServiceExecutionFailed.GENERAL_ERROR,
							TextBuilder.unexpectedException(e));
			throw logger.throwing(Level.FATAL, ex);
		} finally {
			logger.trace(exit, "<<< public byte[] executeDiagnosticService(byte[] request, boolean responseExpected)");
		}
	}

	public void sendDiagnosticMessage(int sourceAddress, int targetAddress, byte[] message) {
		try {
			logger.trace(enter, ">>> public void sendDiagnosticMessage(int sourceAddress, int targetAddress, byte[] message)");
			DoipTcpDiagnosticMessage request = new DoipTcpDiagnosticMessage(sourceAddress, targetAddress, message);
			this.send(request);
		} finally {
			logger.trace(exit, "<<< public void sendDiagnosticMessage(int sourceAddress, int targetAddress, byte[] message)");
		}
	}
}
