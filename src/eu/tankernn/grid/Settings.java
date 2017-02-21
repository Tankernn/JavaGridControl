package eu.tankernn.grid;

public class Settings {
	public final String portname;
	/**
	 * Names of the profiles of each fan.
	 */
	public final String[] fanProfiles;
	public final int pollingRate, minSpeed;
	
	public Settings(String portname, String[] fanProfiles, int pollingRate, int minSpeed) {
		this.portname = portname;
		this.fanProfiles = fanProfiles;
		this.pollingRate = pollingRate;
		this.minSpeed = minSpeed;
	}
}
