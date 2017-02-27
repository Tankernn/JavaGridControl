package eu.tankernn.grid.model;

import static java.lang.Thread.sleep;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fazecast.jSerialComm.SerialPort;

public class Communicator {

	private SerialPort serialPort = null;

	private InputStream input = null;
	private OutputStream output = null;

	/**
	 * This method opens the COM port with port parameters: Baudrate: 4800;
	 * databits: 8; Stopbit: 1; parity: none;
	 * 
	 * @param selectedPort
	 */
	public void connect(SerialPort selectedPort) {
		if (selectedPort.equals(serialPort) && isConnected())
			return; // Already connected

		disconnect();
		try {
			serialPort = selectedPort;

			// Open connection
			serialPort.setBaudRate(4800);
			serialPort.setNumDataBits(8);
			serialPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
			serialPort.setParity(SerialPort.NO_PARITY);
			serialPort.openPort();

			input = serialPort.getInputStream();
			output = serialPort.getOutputStream();

			// Test connection (allow a few failures)
			for (int i = 0; i < 8; i++) {
				if (ping()) {
					System.out.println(selectedPort.getSystemPortName() + " opened successfully.");
					return;
				}
			}

			System.err.println("Device did not respond correctly to ping.");
			serialPort.closePort();
		} catch (Exception e) {
			System.out.println("Failed to open " + selectedPort.getSystemPortName() + ".");
			e.printStackTrace();
		}
	}

	/**
	 * Sends test data to the device to check availability.
	 * 
	 * @return If the ping was successful (the device responded correctly)
	 */
	private boolean ping() {
		byte[] buffer = null;
		try {
			buffer = writeData(new byte[] { (byte) 0xc0 });
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return buffer[0] == 0x21;
	}

	/**
	 * This method disconnects the serial communication by first closing the
	 * serialPort and then closing the IOStreams
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
			System.err.println("Failed to close " + serialPort.getSystemPortName() + ".");
			e.printStackTrace();
		}
	}

	/**
	 * Reads a buffer of data from the device.
	 * 
	 * @return The data buffer
	 */
	private byte[] serialRead() {
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
	 * @param command an array of bytes as a command
	 * @return The response data from the device
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public byte[] writeData(byte[] command) throws IOException, InterruptedException {
		sleep(50);
		output.write(command);
		sleep(50);
		return serialRead();
	}

	public boolean isConnected() {
		return serialPort != null && serialPort.isOpen();
	}

	public String getPortName() {
		return serialPort.getSystemPortName();
	}

}
