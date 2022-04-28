package doip.tester;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import doip.library.util.Helper;
import doip.library.util.StringConstants;
import doip.logging.LogManager;
import doip.logging.Logger;
import doip.tester.gateway.Gateway4UnitTest;

public class TestTcpDiagnosticMessage {

	private static Logger logger = LogManager.getLogger(TestTcpDiagnosticMessage.class);
	
	private static Gateway4UnitTest gateway = null;

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

	//@Test
	public void test() {
		try {
			if (logger.isInfoEnabled()) {
				logger.info(StringConstants.WALL);
				logger.info(">>> public void test()");
			}
			
			// --- TEST CODE BEGIN --------------------------------------------
			
			// --- TEST CODE END ----------------------------------------------
			
		} catch (Exception e) {
			logger.error("Unexpected " + e.getClass().getName() + " in test()");
			logger.error(Helper.getExceptionAsString(e));
			throw e;
		} finally {
			if (logger.isInfoEnabled()) {
				logger.info("<<< public void test()");
				logger.info(StringConstants.WALL);
			}
		}
	}
}
