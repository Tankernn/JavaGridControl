package eu.tankernn.grid;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Arrays;

import javax.swing.JFrame;

import eu.tankernn.grid.frame.GridControlPanel;
import eu.tankernn.grid.model.ComputerModel;

public class GridControl implements WindowListener, Runnable {
	private Thread t;
	
	private int pollingSpeed = 500;

	private ComputerModel model = new ComputerModel();

	private GridControlPanel frame;

	public GridControl(boolean gui) {
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
	 * @param args the command line arguments
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
