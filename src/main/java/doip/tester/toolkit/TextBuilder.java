package doip.tester.toolkit;

import org.apache.logging.log4j.message.Message;

public class TextBuilder {
	
	public static String noValidDoipMessageReceived(String message) {
		return "It was expected to receive a " + message + ", but no valid DoIP message has been received.";
	}
	
	public static String wrongMessageReceived(String expected, String actual) {
		return "It was expected to receive a " + expected + ", but instead a " + actual + " has been received.";
	}
	
	public static String unexpectedException(Throwable e) {
		return "Unexpected " + e.getClass().getName() + ": " + e.getMessage();
	}

	public static String sendMessage(String messageName) {
		return "Send a '" + messageName + "'";
	}
}
