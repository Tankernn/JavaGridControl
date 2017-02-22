package eu.tankernn.grid.model.sensor;

import java.io.IOException;

/**
 *
 * This class is a model for a collection of sensors It contains a datamembers
 * for some specific sensors as well as members for lists of sensors of a
 * specific type. each member has a getter but no setter. Instead of the setter
 * the members (except for the lists) have a poll function which polls the value
 * of the member using the jWMI class
 * 
 * @author Roel
 */
public class WindowsSensor extends Sensor {

	private double cpuPackageTemp;
	private double gpuTemp;

	public void poll() throws IOException {
		pollCPUTemp();
		pollGPUTemp();
	}

	/**
	 * This method polls the value of the GPU Temperature sensor
	 * 
	 * @throws Exception
	 */
	public void pollGPUTemp() throws IOException {
		gpuTemp = Double.parseDouble(jWMI.getWMIValue("Temperature", "GPU Core"));
	}

	/**
	 * This method polls the value of the CPU Temperature sensor
	 * 
	 * @throws Exception
	 */
	public void pollCPUTemp() throws IOException {
		cpuPackageTemp = Double.parseDouble(jWMI.getWMIValue("Temperature", "CPU Package"));
	}

	public double getCPUTemp() {
		return cpuPackageTemp;
	}

	public double getGPUTemp() {
		return gpuTemp;
	}

}
