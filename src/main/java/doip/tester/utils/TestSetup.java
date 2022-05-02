package doip.tester.utils;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Vector;

import doip.library.properties.EmptyPropertyValue;
import doip.library.properties.MissingProperty;
import doip.library.util.Helper;
import doip.logging.LogManager;
import doip.logging.Logger;

/**
 * Contains all utilities to perform tests for a DoIP gateway.
 * It makes implementation of test cases much more easy.
 */
public class TestSetup {

	/**
	 * log4j logger
	 */
	private static Logger logger = LogManager.getLogger(TestSetup.class);
	
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
	
	
	public TestSetup() {
		tcpConnections.ensureCapacity(8);
	}
	
	/**
	 * Initializes the test setup
	 * @param filename Filename of a property file which contains
	 *                 all settings which are required to initialize
	 *                 the test setup.
	 * @return Returns true if initialization was successful.
	 * @throws Exception 
	 * @throws EmptyPropertyValue 
	 * @throws MissingProperty 
	 * @throws IOException 
	 */
	public boolean initialize(String filename) throws Exception {
		
		try {
			if (logger.isTraceEnabled()) {
				logger.trace(">>> public boolean initialize(String filename)");
			}
	
			this.config = new TestConfig(filename);
			this.testerUdpCommModule = new TesterUdpCommModule(this.config);
			DatagramSocket socket = new DatagramSocket();
			this.testerUdpCommModule.start(socket);
			return true;
			
		} catch (Exception e) {
			throw e;
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace("<<< public boolean initialize(String filename)");
			}
		}
	}
	
	/**
	 * Uninitializes the test setup
	 * @return
	 */
	public boolean uninitialize() {
		try {
			if (logger.isTraceEnabled()) {
				logger.trace(">>> public boolean uninitialize()");
			}
	
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

		} catch (Exception e) {
			logger.error(e.getClass().getName() + ": " + e.getMessage());
			throw e;
			
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace("<<< public boolean uninitialize()");
			}
		}
	}
	
	/**
	 * Creates a new TCP connection to the DoIP gateway.
	 * @return The new TCP connection
	 * @throws IOException 
	 */
	public TesterTcpConnection createTesterTcpConnection() throws IOException {
		try {
			if (logger.isTraceEnabled()) {
				logger.trace(">>> public DoipTcpConnectionWithEventCollection createDoipTcpConnectionWithEventCollection()");
			}
		
			TesterTcpConnection conn = new TesterTcpConnection(config);
			this.tcpConnections.add(conn);
			Socket socket = new Socket(config.getTargetAddress(), config.getTargetPort());
			socket.setTcpNoDelay(true);
			conn.start(socket);
			return conn;
		
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("Unexpected " + e.getClass().getName() + " in createDoipTcpConnectionWithEventCollection()");
				logger.error(Helper.getExceptionAsString(e));
			}
			throw e;
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace("<<< public DoipTcpConnectionWithEventCollection createDoipTcpConnectionWithEventCollection()");
			}
		}
	}
	
	/**
	 * Removes a TCP connection.
	 * @param conn
	 */
	public void removeDoipTcpConnectionTest(DoipTcpConnectionWithEventCollection conn) {
		try {
			if (logger.isTraceEnabled()) {
				logger.trace(">>> public void removeDoipTcpConnectionTest(TestDoipTcpConnection conn)");
			}
			
			conn.stop();
			this.tcpConnections.remove(conn);
		
		} catch (Exception e) {
			logger.error("Unexpected " + e.getClass().getName() + " in removeDoipTcpConnectionTest()");
			logger.error(e.getClass().getName());
			throw e;
		} finally {
			if (logger.isTraceEnabled()) {
				logger.trace("<<< public void removeDoipTcpConnectionTest(TestDoipTcpConnection conn)");
			}
		}
	}
	
	
	public TesterUdpCommModule getTesterUdpCommModule() {
		return this.testerUdpCommModule;
	}
	
	public TestConfig getConfig() {
		return this.config;
	}
}
