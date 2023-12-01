package doip.tester.toolkit;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import doip.library.message.DoipTcpAliveCheckRequest;
import doip.library.message.DoipTcpAliveCheckResponse;
import doip.library.message.DoipTcpDiagnosticMessage;
import doip.library.message.DoipTcpDiagnosticMessageNegAck;
import doip.library.message.DoipTcpDiagnosticMessagePosAck;
import doip.library.message.DoipTcpHeaderNegAck;
import doip.library.message.DoipTcpRoutingActivationRequest;
import doip.library.message.DoipTcpRoutingActivationResponse;
import doip.library.message.DoipUdpDiagnosticPowerModeRequest;
import doip.library.message.DoipUdpDiagnosticPowerModeResponse;
import doip.library.message.DoipUdpEntityStatusRequest;
import doip.library.message.DoipUdpEntityStatusResponse;
import doip.library.message.DoipUdpHeaderNegAck;
import doip.library.message.DoipUdpVehicleAnnouncementMessage;
import doip.library.message.DoipUdpVehicleIdentRequest;
import doip.library.message.DoipUdpVehicleIdentRequestWithEid;
import doip.library.message.DoipUdpVehicleIdentRequestWithVin;
import doip.tester.toolkit.event.DoipEvent;
import doip.tester.toolkit.event.DoipEventConnectionClosed;
import doip.tester.toolkit.event.DoipEventMessage;
import doip.tester.toolkit.event.DoipEventTcpAliveCheckRequest;
import doip.tester.toolkit.event.DoipEventTcpAliveCheckResponse;
import doip.tester.toolkit.event.DoipEventTcpDiagnosticMessage;
import doip.tester.toolkit.event.DoipEventTcpDiagnosticMessageNegAck;
import doip.tester.toolkit.event.DoipEventTcpDiagnosticMessagePosAck;
import doip.tester.toolkit.event.DoipEventTcpHeaderNegAck;
import doip.tester.toolkit.event.DoipEventTcpMessage;
import doip.tester.toolkit.event.DoipEventTcpRoutingActivationRequest;
import doip.tester.toolkit.event.DoipEventTcpRoutingActivationResponse;
import doip.tester.toolkit.event.DoipEventUdpDiagnosticPowerModeRequest;
import doip.tester.toolkit.event.DoipEventUdpDiagnosticPowerModeResponse;
import doip.tester.toolkit.event.DoipEventUdpEntityStatusRequest;
import doip.tester.toolkit.event.DoipEventUdpEntityStatusResponse;
import doip.tester.toolkit.event.DoipEventUdpHeaderNegAck;
import doip.tester.toolkit.event.DoipEventUdpMessage;
import doip.tester.toolkit.event.DoipEventUdpVehicleAnnouncementMessage;
import doip.tester.toolkit.event.DoipEventUdpVehicleIdentRequest;
import doip.tester.toolkit.event.DoipEventUdpVehicleIdentRequestWithEid;
import doip.tester.toolkit.event.DoipEventUdpVehicleIdentRequestWithVin;

public class EventChecker {

	private static Logger logger = LogManager.getLogger(EventChecker.class);
	private static Marker enter = MarkerManager.getMarker("ENTER");
	private static Marker exit  = MarkerManager.getMarker("EXIT");

	/**
	 * Checks if the actual event matches the expected event.
	 * @param actualEvent Actual event which also call be null.
	 * @param expectedClass Expected event whcih also can be null.
	 * @return The result which consists of an error code and a error text.
	 */
	public static CheckResult checkEvent(DoipEvent actualEvent, Class<? extends DoipEvent> expectedClass) {
		try {
			logger.trace(enter, ">>> public void checkEvent(DoipEvent event, Class<? extends DoipEvent> clazz)");
		
			if (actualEvent != null) {
				return checkEventIsNotNull(actualEvent, expectedClass);
			} else {
				return checkEventIsNull(expectedClass);
			}
		} finally {
			logger.trace(exit, "<<< public void checkEvent(DoipEvent event, Class<? extends DoipEvent> clazz)");
		}
	}
	
	/**
	 * It's just a dispatcher function which will be called in case the actual event is null.
	 * @param actualEvent
	 * @param clazz
	 * @return
	 */
	private static CheckResult checkEventIsNotNull(DoipEvent actualEvent, Class<? extends DoipEvent> clazz) {
		try {
			logger.trace(enter, ">>> private static int checkEventIsNotNull(DoipEvent event, Class<? extends DoipEvent> clazz)");
			if (clazz != null) {
				return checkEventIsNotNullAndClassIsNotNull(actualEvent, clazz);
			} else {
				return checkEventIsNotNullAndClassIsNull(actualEvent);
			}
		} finally {
			logger.trace(exit, "<<< private static int checkEventIsNotNull(DoipEvent event, Class<? extends DoipEvent> clazz)");
		}
	}
	
	/**
	 * 
	 * @param actualEvent
	 * @param expectedClass
	 * @return
	 */
	private static CheckResult checkEventIsNotNullAndClassIsNotNull(DoipEvent actualEvent, Class<? extends DoipEvent> expectedClass) {
		if (expectedClass.isInstance(actualEvent)) {
			String text = "A event of type '" + actualEvent.getClass().getSimpleName() + "' has been receive which was the expected event"; 
			return new CheckResult(CheckResult.NO_ERROR, text);
		} else {
			// TODO: Distinguish between different event types and expected class
			//String text = "It was expected to receive a event of type '" + expectedClass.getSimpleName()+ "', but a event of type '" + actualEvent.getClass().getSimpleName() + "' has been received";

			// TODO:
			//String text1 = "";
			//if (DoipEventTcpDiagnosticMessagePosAck.class.isAssignableFrom(expectedClass)) {
			//	text1 = "It was expected to receive a '" + DoipTcpDiagnosticMessagePosAck.getMessageNameOfClass() + "' message, ";
			//}
			String text = getTextForExpectedResult(expectedClass) + getTextForActualResult(actualEvent);
			return new CheckResult(CheckResult.WRONG_EVENT, text);
		}		
	}
	
	private static CheckResult checkEventIsNotNullAndClassIsNull(DoipEvent actualEvent) {
		if (actualEvent instanceof DoipEventMessage) {
			DoipEventMessage eventMessage = (DoipEventMessage) actualEvent;
			String text = "It was expected to receive no response, but instead a '" + eventMessage.getDoipMessage().getMessageName() + "' has been received"; 
			return new CheckResult(CheckResult.UNEXPECTED_DOIP_MESSAGE, text);
		} else if (actualEvent instanceof DoipEventConnectionClosed) {
			String text = "It was expected to receive no response, but instead the socket has been closed";
			return new CheckResult(CheckResult.UNEXPECTED_SOCKET_CLOSED, text);
		} else {
			String text = "A unknown event did occur, class = " + actualEvent.getClass().getName();
			throw logger.throwing(Level.FATAL, new IllegalArgumentException(text));
		}		
	}
	
	private static CheckResult checkEventIsNull(Class<? extends DoipEvent> expectedClass) {
		if (expectedClass != null) {
			// 	Check if it a DoipEventMessage was expected
			if (DoipEventUdpMessage.class.isAssignableFrom(expectedClass)) {
//				String text = "It was expected to receive a valid DoIP UDP message, but no valid DoIP UDP message has been received"; 
				String text = getTextForExpectedResult(expectedClass) + ", but this wasn't the case."; 
				return new CheckResult(CheckResult.NO_UDP_RESPONSE_RECEIVED, text);
			} else if (DoipEventTcpMessage.class.isAssignableFrom(expectedClass)) {
//				String text = "It was expected to receive a valid DoIP TCP message, but no valid DoIP TCP message has been received"; 
				String text = getTextForExpectedResult(expectedClass) + ", but this wasn't the case."; 
				return new CheckResult(CheckResult.NO_TCP_RESPONSE_RECEIVED, text);
			} else if (DoipEventConnectionClosed.class.isAssignableFrom(expectedClass)) {
				String text = "It was expected that the socket has been closed, but it hasn't been closed"; 
				return new CheckResult(CheckResult.SOCKET_NOT_CLOSED, text);
			} else {
				String text = "An unknown event class has been passed"; 
				throw logger.throwing(Level.FATAL, new IllegalArgumentException(text));
			}
		} else {
			String text = "No event did occur which is the expected result"; 
			return new CheckResult(CheckResult.NO_ERROR, text);
		}
	}
	
	private static String getTextForActualResult(DoipEvent event) {
		if (event instanceof DoipEventConnectionClosed) {
			return ", but the connection has been closed.";
		}
		if (event instanceof DoipEventMessage) {
			return ", but a '" + ((DoipEventMessage) event).getDoipMessage().getMessageName() + "' has been received.";
		}
		return ", but a event of type '" + event.getClass().getSimpleName() + "' did occure.";
	}
	
	private static String getTextForExpectedResult(Class<? extends DoipEvent> expectedClass) {
			if (DoipEventConnectionClosed.class.isAssignableFrom(expectedClass)) {
				return "It was expected that the connection has been closed";
			} else if (DoipEventTcpHeaderNegAck.class.isAssignableFrom(expectedClass)) {
				return "It was expected to receive a '" + DoipTcpHeaderNegAck.getMessageNameOfClass() + "'";
			} else if (DoipEventTcpDiagnosticMessage.class.isAssignableFrom(expectedClass)) {
				return "It was expected to receive a '" + DoipTcpDiagnosticMessage.getMessageNameOfClass() + "'";
			} else if (DoipEventTcpDiagnosticMessagePosAck.class.isAssignableFrom(expectedClass)) {
				return "It was expected to receive a '" + DoipTcpDiagnosticMessagePosAck.getMessageNameOfClass() + "'";
			} else if (DoipEventTcpDiagnosticMessageNegAck.class.isAssignableFrom(expectedClass)) {
				   return "It was expected to receive a '" + DoipTcpDiagnosticMessageNegAck.getMessageNameOfClass() + "'";
			} else if (DoipEventTcpRoutingActivationRequest.class.isAssignableFrom(expectedClass)) {
				   return "It was expected to receive a '" + DoipTcpRoutingActivationRequest.getMessageNameOfClass() + "'";
			} else if (DoipEventTcpRoutingActivationResponse.class.isAssignableFrom(expectedClass)) {
				   return "It was expected to receive a '" + DoipTcpRoutingActivationResponse.getMessageNameOfClass() + "'";
			} else if (DoipEventTcpAliveCheckRequest.class.isAssignableFrom(expectedClass)) {
				   return "It was expected to receive a '" + DoipTcpAliveCheckRequest.getMessageNameOfClass() + "'";
			} else if (DoipEventTcpAliveCheckResponse.class.isAssignableFrom(expectedClass)) {
				   return "It was expected to receive a '" + DoipTcpAliveCheckResponse.getMessageNameOfClass() + "'";
			} else if (DoipEventUdpVehicleIdentRequest.class.isAssignableFrom(expectedClass)) {
				   return "It was expected to receive a '" + DoipUdpVehicleIdentRequest.getMessageNameOfClass() + "'";
			} else if (DoipEventUdpVehicleIdentRequestWithEid.class.isAssignableFrom(expectedClass)) {
				   return "It was expected to receive a '" + DoipUdpVehicleIdentRequestWithEid.getMessageNameOfClass() + "'";
			} else if (DoipEventUdpVehicleIdentRequestWithVin.class.isAssignableFrom(expectedClass)) {
				   return "It was expected to receive a '" + DoipUdpVehicleIdentRequestWithVin.getMessageNameOfClass() + "'";
			} else if (DoipEventUdpVehicleAnnouncementMessage.class.isAssignableFrom(expectedClass)) {
				   return "It was expected to receive a '" + DoipUdpVehicleAnnouncementMessage.getMessageNameOfClass() + "'";
			} else if (DoipEventUdpHeaderNegAck.class.isAssignableFrom(expectedClass)) {
				   return "It was expected to receive a '" + DoipUdpHeaderNegAck.getMessageNameOfClass() + "'";
			} else if (DoipEventUdpEntityStatusRequest.class.isAssignableFrom(expectedClass)) {
				   return "It was expected to receive a '" + DoipUdpEntityStatusRequest.getMessageNameOfClass() + "'";
			} else if (DoipEventUdpEntityStatusResponse.class.isAssignableFrom(expectedClass)) {
				   return "It was expected to receive a '" + DoipUdpEntityStatusResponse.getMessageNameOfClass() + "'";
			} else if (DoipEventUdpDiagnosticPowerModeRequest.class.isAssignableFrom(expectedClass)) {
				   return "It was expected to receive a '" + DoipUdpDiagnosticPowerModeRequest.getMessageNameOfClass() + "'";
			} else if (DoipEventUdpDiagnosticPowerModeResponse.class.isAssignableFrom(expectedClass)) {
				   return "It was expected to receive a '" + DoipUdpDiagnosticPowerModeResponse.getMessageNameOfClass() + "'";
			}
			return "It was expected that a event of type '" + expectedClass.getSimpleName() + "' will occure";
	}
}
