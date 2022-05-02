package doip.tester.toolkit.gateway4unittest;

import java.io.IOException;
import java.io.OutputStream;

import doip.library.comm.DoipTcpConnection;
import doip.library.message.DoipMessage;
import doip.library.util.Conversion;
import doip.library.util.Helper;
import doip.logging.LogManager;
import doip.logging.Logger;

public class TcpConnection4UnitTest extends DoipTcpConnection {

	private static Logger logger = LogManager.getLogger(DoipTcpConnection.class);

	private boolean isSilent = false;
	
	public void setSilent(boolean value) {
		this.isSilent = value;
	}

	public TcpConnection4UnitTest(String tcpReceiverThreadName, int maxByteArraySizeLogging) {
		super(tcpReceiverThreadName, maxByteArraySizeLogging);
	}
	
	public void send(byte[] data) {
		if (logger.isTraceEnabled()) {
			logger.trace(">>> public void send(byte[] data)");
		}
		
		if (!isSilent) {
			super.send(data);
		} else {
			logger.info("No message will be sent because flag <isSilent> is true");
		}

		if (logger.isTraceEnabled()) {
			logger.trace("<<< public void send(byte[] data)");
		}
	}


}
