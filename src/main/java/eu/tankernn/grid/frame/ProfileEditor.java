package eu.tankernn.grid.frame;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.stream.IntStream;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import eu.tankernn.grid.FanSpeedProfile;

public class ProfileEditor {
	
	private static Dictionary<Integer, JLabel> dictionary = new Hashtable<>();
	static {
		dictionary.put(0, new JLabel("0%"));
		dictionary.put(50, new JLabel("50%"));
		dictionary.put(100, new JLabel("100%"));
	}

	public FanSpeedProfile editProfile(FanSpeedProfile profile) {
		JPanel panel = new JPanel(), sliderPanel = new JPanel();
		JSlider[] sliders;
		JTextField nameField = new JTextField(20);
		if (profile != null) {
			nameField.setText(profile.getName());
			sliders = Arrays.stream(profile.getPercentages()).mapToObj(i -> new JSlider(JSlider.VERTICAL, 0, 100, i))
					.toArray(JSlider[]::new);
		} else {
			sliders = IntStream.range(0, FanSpeedProfile.STEPS).mapToObj(i -> new JSlider(JSlider.VERTICAL, 0, 100, 50))
					.toArray(JSlider[]::new);
		}
		
		sliderPanel.setLayout(new GridLayout(1, sliders.length));
		
		sliders[sliders.length -1].setPaintLabels(true);

		for (int i = 0; i < sliders.length; i++) {
			JSlider s = sliders[i];
			s.setPaintTicks(true);
			s.setSnapToTicks(true);
			s.setLabelTable(dictionary);
			s.setMinorTickSpacing(5);
			s.setMajorTickSpacing(50);
			JPanel p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
			p.add(s);
			p.add(new JLabel(FanSpeedProfile.MIN_TEMP + FanSpeedProfile.STEP_SIZE * i + " \u00B0C"));
			sliderPanel.add(p);
		}

		panel.setLayout(new BorderLayout());
		panel.add(GridControlPanel.labelledComponent("Profile name: ", nameField), BorderLayout.NORTH);

		panel.add(sliderPanel, BorderLayout.CENTER);

		int response = JOptionPane.showConfirmDialog(null, panel, "Fan Speed Profile Editor",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (response == JOptionPane.OK_OPTION) {
			int[] percs = Arrays.stream(sliders).mapToInt(JSlider::getValue).toArray();
			if (profile == null)
				profile = new FanSpeedProfile(nameField.getText(), percs);
			else {
				profile.setPercentages(percs);
				profile.setName(nameField.getText());
			}
			if (nameField.getText().isEmpty()) {
				JOptionPane.showMessageDialog(null, "Please enter a name for the profile.");
				return editProfile(profile);
			}
			return profile;
		} else {
			return profile;
		}
	}

	public static void main(String[] args) {
		System.out.println(new ProfileEditor().editProfile(null));
	}
}
