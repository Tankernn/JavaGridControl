package eu.tankernn.grid.frame;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.stream.IntStream;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import eu.tankernn.grid.FanSpeedProfile;

public class ProfileEditor {

	public FanSpeedProfile editProfile(FanSpeedProfile profile) {
		JPanel panel = new JPanel(), sliderPanel = new JPanel();
		JSlider[] sliders;
		JTextField nameField = new JTextField();
		if (profile != null) {
			nameField.setText(profile.name);
			sliders = Arrays.stream(profile.percentages).mapToObj(i -> new JSlider(JSlider.VERTICAL, 0, 100, i)).toArray(JSlider[]::new);
		} else {
			sliders = IntStream.range(0, FanSpeedProfile.STEPS).mapToObj(i -> new JSlider(JSlider.VERTICAL)).toArray(JSlider[]::new);
		}
		
		for (JSlider s : sliders) {
			s.setSnapToTicks(true);
			s.setMinorTickSpacing(5);
		}
		
		panel.setLayout(new BorderLayout());
		panel.add(new JLabel("Profile name: "));
		panel.add(nameField, BorderLayout.NORTH);
		
		sliderPanel.setLayout(new GridLayout(1, sliders.length));
		for (JSlider s : sliders)
			sliderPanel.add(s);
		panel.add(sliderPanel, BorderLayout.CENTER);
		

		int response = JOptionPane.showConfirmDialog(null, panel, "Fan Speed Profile Editor", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (response == JOptionPane.OK_OPTION) {
			FanSpeedProfile newProfile = new FanSpeedProfile(nameField.getText(), Arrays.stream(sliders).mapToInt(JSlider::getValue).toArray());
			if (nameField.getText().isEmpty()) {
				JOptionPane.showMessageDialog(null, "Please enter a name for the profile.");
				return editProfile(newProfile);
			}
			return newProfile;
		} else {
			return profile;
		}
	}
	
	public static void main(String[] args) {
		System.out.println(new ProfileEditor().editProfile(null));
	}
}
