package eu.tankernn.grid;

public class FanSpeedProfile {
	public static final int MAX_TEMP = 80, MIN_TEMP = 30, STEPS = 5;
	
	private int[] percentages = new int[STEPS];
	
	public int getSpeedPercentage(double temp) {
		int stepSize = (MAX_TEMP - MIN_TEMP) / STEPS;
		
		int currentTemp = MIN_TEMP;
		
		for (int i : percentages) {
			if (temp < currentTemp) {
				return i;
			}
			currentTemp += stepSize;
		}
		
		return 100;
	}
}
