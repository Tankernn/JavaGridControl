package camsucks.model;

/**
 *
 * This is a model for the GRID controller This class uses the communicator
 * class to communicate to the GRID+ controller This class contains a few data
 * members that contain data that can be gathered from the GRID+ This class has
 * some getters for these members To update their values they each have poll
 * functions instead of setters which send a command to the GRID and read the
 * response To set the fan speed this class has a setFanSpeed method which sets
 * the voltage of the GRID+ according to a percentage input.
 * 
 * This class also has a boolean check that checks if the voltage command is the
 * same as the previous voltage command, this is to prevent pointless serial
 * communication This check also has a getter to make it usable for other
 * classes
 * 
 * @author Roel
 */
public class GRID {

	private double[] voltage = new double[6];

	private int[] fanRPM = new int[6];

	private double[] fanAMP = new double[6];

	private double totalWattage = 0;

	private byte[] lastCommand = new byte[6];

	private Communicator communicator;

	private boolean same;

	/**
	 * This constructor initialises all members afterwards it opens a
	 * communicator at the selected port
	 * 
	 * @param selectedPort the com port to connect to
	 */
	public GRID(String selectedPort) {
		lastCommand[4] = 1;
		lastCommand[5] = 2;

		communicator = new Communicator();
		communicator.searchForPorts();
		communicator.connect(selectedPort);
	}

	/**
	 * This method simply runs the disconnect method of the communicator
	 */
	public void disconnect() {
		getCommunicator().disconnect();
	}

	/**
	 * @return the voltage
	 */
	public double[] getVoltage() {
		return voltage;
	}

	/**
	 * @return the fanRPM
	 */
	public int[] getFanRPM() {
		return fanRPM;
	}

	/**
	 * @return the fanAMP
	 */
	public double[] getFanAMP() {
		return fanAMP;
	}

	/**
	 * @return the voltage
	 */
	public double getVoltage(int fan) {
		return voltage[fan];
	}

	/**
	 * @param fan the index of the fan
	 * @return the fanRPM of the fan with index fan
	 */
	public int getRPM(int fan) {
		return fanRPM[fan];
	}

	/**
	 * @param fan the index of the fan
	 * @return the fanAMP of the fan with index fan
	 */
	public double getAMP(int fan) {
		return fanAMP[fan];
	}

	/**
	 * This method polls the Fan Amperage of the fan with index fan it first
	 * send the poll command and reads the byte data from the buffer this byte
	 * data is then converted to a double
	 * 
	 * @param fan the index of the fan
	 */
	public void pollFanAMP(int fan) {
		if (getCommunicator().isConnected()) {
			// 0x85 = -123
			byte[] command = { -123, (byte) (fan + 1) };

			getCommunicator().writeData(command);

			fanAMP[fan] = (double) (((int) getCommunicator().getSecondToLast() & 0xFF) + (((double) (getCommunicator().getlast() & 0xff) / 100)));
		} else {
			fanAMP[fan] = 0d;
		}
	}

	/**
	 * This method polls the Fan RPM of the fan with index fan it first send the
	 * poll command and reads the byte data from the buffer this byte data is
	 * then converted to an int
	 * 
	 * @param fan the index of the fan
	 */
	public void pollFanRPM(int fan) {
		if (getCommunicator().isConnected()) {
			// 0x8A = -118
			byte[] command = { -118, (byte) (fan + 1) };

			getCommunicator().writeData(command);

			fanRPM[fan] = (((int) (getCommunicator().getSecondToLast() & 0xFF) << 8) | ((getCommunicator().getlast() & 0xFF)));
		} else {
			fanRPM[fan] = 0;

		}
	}

	/**
	 * This method polls the voltage of all the fans it first send the poll
	 * command and reads the byte data from the buffer this byte data is then
	 * converted to a double
	 */
	public void pollVoltage(int fan) {
		if (getCommunicator().isConnected()) {
			// 0x84 = -124
			byte[] command = { -124, (byte) (fan + 1) };

			getCommunicator().writeData(command);

			voltage[fan] = (double) (((int) getCommunicator().getSecondToLast() & 0xFF) + (((double) (getCommunicator().getlast() & 0xff) / 100)));
		} else {
			voltage[fan] = 0d;
		}
	}

	/**
	 * this method calculates the voltage to set the fans at according to a
	 * percentage of the maximum voltage 12V then it send the command to set
	 * that voltage the voltages between 0 and 4 are not recognised so these are
	 * converted to 4V the comma value of the commands are always rounded to .50
	 * and .0
	 * 
	 * @param percent
	 */
	public void setFanSpeed(int fan, int percent) {

		if (getCommunicator().isConnected()) {
			int firstByte, lastByte, wantedVoltage;

			// The voltages between 0 and 4 are not recognised by the grid so
			// any voltage under 4 will still be 4 and from 0 it will be 0
			if (percent <= 0) {
				firstByte = 0;
				lastByte = 0;

			} else if (percent < 34) {
				firstByte = 4;
				lastByte = 0;
			} else {

				wantedVoltage = (1200 * percent) / 100;
				firstByte = wantedVoltage / 100;
				lastByte = (wantedVoltage - (firstByte * 100));

				if (lastByte < 50) {
					lastByte = 0x00;
				} else {
					lastByte = 0x50;
				}

			}

			byte[] command = { 0x44, (byte) (fan + 1), -64, 0x00, 0x00, (byte) firstByte, (byte) lastByte };

			if ((lastCommand[5] != command[5] || lastCommand[6] != command[6])) {
				getCommunicator().writeData(command);
				lastCommand = command;
				setSame(false);
			} else {
				setSame(true);
			}

		}

	}

	/**
	 * @return the communicator
	 */
	public Communicator getCommunicator() {
		return communicator;
	}

	/**
	 * 
	 * @return A boolean value that is true if the last command is the same as
	 *         the new
	 */
	public boolean isSame() {
		return same;
	}

	/**
	 * @param same boolean value that is true if the last command is the same as
	 *        the new
	 */
	public void setSame(boolean same) {
		this.same = same;
	}

	/**
	 * @return the totalWattage
	 */
	public double getTotalWattage() {
		totalWattage = 0;
		for (int i = 0; i < fanAMP.length; i++) {
			totalWattage += getAMP(i) * getVoltage(i);
		}

		return totalWattage;
	}

}
