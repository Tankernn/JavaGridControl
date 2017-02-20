package eu.tankernn.grid.frame;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import eu.tankernn.grid.Fan;
import eu.tankernn.grid.FanSpeedProfile;

public class FanPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Fan fan;

	private JLabel voltageLabel = new JLabel(), currentLabel = new JLabel(),
			rpmLabel = new JLabel();
	private JComboBox<FanSpeedProfile> profileBox = new JComboBox<>();

	public FanPanel(Fan fan, List<FanSpeedProfile> profiles) {
		for (FanSpeedProfile p : profiles)
			profileBox.addItem(p);
		this.fan = fan;

		this.setBorder(new TitledBorder("Fan " + (fan.getIndex() + 1)));

		this.setLayout(new GridLayout(4, 1));

		this.add(voltageLabel);
		this.add(currentLabel);
		this.add(rpmLabel);
		this.add(profileBox);

		profileBox.addActionListener(this::updateProfile);
		this.updateProfile(null);
	}

	public void update() {
		DecimalFormat df = new DecimalFormat("#.##");

		voltageLabel.setText("Voltage: " + df.format(fan.getVoltage()) + "V");
		currentLabel.setText("Current: " + df.format(fan.getAMP()) + "A");
		rpmLabel.setText("Speed: " + df.format(fan.getRPM()) + "RPM");
	}
	
	public void addProfile(FanSpeedProfile p) {
		profileBox.addItem(p);
	}
	
	private void updateProfile(ActionEvent e) {
		fan.setProfile((FanSpeedProfile) profileBox.getSelectedItem());
	}
}
