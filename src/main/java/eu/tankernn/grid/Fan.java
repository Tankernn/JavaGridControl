package eu.tankernn.grid;

import java.io.IOException;
import java.util.function.BiFunction;

import eu.tankernn.grid.model.Communicator;

public class Fan {
	private double voltage, current;
	private int rpm, index;
	private Communicator communicator;
	private FanSpeedProfile profile;
	private int speed = 0;

	public Fan(Communicator communicator, int index) {
		this.communicator = communicator;
		this.index = index;
		try {
			poll();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.speed = (int) (100 * voltage / 12);
	}

	public void update(double cpuTemp, double gpuTemp, int minSpeed) {
		int calcSpeed = profile.getSpeedPercentage(cpuTemp, gpuTemp);
		try {
			setFanSpeed(calcSpeed < minSpeed ? 0 : calcSpeed);
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void poll() throws IOException, InterruptedException {
		pollAMP();
		pollRPM();
		pollVoltage();
	}

	/**
	 * This method polls the Fan Amperage of the fan with index fan it first
	 * send the poll command and reads the byte data from the buffer this byte
	 * data is then converted to a double
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	private void pollAMP() throws IOException, InterruptedException {
		current = pollValue((byte) 0x85, (a, b) -> a + (double) b / 100);
	}

	/**
	 * This method polls the Fan RPM of the fan with index fan it first send the
	 * poll command and reads the byte data from the buffer this byte data is
	 * then converted to an int
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	private void pollRPM() throws IOException, InterruptedException {
		rpm = pollValue((byte) 0x8A, (a, b) -> (double) ((a << 8) | b)).intValue();
	}

	/**
	 * This method polls the voltage of all the fans it first send the poll
	 * command and reads the byte data from the buffer this byte data is then
	 * converted to a double
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	private void pollVoltage() throws IOException, InterruptedException {
		voltage = pollValue((byte) 0x84, (a, b) -> a + (double) b / 100);
	}

	private Double pollValue(byte commandByte, BiFunction<Integer, Integer, Double> resultConsumer) throws IOException, InterruptedException {
		if (communicator.isConnected()) {
			byte[] command = { commandByte, (byte) (index + 1) };

			byte[] response = communicator.writeData(command);

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
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	public void setFanSpeed(int newSpeed) throws IOException, InterruptedException {
		if (newSpeed == speed)
			return;
		// Spin up to 100 during first tick after being turned off
		else if (speed == 0)
			newSpeed = 100;
		if (communicator.isConnected()) {
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

			communicator.writeData(command);
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
