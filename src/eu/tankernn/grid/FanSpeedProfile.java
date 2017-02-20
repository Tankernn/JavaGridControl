package eu.tankernn.grid;

import java.util.Arrays;

public class FanSpeedProfile {
	public static final double MAX_TEMP = 80, MIN_TEMP = 30;
	public static final int STEPS = 5;

	public final String name;
	public final int[] percentages;

	public FanSpeedProfile(String name, int[] percentages) {
		this.name = name;
		this.percentages = percentages;
	}

	public int getSpeedPercentage(double temp) {
		double stepSize = (MAX_TEMP - MIN_TEMP) / (double) STEPS;

		double currentTemp = MIN_TEMP;

		for (int i : percentages) {
			if (temp < currentTemp) {
				return i;
			}
			currentTemp += stepSize;
		}

		return percentages[percentages.length - 1];
	}

	@Override
	public String toString() {
		return name + ": " + Arrays.toString(percentages);
	}
}
