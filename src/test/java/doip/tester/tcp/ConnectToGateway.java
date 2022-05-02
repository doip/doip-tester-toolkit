package doip.tester.tcp;

import org.junit.jupiter.api.Test;

import doip.library.util.Helper;
import doip.library.util.StringConstants;
import doip.logging.LogManager;
import doip.logging.Logger;
import doip.tester.gateway.Gateway4UnitTest;
import doip.tester.utils.TestSetup;
import doip.tester.utils.TesterTcpConnection;

class ConnectToGateway {

	private static Logger logger = LogManager.getLogger(ConnectToGateway.class);
	

	@Test
	public void test() throws Exception {
		try {
			if (logger.isInfoEnabled()) {
				logger.info(StringConstants.FENCE);
				logger.info(">>> public void test()");
			}
			
			// --- TEST CODE BEGIN --------------------------------------------

			Gateway4UnitTest gateway = new Gateway4UnitTest();
			logger.info("Start Gateway4UnitTest");
			gateway.start();
			logger.info("Gateway4UnitTest started");
			Thread.sleep(100);
			TestSetup setup = new TestSetup();
			logger.info("Initialize TesterSetup");
			setup.initialize("src/test/resources/tester.properties");
			logger.info("TesterSetup initialized");
			
			logger.info("Establish connection to Gateway4UnitTest");
			TesterTcpConnection conn =  setup.createTesterTcpConnection();
			logger.info("Connection to Gateway4UnitTest established");
			
			Thread.sleep(100);
			logger.info("Close connection to Gateway4UnitTest");
			conn.stop();
			Thread.sleep(100);
			logger.info("Connection to Gateway closed");

			logger.info("Uninitialize TesterSetup");
			setup.uninitialize();
			logger.info("TesterSetup uninitialized");
			
			logger.info("Stop Gateway4UnitTest");
			gateway.stop();
			logger.info("Gateway4UnitTest stopped");
			
			// --- TEST CODE END ----------------------------------------------
			
		} catch (Exception e) {
			logger.error("Unexpected " + e.getClass().getName() + " in test()");
			logger.error(Helper.getExceptionAsString(e));
			throw e;
		} finally {
			if (logger.isInfoEnabled()) {
				logger.info("<<< public void test()");
				logger.info(StringConstants.FENCE);
			}
		}
	}
}
