package eu.tankernn.grid.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.fazecast.jSerialComm.SerialPort;

import eu.tankernn.grid.FanSpeedProfile;
import eu.tankernn.grid.model.sensor.Sensor;
import eu.tankernn.grid.model.sensor.SensorFactory;

/**
 * This model contains two main data members as well as global configuration
 * fields.
 *
 * @author Frans
 */
public class ComputerModel {

	/**
	 * Maps port names to port objects.
	 */
	private Map<String, SerialPort> portMap = new HashMap<>();

	private Sensor sensor;
	private GRID grid;

	/**
	 * A global minimum percentage. This is used to prevent the controller from
	 * constantly turning the fans on and off.
	 */
	private int minSpeed = 30;

	private List<FanSpeedProfile> defaultProfiles, customProfiles = new ArrayList<>();

	/**
	 * Populates the port map, generates the default speed profiles and
	 * initializes the GRID and sensors.
	 */
	public ComputerModel() {
		scanPorts();
		grid = new GRID();
		defaultProfiles = generateProfiles();

		try {
			sensor = SensorFactory.getSensor();
		} catch (Exception ex) {
			Logger.getLogger(ComputerModel.class.getName()).log(Level.SEVERE, "Failed to init sensor.", ex);
		}
	}

	/**
	 * This method searches for COM ports on the system and saves their
	 * identifiers in the map with their name as key.
	 */
	public void scanPorts() {
		portMap = Arrays.stream(SerialPort.getCommPorts())
				.collect(Collectors.toMap(SerialPort::getSystemPortName, Function.identity()));
	}

	/**
	 * Polls the GRID and the sensor.
	 */
	public void poll() {
		grid.pollFans();

		try {
			sensor.poll();
		} catch (Exception ex) {
			System.out.println("Temperature polling failed");
			ex.printStackTrace();
		}
	}

	private List<FanSpeedProfile> generateProfiles() {
		return IntStream.range(30 / 5, 100 / 5 + 1).map(i -> i * 5)
				.mapToObj(i -> new FanSpeedProfile(i + "%", new int[] { i }))
				.collect(Collectors.toCollection(ArrayList::new));
	}

	/**
	 *
	 * In this method the percentage to sent is computed. The calculation aims
	 * to act as a proportional controller. A later step could be to add an
	 * integral controller to the calculation to get a better calculated fan
	 * curve
	 *
	 */
	public void compute() {
		grid.updateFanSpeeds(getTemp(), minSpeed);
	}

	/**
	 * A getter for the Sensor Object to make the methods of the object
	 * available
	 *
	 * @return the sensor
	 */
	public Sensor getSensor() {
		return sensor;
	}

	/**
	 * A getter for the GRID Object to make the methods of the object available
	 *
	 * @return the grid
	 */
	public GRID getGrid() {
		return grid;
	}

	/**
	 * Connects to the GRID on the port specified.
	 *
	 * @param selectedPort
	 *            The COM port the GRID controller is located at
	 */
	public void setGrid(String selectedPort) {
		if (!portMap.containsKey(selectedPort)) {
			System.err.println("Unable to find port " + selectedPort + ".");
			return;
		}
		grid.getCommunicator().connect(portMap.get(selectedPort));
	}

	public int getMinSpeed() {
		return minSpeed;
	}

	public void setMinSpeed(int minSpeed) {
		this.minSpeed = minSpeed;
	}

	public Map<String, SerialPort> getPortMap() {
		return portMap;
	}

	/**
	 * 
	 * @return The temperature used to calculate fan speeds.
	 */
	public double getTemp() {
		// TODO Make configurable
		return (sensor.getCPUTemp() + sensor.getGPUTemp()) / 2;
	}

	public List<FanSpeedProfile> getProfiles() {
		return Stream.concat(defaultProfiles.stream(), customProfiles.stream()).collect(Collectors.toList());
	}

	public List<FanSpeedProfile> getCustomProfiles() {
		return customProfiles;
	}

	public void setProfiles(List<FanSpeedProfile> profiles) {
		this.defaultProfiles = profiles;
	}

	public void addProfile(FanSpeedProfile profile) {
		customProfiles.add(profile);
	}

	public FanSpeedProfile getProfile(String string) {
		return getProfiles().stream().filter(p -> p.name.equals(string)).findFirst().get();
	}
}
