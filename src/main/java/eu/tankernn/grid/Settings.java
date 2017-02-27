package eu.tankernn.grid;

public class Settings {
	public final String portname;
	/**
	 * Names of the profiles of each fan.
	 */
	public final String[] fanProfiles;
	public final int pollingRate, minSpeed;
	public final boolean startMinimized;
	
	public Settings(String portname, String[] fanProfiles, int pollingRate, int minSpeed, boolean startMinimized) {
		this.portname = portname;
		this.fanProfiles = fanProfiles;
		this.pollingRate = pollingRate;
		this.minSpeed = minSpeed;
		this.startMinimized = startMinimized;
	}
}
