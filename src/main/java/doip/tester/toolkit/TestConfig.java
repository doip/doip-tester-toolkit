package doip.tester.toolkit;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import doip.library.properties.EmptyPropertyValue;
import doip.library.properties.MissingProperty;
import doip.library.properties.MissingSystemProperty;
import doip.library.properties.PropertyFile;

public class TestConfig {
	
	private static Logger logger = LogManager.getLogger(TestConfig.class);
	
	private static Marker markerEnter = MarkerManager.getMarker("ENTER");
	private static Marker markerExit  = MarkerManager.getMarker("EXIT");

	public static final String TESTER_CONFIG = "tester.config";
	
	public static final String SELFTEST_CONFIG = "selftest.config";
	
	/**
	 * IP address of the DoIP gateway which shall be tested
	 */
	private InetAddress targetAddress = null;
	
	/**
	 * IP broadcast address to which broadcast messages will be sent to
	 */
	private InetAddress broadcastAddress = null;
	
	/**
	 * Expected VIN of the gateway
	 */
	//byte[] vin = null;
	
	/**
	 * Expected EID of the gateway
	 */
	//byte[] eid = null;
	
	/**
	 * Random class is used to generate random numbers
	 */
	private Random random = new Random();

	/**
	 * Port number of the DoIP gateway (usually 13400)
	 */
	private int targetPort = 0;
	
	/**
	 * Tester address (typically it is 0x0E00)
	 */
	private int testerAddress;
	
	/**
	 * Physical ECU address of ECU which will be tested
	 */
	private int ecuAddressPhysical;
	
	/**
	 * Functional address of the ECUs
	 */
	private int ecuAddressFunctional;
	
	/**
	 * Maximum byte array size for logging. If byte arrays are longer
	 * then the will be truncated and three dots will be added.
	 */
	private int maxByteArraySizeLogging = 0;
	
	
	
//-----------------------------------------------------------------------------	
// Constructor
//-----------------------------------------------------------------------------
	public TestConfig()
			throws IOException, MissingProperty, EmptyPropertyValue, MissingSystemProperty {
		
		logger.trace(markerEnter, ">>> public TestConfig()");
		try {
			String filename = System.getProperty(TestConfig.TESTER_CONFIG);
			if (filename == null) {
				logger.error("Failed to get system property \"" + TestConfig.TESTER_CONFIG + "\"");
				throw logger.throwing(new MissingSystemProperty(TestConfig.TESTER_CONFIG));
			}
			
			logger.info("Reading the test configuration from file '" + filename + "'...");
			
			PropertyFile file = new PropertyFile(filename);
			
			targetAddress = file.getMandatoryPropertyAsInetAddress("target.address");
			logger.info("target.address = " + targetAddress.getHostAddress());
			
			targetPort = file.getMandatoryPropertyAsInt("target.port");
			logger.info("target.port = " + targetPort);
			
			broadcastAddress = file.getMandatoryPropertyAsInetAddress("broadcast.address");
			logger.info("broadcast.address = " + broadcastAddress.getHostAddress());
			
			maxByteArraySizeLogging = file.getMandatoryPropertyAsInt("maxByteArraySizeLogging");
			logger.info("maxByteArraySizeLogging = " + maxByteArraySizeLogging);
			
			//vin = file.getMandatoryPropertyAsByteArray("vin.hex");
			//eid = file.getMandatoryPropertyAsByteArray("eid");
			
			testerAddress = file.getMandatoryPropertyAsInt("tester.address");
			logger.info("tester.Address = 0x" + Integer.toHexString(testerAddress).toUpperCase()); 
			
			ecuAddressPhysical = file.getMandatoryPropertyAsInt("ecu.address.physical");
			logger.info("ecu.address.physical = 0x" + Integer.toHexString(ecuAddressPhysical).toUpperCase()); 
			
			ecuAddressFunctional = file.getMandatoryPropertyAsInt("ecu.address.functional");
			logger.info("ecu.address.functional = 0x" + Integer.toHexString(ecuAddressFunctional).toUpperCase());
			
			logger.info("Reading configuration file finished.");
		} finally {
			logger.trace(markerExit, "<<< public TestConfig()");
		}
	}

//-----------------------------------------------------------------------------	
// Getter & Setter
//-----------------------------------------------------------------------------
	
	public InetAddress getTargetAddress() {
		return targetAddress;
	}
	
	public InetAddress getBroadcastAddress() {
		return broadcastAddress;
	}

	public int getTargetPort() {
		return targetPort;
	}
	
	public int getMaxByteArraySizeLogging() {
		return maxByteArraySizeLogging;
	}
	
	/**
	 * Time to wait for a UDP response after a UDP request has ben sent
	 * @return
	 */
	public int get_A_DoIP_Ctrl() {
		return 2000;
	}
	
	public int get_A_DoIP_Announce_Wait() {
		return random.nextInt(500);
	}
	
	public int get_A_DoIP_Announce_Interval() {
		return 500;
	}
	
	public int get_A_DoIP_Announce_Num() {
		return 3;
	}
	
	/**
	 * This is the time between receipt of the last byte of a DoIP
	 * diagnostic message and the transmission of the confirmation ACK or NACK.
	 * After the timeout has elapsed, the request or the response shall be
	 * considered lost and the request may be repeated.
	 * @return
	 */
	public int get_A_DoIP_Diagnostic_Message() {
		return 2000;
	}
	
	public int get_T_TCP_General_Inactivity() {
		return 300000;
	}
	
	public int get_T_TCP_Initial_Inactivity() {
		return 2000;
	}
	
	public int get_T_TCP_Alive_Check() {
		return 500;
	}
	
	public int get_A_Processing_Time() {
		return 2000;
	}
	
	public int get_A_Vehicle_Discovery_Timer() {
		return 5000;
	}
	
	/*
	public byte[] getVin() {
		return this.vin;
	}
	
	public byte[] getEid() {
		return this.eid;
	}*/

	public int getTesterAddress() {
		return testerAddress;
	}

	public int getEcuAddressPhysical() {
		return ecuAddressPhysical;
	}
	
	public int getEcuAddressFunctional() {
		return ecuAddressFunctional;
	}
	
	public int getRoutingActivationTimeout() {
		return 2000;
	}
}
