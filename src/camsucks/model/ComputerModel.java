package camsucks.model;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is the model of this project
 *
 * This model contains two main data members as well as some data members used
 * to configure the calculations and two checks
 *
 * Along with all the getters and setters this model has two methods used for
 * polling and calculating
 *
 * @author Roel
 */
public class ComputerModel {

	// The main components of the Computermodel
	private Sensor sensor;
	private GRID grid;

	// The variables used to calculate the percentage to send
	private double maxCPUTemp;
	private double maxGPUTemp;
	private double targetCPUTemp;
	private double targetGPUTemp;
	private double targetRPM;
	// a global minimum percentage, this is used to prevent the controller to
	// constantly turn the fans on and off
	private int minRPM;

	// Boolean Checks used to know whether to run certain pieces of code
	private boolean manual;

	/**
	 *
	 * All members get initialised here.
	 *
	 * @param selectedPort
	 */
	public ComputerModel(String selectedPort) {
		targetRPM = 35;
		targetCPUTemp = 50;
		maxCPUTemp = 80;
		targetGPUTemp = 50;
		maxGPUTemp = 80;
		minRPM = 20;
		manual = false;

		grid = new GRID(selectedPort);

		try {
			sensor = new Sensor();
		} catch (Exception ex) {
			Logger.getLogger(ComputerModel.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 *
	 * Currently only the pollCPUPackageTemp method of the sensor object is
	 * called This can change in the future
	 *
	 */
	public void poll() {
		System.out.println("Polling...");

		if (!getGrid().isSame()) {
			for (int i = 0; i < 6; i++) {
				getGrid().pollVoltage(i);
				getGrid().pollFanAMP(i);
				getGrid().pollFanRPM(i);
			}
		}

		try {
			getSensor().pollCPUTemp();
			getSensor().pollCPUMax();
			getSensor().pollGPUTemp();
			getSensor().pollGPUMax();

		} catch (Exception ex) {
			System.out.println("Polling Failed");
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
		double currentCPUTemp = getSensor().getCPUTemp();

		double CPUerror = (currentCPUTemp - getTargetCPUTemp());

		double CPUkFactor = ((100 - getTargetRPM()) / (getMaxCPUTemp() - getTargetCPUTemp()));

		int tempCPUPercentage = (int) (getTargetRPM() + (CPUkFactor * CPUerror));

		if (tempCPUPercentage < minRPM) {
			tempCPUPercentage = minRPM;
		}

		double currentGPUTemp = getSensor().getGPUTemp();

		double GPUerror = (currentGPUTemp - getTargetGPUTemp());

		double GPUkFactor = ((100 - getTargetRPM()) / (getMaxGPUTemp() - getTargetGPUTemp()));

		int tempGPUPercentage = (int) (getTargetRPM() + (GPUkFactor * GPUerror));

		if (tempGPUPercentage < minRPM) {
			tempGPUPercentage = minRPM;
		}
		
		updateFanSpeed(Math.max(tempCPUPercentage, tempGPUPercentage));
	}

	public void updateFanSpeed(int percentage) {
		for (int i = 0; i < 6; i++)
			getGrid().setFanSpeed(i, percentage);
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
		grid = new GRID(selectedPort);
	}

	/**
	 *
	 * @return the targetRPM
	 */
	public double getTargetRPM() {
		return targetRPM;
	}

	/**
	 * @param targetRPM the targetRPM to set
	 */
	public void setTargetRPM(double targetRPM) {
		this.targetRPM = targetRPM;
	}

	/**
	 * @return the targetTemp
	 */
	public double getTargetCPUTemp() {
		return targetCPUTemp;
	}

	/**
	 * @param targetTemp the targetTemp to set
	 */
	public void setTargetCPUTemp(double targetTemp) {
		this.targetCPUTemp = targetTemp;
	}

	/**
	 * @return the maxTemp
	 */
	public double getMaxCPUTemp() {
		return maxCPUTemp;
	}

	/**
	 * @param maxTemp the maxTemp to set
	 */
	public void setMaxCPUTemp(double maxTemp) {
		this.maxCPUTemp = maxTemp;
	}

	/**
	 * @return the targetTemp
	 */
	public double getTargetGPUTemp() {
		return targetGPUTemp;
	}

	/**
	 * @param targetTemp the targetTemp to set
	 */
	public void setTargetGPUTemp(double targetTemp) {
		this.targetGPUTemp = targetTemp;
	}

	/**
	 * @return the maxTemp
	 */
	public double getMaxGPUTemp() {
		return maxGPUTemp;
	}

	/**
	 * @param maxTemp the maxTemp to set
	 */
	public void setMaxGPUTemp(double maxTemp) {
		this.maxGPUTemp = maxTemp;
	}

	/**
	 * A check to see whether the percentage to send is manually set or has to
	 * be calculated
	 *
	 * @return the manual
	 */
	public boolean isManual() {
		return manual;
	}

	/**
	 * @param manual the manual to set
	 */
	public void setManual(boolean manual) {
		this.manual = manual;
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
}
