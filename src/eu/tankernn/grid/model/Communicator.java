package eu.tankernn.grid.model;

import static java.lang.Thread.sleep;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

import com.fazecast.jSerialComm.SerialPort;

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
	 * 
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
			System.out.println(selectedPort + " opened successfully.");
		} catch (Exception e) {
			System.out.println("Failed to open " + selectedPort + "(" + e.toString() + ")");
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
			ping();
		}
	}

	private boolean ping() {
		byte[] buffer = writeData(new byte[] { (byte) 0xc0 });
		return buffer[0] == 0x21;
	}

	/**
	 * This method disconnects the serial communication by first closing the
	 * serialPort and then closing the IOStreams
	 * 
	 */
	public void disconnect() {
		if (serialPort == null || !serialPort.isOpen())
			return;
		try {
			input.close();
			output.close();
			serialPort.closePort();
			System.out.println("Disconnected.");
		} catch (IOException e) {
			System.err.println("Failed to close " + serialPort.getSystemPortName() + "(" + e.toString() + ")");
		}
	}

	/**
	 * This method reads data from the input stream and puts it in a buffer
	 * after the data is read the method waits 50 msec to make sure a new
	 * command doesn't follow up to quickly This promotes stability in the
	 * program.
	 * 
	 * @return
	 * 
	 * 
	 */
	private byte[] serialRead() throws InterruptedException {
		// This prevents commands from being sent too soon
		sleep(50);
		try {
			if (input.available() > 0) {
				byte[] buffer = new byte[input.available()];
				input.read(buffer);
				return buffer;
			} else {
				System.err.println("Failed to read data. No data to read");
			}
		} catch (IOException e) {
			System.err.println("Failed to read data. (" + e.toString() + ")");
		}
		return new byte[32];
	}

	/**
	 * This method sends a byte array command over the serial communication
	 * afterwards the method calls the read method
	 * 
	 * @param command
	 *            an array of bytes as a command
	 */
	public byte[] writeData(byte[] command) {
		try {
			output.write(command);
			sleep(50);
			return serialRead();
		} catch (InterruptedException | IOException e) {
			System.out.println("Failed to write data. (" + e.toString() + ")");
			return new byte[32];
		}
	}

	/**
	 * @return the portMap
	 */
	public HashMap<String, SerialPort> getPortMap() {
		return portMap;
	}

	public boolean isConnected() {
		return serialPort != null && serialPort.isOpen();
	}

	public String getPortName() {
		return serialPort.getSystemPortName();
	}

}
