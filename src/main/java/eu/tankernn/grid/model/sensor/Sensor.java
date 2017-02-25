package eu.tankernn.grid.model.sensor;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class Sensor {
	OperatingSystemMXBean bean;
	public final int cpuCoreNumber;
	protected Map<String, Double> temperatures = new HashMap<>();
	private List<String> cpuSensors = new ArrayList<>(), gpuSensors = new ArrayList<>();
	
	public Sensor() {
		bean = ManagementFactory.getOperatingSystemMXBean();
		cpuCoreNumber = bean.getAvailableProcessors();
	}
	
	public abstract void poll() throws IOException;
	
	public double getCpuLoad() {
		return bean.getSystemLoadAverage();
	}
	
	public Set<String> getSensorNames() {
		return temperatures.keySet();
	}

	public double getCPUTemp() {
		return getTemp(cpuSensors);
	}

	public double getGPUTemp() {
		return getTemp(gpuSensors);
	}
	
	private double getTemp(List<String> sensorNames) {
		return sensorNames.stream().mapToDouble(temperatures::get).sum() / sensorNames.size();
	}

	public List<String> getCpuSensors() {
		return cpuSensors;
	}

	public void setCpuSensors(List<String> cpuSensors) {
		this.cpuSensors = cpuSensors;
	}

	public List<String> getGpuSensors() {
		return gpuSensors;
	}

	public void setGpuSensors(List<String> gpuSensors) {
		this.gpuSensors = gpuSensors;
	}
}
