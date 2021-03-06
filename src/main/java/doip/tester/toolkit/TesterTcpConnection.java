package doip.tester.toolkit;

import doip.library.message.DoipTcpDiagnosticMessage;
import doip.library.message.DoipTcpDiagnosticMessagePosAck;
import doip.library.message.DoipTcpRoutingActivationRequest;
import doip.library.message.DoipTcpRoutingActivationResponse;
import doip.library.util.Helper;
import doip.logging.LogManager;
import doip.logging.Logger;
import doip.tester.toolkit.event.DoipEvent;
import doip.tester.toolkit.event.DoipEventTcpDiagnosticMessage;
import doip.tester.toolkit.event.DoipEventTcpDiagnosticMessagePosAck;
import doip.tester.toolkit.event.DoipEventTcpRoutingActivationResponse;
import doip.tester.toolkit.exception.DiagnosticServiceExecutionFailed;
import doip.tester.toolkit.exception.RoutingActivationFailed;

public class TesterTcpConnection extends DoipTcpConnectionWithEventCollection {

	private static Logger logger = LogManager.getLogger(TesterTcpConnection.class);

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

		if (logger.isTraceEnabled()) {
			logger.trace(">>> public void sendRoutingActivationRequest(int sourceAddress, int activationType, long oemData)");
		}

		DoipTcpRoutingActivationRequest request = new DoipTcpRoutingActivationRequest(sourceAddress, activationType, oemData);
		logger.info("Send routing activation request");
		this.send(request);

		if (logger.isTraceEnabled()) {
			logger.trace("<<< public void sendRoutingActivationRequest(int sourceAddress, int activationType, long oemData)");
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
	public DoipEventTcpRoutingActivationResponse performRoutingActivation(int activationType) throws InterruptedException, RoutingActivationFailed {
		String function = "public boolean performRoutingActivation(int activationType, int expectedResponseCode)";
		try {
			logger.trace(">>> " + function);

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
				throw new RoutingActivationFailed(
						RoutingActivationFailed.NO_RESPONSE_RECEIVED,
						"No routing activation response received");
			}

			// Get the event out of the queue
			DoipEvent event = this.getEvent(0);
			if (!(event instanceof DoipEventTcpRoutingActivationResponse)) {
				logger.error("Received event is not type of DoipEventTcpRoutingActivationResponse");
				throw new RoutingActivationFailed(
						RoutingActivationFailed.WRONG_RESPONSE_RECEIVED,
						"No routing activation response received");
			}

			// Check the response code which shall match to the expected response code
			DoipEventTcpRoutingActivationResponse eventRoutingActivationResponse = (DoipEventTcpRoutingActivationResponse) event;
			return eventRoutingActivationResponse;
		} finally {
			logger.trace("<<< " + function);
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
		if (logger.isTraceEnabled()) {
			logger.trace(">>> " + function);
			logger.trace("<<< " + function);
		}
	}

	/**
	 * Executes a diagnostic service
	 * @param request
	 * @param responseExpected
	 * @return
	 * @throws DiagnosticServiceExecutionFailed
	 */

	public byte[] executeDiagnosticService(byte[] request, boolean responseExpected) throws DiagnosticServiceExecutionFailed {

		if (logger.isTraceEnabled()) {
			logger.trace(">>> public byte[] executeDiagnosticService(byte[] request, boolean responseExpected)");
		}

		try {
			this.clearEvents();
			this.sendDiagnosticMessage(config.getTesterAddress(), config.getEcuAddressPhysical(), request);
			boolean ret = this.waitForEvents(1, config.get_A_DoIP_Diagnostic_Message());
			if (!ret) {
				DiagnosticServiceExecutionFailed ex =
						new DiagnosticServiceExecutionFailed(
								DiagnosticServiceExecutionFailed.NO_DIAG_MESSAGE_POS_ACK_RECEIVED,
								"No DoIP message received after sending diagnostic request");
				logger.error(Helper.getExceptionAsString(ex));
				throw ex;
			}

			DoipEvent event = this.getEvent(0);
			if (!(event instanceof DoipEventTcpDiagnosticMessagePosAck)) {
				DiagnosticServiceExecutionFailed ex =
						new DiagnosticServiceExecutionFailed(
								DiagnosticServiceExecutionFailed.NO_DIAG_MESSAGE_POS_ACK_RECEIVED,
								"Received Event was not of type DoipEventTcpDiagnosticMessagePosAck");
				logger.error(Helper.getExceptionAsString(ex));
				throw ex;
			}

			DoipEventTcpDiagnosticMessagePosAck posAckEvent = (DoipEventTcpDiagnosticMessagePosAck) event;
			DoipTcpDiagnosticMessagePosAck posAckMsg = (DoipTcpDiagnosticMessagePosAck) posAckEvent.getDoipMessage();
			if (posAckMsg == null) {
				RuntimeException ex = new RuntimeException("DoipEventTcpDiagnosticMessagePosAck.getDoipMessage() returned null");
				logger.fatal(Helper.getExceptionAsString(ex));
				throw ex;
			}

			ret = this.waitForEvents(2, config.get_A_DoIP_Diagnostic_Message());
			if (!ret) {
				DiagnosticServiceExecutionFailed ex =
						new DiagnosticServiceExecutionFailed(
								DiagnosticServiceExecutionFailed.NO_DIAG_MESSAGE_RECEIVED,
								"No event received after receiving the event DoipEventTcpDiagnosticMessagePosAck");
				logger.error(Helper.getExceptionAsString(ex));
				throw ex;
			}

			event = this.getEvent(1);
			if (!(event instanceof DoipEventTcpDiagnosticMessage)) {
				DiagnosticServiceExecutionFailed ex =
						new DiagnosticServiceExecutionFailed(
								DiagnosticServiceExecutionFailed.NO_DIAG_MESSAGE_RECEIVED,
								"Received Event was not of type DoipEventTcpDiagnosticMessage");
				logger.error(Helper.getExceptionAsString(ex));
				throw ex;
			}

			DoipEventTcpDiagnosticMessage doipEventTcpDiagnosticMessage = (DoipEventTcpDiagnosticMessage) event;
			DoipTcpDiagnosticMessage doipTcpDiagnosticMessage = (DoipTcpDiagnosticMessage) doipEventTcpDiagnosticMessage.getDoipMessage();
			if (doipTcpDiagnosticMessage == null) {
				RuntimeException ex = new RuntimeException("DoipEventTcpDiagnosticMessage.getDoipMessage() returned null");
			}
			return doipTcpDiagnosticMessage.getDiagnosticMessage();

		} catch (InterruptedException e) {
			DiagnosticServiceExecutionFailed ex =
					new DiagnosticServiceExecutionFailed(
							DiagnosticServiceExecutionFailed.UNSPECIFIC_ERROR,
							"Unexpected InterruptedException", e);
			logger.fatal(Helper.getExceptionAsString(ex));
			throw ex;

		}
	}



	public void sendDiagnosticMessage(int sourceAddress, int targetAddress, byte[] message) {
		if (logger.isTraceEnabled()) {
			logger.trace(">>> public void sendDiagnosticMessage()");
		}

		DoipTcpDiagnosticMessage request = new DoipTcpDiagnosticMessage(sourceAddress, targetAddress, message);
		this.send(request);

		if (logger.isTraceEnabled()) {
			logger.trace("<<< public void sendDiagnosticMessage()");
		}
	}

}
