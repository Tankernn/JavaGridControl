package eu.tankernn.grid.model.sensor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LMSensor extends Sensor {
	
	public static final String REGEX = "(.*):\\s*([\\+\\-][\\d\\.]*).C";
	
	Map<String, Double> temperatures = new HashMap<>();

	@Override
	public void poll() throws IOException {
		Process proc = Runtime.getRuntime().exec("sensors -A");
		try {
			proc.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}

		BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		Pattern p = Pattern.compile(REGEX, Pattern.MULTILINE);
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = stdoutReader.readLine()) != null) {
			sb.append(line + '\n');
		}
		Matcher m = p.matcher(sb);
		while (m.find()) {
			temperatures.put(m.group(1), Double.valueOf(m.group(2)));
		}
	}

	@Override
	public double getCPUTemp() {
		//TODO Make configurable
		return temperatures.get("Core 0");
	}

	@Override
	public double getGPUTemp() {
		// TODO Make configurable
		return temperatures.get("SYSTIN");
	}

}
