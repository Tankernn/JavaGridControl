package eu.tankernn.grid.frame;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

public class SensorZone extends JScrollPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JList<String> sensors = new JList<>();

	public SensorZone(String name, List<String> sensors) {
		this.setBorder(new TitledBorder(name + ":"));

		DefaultListModel<String> model = new DefaultListModel<>();
		for (String s : sensors)
			model.addElement(s);
		this.sensors.setModel(model);
		this.setViewportView(this.sensors);
	}

	public JList<String> getSensors() {
		return sensors;
	}
}
