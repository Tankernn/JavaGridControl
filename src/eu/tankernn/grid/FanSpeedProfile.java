package eu.tankernn.grid;

import java.util.Arrays;

public class FanSpeedProfile {
	public static final int MAX_TEMP = 80, MIN_TEMP = 30, STEPS = 5;
	
	public final String name;
	public final int[] percentages;
	
	public FanSpeedProfile(String name, int[] percentages) {
		this.name = name;
		this.percentages = percentages;
	}
	
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
	
	@Override
	public String toString() {
		return name + ": " + Arrays.toString(percentages);
	}
}
