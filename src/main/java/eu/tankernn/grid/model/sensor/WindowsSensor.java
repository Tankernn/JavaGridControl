package eu.tankernn.grid.model.sensor;

import java.io.IOException;
import java.util.Arrays;

/**
 *
 * Sensor for Windows systems. Uses the jWMI class to get all temperature
 * readings from the OpenHardwareMonitor instance.
 * 
 * @author Frans
 */
public class WindowsSensor extends Sensor {
	public void poll() throws IOException {
		String sensorList = jWMI.getWMISensorList("Temperature");
		String[] sensors = sensorList.split(", ");
		System.out.println(Arrays.toString(sensors));
		for (String s : sensors) {
			String value = jWMI.getWMIValue("Temperature", s);
			if (!value.isEmpty())
				temperatures.put(s, Double.parseDouble(value));
		}
		System.out.println(temperatures.toString());
	}
}
