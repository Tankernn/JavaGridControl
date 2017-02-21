package eu.tankernn.grid;

import java.util.function.BiFunction;

import eu.tankernn.grid.model.GRID;

public class Fan {
	private double voltage, current;
	private int rpm, index;
	private GRID grid;
	private FanSpeedProfile profile;
	private int speed = 0;

	public Fan(GRID grid, int index) {
		this.grid = grid;
		this.index = index;
		poll();
		this.speed = (int) (100 * voltage / 12);
	}

	public void update(double temp, int minSpeed) {
		int calcSpeed = profile.getSpeedPercentage(temp);
		setFanSpeed(calcSpeed < minSpeed ? 0 : calcSpeed);
	}

	public void poll() {
		pollAMP();
		pollRPM();
		pollVoltage();
	}

	/**
	 * This method polls the Fan Amperage of the fan with index fan it first
	 * send the poll command and reads the byte data from the buffer this byte
	 * data is then converted to a double
	 */
	private void pollAMP() {
		current = pollValue((byte) 0x85, (a, b) -> a + (double) b / 100);
	}

	/**
	 * This method polls the Fan RPM of the fan with index fan it first send the
	 * poll command and reads the byte data from the buffer this byte data is
	 * then converted to an int
	 */
	private void pollRPM() {
		rpm = pollValue((byte) 0x8A, (a, b) -> (double) ((a << 8) | b)).intValue();
	}

	/**
	 * This method polls the voltage of all the fans it first send the poll
	 * command and reads the byte data from the buffer this byte data is then
	 * converted to a double
	 */
	private void pollVoltage() {
		voltage = pollValue((byte) 0x84, (a, b) -> a + (double) b / 100);
	}

	private Double pollValue(byte commandByte, BiFunction<Integer, Integer, Double> resultConsumer) {
		if (grid.getCommunicator().isConnected()) {
			byte[] command = { commandByte, (byte) (index + 1) };

			byte[] response = grid.getCommunicator().writeData(command);

			return resultConsumer.apply((response[response.length - 2] & 0xFF),
					(response[response.length - 1] & 0xff));
		} else {
			return 0d;
		}
	}

	/**
	 * this method calculates the voltage to set the fans at according to a
	 * percentage of the maximum voltage 12V then it send the command to set
	 * that voltage the voltages between 0 and 4 are not recognised so these are
	 * converted to 4V the comma value of the commands are always rounded to .50
	 * and .0
	 * 
	 * @param newSpeed
	 */
	public void setFanSpeed(int newSpeed) {
		if (newSpeed == speed)
			return;
		// Spin up to 100 during first tick after being turned off
		else if (speed == 0)
			newSpeed = 100;
		if (grid.getCommunicator().isConnected()) {
			int firstByte, lastByte, wantedVoltage = 0;

			// The voltages between 0 and 4 are not recognised by the grid so
			// any voltage under 4 will still be 4 and from 0 it will be 0
			if (newSpeed <= 0) {
				firstByte = 0;
				lastByte = 0;

			} else if (newSpeed < 34) {
				firstByte = 4;
				lastByte = 0;
			} else {
				wantedVoltage = 1200 * newSpeed / 100;
				firstByte = wantedVoltage / 100;
				lastByte = (wantedVoltage - (firstByte * 100));

				if (lastByte < 50) {
					lastByte = 0x00;
				} else {
					lastByte = 0x50;
				}
			}

			byte[] command = { 0x44, (byte) (index + 1), -64, 0x00, 0x00, (byte) firstByte, (byte) lastByte };

			grid.getCommunicator().writeData(command);
			speed = newSpeed;
		}
	}

	public void setProfile(FanSpeedProfile profile) {
		this.profile = profile;
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

	public FanSpeedProfile getProfile() {
		return profile;
	}
}
