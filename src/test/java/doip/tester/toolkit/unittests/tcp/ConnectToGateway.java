package doip.tester.toolkit.unittests.tcp;

import static doip.junit.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import doip.junit.InitializationError;
import doip.library.properties.MissingProperty;
import doip.library.properties.MissingSystemProperty;
import doip.library.util.Helper;
import doip.library.util.StringConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import doip.tester.toolkit.TestSetup;
import doip.tester.toolkit.TesterTcpConnection;
import doip.tester.toolkit.server4unittest.DoipServer4UnitTest;

class ConnectToGateway {

	private static Logger logger = LogManager.getLogger(ConnectToGateway.class);
	

	@Test
	public void test() throws Exception {
		try {
			if (logger.isInfoEnabled()) {
				logger.info(StringConstants.HASH_LINE);
				logger.info(">>> public void test()");
			}
			
			// --- TEST CODE BEGIN --------------------------------------------

			logger.info("Create instance of Gateway4UnitTest");
			DoipServer4UnitTest gateway = new DoipServer4UnitTest();
			logger.info("Instance of Gateway4UnitTest created");
			
			logger.info("Start Gateway4UnitTest");
			gateway.start();
			logger.info("Gateway4UnitTest started");
			
			logger.info("Wait 100 ms to give gateway time to start up");
			Thread.sleep(100);
			
			logger.info("Create instance of TestSetup");
			TestSetup setup = new TestSetup();
			logger.info("Instance of TestSetup created");
			
			logger.info("Initialize TestSetup using file src/test/resources/tester.properties");
			setup.initialize();
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
			
		} catch (MissingSystemProperty e) {
			throw logger.throwing(e);
		} catch (InterruptedException e) {
			throw logger.throwing(e);
		} finally {
			if (logger.isInfoEnabled()) {
				logger.info("<<< public void test()");
				logger.info(StringConstants.HASH_LINE);
			}
		}
	}
}
