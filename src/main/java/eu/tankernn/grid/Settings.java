package eu.tankernn.grid;

public class Settings {
	public final String portname;
	/**
	 * Names of the profiles of each fan.
	 */
	public final String[] fanProfiles, cpuSensors, gpuSensors;
	public final int pollingRate, minSpeed;
	public final boolean startMinimized;

	public Settings(String portname, String[] fanProfiles, String[] cpuSensors, String[] gpuSensors, int pollingRate,
			int minSpeed, boolean startMinimized) {
		this.portname = portname;
		this.fanProfiles = fanProfiles;
		this.cpuSensors = cpuSensors;
		this.gpuSensors = gpuSensors;
		this.pollingRate = pollingRate;
		this.minSpeed = minSpeed;
		this.startMinimized = startMinimized;
	}

}
