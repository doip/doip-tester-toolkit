package doip.tester.toolkit.server4unittest;

import java.io.IOException;
import java.io.OutputStream;

import doip.library.comm.DoipTcpConnection;
import doip.library.message.DoipMessage;
import doip.library.util.Conversion;
import doip.library.util.Helper;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class DoipTcpConnection4UnitTest extends DoipTcpConnection {

	private static Logger logger = LogManager.getLogger(DoipTcpConnection.class);

	private boolean isSilent = false;
	
	public void setSilent(boolean value) {
		this.isSilent = value;
	}

	public DoipTcpConnection4UnitTest(String tcpReceiverThreadName, int maxByteArraySizeLogging) {
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
