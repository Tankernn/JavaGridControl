package eu.tankernn.grid.model.sensor;

import java.io.IOException;

import org.apache.commons.lang3.SystemUtils;

public class SensorFactory {
	public static Sensor getSensor() {
		if (SystemUtils.IS_OS_WINDOWS) {
			return new WindowsSensor();
		} else if (SystemUtils.IS_OS_LINUX) {
			return new LMSensor();
		} else {
			System.err.println(SystemUtils.OS_NAME + " is not a supported operating system.");
			return new Sensor() {
				
				@Override
				public void poll() throws IOException {
					temperatures.put("Fake 0", 0d);
					temperatures.put("Fake 100", 100d);
				}
			};
		}
	}
}
