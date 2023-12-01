package doip.tester.toolkit;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import doip.library.properties.EmptyPropertyValue;
import doip.library.properties.MissingProperty;
import doip.library.properties.MissingSystemProperty;

/**
 * Contains all utilities to perform tests for a DoIP gateway.
 * It makes implementation of test cases much more easy.
 */
public class TestSetup {

	/**
	 * log4j logger
	 */
	private static Logger logger = LogManager.getLogger(TestSetup.class);
	private static Marker enter = MarkerManager.getMarker("ENTER");
	private static Marker exit = MarkerManager.getMarker("EXIT");
	
	/**
	 * Test configuration with parameters for all tests.
	 */
	private TestConfig config = null; 
	
	/**
	 * List of TCP connections to the gateway
	 */
	private Vector<TesterTcpConnection> tcpConnections = 
			new Vector<TesterTcpConnection>();
	
	/**
	 * Module for udp communication for a tester
	 */
	private TesterUdpCommModule testerUdpCommModule = null;
	
	private Map<String, String> context;
	
	public TestSetup() {
		context = new HashMap<String, String>();
		context.put("context", "tester");
		tcpConnections.ensureCapacity(8);
	}
	
	/**
	 * Initializes the test setup
	 * @return Returns true if initialization was successful.
	 * @throws Exception 
	 * @throws EmptyPropertyValue 
	 * @throws MissingProperty 
	 * @throws IOException 
	 */
	public void initialize() throws IOException, EmptyPropertyValue, MissingProperty, MissingSystemProperty {
		try {
			logger.trace(enter, ">>> public void initialize()");
			
			logger.debug("Initialize the test setup");
	
			this.config = new TestConfig();
			logger.debug("Create UDP socket");
			this.testerUdpCommModule = new TesterUdpCommModule(this.config);
			this.testerUdpCommModule.setContext(context);
			DatagramSocket socket = new DatagramSocket();
			logger.debug("Start thread which listens on data from UDP socket");
			this.testerUdpCommModule.start(socket);
			
			logger.debug("Test setup has been completely initialized.");
	
		} catch (IOException e) {
			throw logger.throwing(e);
		} finally {
				logger.trace(exit, ">>> public void initialize()");
		}
	}
	
	/**
	 * Uninitializes the test setup
	 * @return
	 */
	public boolean uninitialize() {
		String function = "public boolean uninitialize()";
		try {
			logger.trace(enter, ">>> public boolean uninitialize()");
	
			if (this.tcpConnections != null) {
				for (DoipTcpConnectionWithEventCollection conn : this.tcpConnections) {
					conn.stop();																	
				}
				this.tcpConnections.clear();
			}
			
			if (this.testerUdpCommModule != null) {
				this.testerUdpCommModule.stop();
				this.testerUdpCommModule = null;
			}
			
			this.config = null;
		
			return true;

		} finally {
			logger.trace(exit, "<<< public boolean uninitialize()");
		}
	}
	
	/**
	 * Creates a new TCP connection to the DoIP gateway.
	 * @return The new TCP connection
	 * @throws IOException 
	 */
	public TesterTcpConnection createTesterTcpConnection() throws IOException {
		try {
			logger.trace(enter, ">>> public TesterTcpConnection createTesterTcpConnection()");
		
			TesterTcpConnection conn = new TesterTcpConnection(config);
			this.tcpConnections.add(conn);
			logger.info("Connect to host with IP address " + config.getTargetAddress() + " and port number " + config.getTargetPort());
			long before = System.nanoTime();
			Socket socket = new Socket(config.getTargetAddress(), config.getTargetPort());
			long after = System.nanoTime();
			long duration = after - before;
			logger.info("Connection established. It took " + duration + " ns to establish the connection.");
			socket.setTcpNoDelay(true);
			conn.setContext(context);
			conn.start(socket);
			return conn;
		
		} finally {
			logger.trace(exit, "<<< public TesterTcpConnection createTesterTcpConnection()");
		}
	}
	
	/**
	 * Removes a TCP connection.
	 * @param conn
	 */
	public void removeDoipTcpConnectionTest(DoipTcpConnectionWithEventCollection conn) {
		try {
			logger.trace(enter, ">>> public void removeDoipTcpConnectionTest(TestDoipTcpConnection conn)");
			
			conn.stop();
			this.tcpConnections.remove(conn);
		
		} finally {
			logger.trace(exit, "<<< public void removeDoipTcpConnectionTest(TestDoipTcpConnection conn)");
		}
	}
	
	public TesterUdpCommModule getTesterUdpCommModule() {
		return this.testerUdpCommModule;
	}
	
	public TestConfig getConfig() {
		return this.config;
	}
}
