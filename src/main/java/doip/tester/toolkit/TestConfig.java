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
	byte[] vin = null;
	
	/**
	 * Expected EID of the gateway
	 */
	byte[] eid = null;
	
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
	
	
	private int A_DoIP_Ctrl = 2000;
	
	private int A_DoIP_Announce_Wait = 500;	
	
	private int A_DoIP_Announce_Interval = 500;
	
	private int A_DoIP_Announce_Num = 3;
	
	private int A_DoIP_Diagnostic_Message = 2000;
	
	private int T_TCP_General_Inactivity = 300000;
	
	private int T_TCP_Initial_Inactivity = 2000;
	
	private int T_TCP_Alive_Check = 500;
	
	private int A_Processing_Time = 2000;
	
	private int A_Vehicle_Discovery_Timer = 5000;

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
			
			vin = file.getMandatoryPropertyAsByteArray("vin.hex");
			eid = file.getMandatoryPropertyAsByteArray("eid");
			
			testerAddress = file.getMandatoryPropertyAsInt("tester.address");
			logger.info("tester.Address = 0x" + Integer.toHexString(testerAddress).toUpperCase()); 
			
			ecuAddressPhysical = file.getMandatoryPropertyAsInt("ecu.address.physical");
			logger.info("ecu.address.physical = 0x" + Integer.toHexString(ecuAddressPhysical).toUpperCase()); 
			
			ecuAddressFunctional = file.getMandatoryPropertyAsInt("ecu.address.functional");
			logger.info("ecu.address.functional = 0x" + Integer.toHexString(ecuAddressFunctional).toUpperCase());
			
			A_DoIP_Ctrl = file.getOptionalPropertyAsInt("A_DoIP_Ctrl", 2000);
			A_DoIP_Announce_Wait = file.getOptionalPropertyAsInt("A_DoIP_Announce_Wait", 500);
			A_DoIP_Announce_Interval = file.getOptionalPropertyAsInt("A_DoIP_Announce_Interval", 500);
			A_DoIP_Announce_Num = file.getOptionalPropertyAsInt("A_DoIP_Announce_Num", 3);
			A_DoIP_Diagnostic_Message = file.getOptionalPropertyAsInt("A_DoIP_Diagnostic_Message", 2000);
			T_TCP_Initial_Inactivity = file.getOptionalPropertyAsInt("T_TCP_Initial_Inactivity", 2000);
			T_TCP_General_Inactivity = file.getOptionalPropertyAsInt("T_TCP_General_Inactivity", 300000);
			T_TCP_Alive_Check = file.getOptionalPropertyAsInt("T_TCP_Alive_Check", 500);
			A_Processing_Time = file.getOptionalPropertyAsInt("A_Processing_Time", 2000);
			A_Vehicle_Discovery_Timer = file.getOptionalPropertyAsInt("A_Vehicle_Discovery_Timer", 5000);
			
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
	 * Time to wait for a UDP response after a UDP request has been sent
	 * @return
	 */
	public int get_A_DoIP_Ctrl() {
		return A_DoIP_Ctrl;
	}
	
	public int get_A_DoIP_Announce_Wait() {
		return random.nextInt(A_DoIP_Announce_Wait);
	}
	
	public int get_A_DoIP_Announce_Interval() {
		return A_DoIP_Announce_Interval;
	}
	
	public int get_A_DoIP_Announce_Num() {
		return A_DoIP_Announce_Num;
	}
	
	/**
	 * This is the time between receipt of the last byte of a DoIP
	 * diagnostic message and the transmission of the confirmation ACK or NACK.
	 * After the timeout has elapsed, the request or the response shall be
	 * considered lost and the request may be repeated.
	 * @return
	 */
	public int get_A_DoIP_Diagnostic_Message() {
		return A_DoIP_Diagnostic_Message;
	}
	
	public int get_T_TCP_General_Inactivity() {
		return T_TCP_General_Inactivity;
	}
	
	public int get_T_TCP_Initial_Inactivity() {
		return T_TCP_Initial_Inactivity;
	}
	
	public int get_T_TCP_Alive_Check() {
		return T_TCP_Alive_Check;
	}
	
	public int get_A_Processing_Time() {
		return A_Processing_Time;
	}
	
	public int get_A_Vehicle_Discovery_Timer() {
		return A_Vehicle_Discovery_Timer;
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
