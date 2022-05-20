package doip.tester.toolkit.gateway4unittest;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import doip.library.comm.DoipTcpConnection;
import doip.library.comm.DoipTcpConnectionListener;
import doip.library.comm.DoipUdpMessageHandler;
import doip.library.comm.DoipUdpMessageHandlerListener;
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
import doip.library.net.TcpServer;
import doip.library.net.TcpServerListener;
import doip.library.net.TcpServerThread;
import doip.library.util.Helper;
import doip.logging.LogManager;
import doip.logging.Logger;

/**
 * Implements a DoIP gateway which will be used for unit tests. The project "DoIP Simulation"
 * implements the good case for a DoIP gateway, that means it behaves according to the ISO 13400.
 * Compared to that the Gateway4UnitTest implements a much more simple gateway, but gives in addition
 * to that the possibility to modify its behavior so it can be used for bad case tests. In other words
 * the Gateway4UnitTest can be configured to behave NOT according to ISO specification.
 */
public class Gateway4UnitTest implements TcpServerListener, DoipTcpConnectionListener, DoipUdpMessageHandlerListener {
	
	private static Logger logger = LogManager.getLogger(Gateway4UnitTest.class);
	
	private TcpServerThread tcpServerThread = null;
	
	/**
	 * UDP socket for this gateway
	 */
	private MulticastSocket udpSocket = null;
	
	private DoipUdpMessageHandler udpMessageHandler = null;
	
	private ServerSocket tcpSocket = null;

	private LinkedList<TcpConnection4UnitTest> tcpConnectionList = new LinkedList<TcpConnection4UnitTest>();
	
	private int entityAddress = 0xE000;
	
	private byte[] vin = new byte[17];
	private byte[] eid = new byte[6];
	private byte[] gid = new byte[6];
	
	private static int connectionCounter = 1;
	
	private boolean isSilent = false;
	
	private byte[] nextUdpResponse = null;
	
	public void setSilent(boolean value) {
		this.isSilent = value;
	}

	public void start() throws IOException {
		String function = "public void start()";
		logger.trace(">>> " + function);
		
		try {
			logger.info("Create UDP socket");
			this.udpSocket = Helper.createUdpSocket(null, 13400, null); 
			udpMessageHandler = new DoipUdpMessageHandler("GW-UDP", null);
			this.udpMessageHandler.addListener(this);
			logger.info("Start UDP message handler");
			udpMessageHandler.start(this.udpSocket);
			
			logger.info("Create new TcpServerThread with name 'TCP-SERV'");
			tcpServerThread = new TcpServerThread("TCP-SERV");
			tcpServerThread.addListener(this);
			logger.info("Create TCP server socket on port 13400");
			tcpSocket = Helper.createTcpServerSocket(null, 13400);
			logger.info("Start TcpServerThread");
			tcpServerThread.start(tcpSocket);
		} catch (IOException e) {
			logger.fatal("Unexpected " + e.getClass().getName() + " in start()");
			logger.fatal(Helper.getExceptionAsString(e));
			throw e;
		} finally {
			logger.trace("<<< " + function);
		}
	}
	
	public void stop() {
		try {
			logger.trace(">>> public void stop()");
			if (tcpServerThread != null) {
				logger.info("Stop TCP server thread");
				tcpServerThread.stop();
				tcpServerThread = null;
			}
			
			if (udpMessageHandler != null) {
				logger.info("Stop UDP message handler");
				udpMessageHandler.stop();
				this.udpMessageHandler = null;
			}
		} finally {		
			logger.trace("<<< public void stop()");
		}
	}
	
	public void setNextUdpResponse(byte[] msg) {
		this.nextUdpResponse = msg;
	}

	@Override
	public void onConnectionAccepted(TcpServer tcpServer, Socket socket) {
		logger.trace(">>> public void onConnectionAccepted(TcpServer tcpServer, Socket socket)");
		try {
			logger.info("New TCP connection established");
			logger.info("Set TCP no delay on connection socket");
			socket.setTcpNoDelay(true);
			logger.info("Create new instance of TcpConnection4UnitTest for TCP connection");
			TcpConnection4UnitTest conn = new TcpConnection4UnitTest("TCP-RECV-GW-" + connectionCounter, 64);
			connectionCounter++;
			conn.addListener(this);
			this.tcpConnectionList.add(conn);
			logger.info("Start thread for new TCP connection");
			conn.start(socket);
			
		} catch (Exception e) {
			logger.fatal("Unexpected Exception");
		} finally {
			logger.trace("<<< public void onConnectionAccepted(TcpServer tcpServer, Socket socket)");
		}
	}
	
	public TcpConnection4UnitTest getConnection(int index) {
		return this.tcpConnectionList.get(index);
	}

	
	@Override
	public void onConnectionClosed(DoipTcpConnection doipTcpConnection) {
		logger.trace(">>> public void onConnectionClosed(DoipTcpConnection doipTcpConnection)");
		logger.info("TCP connection has been closed");
		tcpConnectionList.remove(doipTcpConnection);
		logger.trace("<<< public void onConnectionClosed(DoipTcpConnection doipTcpConnection)");
	}

	@Override
	public void onDoipTcpDiagnosticMessage(DoipTcpConnection doipTcpConnection, DoipTcpDiagnosticMessage doipMessage) {
		int sourceAddress = doipMessage.getSourceAddress();
		int targetAddress = doipMessage.getTargetAddress();
		DoipTcpDiagnosticMessagePosAck posAck =
				new DoipTcpDiagnosticMessagePosAck(targetAddress, sourceAddress, 0, new byte[0]);
		doipTcpConnection.send(posAck);
		
		DoipTcpDiagnosticMessage response = 
				new DoipTcpDiagnosticMessage(
						targetAddress, sourceAddress, new byte[] {0x7F, 0x10, 0x10});
		
		doipTcpConnection.send(response);
	}

	@Override
	public void onDoipTcpDiagnosticMessageNegAck(DoipTcpConnection doipTcpConnection,
			DoipTcpDiagnosticMessageNegAck doipMessage) {
		
	}

	@Override
	public void onDoipTcpDiagnosticMessagePosAck(DoipTcpConnection doipTcpConnection,
			DoipTcpDiagnosticMessagePosAck doipMessage) {
		
	}

	@Override
	public void onDoipTcpRoutingActivationRequest(DoipTcpConnection doipTcpConnection,
			DoipTcpRoutingActivationRequest doipMessage) {
		int testerAddress = doipMessage.getSourceAddress();
		
		DoipTcpRoutingActivationResponse resp = new DoipTcpRoutingActivationResponse(testerAddress, entityAddress, 0x10, -1);
		doipTcpConnection.send(resp);
	}

	@Override
	public void onDoipTcpRoutingActivationResponse(DoipTcpConnection doipTcpConnection,
			DoipTcpRoutingActivationResponse doipMessage) {
		
	}

	@Override
	public void onDoipTcpAliveCheckRequest(DoipTcpConnection doipTcpConnection, DoipTcpAliveCheckRequest doipMessage) {
		
	}

	@Override
	public void onDoipTcpAliveCheckResponse(DoipTcpConnection doipTcpConnection,
			DoipTcpAliveCheckResponse doipMessage) {
		
	}

	@Override
	public void onDoipTcpHeaderNegAck(DoipTcpConnection doipTcpConnection, DoipTcpHeaderNegAck doipMessage) {
		
	}

	@Override
	public void onDoipUdpVehicleIdentRequest(DoipUdpVehicleIdentRequest doipMessage, DatagramPacket packet) {
		String function = "public void onDoipUdpVehicleIdentRequest(DoipUdpVehicleIdentRequest doipMessage, DatagramPacket packet)";
		try {
			logger.trace(">>> " + function);
			if (!isSilent) {
				if (nextUdpResponse != null) {
					try {
						this.udpMessageHandler.sendDatagramPacket(nextUdpResponse,  nextUdpResponse.length, packet.getAddress(), packet.getPort());
					} catch (IOException e) {
					} finally {
						nextUdpResponse = null;
					}
				} else {
					DoipUdpVehicleAnnouncementMessage vam = new DoipUdpVehicleAnnouncementMessage(vin, entityAddress, eid, gid, 0, 0);
					try {
						this.udpMessageHandler.send(vam, packet.getAddress(), packet.getPort());
					} catch (IOException e) {
					}
				}
			}
		} finally {
			logger.trace("<<< " + function);
		}
	}

	@Override
	public void onDoipUdpVehicleIdentRequestWithEid(DoipUdpVehicleIdentRequestWithEid doipMessage,
			DatagramPacket packet) {
		
	}

	@Override
	public void onDoipUdpVehicleIdentRequestWithVin(DoipUdpVehicleIdentRequestWithVin doipMessage,
			DatagramPacket packet) {
		
	}

	@Override
	public void onDoipUdpVehicleAnnouncementMessage(DoipUdpVehicleAnnouncementMessage doipMessage,
			DatagramPacket packet) {
		
	}

	@Override
	public void onDoipUdpDiagnosticPowerModeRequest(DoipUdpDiagnosticPowerModeRequest doipMessage,
			DatagramPacket packet) {
		
	}

	@Override
	public void onDoipUdpDiagnosticPowerModeResponse(DoipUdpDiagnosticPowerModeResponse doipMessage,
			DatagramPacket packet) {
		
	}

	@Override
	public void onDoipUdpEntityStatusRequest(DoipUdpEntityStatusRequest doipMessage, DatagramPacket packet) {
		
	}

	@Override
	public void onDoipUdpEntityStatusResponse(DoipUdpEntityStatusResponse doipMessage, DatagramPacket packet) {
		
	}

	@Override
	public void onDoipUdpHeaderNegAck(DoipUdpHeaderNegAck doipMessage, DatagramPacket packet) {
		
	}
}
