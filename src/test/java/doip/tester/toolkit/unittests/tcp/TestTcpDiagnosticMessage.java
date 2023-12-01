package doip.tester.toolkit.unittests.tcp;

import static com.starcode88.jtest.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import doip.library.message.DoipTcpDiagnosticMessage;
import doip.library.util.Helper;
import doip.library.util.StringConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import doip.tester.toolkit.TestSetup;
import doip.tester.toolkit.TesterTcpConnection;
import doip.tester.toolkit.event.DoipEventTcpDiagnosticMessage;
import doip.tester.toolkit.exception.DiagnosticServiceExecutionFailed;
import doip.tester.toolkit.server4unittest.DoipServer4UnitTest;

public class TestTcpDiagnosticMessage {

	private static Logger logger = LogManager.getLogger(TestTcpDiagnosticMessage.class);
	
	private static DoipServer4UnitTest gateway = null;
	
	private TestSetup testSetup = null;
	
	private TesterTcpConnection tcpConn = null; 

	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
		
		try {
			if (logger.isInfoEnabled()) {
				logger.info(StringConstants.SINGLE_LINE);
				logger.info(">>> public static void setUpBeforeClass() throws Exception");
			}

			// --- SET UP BEFORE CLASS BEGIN --------------------------------
			gateway = new DoipServer4UnitTest();
			gateway.start();
			// --- SET UP BEFORE CLASS END ----------------------------------
			
		} catch (Exception e) {
			logger.error("Unexpected " + e.getClass().getName() + " in setUpBeforeClass()");
			logger.error(Helper.getExceptionAsString(e));
			throw e;
		} finally {
			if (logger.isInfoEnabled()) {
				logger.info("<<< public static void setUpBeforeClass() throws Exception");
				logger.info(StringConstants.SINGLE_LINE);
			}
		}
	}

	@AfterAll
	public static void tearDownAfterClass() throws Exception {
		try {
			if (logger.isInfoEnabled()) {
				logger.info(StringConstants.SINGLE_LINE);
				logger.info(">>> public static void tearDownAfterClass() throws Exception");
			}
			
			// --- TEAR DOWN AFTER CLASS BEGIN ------------------------------
			if (gateway != null) {
				gateway.stop();
				gateway = null;
			}
			// --- TEAR DOWN AFTER CLASS END --------------------------------
			
		} catch (Exception e) {
			logger.error("Unexpected " + e.getClass().getName() + " in tearDownAfterClass()");
			logger.error(Helper.getExceptionAsString(e));
			throw e;
		} finally {
			if (logger.isInfoEnabled()) {
				logger.info("<<< public static void tearDownAfterClass() throws Exception");
				logger.info(StringConstants.SINGLE_LINE);
			}
		}
	}

	@BeforeEach
	public void setUp() throws Exception {
		try {
			if (logger.isInfoEnabled()) {
				logger.info(StringConstants.SINGLE_LINE);
				logger.info(">>> public void setUp() throws Exception");
			}
			
			// --- SET UP CODE BEGIN ----------------------------------------
			testSetup = new TestSetup();
			testSetup.initialize();
			tcpConn = testSetup.createTesterTcpConnection();
			// --- SET UP CODE END ------------------------------------------
			
		} catch (Exception e) {
			logger.error("Unexpected " + e.getClass().getName() + " in setUp()");
			logger.error(Helper.getExceptionAsString(e));
			throw e;
		} finally {
			if (logger.isInfoEnabled()) {
				logger.info("<<< public void setUp() throws Exception");
				logger.info(StringConstants.SINGLE_LINE);
			}	
		}
	}

	@AfterEach
	public void tearDown() throws Exception {
		try {
			if (logger.isInfoEnabled()) {
				logger.info(StringConstants.SINGLE_LINE);
				logger.info(">>> public void tearDown() throws Exception");
			}
			
			// --- TEAR DOWN CODE BEGIN --------------------------------------
			if (tcpConn != null) {
				testSetup.removeDoipTcpConnectionTest(tcpConn);
				tcpConn = null;
			}
			
			if (testSetup != null) {
				testSetup.uninitialize();
				testSetup = null;
			}
			// --- TEAR DOWN CODE END ----------------------------------------
			
		} catch (Exception e) {
			logger.error("Unexpected " + e.getClass().getName() + " in tearDown()");
			logger.error(Helper.getExceptionAsString(e));
			throw e;
		} finally {
			if (logger.isInfoEnabled()) {
				logger.info("<<< public void tearDown() throws Exception");
				logger.info(StringConstants.SINGLE_LINE);
			}
		}
	}

	@Test
	public void testSuccessfulExecuteDiagnosticService() {
		try {
			if (logger.isInfoEnabled()) {
				logger.info(StringConstants.HASH_LINE);
				logger.info(">>> public void testSuccessfulExecuteDiagnosticService()");
			}
			
			// --- TEST CODE BEGIN --------------------------------------------
			int testerAddress = testSetup.getConfig().getTesterAddress();
			DoipEventTcpDiagnosticMessage doipEventDiagResponse = tcpConn.executeDiagnosticServicePosAck(new byte[] {0x10, 0x01});
			assertNotNull(doipEventDiagResponse);
			DoipTcpDiagnosticMessage doipDiagResponse = (DoipTcpDiagnosticMessage) doipEventDiagResponse.getDoipMessage();
			byte[] response = doipDiagResponse.getDiagnosticMessage();
			assertArrayEquals(new byte[] {0x50, 0x03, 0x00, 0x32, 0x01, (byte) 0xF4}, response, "Response does not match expected value");
			// --- TEST CODE END ----------------------------------------------
			
		} catch (DiagnosticServiceExecutionFailed e) {
			logger.error(Helper.getExceptionAsString(e));
			fail("Got a exception of type DiagnosticServiceExcecutionFailed");
		} catch (Exception e) {
			logger.error("Unexpected " + e.getClass().getName() + " in testSuccessfulExecuteDiagnosticService()");
			logger.error(Helper.getExceptionAsString(e));
			throw e;
		} finally {
			if (logger.isInfoEnabled()) {
				logger.info("<<< public void testSuccessfulExecuteDiagnosticService()");
				logger.info(StringConstants.HASH_LINE);
			}
		}
	}
}
