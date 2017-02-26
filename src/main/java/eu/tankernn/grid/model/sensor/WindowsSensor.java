package eu.tankernn.grid.model.sensor;

import java.io.IOException;

/**
 *
 * Sensor for Windows systems. Uses the jWMI class to get all temperature
 * readings from the OpenHardwareMonitor instance.
 * 
 * @author Frans
 */
public class WindowsSensor extends Sensor {
	public void poll() throws IOException {
		String[] sensors = jWMI.getWMISensorList("Temperature").split(",");
		for (String s : sensors)
			temperatures.put(s, Double.parseDouble(jWMI.getWMIValue("Temperature", s)));
	}
}
