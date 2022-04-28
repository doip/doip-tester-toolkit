package doip.tester;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import doip.library.comm.DoipTcpConnection;
import doip.library.util.Helper;
import doip.library.util.StringConstants;
import doip.logging.LogManager;
import doip.logging.Logger;
import doip.tester.gateway.Gateway4UnitTest;
import doip.tester.utils.DoipTcpConnectionWithEventCollection;
import doip.tester.utils.TestSetup;
import doip.tester.utils.TesterTcpConnection;

class TestTcpRoutingActivation {
	
	private static Logger logger = LogManager.getLogger(TestTcpRoutingActivation.class);
	
	private static Gateway4UnitTest gateway = null;
	
	private static TestSetup testerSetup = null;
	
	@BeforeAll
	public static void setUpBeforeClass() throws Exception {
		
		try {
			if (logger.isInfoEnabled()) {
				logger.info(StringConstants.LINE);
				logger.info(">>> public static void setUpBeforeClass() throws Exception");
			}

			gateway = new Gateway4UnitTest();
			gateway.start();
			
			testerSetup = new TestSetup();
			testerSetup.initialize("src/test/resources/tester.properties");
			
			
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
				logger.info("<<< public static void tearDownAfterClass() throws Exception");
				logger.info(StringConstants.LINE);
			}
		}
	}
	
	@Test
	@Disabled
	public void test() throws IOException, InterruptedException {
		
		TesterTcpConnection conn = null;
		
		try {
			if (logger.isInfoEnabled()) {
				logger.info(StringConstants.FENCE);
				logger.info(">>> public void test()");
			}
			
			// --- TEST CODE BEGIN --------------------------------------------
			conn = testerSetup.createTesterTcpConnection();
			boolean ret = conn.performRoutingActivation(0, 0x10);
			assertTrue(ret, "Routing activation failed");
		
			// --- TEST CODE END ----------------------------------------------
			
		} catch (Exception e) {
			logger.error("Unexpected " + e.getClass().getName() + " in test()");
			logger.error(Helper.getExceptionAsString(e));
			throw e;
		} finally {
			if (conn != null) {
				conn.stop();
				conn = null;
			}
			if (logger.isInfoEnabled()) {
				logger.info("<<< public void test()");
				logger.info(StringConstants.FENCE);
			}
		}
	}
}
