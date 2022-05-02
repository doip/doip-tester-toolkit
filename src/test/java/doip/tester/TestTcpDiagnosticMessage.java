package doip.tester;

import static doip.junit.Assertions.assertNotNull;
import static doip.junit.Assertions.fail;
import static doip.junit.Assertions.assertArrayEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import doip.library.util.Helper;
import doip.library.util.StringConstants;
import doip.logging.LogManager;
import doip.logging.Logger;
import doip.tester.exception.DiagnosticServiceExecutionFailed;
import doip.tester.gateway.Gateway4UnitTest;
import doip.tester.utils.TestSetup;
import doip.tester.utils.TesterTcpConnection;

public class TestTcpDiagnosticMessage {

	private static Logger logger = LogManager.getLogger(TestTcpDiagnosticMessage.class);
	
	private static Gateway4UnitTest gateway = null;
	
	private TestSetup testSetup = null;
	
	private TesterTcpConnection tcpConn = null; 

	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
		
		try {
			if (logger.isInfoEnabled()) {
				logger.info(StringConstants.LINE);
				logger.info(">>> public static void setUpBeforeClass() throws Exception");
			}

			// --- SET UP BEFORE CLASS BEGIN --------------------------------
			gateway = new Gateway4UnitTest();
			gateway.start();
			// --- SET UP BEFORE CLASS END ----------------------------------
			
		} catch (Exception e) {
			logger.error("Unexpected " + e.getClass().getName() + " in setUpBeforeClass()");
			logger.error(Helper.getExceptionAsString(e));
			throw e;
		} finally {
			if (logger.isInfoEnabled()) {
				logger.info("<<< public static void setUpBeforeClass() throws Exception");
				logger.info(StringConstants.LINE);
			}
		}
	}

	@AfterAll
	public static void tearDownAfterClass() throws Exception {
		try {
			if (logger.isInfoEnabled()) {
				logger.info(StringConstants.LINE);
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
				logger.info(StringConstants.LINE);
			}
		}
	}

	@BeforeEach
	public void setUp() throws Exception {
		try {
			if (logger.isInfoEnabled()) {
				logger.info(StringConstants.LINE);
				logger.info(">>> public void setUp() throws Exception");
			}
			
			// --- SET UP CODE BEGIN ----------------------------------------
			testSetup = new TestSetup();
			testSetup.initialize("src/test/resources/tester.properties");
			tcpConn = testSetup.createTesterTcpConnection();
			// --- SET UP CODE END ------------------------------------------
			
		} catch (Exception e) {
			logger.error("Unexpected " + e.getClass().getName() + " in setUp()");
			logger.error(Helper.getExceptionAsString(e));
			throw e;
		} finally {
			if (logger.isInfoEnabled()) {
				logger.info("<<< public void setUp() throws Exception");
				logger.info(StringConstants.LINE);
			}	
		}
	}

	@AfterEach
	public void tearDown() throws Exception {
		try {
			if (logger.isInfoEnabled()) {
				logger.info(StringConstants.LINE);
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
				logger.info(StringConstants.LINE);
			}
		}
	}

	@Test
	public void testSuccessfulExecuteDiagnosticService() {
		try {
			if (logger.isInfoEnabled()) {
				logger.info(StringConstants.WALL);
				logger.info(">>> public void testSuccessfulExecuteDiagnosticService()");
			}
			
			// --- TEST CODE BEGIN --------------------------------------------
			byte[] response = tcpConn.executeDiagnosticService(new byte[] {0x10, 0x01}, true);
			assertNotNull(response);
			assertArrayEquals(new byte[] {0x7F, 0x10, 0x10}, response, "Response does not match expected value");
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
				logger.info(StringConstants.WALL);
			}
		}
	}
}
