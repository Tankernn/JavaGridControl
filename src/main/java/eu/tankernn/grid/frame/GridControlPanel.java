package eu.tankernn.grid.frame;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.util.Arrays;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;

import eu.tankernn.grid.FanSpeedProfile;
import eu.tankernn.grid.GridControl;
import eu.tankernn.grid.model.ComputerModel;

public class GridControlPanel extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ComputerModel model;

	private JMenuBar menuBar = new JMenuBar();
	private JMenu fileMenu = new JMenu("File"), settingsMenu = new JMenu("Settings"),
			profileMenu = new JMenu("Profiles");
	private JMenuItem saveSettings = new JMenuItem("Save settings..."), exit = new JMenuItem("Exit"),
			sensorConf = new JMenuItem("Configure sensors..."), addProfile = new JMenuItem("Add profile...");

	private FanPanel[] fanPanels;
	private JPanel serialPanel = new JPanel(), gridPanel = new JPanel(), infoPanel = new JPanel();

	private JSpinner minSpeed = new JSpinner(new SpinnerNumberModel(30, 0, 100, 5)),
			pollingSpeed = new JSpinner(new SpinnerNumberModel(500, 100, 2000, 100));

	private JComboBox<String> portMap = new JComboBox<>();

	private JLabel CPULabel = new JLabel("CPU:");

	private JLabel CPULoadLabel = new JLabel("CPU load");

	private JLabel GPULabel = new JLabel("GPU:");

	private JLabel PowerLabel = new JLabel("Power");

	private void setMinRPM(ChangeEvent event) {
		getModel().setMinSpeed((int) minSpeed.getValue());
	}

	private void setPort(ItemEvent event) {
		model.setGrid((String) portMap.getSelectedItem());
	}

	public GridControlPanel(GridControl control, ComputerModel model) {
		setModel(model);
		this.setLayout(new BorderLayout());

		menuBar.add(fileMenu);
		fileMenu.add(exit);
		exit.addActionListener(a -> control.exit());
		fileMenu.add(saveSettings);
		saveSettings.addActionListener(e -> control.saveSettings());
		menuBar.add(settingsMenu);
		settingsMenu.add(sensorConf);
		sensorConf.addActionListener(e -> new SensorConfig(model.getSensor()));
		menuBar.add(profileMenu);
		profileMenu.add(addProfile);
		addProfile.addActionListener(e -> {
			FanSpeedProfile p = new ProfileEditor().editProfile(null);
			if (p != null) {
				model.addProfile(p);
				Arrays.stream(fanPanels).forEach(f -> f.addProfile(p));
			}
		});

		this.setJMenuBar(this.menuBar);

		serialPanel.setLayout(new FlowLayout());
		serialPanel.setBorder(new TitledBorder("Serial settings"));
		serialPanel.add(labelledComponent("COM port: ", portMap));
		serialPanel.add(labelledComponent("Polling speed: ", pollingSpeed));
		this.add(serialPanel, BorderLayout.NORTH);

		fanPanels = model.getGrid().fanStream().map(f -> new FanPanel(f, model.getProfiles())).toArray(FanPanel[]::new);

		gridPanel.setLayout(new GridLayout(3, 2));
		for (FanPanel p : fanPanels)
			gridPanel.add(p);

		this.add(gridPanel, BorderLayout.CENTER);

		minSpeed.setValue(model.getMinSpeed());
		minSpeed.addChangeListener(this::setMinRPM);

		pollingSpeed.setValue(control.getPollingSpeed());
		pollingSpeed.addChangeListener(e -> {
			control.setPollingSpeed((int) pollingSpeed.getValue());
		});

		infoPanel.setBorder(new TitledBorder("System info"));
		infoPanel.setLayout(new GridLayout(3, 2));
		infoPanel.add(CPULabel);
		infoPanel.add(GPULabel);
		infoPanel.add(CPULoadLabel);
		infoPanel.add(PowerLabel);
		infoPanel.add(labelledComponent("Minimum speed (%): ", minSpeed));
		this.add(infoPanel, BorderLayout.SOUTH);

		portMap.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				setPort(e);
			}
		});

		this.setTitle("JavaGridControl");
	}

	static JPanel labelledComponent(String labelText, JComponent component) {
		JPanel panel = new JPanel(new FlowLayout());
		panel.add(new JLabel(labelText));
		panel.add(component);
		return panel;
	}

	/**
	 * This method sets the model for this controller. After the model is set
	 * certain UI elements are updated Finally a pollAndCompute Thread is
	 * started
	 *
	 * @param model
	 *            the model to set
	 */
	private void setModel(ComputerModel model) {
		this.model = model;

		portMap.removeAllItems();
		for (String key : model.getPortMap().keySet()) {
			portMap.addItem(key);
		}

		setPort(null);
	}

	/**
	 * This method updates the values of some UI elements and binds properties
	 * to others
	 *
	 */
	public void updateProperties() {
		DecimalFormat df = new DecimalFormat("#.##");
		// \u00B0 = "Degree sign"
		CPULabel.setText("CPU: " + df.format(getModel().getSensor().getCPUTemp()) + " \u00B0C");
		PowerLabel.setText("Total power: " + df.format(getModel().getGrid().getTotalWattage()) + " W");
		CPULoadLabel.setText("CPU load: " + df.format(getModel().getSensor().getCpuLoad()) + " %");
		GPULabel.setText("GPU: " + df.format(getModel().getSensor().getGPUTemp()) + " \u00B0C");

		for (FanPanel p : fanPanels)
			p.update();
	}

	public ComputerModel getModel() {
		return model;
	}
}
