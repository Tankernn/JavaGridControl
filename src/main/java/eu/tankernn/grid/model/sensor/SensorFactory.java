package eu.tankernn.grid.model.sensor;

import org.apache.commons.lang3.SystemUtils;

public class SensorFactory {
	public static Sensor getSensor() {
		if (SystemUtils.IS_OS_WINDOWS) {
			return new WindowsSensor();
		} else if (SystemUtils.IS_OS_LINUX) {
			return new LMSensor();
		} else {
			throw new UnsupportedOperationException(SystemUtils.OS_NAME + " is not a supported operating system.");
		}
	}
}
