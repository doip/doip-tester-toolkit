package doip.tester.toolkit.server4unittest;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

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
import doip.library.message.DoipUdpMessage;
import doip.library.message.DoipUdpVehicleAnnouncementMessage;
import doip.library.message.DoipUdpVehicleIdentRequest;
import doip.library.message.DoipUdpVehicleIdentRequestWithEid;
import doip.library.message.DoipUdpVehicleIdentRequestWithVin;
import doip.library.net.TcpServer;
import doip.library.net.TcpServerListener;
import doip.library.net.TcpServerThread;
import doip.library.timer.NanoTimer;
import doip.library.util.Helper;
import doip.library.util.LookupTable;
import doip.tester.toolkit.TextBuilder;

/**
 * Implements a DoIP gateway which will be used for unit tests. The project "DoIP Simulation"
 * implements the good case for a DoIP gateway, that means it behaves according to the ISO 13400.
 * Compared to that the Gateway4UnitTest implements a much more simple gateway, but gives in addition
 * to that the possibility to modify its behavior so it can be used for bad case tests. In other words
 * the Gateway4UnitTest can be configured to behave NOT according to ISO specification.
 */
public class DoipServer4UnitTest implements TcpServerListener, DoipTcpConnectionListener, DoipUdpMessageHandlerListener {
	
	private static Logger logger = LogManager.getLogger(DoipServer4UnitTest.class);
	private	Marker markerEnter = MarkerManager.getMarker("ENTER");
	private Marker markerExit = MarkerManager.getMarker("EXIT");
	
	private TcpServerThread tcpServerThread = null;
	
	/**
	 * UDP socket for this gateway
	 */
	private MulticastSocket udpSocket = null;
	
	private DoipUdpMessageHandler udpMessageHandler = null;
	
	private ServerSocket tcpSocket = null;

	private LinkedList<DoipTcpConnection4UnitTest> tcpConnectionList = new LinkedList<DoipTcpConnection4UnitTest>();
	
	private int entityAddress = 0xE000;
	
	private byte[] vin = new byte[17];
	private byte[] eid = new byte[6];
	private byte[] gid = new byte[6];
	
	private static int connectionCounter = 1;
	
	private boolean isSilent = false;
	
	public boolean isSilent() {
		return isSilent;
	}
	
	private byte[] nextUdpResponse = null;
	
	public void setSilent(boolean value) {
		this.isSilent = value;
	}
	
	public int getConnectionCount() {
		return tcpConnectionList.size();
	}
	
	public DoipUdpMessageHandler createDoipUdpMessageHandler(String udpReceiverThreadName, LookupTable lookupTable) {
		return new DoipUdpMessageHandler(udpReceiverThreadName, lookupTable);
	}

	public void start() throws IOException {
		String function = "public void start()";
		logger.trace(">>> " + function);
		
		try {
			this.isSilent = false;
			logger.info("Create UDP socket");
			this.udpSocket = Helper.createUdpSocket(null, 13400, null); 
			udpMessageHandler = createDoipUdpMessageHandler("GW-UDP", null);
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
			
			closeAllConnections();
		
			
			if (udpMessageHandler != null) {
				logger.info("Stop UDP message handler");
				udpMessageHandler.stop();
				this.udpMessageHandler = null;
			}

		} finally {		
			logger.trace("<<< public void stop()");
		}
	}
	
	public void closeAllConnections() {
		try {
			logger.trace(markerEnter, ">>> public void closeAllConnections()");
			// We need to create a copy of this list, because when
			// calling stop on a connection it will be automatically
			// removed from the list, that means list will be modified
			// while iterate over all connections
			LinkedList<DoipTcpConnection4UnitTest> copy = new LinkedList<DoipTcpConnection4UnitTest>();
			
			for (DoipTcpConnection4UnitTest conn : tcpConnectionList) {
				copy.add(conn);
			}
			
			for (DoipTcpConnection4UnitTest conn : copy) {
				conn.stop();
			}
			
			Thread.sleep(10);
			if (getConnectionCount() > 0) {
				logger.fatal("It was not possible to close all connections in the DoipServer4UnitTest");
			} else {
				logger.debug("All connections have been closed, there are no connections any more in the list of connections in the DoIP server.");
			}
		} catch (InterruptedException e) {
			logger.fatal(TextBuilder.unexpectedException(e), e);
		} finally {
			logger.trace(markerExit, "<<< public void closeAllConnections()");
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
			logger.info("Create new instance of DoipTcpConnection4UnitTest for TCP connection");
			DoipTcpConnection4UnitTest conn = new DoipTcpConnection4UnitTest("TCP-RECV-GW-" + connectionCounter, 64);
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
	
	public DoipTcpConnection4UnitTest getConnection(int index) {
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
		try {
			logger.trace(markerEnter, ">>> public void onDoipTcpDiagnosticMessage(DoipTcpConnection doipTcpConnection, DoipTcpDiagnosticMessage doipMessage)");
			if (isSilent()) {
				logger.debug("Gateway has been set to silent, therefore no response will be send.");
				return;
			}
			int sourceAddress = doipMessage.getSourceAddress();
			int targetAddress = doipMessage.getTargetAddress();
			byte[] request = doipMessage.getDiagnosticMessage();
			
			DoipTcpDiagnosticMessagePosAck posAck =
					new DoipTcpDiagnosticMessagePosAck(targetAddress, sourceAddress, 0, request);
			doipTcpConnection.send(posAck);
			
			byte[] response = null;
			switch (request[0]) {
			case 0x10:
				response = new byte[] {0x50, 0x03, 0x00, 0x32, 0x01, (byte) 0xF4};
				break;
			default:
				response = new byte[] {0x7F, request[0], 0x10};
				break;
			}
			
			DoipTcpDiagnosticMessage doipResponse = 
					new DoipTcpDiagnosticMessage(
							targetAddress, sourceAddress, response);
			
			doipTcpConnection.send(doipResponse);
		} finally {
			logger.trace(markerExit, "<<< public void onDoipTcpDiagnosticMessage(DoipTcpConnection doipTcpConnection, DoipTcpDiagnosticMessage doipMessage)");
		}
	}

	@Override
	public void onDoipTcpDiagnosticMessageNegAck(DoipTcpConnection doipTcpConnection,
			DoipTcpDiagnosticMessageNegAck doipMessage) {
		
		logger.fatal("Function 'public void onDoipTcpDiagnosticMessageNegAck(DoipTcpConnection doipTcpConnection,DoipTcpDiagnosticMessageNegAck doipMessage)'.");
	}

	@Override
	public void onDoipTcpDiagnosticMessagePosAck(DoipTcpConnection doipTcpConnection,
			DoipTcpDiagnosticMessagePosAck doipMessage) {
		
		logger.fatal("Function 'public void onDoipTcpDiagnosticMessagePosAck(DoipTcpConnection doipTcpConnection,DoipTcpDiagnosticMessageNegAck doipMessage)'.");
	}

	@Override
	public void onDoipTcpRoutingActivationRequest(DoipTcpConnection doipTcpConnection,
			DoipTcpRoutingActivationRequest doipMessage) {
		String funcion = "public void onDoipTcpRoutingActivationRequest(DoipTcpConnection doipTcpConnection, DoipTcpRoutingActivationRequest doipMessage)";
		try {
			logger.trace(">>> " + funcion);
			if (isSilent) {
				logger.debug("Gateway has been set to silent, therefore no response will be send.");
				return;
			}
			int testerAddress = doipMessage.getSourceAddress();
		
			DoipTcpRoutingActivationResponse resp = new DoipTcpRoutingActivationResponse(testerAddress, entityAddress, 0x10, -1);
			doipTcpConnection.send(resp);
		} finally {
			logger.trace("<<< " + funcion);
			 
		}
	}

	@Override
	public void onDoipTcpRoutingActivationResponse(DoipTcpConnection doipTcpConnection,
			DoipTcpRoutingActivationResponse doipMessage) {
		logger.fatal("Method not implemented: "
				+ "public void onDoipTcpRoutingActivationResponse("
				+ "DoipTcpConnection doipTcpConnection, DoipTcpRoutingActivationResponse doipMessage)");
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

	/**
	 * Generic handler for all UDP DoIP messages. This method shall be called
	 * by all 'callback' methods, for example 'onDoipUdpVehicleIdentRequest'.
	 * @param doipMessage
	 * @param packet
	 */
	public void handleDoipUdpMessage(DoipUdpMessage doipMessage, DatagramPacket packet) {
		logger.trace(">>> public void handleUdpMessage(DoipUdpMessage doipMessage, DatagramPacket packet)");
		try {
			if (isSilent) {
				logger.debug("Gateway has been set to silent, therefore no response will be send.");
				return;
			}
			if (nextUdpResponse != null) {
				logger.debug("A specific UDP response had been set, this will be send instead of regular UDP response.");
				this.udpMessageHandler.sendDatagramPacket(nextUdpResponse, nextUdpResponse.length, packet.getAddress(), packet.getPort());
				nextUdpResponse = null;
			} else {
				// nextUdpResponse == null
				if (doipMessage instanceof DoipUdpVehicleIdentRequest) {
					sendDoipUdpVehicleIdentResponse(packet.getAddress(), packet.getPort());
				} else if (doipMessage instanceof DoipUdpVehicleIdentRequestWithEid) {
					sendDoipUdpVehicleIdentResponse(packet.getAddress(), packet.getPort());
				} else if (doipMessage instanceof DoipUdpVehicleIdentRequestWithVin) {
					sendDoipUdpVehicleIdentResponse(packet.getAddress(), packet.getPort());
				} else if (doipMessage instanceof DoipUdpEntityStatusRequest) {
					sendDoipUdpEntityStatusResponse(packet.getAddress(), packet.getPort());
				} else {
					logger.fatal("Handling of this message has not yet been implemented");
				}
			}
		} catch (IOException e) {
			logger.fatal("Unexpected " + e.getClass().getName() + ": " + e.getMessage());
			logger.catching(e);
		} finally {
			logger.trace("<<< public void handleUdpMessage(DoipUdpMessage doipMessage, DatagramPacket packet)");
		}
	}

//---------------------------------------------------------------------------
// All methods to send a response
//---------------------------------------------------------------------------
	
	/**
	 * Sends a 'vehicle identification response message
	 * (payload type = 0x0004).
	 * 
	 * @param doipRequest
	 * @param packet
	 */
	private void sendDoipUdpVehicleIdentResponse(InetAddress address, int port) {
		try {
			logger.trace(markerEnter, ">>> private void sendDoipUdpVehicleIdentResponse(DoipUdpVehicleIdentRequestWithEid doipRequest, DatagramPacket packet)");
			DoipUdpVehicleAnnouncementMessage doipResponse =
					new DoipUdpVehicleAnnouncementMessage(vin, entityAddress, eid, gid, 0, 0);					
			this.udpMessageHandler.send(doipResponse, address, port);
		} catch (IOException e) {
			logger.catching(Level.FATAL, e);
			logger.trace(markerExit, "<<< private void sendDoipUdpVehicleIdentResponse(DoipUdpVehicleIdentRequestWithEid doipRequest, DatagramPacket packet)");
		}
	}
	
	private void sendDoipUdpEntityStatusResponse(InetAddress address, int port) { 
		try {
			logger.trace(markerEnter, ">>> private void sendDoipEntityStatusResponse(DoipUdpEntityStatusRequest request, DatagramPacket packet)");
			DoipUdpEntityStatusResponse response =
					new DoipUdpEntityStatusResponse(0, 8, this.tcpConnectionList.size(), 0x1000000);
			this.udpMessageHandler.send(response, address, port);
		} catch (IOException e) {
			logger.catching(Level.FATAL, e);
		} finally {
			logger.trace(markerExit,  "<<< private void sendDoipEntityStatusResponse(DoipUdpEntityStatusRequest request, DatagramPacket packet)");
		}
	}
	
//---------------------------------------------------------------------------
// Callback methods for UDP messages
//---------------------------------------------------------------------------
	
	@Override
	public void onDoipUdpVehicleIdentRequest(DoipUdpVehicleIdentRequest doipRequest, DatagramPacket packet) {
		String function = "public void onDoipUdpVehicleIdentRequest(DoipUdpVehicleIdentRequest doipMessage, DatagramPacket packet)";
		try {
			logger.trace(">>> " + function);
			this.handleDoipUdpMessage(doipRequest, packet);
		} finally {
			logger.trace("<<< " + function);
		}
	}

	@Override
	public void onDoipUdpVehicleIdentRequestWithEid(DoipUdpVehicleIdentRequestWithEid doipMessage,
			DatagramPacket packet) {
		this.handleDoipUdpMessage(doipMessage, packet);
		
	}

	@Override
	public void onDoipUdpVehicleIdentRequestWithVin(DoipUdpVehicleIdentRequestWithVin doipMessage,
			DatagramPacket packet) {
		this.handleDoipUdpMessage(doipMessage, packet);
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
		try {
			logger.trace(">>> public void onDoipUdpEntityStatusRequest(DoipUdpEntityStatusRequest doipMessage, DatagramPacket packet)");
			this.handleDoipUdpMessage(doipMessage, packet);
		} finally {
			logger.trace("<<< public void onDoipUdpEntityStatusRequest(DoipUdpEntityStatusRequest doipMessage, DatagramPacket packet)");
		}
	}

	@Override
	public void onDoipUdpEntityStatusResponse(DoipUdpEntityStatusResponse doipMessage, DatagramPacket packet) {
		
	}

	@Override
	public void onDoipUdpHeaderNegAck(DoipUdpHeaderNegAck doipMessage, DatagramPacket packet) {
		
	}
}
