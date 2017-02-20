package eu.tankernn.grid.model;

import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import eu.tankernn.grid.Fan;

/**
 *
 * This is a model for the GRID controller. This class uses the communicator
 * class to communicate to the GRID+ controller. To update their values they
 * each have poll functions instead of setters which send a command to the GRID
 * and read the response.
 * 
 * This class also has a boolean check that checks if the voltage command is the
 * same as the previous voltage command, this is to prevent pointless serial
 * communication.
 * 
 * @author Roel
 */
public class GRID {
	private Communicator communicator;
	private Fan[] fans;

	/**
	 * This constructor initiates all members afterwards it opens a communicator
	 * at the selected port
	 */
	public GRID() {
		fans = IntStream.range(0, 6).mapToObj(i -> new Fan(this, i)).toArray(Fan[]::new);

		communicator = new Communicator();
		communicator.searchForPorts();
	}

	/**
	 * This method simply runs the disconnect method of the communicator.
	 */
	public void disconnect() {
		getCommunicator().disconnect();
	}

	public Fan getFan(int index) {
		return fans[index];
	}

	public Communicator getCommunicator() {
		return communicator;
	}

	public double getTotalWattage() {
		return Arrays.stream(fans).mapToDouble(Fan::getWattage).sum();
	}

	public void pollFans() {
		fanStream().forEach(Fan::poll);
	}

	public void updateFanSpeeds(double temp, int minSpeed) {
		fanStream().forEach(f -> f.update(temp, minSpeed));
	}

	public Stream<Fan> fanStream() {
		return Arrays.stream(fans);
	}

}
