package eu.tankernn.grid.model.sensor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LMSensor extends Sensor {

	public static final String REGEX = "(.*):\\s*([\\+\\-][\\d\\.]*).C";
	
	public LMSensor() {
		try {
			Process proc = Runtime.getRuntime().exec("sensors -A");
			proc.waitFor();
			if (proc.exitValue() != 0)
				System.err.println("Error polling LMSensor, make sure that 'lm-sensors' is installed on the system.");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void poll() throws IOException {
		Process proc = Runtime.getRuntime().exec("sensors -A");
		try {
			proc.waitFor();
		} catch (InterruptedException e) {
			System.err.println("LMSensor polling was interrupted.");
			return;
		}

		BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		Pattern p = Pattern.compile(REGEX);
		String line;
		while ((line = stdoutReader.readLine()) != null) {
			Matcher m = p.matcher(line);
			if (m.find())
				temperatures.put(m.group(1), Double.valueOf(m.group(2)));
		}
	}

}
