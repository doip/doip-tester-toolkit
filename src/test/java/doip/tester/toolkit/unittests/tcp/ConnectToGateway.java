package doip.tester.toolkit.unittests.tcp;

import static doip.junit.Assertions.fail;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import doip.library.properties.EmptyPropertyValue;
import doip.library.properties.MissingProperty;
import doip.library.util.Helper;
import doip.library.util.StringConstants;
import doip.logging.LogManager;
import doip.logging.Logger;
import doip.tester.toolkit.TestSetup;
import doip.tester.toolkit.TesterTcpConnection;
import doip.tester.toolkit.gateway4unittest.Gateway4UnitTest;

class ConnectToGateway {

	private static Logger logger = LogManager.getLogger(ConnectToGateway.class);
	

	@Test
	public void test() {
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
			
		} catch (IOException | InterruptedException | MissingProperty | EmptyPropertyValue e) {
			String message = "Unexpected " + e.getClass().getName() + " in test()"; 
			logger.error(message);
			logger.error(Helper.getExceptionAsString(e));
			fail(message);
		} finally {
			if (logger.isInfoEnabled()) {
				logger.info("<<< public void test()");
				logger.info(StringConstants.FENCE);
			}
		}
	}
}
