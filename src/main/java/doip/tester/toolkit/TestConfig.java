package doip.tester.toolkit;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Random;

import doip.library.properties.EmptyPropertyValue;
import doip.library.properties.MissingProperty;
import doip.library.properties.PropertyFile;

public class TestConfig {

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
	
	
	
//-----------------------------------------------------------------------------	
// Constructor
//-----------------------------------------------------------------------------
	public TestConfig(String filename) 
			throws IOException, MissingProperty, EmptyPropertyValue {
		
		PropertyFile file = new PropertyFile(filename);
		targetAddress = file.getMandatoryPropertyAsInetAddress("target.address");
		targetPort = file.getMandatoryPropertyAsInt("target.port");
		broadcastAddress = file.getMandatoryPropertyAsInetAddress("broadcast.address");
		maxByteArraySizeLogging = file.getMandatoryPropertyAsInt("maxByteArraySizeLogging");
		vin = file.getMandatoryPropertyAsByteArray("vin.hex");
		eid = file.getMandatoryPropertyAsByteArray("eid");
		testerAddress = file.getMandatoryPropertyAsInt("tester.address");
		ecuAddressPhysical = file.getMandatoryPropertyAsInt("ecu.address.physical");
		ecuAddressFunctional = file.getMandatoryPropertyAsInt("ecu.address.functional");
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
	
	public byte[] getVin() {
		return this.vin;
	}
	
	public byte[] getEid() {
		return this.eid;
	}

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
