package eu.tankernn.grid.frame;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
import eu.tankernn.grid.model.ComputerModel;

public class GridControlPanel extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ComputerModel model;
	
	private JMenuBar menuBar = new JMenuBar();
	private JMenu fileMenu = new JMenu("File"), profileMenu = new JMenu("Profiles");
	private JMenuItem saveSettings = new JMenuItem("Save settings..."), addProfile = new JMenuItem("Add profile...");
	
	private FanPanel[] fanPanels;
	private JPanel gridPanel = new JPanel(), infoPanel = new JPanel();

	private JSpinner minSpeed = new JSpinner();

	private JComboBox<String> portMap = new JComboBox<>();

	private JLabel CPULabel = new JLabel("CPU:");

	private JLabel CPULabelMax = new JLabel("CPU max");

	private JLabel GPULabel = new JLabel("GPU:");

	private JLabel GPULabelMax = new JLabel("GPU max");

	private JLabel PowerLabel = new JLabel("Power");

	private List<FanSpeedProfile> profiles;

	private void setMinRPM(ChangeEvent event) {
		getModel().setMinSpeed((int) minSpeed.getValue());
	}

	private void setPort(ItemEvent event) {
		String selectedPort = (String) portMap.getSelectedItem();

		getModel().getGrid().disconnect();

		getModel().setGrid(selectedPort);
	}

	public GridControlPanel(ComputerModel model) {
		setModel(model);
		this.setLayout(new BorderLayout());
		
		menuBar.add(fileMenu);
		fileMenu.add(saveSettings);
		saveSettings.addActionListener(e -> model.saveSettings());
		menuBar.add(profileMenu);
		profileMenu.add(addProfile);
		addProfile.addActionListener(e -> {
			profiles.add(new ProfileEditor().editProfile(null));
		});
		
		this.setJMenuBar(this.menuBar);

		profiles = generateProfiles();
		fanPanels = model.getGrid().fanStream().map(f -> new FanPanel(f, profiles)).toArray(FanPanel[]::new);

		gridPanel.setLayout(new GridLayout(3, 2));
		for (FanPanel p : fanPanels)
			gridPanel.add(p);

		this.add(gridPanel, BorderLayout.CENTER);

		minSpeed.addChangeListener(this::setMinRPM);
		minSpeed.setModel(new SpinnerNumberModel(30, 0, 100, 5));
		
		infoPanel.setBorder(new TitledBorder("System info"));
		infoPanel.setLayout(new GridLayout(3, 2));
		infoPanel.add(CPULabel);
		infoPanel.add(GPULabel);
		infoPanel.add(CPULabelMax);
		infoPanel.add(GPULabelMax);
		infoPanel.add(PowerLabel);
		infoPanel.add(labelledComponent("Minimum speed (%): ", minSpeed));
		this.add(infoPanel, BorderLayout.SOUTH);
		
		portMap.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				setPort(e);
			}
		});
		this.add(labelledComponent("COM port: ", portMap), BorderLayout.NORTH);
		
		this.setTitle("JavaGridControl");
	}
	
	private JPanel labelledComponent(String labelText, JComponent component) {
		JPanel panel = new JPanel(new FlowLayout());
		panel.add(new JLabel(labelText));
		panel.add(component);
		return panel;
	}

	private List<FanSpeedProfile> generateProfiles() {
		return IntStream.range(30 / 5, 100 / 5).map(i -> i * 5).mapToObj(i -> new FanSpeedProfile(i + "%", new int[] { i })).collect(Collectors.toList());
	}

	/**
	 * This method sets the model for this controller. After the model is set
	 * certain UI elements are updated Finally a pollAndCompute Thread is
	 * started
	 *
	 * @param model the model to set
	 */
	private void setModel(ComputerModel model) {
		this.model = model;

		portMap.removeAllItems();
		for (String key : model.getGrid().getCommunicator().getPortMap().keySet()) {
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

		CPULabel.setText("CPU: " + df.format(getModel().getSensor().getCPUTemp()) + " °C");
		PowerLabel.setText("Total power: " + df.format(getModel().getGrid().getTotalWattage()) + " W");
		CPULabelMax.setText("CPU: " + df.format(getModel().getSensor().getCpuMax()) + " °C Max");
		GPULabel.setText("GPU: " + df.format(getModel().getSensor().getGPUTemp()) + " °C");
		GPULabelMax.setText("GPU: " + df.format(getModel().getSensor().getGpuMax()) + " °C Max");

		for (FanPanel p : fanPanels)
			p.update();
	}

	public ComputerModel getModel() {
		return model;
	}
}
