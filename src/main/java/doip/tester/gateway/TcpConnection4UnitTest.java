package doip.tester.gateway;

import doip.library.comm.DoipTcpConnection;

public class TcpConnection4UnitTest extends DoipTcpConnection {

	public TcpConnection4UnitTest(String tcpReceiverThreadName, int maxByteArraySizeLogging) {
		super(tcpReceiverThreadName, maxByteArraySizeLogging);
	}
}
