package camsucks.model;

import static java.lang.Thread.sleep;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import com.fazecast.jSerialComm.SerialPort;

/**
 * This class communicates with a serial com device
 * 
 * @author Henry Poon @ https://blog.henrypoon.com/ Changes by Roel: Removed all
 *         references to the GUI class from Henry Poon's original project.
 *         Changed the event based serial read to a synchronised read after each
 *         write. Added a Buffer for the serial read so that the read data can
 *         easily be accessed by other classes Completely rewritten the
 *         writeData method so that it works with the GRID+ controller Added
 *         some getters and setters for the buffer added a getter for the
 *         portMap so that the controller and view can access it
 */
public class Communicator {

	// for containing the ports that will be found
	private SerialPort[] ports;
	// map the port names to CommPortIdentifiers
	private HashMap<String, SerialPort> portMap = new HashMap<>();
	// this is the object that contains the opened port
	private SerialPort serialPort = null;

	// input and output streams for sending and receiving data
	private InputStream input = null;
	private OutputStream output = null;

	// the timeout value for connecting with the port
	final static int TIMEOUT = 2000;

	// some ascii values for for certain things
	final static int SPACE_ASCII = 32;
	final static int DASH_ASCII = 45;
	final static int NEW_LINE_ASCII = 10;

	// Buffer
	private int leng;
	private byte[] buffer;

	// a string for recording what goes on in the program
	String logText = "";

	// search for all the serial ports
	// pre: none
	// post: adds all the found ports to a combo box on the GUI

	/**
	 *
	 * This method searches for COM ports on the system and saves their
	 * Identifier in the hashmap portMap with their name as key.
	 * 
	 */
	public void searchForPorts() {
		ports = SerialPort.getCommPorts();

		for (SerialPort p : ports) {
			portMap.put(p.getSystemPortName(), p);
		}
	}

	// connect to the selected port in the combo box
	// pre: ports are already found by using the searchForPorts method
	// post: the connected comm port is stored in commPort, otherwise,
	// an exception is generated

	/**
	 * This method opens the COM port with port parameters: Baudrate: 4800;
	 * databits: 8; Stopbit: 1; parity: none;
	 * @param selectedPort 
	 */
	public void connect(String selectedPort) {

		try {
			serialPort = portMap.get(selectedPort);

			serialPort.setBaudRate(4800);
			serialPort.setNumDataBits(8);
			serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
			serialPort.setParity(SerialPort.NO_PARITY);
			serialPort.openPort();
			initIOStream();

			// logging
			logText = selectedPort + " opened successfully.";
			System.out.println(logText);

			// CODE ON SETTING BAUD RATE ETC OMITTED
			// XBEE PAIR ASSUMED TO HAVE SAME SETTINGS ALREADY

			// } catch (PortInUseException e) {
			// logText = selectedPort + " is in use. (" + e.toString() + ")";
			//
			// System.out.println(logText);
		} catch (Exception e) {
			logText = "Failed to open " + selectedPort + "(" + e.toString() + ")";
			System.out.println(logText);
			e.printStackTrace();
		}

	}

	/**
	 * This method initializes the serial IO stream, after the init is complete
	 * the method sends an initialize command to the GRID+ controller
	 * 
	 * @return successful Boolean value which indicates whether the method was
	 *         completed successfully
	 */
	public void initIOStream() {
		input = serialPort.getInputStream();
		output = serialPort.getOutputStream();

		knock();
		if (!ping())
			System.err.println("Device did not respond to ping.");
	}

	private void knock() {
		for (int i = 0; i < 8; i++) {
			writeData(new byte[]{(byte) 0xc0});
		}
	}

	private boolean ping() {
		writeData(new byte[]{(byte) 0xc0});
		return buffer[0] == 0x21;
	}

	/**
	 * This method disconnects the serial communication by first closing the
	 * serialPort and then closing the IOStreams
	 * 
	 * 
	 */
	public void disconnect() {
		if (!serialPort.isOpen())
			return;
		try {
			// serialPort.removeEventListener();
			input.close();
			output.close();
			serialPort.closePort();
			System.out.println("Disconnected.");
		} catch (IOException e) {
			logText = "Failed to close " + serialPort.getSystemPortName() + "(" + e.toString() + ")";
			System.out.println(logText);
		}
	}

	// what happens when data is received
	// pre: serial event is triggered
	// post: processing on the data it reads

	/**
	 * This method reads data from the input stream and puts it in a buffer
	 * after the data is read the method waits 50 msec to make sure a new
	 * command doesn't follow up to quickly This promotes stability in the
	 * program.
	 * @return 
	 * 
	 * 
	 */
	private byte[] serialRead() throws InterruptedException {
		try {
			if (input.available() > 0) {
				try {

					buffer = (new byte[32]);

					leng = input.read(buffer);

					// Debug.
					/*
					 * System.out.println("Length: " + getLeng());
					 * 
					 * for (int itera = 0; itera < leng; itera++) {
					 * System.out.println("byte " + itera + ": " +
					 * Integer.toHexString(getBuffer(itera) & 0xFF)); }
					 */
				} catch (Exception e) {
					logText = "Failed to read data. (" + e.toString() + ")";
					System.out.println(logText);
				}
			} else {
				logText = "Failed to read data. No data to read";
				// System.out.println(logText);
			}

		} catch (IOException e) {
			logText = "Failed to read data. (" + e.toString() + ")";
			System.out.println(logText);
		}
		// This prevents commands from being send too soon
		sleep(50);
		return buffer;
	}

	/**
	 * This method sends a byte array command over the serial communication
	 * afterwards the method calls the read method
	 * 
	 * @param command an array of bytes as a command
	 */
	public byte[] writeData(byte[] command) {
		try {
			output.write(command);
			sleep(50);
			return serialRead();
		} catch (Exception e) {
			logText = "Failed to write data. (" + e.toString() + ")";
			System.out.println(logText);
			return new byte[32];
		}
	}

	/**
	 *
	 * @return The second to last byte of the buffer
	 */
	public byte getSecondToLast() {
		if (leng >= 2) {
			return buffer[leng - 2];
		} else {
			return 0;
		}
	}

	/**
	 *
	 * @return The last byte of the buffer
	 */
	public byte getlast() {
		if (leng >= 2) {
			return buffer[leng - 1];
		} else {
			return 0;
		}
	}
	
	/**
	 * @return the portMap
	 */
	public HashMap<String, SerialPort> getPortMap() {
		return portMap;
	}

	public boolean isConnected() {
		return serialPort.isOpen();
	}

}
