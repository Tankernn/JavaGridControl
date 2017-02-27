package eu.tankernn.grid;

import java.awt.Image;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dorkbox.systemTray.MenuItem;
import dorkbox.systemTray.SystemTray;
import eu.tankernn.grid.frame.GridControlPanel;
import eu.tankernn.grid.model.ComputerModel;

public class GridControl implements Runnable {

	private static final String PROFILE_PATH = "profiles.json";
	private static final String SETTINGS_PATH = "settings.json";

	private Thread t = new Thread(this, "Polling thread");

	private int pollingSpeed = 500;
	private boolean startMinimized = false;

	private ComputerModel model = new ComputerModel();

	private GridControlPanel frame;
	private SystemTray systemTray;

	public GridControl(boolean gui) {
		readSettings();

		t.start();

		if (gui) {
			Image image;
			try {
				image = ImageIO.read(ClassLoader.class.getResourceAsStream("/JGC.png"));
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			frame = new GridControlPanel(this, model);
			frame.setResizable(true);
			frame.setIconImage(image);
			frame.pack();
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			frame.setVisible(!startMinimized);
			addTrayIcon(image);
		}
	}

	private void addTrayIcon(Image image) {
		systemTray = SystemTray.get();
		if (systemTray == null) {
			throw new RuntimeException("Unable to load SystemTray!");
		}

		systemTray.setImage(image);

		systemTray.getMenu().add(new MenuItem("Show", a -> {
			frame.setVisible(true);
		}));

		systemTray.getMenu().add(new JSeparator());

		systemTray.getMenu().add(new MenuItem("Quit", a -> {
			exit();
		})).setShortcut('q'); // case does not matter

	}

	public void readSettings() {
		Gson gson = new GsonBuilder().create();
		try (Reader reader = new FileReader(PROFILE_PATH)) {
			Arrays.stream(gson.fromJson(reader, FanSpeedProfile[].class)).forEach(model::addProfile);
		} catch (FileNotFoundException e) {
			// Not a problem
		} catch (IOException e) {
			e.printStackTrace();
		}
		try (Reader reader = new FileReader(SETTINGS_PATH)) {
			Settings settings = gson.fromJson(reader, Settings.class);
			model.setGrid(settings.portname);
			for (int i = 0; i < 6; i++)
				model.getGrid().getFan(i).setProfile(model.getProfile(settings.fanProfiles[i]));
			pollingSpeed = settings.pollingRate;
			model.setMinSpeed(settings.minSpeed);
			model.getSensor().setCpuSensors(Arrays.asList(settings.cpuSensors));
			model.getSensor().setGpuSensors(Arrays.asList(settings.gpuSensors));
			startMinimized = settings.startMinimized;
		} catch (FileNotFoundException e) {
			System.out.println("No config file found, using default settings.");
			for (int i = 0; i < 6; i++)
				model.getGrid().getFan(i).setProfile(model.getProfiles().get(0));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveSettings() {
		Gson gson = new GsonBuilder().create();
		// Save profiles
		try (Writer writer = new FileWriter(PROFILE_PATH)) {
			gson.toJson(model.getCustomProfiles(), writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Save misc. settings
		try (Writer writer = new FileWriter(SETTINGS_PATH)) {
			gson.toJson(new Settings(model.getGrid().getCommunicator().getPortName(), model.getGrid().fanStream().map(f -> f.getProfile().getName()).toArray(String[]::new), model.getSensor().getCpuSensors().stream().toArray(String[]::new), model.getSensor().getGpuSensors().stream().toArray(String[]::new), pollingSpeed, model.getMinSpeed(), this.startMinimized), writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (!t.isInterrupted()) {
			try {
				model.poll();
				model.compute();

				if (frame != null)
					frame.updateProperties();
				Thread.sleep(pollingSpeed);
			} catch (InterruptedException ex) {
				System.out.println("Thread was interrupted.");
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		try {
			// Set System L&F
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}
		new GridControl(!Arrays.asList(args).contains("nogui"));
	}

	public void exit() {
		t.interrupt();
		model.getGrid().disconnect();
		saveSettings();
		frame.dispose();
		if (systemTray != null)
			systemTray.shutdown();
//		for (Thread t : Thread.getAllStackTraces().keySet())
//			if (t.isAlive())
//				System.out.println(t);
	}

	public void setPollingSpeed(int value) {
		this.pollingSpeed = value;
	}

	public int getPollingSpeed() {
		return pollingSpeed;
	}

	public void setStartMinimized(boolean startMinimized) {
		this.startMinimized = startMinimized;
	}

}
