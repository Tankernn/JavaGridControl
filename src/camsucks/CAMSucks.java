package camsucks;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 * This Project aims to replace the default software made for the GRID+
 * Controller; CAM
 * <p>
 * Software used for this project
 * <p>
 * <ul>
 * <li>The reverse engineering of the GRID+ communication was done by rizvanrp,
 * their site is no longer available but here is a screenshot of their article
 * on the GRID+
 * http://research.domaintools.com/research/screenshot-history/rizvanrp.com/#0
 * <li>The aim is to be able to control the fan speed of the fans connected to a
 * GRID controller according to the temperature of the CPU Packages
 * <li>The project uses a Communicator class that has been created by Henry
 * Poon @ https://blog.henrypoon.com/
 * <li>With the help of this class the communication with the GRID+ controller
 * is handled
 * <li>The sensor data is read with the help of the jWMI class made by Henry
 * Ranch @ http://henryranch.net
 * <li>This class communicates with an external program called
 * openhardwaremonitor @ http://openhardwaremonitor.org/
 * </ul>
 * <p>
 * 
 * Currently monitoring is a bit bugged and is disabled by default but can be
 * turned on with a checkbox.
 * <p>
 * Future plans and TODOs:
 * <p>
 * <ul>
 * <li>Make it possible to control fans according to GPU or CPU or Both
 * temperatures (seems easy enough).
 * <li>Add Integral control to achieve full PI control (Before this can happen a
 * the time constant of the system must be defined reliably .
 * <li>Make program not crash after/during system sleep/hibernation.
 * <li>Find a way to compile program and not get security warnings (because of
 * the filewriter in the jWMI class).
 * <li>Make a config file to save user setting in.
 * </ul>
 * <p>
 * 
 * 
 * @author Roel
 */
public class CAMSucks extends JFrame implements WindowListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	CAMSucksPanel panel = new CAMSucksPanel();

	public CAMSucks() {
		start();
	}

	public void start() {
		this.add(panel);

		setResizable(true);
		setTitle("CAM Sucks!");
		try {
			setIconImage(ImageIO.read(getClass().getResourceAsStream("NoCAMIcon.jpg")));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		pack();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
	}

	/**
	 * 
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		CAMSucks cams = new CAMSucks();
		cams.setVisible(true);
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		panel.getModel().getGrid().disconnect();
		System.exit(0);
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

}
