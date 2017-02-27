package eu.tankernn.grid;

import java.util.Arrays;

public class FanSpeedProfile {
	public static final int STEPS = 5;
	public static final double MAX_TEMP = 80, MIN_TEMP = 30, STEP_SIZE = (MAX_TEMP - MIN_TEMP) / (double) STEPS;

	private String name;
	private int[] percentages;

	public FanSpeedProfile(String name, int[] percentages) {
		this.setName(name);
		this.setPercentages(percentages);
	}

	public int getSpeedPercentage(double temp) {
		double currentTemp = MIN_TEMP;

		for (int i : getPercentages()) {
			if (temp < currentTemp) {
				return i;
			}
			currentTemp += STEP_SIZE;
		}

		return getPercentages()[getPercentages().length - 1];
	}

	@Override
	public String toString() {
		return getName() + ": " + Arrays.toString(getPercentages());
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int[] getPercentages() {
		return percentages;
	}

	public void setPercentages(int[] percentages) {
		this.percentages = percentages;
	}
}
