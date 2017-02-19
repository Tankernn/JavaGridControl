package eu.tankernn.grid.model;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This model contains two main data members as well as some data members used
 * to configure the calculations and two checks
 *
 * Along with all the getters and setters this model has two methods used for
 * polling and calculating.
 *
 * @author Roel
 */
public class ComputerModel {

	// The main components of the Computermodel
	private Sensor sensor;
	private GRID grid;

	/**
	 * A global minimum percentage. This is used to prevent the controller from
	 * constantly turning the fans on and off.
	 */
	private int minRPM = 30;

	/**
	 *
	 * All members get initialised here.
	 */
	public ComputerModel() {
		grid = new GRID();

		try {
			sensor = new Sensor();
		} catch (Exception ex) {
			Logger.getLogger(ComputerModel.class.getName()).log(Level.SEVERE, "Failed to init sensor.", ex);
		}
	}

	/**
	 *
	 * Currently only the pollCPUPackageTemp method of the sensor object is
	 * called This can change in the future
	 *
	 */
	public void poll() {
		grid.pollFans();

		try {
			getSensor().pollCPUTemp();
			getSensor().pollCPUMax();
			getSensor().pollGPUTemp();
			getSensor().pollGPUMax();
		} catch (Exception ex) {
			System.out.println("Temperature polling failed");
			ex.printStackTrace();
		}
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
		grid.updateFanSpeeds(getTemp());
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
	 * This setter overwrites the old GRID with a new Object with the
	 * selectedport as the COM port to connect to
	 *
	 * @param selectedPort The com port the GRID controller is located at
	 */
	public void setGrid(String selectedPort) {
		grid.getCommunicator().connect(selectedPort);
	}

	/**
	 * @return the minRPM
	 */
	public double getMinRPM() {
		return minRPM;
	}

	/**
	 *
	 * @param minRPM
	 */
	public void setMinRPM(int minRPM) {
		this.minRPM = minRPM;
	}
	
	/**
	 * 
	 * @return The temperature used to calculate fan speeds.
	 */
	public double getTemp() {
		// TODO Calculate temp based on CPU/GPU
		return 0;
	}
}
