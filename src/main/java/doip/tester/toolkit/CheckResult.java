package doip.tester.toolkit;

public class CheckResult {
	
	public static final int NO_ERROR = 0;
	public static final int WRONG_EVENT = 1;
	public static final int UNEXPECTED_DOIP_MESSAGE = 2;
	public static final int UNEXPECTED_SOCKET_CLOSED = 3;
	// public static final int UNKNOWN_EVENT = 4;
	public static final int NO_UDP_RESPONSE_RECEIVED = 5;
	public static final int NO_TCP_RESPONSE_RECEIVED = 6;
	public static final int SOCKET_NOT_CLOSED = 7;
	// public static final int UNKOWN_EVENT_CLASS = 8;
	
	private int code = 0;
	
	private String text = null;
	
	public CheckResult(int code, String text) {
		this.code = code;
		this.text = text;
	}

	public int getCode() {
		return code;
	}

	public String getText() {
		return text;
	}

}
