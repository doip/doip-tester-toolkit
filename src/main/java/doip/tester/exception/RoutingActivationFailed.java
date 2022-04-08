package doip.tester.exception;

import doip.library.exception.DoipException;

/**
 * Exception which will be thrown if routing activation fails.
 * 
 */
public class RoutingActivationFailed extends DoipException {

	private static final long serialVersionUID = 2319347034953538180L;

	public RoutingActivationFailed(String message) {
		super(message);
	}

	public RoutingActivationFailed(String string, Throwable e) {
		super(string, e);
	}
}
