package doip.tester.utils;

import doip.library.message.DoipTcpDiagnosticMessage;
import doip.library.message.DoipTcpDiagnosticMessagePosAck;
import doip.library.message.DoipTcpRoutingActivationRequest;
import doip.library.message.DoipTcpRoutingActivationResponse;
import doip.library.util.Helper;
import doip.logging.LogManager;
import doip.logging.Logger;
import doip.tester.event.DoipEvent;
import doip.tester.event.DoipEventTcpDiagnosticMessagePosAck;
import doip.tester.event.DoipEventTcpRoutingActivationResponse;
import doip.tester.exception.DiagnosticServiceExecutionFailed;

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
			
			DoipEvent event = this.getEvent(0);
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
