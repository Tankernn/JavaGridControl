package eu.tankernn.grid;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import eu.tankernn.grid.frame.GridControlPanel;
import eu.tankernn.grid.model.ComputerModel;

public class GridControl extends JFrame implements WindowListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ComputerModel model = new ComputerModel();
	GridControlPanel panel = new GridControlPanel(model);

	public GridControl() {
		this.add(panel);
		setResizable(true);
		setTitle("JavaGridControl");
		pack();
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
	}

	/**
	 * 
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		GridControl cams = new GridControl();
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
