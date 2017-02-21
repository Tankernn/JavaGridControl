package eu.tankernn.grid;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Arrays;

import javax.swing.JFrame;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import eu.tankernn.grid.frame.GridControlPanel;
import eu.tankernn.grid.model.ComputerModel;

public class GridControl implements WindowListener, Runnable {

	private static final String PROFILE_PATH = "profiles.json";
	private static final String SETTINGS_PATH = "settings.json";

	private Thread t;

	private int pollingSpeed = 500;

	private ComputerModel model = new ComputerModel();

	private GridControlPanel frame;

	public GridControl(boolean gui) {
		readSettings();
		
		if (gui) {
			frame = new GridControlPanel(this, model);
			frame.setResizable(true);
			frame.pack();
			frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			frame.addWindowListener(this);
			frame.setVisible(true);
		}

		t = new Thread(this);
		t.setDaemon(true);
		t.start();
	}
	
	public void readSettings() {
		Gson gson = new GsonBuilder().create();
		try (Reader reader = new FileReader(PROFILE_PATH)) {
			Arrays.stream(gson.fromJson(reader, FanSpeedProfile[].class)).forEach(model::addProfile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try (Reader reader = new FileReader(SETTINGS_PATH)) {
			Settings settings = gson.fromJson(reader, Settings.class);
			model.getGrid().getCommunicator().connect(settings.portname);
			for (int i = 0; i < 6; i++)
				model.getGrid().getFan(i).setProfile(model.getProfile(settings.fanProfiles[i]));
			pollingSpeed = settings.pollingRate;
			model.setMinSpeed(settings.minSpeed);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
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
			gson.toJson(new Settings(model.getGrid().getCommunicator().getPortName(),
					model.getGrid().fanStream().map(f -> f.getProfile().name).toArray(String[]::new), pollingSpeed,
					model.getMinSpeed()), writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (!t.isInterrupted()) {
			model.poll();
			model.compute();

			if (frame != null)
				frame.updateProperties();

			try {
				Thread.sleep(pollingSpeed);
			} catch (InterruptedException ex) {
				System.out.println("Thread was interrupted.");
				return;
			}
		}

	}

	/**
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		new GridControl(!Arrays.asList(args).contains("nogui"));
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowClosing(WindowEvent e) {
		t.interrupt();
		model.getGrid().disconnect();
		e.getWindow().dispose();
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub

	}

	public void setPollingSpeed(int value) {
		this.pollingSpeed = value;
	}

}
