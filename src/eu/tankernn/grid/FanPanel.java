package eu.tankernn.grid;

import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

public class FanPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Fan fan;
	
	private JLabel voltageLabel, currentLabel, rpmLabel;

	public FanPanel(Fan fan) {
		this.fan = fan;
		
		this.setBorder(new TitledBorder("Fan " + fan.getIndex()));
	}
	
	public void update() {
		DecimalFormat df = new DecimalFormat("#.##");
		
		voltageLabel.setText("Voltage: " + df.format(fan.getVoltage()) + "V");
		currentLabel.setText("Current: " + df.format(fan.getAMP()) + "A");
		rpmLabel.setText("Speed: " + df.format(fan.getRPM()) + "RPM");
	}
}
