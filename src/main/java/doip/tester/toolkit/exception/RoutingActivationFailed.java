package doip.tester.toolkit.exception;

import doip.library.exception.DoipException;

/**
 * Exception which will be thrown if routing activation fails.
 * 
 */
public class RoutingActivationFailed extends DoipException {

	private static final long serialVersionUID = 2319347034953538180L;
	
	public static final int NO_RESPONSE_RECEIVED = 1;
	public static final int WRONG_RESPONSE_RECEIVED = 2;
	public static final int INVALID_RESPONSE_RECEIVED = 3;
	
	private int errorCode = 0;

	public int getErrorCode() {
		return errorCode;
	}

	public RoutingActivationFailed(int errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public RoutingActivationFailed(int errorCode, String string, Throwable e) {
		super(string, e);
		this.errorCode = errorCode;
	}
}
