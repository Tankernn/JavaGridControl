package eu.tankernn.grid.model;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * This class uses the communicator class to communicate with the GRID+
 * controller.
 * 
 * @author Frans
 */
public class GRID {
	private Communicator communicator = new Communicator();
	private Fan[] fans = IntStream.range(0, 6).mapToObj(i -> new Fan(communicator, i)).toArray(Fan[]::new);

	/**
	 * This method simply runs the disconnect method of the communicator.
	 */
	public void disconnect() {
		communicator.disconnect();
	}

	/**
	 * Gets the fan at the specified index.
	 * 
	 * @param index
	 *            The fan index (0-5)
	 * @return The fan object
	 */
	public Fan getFan(int index) {
		return fans[index];
	}

	public Communicator getCommunicator() {
		return communicator;
	}

	public double getTotalWattage() {
		return fanStream().mapToDouble(Fan::getWattage).sum();
	}

	public void pollFans() throws IOException, InterruptedException {
		if (communicator.isConnected())
			for (Fan f : fans)
				f.poll();
	}

	public void updateFanSpeeds(double cpuTemp, double gpuTemp, int minSpeed) {
		fanStream().forEach(f -> f.update(cpuTemp, gpuTemp, minSpeed));
	}

	public Stream<Fan> fanStream() {
		return Arrays.stream(fans);
	}

}
