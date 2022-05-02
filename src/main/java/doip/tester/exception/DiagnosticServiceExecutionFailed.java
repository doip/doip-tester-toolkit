package doip.tester.exception;

import doip.library.exception.DoipException;

public class DiagnosticServiceExecutionFailed extends DoipException {
	
	private static final long serialVersionUID = -2816345361633992707L;
	
	public static final int UNSPECIFIC_ERROR = 0;
	public static final int NO_DIAG_MESSAGE_POS_ACK_RECEIVED = 1;
	public static final int NO_DIAG_MESSAGE_RECEIVED = 2;
	
	private int errorCode = 0;

	public int getErrorCode() {
		return errorCode;
	}

	public DiagnosticServiceExecutionFailed(int code, String string) {
		super(string);
		this.errorCode = code;
	}

	public DiagnosticServiceExecutionFailed(int code, String string, Throwable e) {
		super(string, e);
		this.errorCode = code;
	}
}
