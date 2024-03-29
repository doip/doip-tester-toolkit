package doip.tester.toolkit.unittests.tcp;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import doip.library.comm.DoipTcpConnection;
import doip.library.message.DoipMessage;
import doip.library.message.DoipTcpRoutingActivationResponse;
import doip.library.util.Helper;
import doip.library.util.StringConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import doip.tester.toolkit.DoipTcpConnectionWithEventCollection;
import doip.tester.toolkit.TestSetup;
import doip.tester.toolkit.TesterTcpConnection;
import doip.tester.toolkit.event.DoipEventTcpRoutingActivationResponse;
import doip.tester.toolkit.exception.RoutingActivationFailed;
import doip.tester.toolkit.server4unittest.DoipServer4UnitTest;
import doip.tester.toolkit.server4unittest.DoipTcpConnection4UnitTest;

class TestTcpRoutingActivation {
	
	private static Logger logger = LogManager.getLogger(TestTcpRoutingActivation.class);
	
	private static DoipServer4UnitTest gateway = null;
	
	private static TestSetup testerSetup = null;
	
	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
		
		try {
			if (logger.isInfoEnabled()) {
				logger.info(StringConstants.SINGLE_LINE);
				logger.info(">>> public static void setUpBeforeClass()");
			}

			gateway = new DoipServer4UnitTest();
			gateway.start();
			
			testerSetup = new TestSetup();
			testerSetup.initialize();
			
			
		} catch (Exception e) {
			logger.error("Unexpected " + e.getClass().getName() + " in setUpBeforeClass()");
			logger.error(Helper.getExceptionAsString(e));
			throw e;
		} finally {
			if (logger.isInfoEnabled()) {
				logger.info("<<< public static void setUpBeforeClass()");
				logger.info(StringConstants.SINGLE_LINE);
			}
		}
	}

	@AfterAll
	public static void tearDownAfterClass() throws Exception {
		try {
			if (logger.isInfoEnabled()) {
				logger.info(StringConstants.SINGLE_LINE);
				logger.info(">>> public static void tearDownAfterClass()");
			}
			
			if (testerSetup != null) {
				testerSetup.uninitialize();
				testerSetup = null;
			}
			
			if (gateway != null) {
				gateway.stop();
				gateway = null;
			}
						
		} catch (Exception e) {
			logger.error("Unexpected " + e.getClass().getName() + " in tearDownAfterClass()");
			logger.error(Helper.getExceptionAsString(e));
			throw e;
		} finally {
			if (logger.isInfoEnabled()) {
				logger.info("<<< public static void tearDownAfterClass()");
				logger.info(StringConstants.SINGLE_LINE);
			}
		}
	}
	
	@Test
	public void testSuccessfulRoutingActivation() throws IOException, InterruptedException, RoutingActivationFailed {
		
		TesterTcpConnection conn = null;
		
		try {
			if (logger.isInfoEnabled()) {
				logger.info(StringConstants.HASH_LINE);
				logger.info(">>> public void testsuccessfulRoutingActivation()");
			}
			
			// --- TEST CODE BEGIN --------------------------------------------
			conn = testerSetup.createTesterTcpConnection();
			int testerAddress = testerSetup.getConfig().getTesterAddress();

			DoipEventTcpRoutingActivationResponse event = conn.performRoutingActivation(testerAddress, 0);
			assertTrue(event != null, "Response on routing activation was null");
			DoipMessage message = event.getDoipMessage();
			assertTrue(message != null);
			assertTrue(message instanceof DoipTcpRoutingActivationResponse);
			DoipTcpRoutingActivationResponse response = (DoipTcpRoutingActivationResponse) message;
			int code = response.getResponseCode();
			assertEquals(0x10, code);
			// --- TEST CODE END ----------------------------------------------
			
		} catch (Exception e) {
			logger.error("Unexpected " + e.getClass().getName() + " in testSuccessfulRoutingActivation()");
			logger.error(Helper.getExceptionAsString(e));
			throw e;
		} finally {
			if (conn != null) {
				testerSetup.removeDoipTcpConnectionTest(conn);
			}
			if (logger.isInfoEnabled()) {
				logger.info("<<< public void testSuccessfulRoutingActivation()");
				logger.info(StringConstants.HASH_LINE);
			}
		}
	}
	
	@Test
	public void testRoutingActivationNoResponse() throws IOException, InterruptedException {
		TesterTcpConnection conn = null;
		try {
			if (logger.isInfoEnabled()) {
				logger.info(StringConstants.HASH_LINE);
				logger.info(">>> public void testRoutingActivationNoResponse()");
			}
			
			// --- TEST CODE BEGIN --------------------------------------------
			
			conn = testerSetup.createTesterTcpConnection();
			Thread.sleep(10);
			DoipTcpConnection4UnitTest gwconn = gateway.getConnection(0);
			gwconn.setSilent(true);
			final TesterTcpConnection conntmp = conn;
			int testerAddress = testerSetup.getConfig().getTesterAddress();
			assertThrows(RoutingActivationFailed.class, () -> conntmp.performRoutingActivation(testerAddress, 0));
		
			// --- TEST CODE END ----------------------------------------------
			
		} catch (Exception e) {
			logger.error("Unexpected " + e.getClass().getName() + " in testRoutingActivationNoResponse()");
			logger.error(Helper.getExceptionAsString(e));
			throw e;
		} finally {
			if (logger.isInfoEnabled()) {
				logger.info("<<< public void testRoutingActivationNoResponse()");
				logger.info(StringConstants.HASH_LINE);
			}
			
		}
	}
}
