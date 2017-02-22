package eu.tankernn.grid.model.sensor;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

public abstract class Sensor {
	OperatingSystemMXBean bean;
	public final int cpuCoreNumber;
	
	public Sensor() {
		bean = ManagementFactory.getOperatingSystemMXBean();
		cpuCoreNumber = bean.getAvailableProcessors();
	}
	
	public abstract void poll() throws IOException;
	
	public double getCpuLoad() {
		return bean.getSystemLoadAverage();
	}

	public abstract double getCPUTemp();

	public abstract double getGPUTemp();
}
