package eu.tankernn.grid;

import eu.tankernn.grid.model.GRID;

public class Fan {
	private double voltage, current;
	private int rpm, index;
	private GRID grid;
	private FanSpeedProfile profile;
	private int speed;

	public Fan(GRID grid, int index) {
		this.grid = grid;
		this.index = index;
	}

	public void update(double temp) {
		if (profile != null)
			this.speed = profile.getSpeedPercentage(temp);
		
		setFanSpeed(speed);
	}

	public void poll() {
		pollFanAMP();
		pollFanRPM();
		pollVoltage();
	}

	/**
	 * This method polls the Fan Amperage of the fan with index fan it first
	 * send the poll command and reads the byte data from the buffer this byte
	 * data is then converted to a double
	 * 
	 * @param fan the index of the fan
	 */
	public void pollFanAMP() {
		if (grid.getCommunicator().isConnected()) {
			// 0x85 = -123
			byte[] command = { -123, (byte) (index + 1) };

			grid.getCommunicator().writeData(command);

			current = (double) (((int) grid.getCommunicator().getSecondToLast() & 0xFF) + (((double) (grid.getCommunicator().getlast() & 0xff) / 100)));
		} else {
			current = 0d;
		}
	}

	/**
	 * This method polls the Fan RPM of the fan with index fan it first send the
	 * poll command and reads the byte data from the buffer this byte data is
	 * then converted to an int
	 * 
	 * @param fan the index of the fan
	 */
	public void pollFanRPM() {
		if (grid.getCommunicator().isConnected()) {
			// 0x8A = -118
			byte[] command = { -118, (byte) (index + 1) };

			grid.getCommunicator().writeData(command);

			rpm = (((int) (grid.getCommunicator().getSecondToLast() & 0xFF) << 8) | ((grid.getCommunicator().getlast() & 0xFF)));
		} else {
			rpm = 0;

		}
	}

	/**
	 * This method polls the voltage of all the fans it first send the poll
	 * command and reads the byte data from the buffer this byte data is then
	 * converted to a double
	 */
	public void pollVoltage() {
		if (grid.getCommunicator().isConnected()) {
			// 0x84 = -124
			byte[] command = { -124, (byte) (index + 1) };

			grid.getCommunicator().writeData(command);

			voltage = (double) (((int) grid.getCommunicator().getSecondToLast() & 0xFF) + (((double) (grid.getCommunicator().getlast() & 0xff) / 100)));
		} else {
			voltage = 0d;
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
	public void setFanSpeed(int percent) {
		if (grid.getCommunicator().isConnected()) {
			int firstByte, lastByte, wantedVoltage = 0;

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
			
			if (wantedVoltage == voltage)
				return;

			byte[] command = { 0x44, (byte) (index + 1), -64, 0x00, 0x00, (byte) firstByte, (byte) lastByte };

			grid.getCommunicator().writeData(command);

		}

	}

	/**
	 * @return the voltage
	 */
	public double getVoltage() {
		return voltage;
	}

	/**
	 * @return the fanRPM
	 */
	public int getRPM() {
		return rpm;
	}

	/**
	 * @return the fanAMP
	 */
	public double getAMP() {
		return current;
	}

	public double getWattage() {
		return voltage * current;
	}

	public int getIndex() {
		return index;
	}
}
