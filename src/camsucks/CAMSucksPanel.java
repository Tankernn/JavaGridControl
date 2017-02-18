package camsucks;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.stream.IntStream;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;

import camsucks.model.ComputerModel;

/**
 * This class is the controller of this Project. It initializes the Interactive
 * UI elements When its model is set it adds values to certain UI elements and
 * starts a pollAndCompute thread
 *
 *
 * @author Roel
 */
public class CAMSucksPanel extends JPanel implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Thread t;

	private ComputerModel model;

	private String[] fanColumns = { "#", "Voltage", "Current", "RPM" };
	private String[][] fanData;
	private JTable fanTable = new JTable(fanData, fanColumns);

	private JTextField targetRPM = new JTextField();

	private JTextField targetCPUTemp = new JTextField();

	private JTextField maxCPUTemp = new JTextField();

	private JTextField targetGPUTemp = new JTextField();

	private JTextField maxGPUTemp = new JTextField();

	private JTextField minRPM = new JTextField();

	private JComboBox<String> portMap = new JComboBox<>();

	private JCheckBox manualCheck = new JCheckBox("Manual");

	private JSlider manualSlider = new JSlider();

	// private JTabbedPane MonitorTab;

	private JLabel CPULabel = new JLabel("CPU preferred");

	private JLabel CPULabelMax = new JLabel("CPU max");

	private JLabel GPULabel = new JLabel("GPU preferred");

	private JLabel GPULabelMax = new JLabel("GPU max");

	private JLabel PowerLabel = new JLabel("Power");

	private JLabel[] VoltageLabels;

	private JLabel[] AMPLabels;

	private JLabel[] RPMLabels;
	// </editor-fold>

	/*
	 * private void closeMonitor(Event event) {
	 * 
	 * if (MonitorTab.isSelected() && monitorCheck.isSelected() ) {
	 * //System.out.println(" opened monitor"); model.setExtraPoll(true);
	 * 
	 * } else {
	 * 
	 * //System.out.println(" open configure"); model.setExtraPoll(false); }
	 * 
	 * }
	 */
	private void setTargetRPM(ActionEvent event) {
		double dTargetRPM = Double.parseDouble(targetRPM.getText());

		getModel().setTargetRPM(dTargetRPM);

		// System.out.println(model.getTargetRPM());
	}

	private void setTargetCPUTemp(ActionEvent event) {
		double dTargetTemp = Double.parseDouble(targetCPUTemp.getText());

		getModel().setTargetCPUTemp(dTargetTemp);

		// System.out.println(model.getTargetTemp());
	}

	private void setTargetGPUTemp(ActionEvent event) {
		double dTargetTemp = Double.parseDouble(targetGPUTemp.getText());

		getModel().setTargetGPUTemp(dTargetTemp);

		// System.out.println(model.getTargetTemp());
	}

	private void setMinRPM(ActionEvent event) {
		int dMinRPM = Integer.parseInt(minRPM.getText());

		getModel().setMinRPM(dMinRPM);

		// System.out.println(model.getMaxTemp());
	}

	private void setMaxCPUTemp(ActionEvent event) {
		double dMaxTemp = Double.parseDouble(maxCPUTemp.getText());

		getModel().setMaxCPUTemp(dMaxTemp);

		// System.out.println(model.getMaxTemp());
	}

	private void setMaxGPUTemp(ActionEvent event) {
		double dMaxTemp = Double.parseDouble(maxGPUTemp.getText());

		getModel().setMaxGPUTemp(dMaxTemp);

		// System.out.println(model.getMaxTemp());
	}

	private void setPort(ItemEvent event) {

		String selectedPort = (String) portMap.getSelectedItem();

		getModel().getGrid().disconnect();

		getModel().setGrid(selectedPort);

	}

	public CAMSucksPanel() {
		VoltageLabels = IntStream.range(0, 6).mapToObj(i -> new JLabel("V " + i)).toArray(i -> new JLabel[i]);
		AMPLabels = IntStream.range(0, 6).mapToObj(i -> new JLabel("AMP " + i)).toArray(i -> new JLabel[i]);
		RPMLabels = IntStream.range(0, 6).mapToObj(i -> new JLabel("RPM " + i)).toArray(i -> new JLabel[i]);

		for (JLabel l : VoltageLabels)
			this.add(l);
		for (JLabel l : AMPLabels)
			this.add(l);
		for (JLabel l : RPMLabels)
			this.add(l);

		this.add(fanTable);

		// MonitorTab.setOnSelectionChanged(this::closeMonitor);

		targetRPM.addActionListener(this::setTargetRPM);

		targetCPUTemp.addActionListener(this::setTargetCPUTemp);

		maxCPUTemp.addActionListener(this::setMaxCPUTemp);

		targetGPUTemp.addActionListener(this::setTargetGPUTemp);

		maxGPUTemp.addActionListener(this::setMaxGPUTemp);

		minRPM.addActionListener(this::setMinRPM);

		//manualCheck.addActionListener(this::setManual);

		manualSlider.setMaximum(100);

		manualSlider.setMinimum(0);

//		manualSlider.addChangeListener(new ChangeListener() {
//
//			@Override
//			public void stateChanged(ChangeEvent e) {
//				manualSpeed(e);
//			}
//		});

		portMap.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				setPort(e);
			}
		});

	}

	/**
	 * This method sets the model for this controller. After the model is set
	 * certain UI elements are updated Finally a pollAndCompute Thread is
	 * started
	 *
	 * @param model the model to set
	 */
	public void setModel(ComputerModel model) {
		this.model = model;

		portMap.removeAllItems();
		for (String key : model.getGrid().getCommunicator().getPortMap().keySet()) {
			portMap.addItem(key);
		}

		maxCPUTemp.setText(Double.toString(model.getMaxCPUTemp()));
		maxGPUTemp.setText(Double.toString(model.getMaxGPUTemp()));

		targetRPM.setText(Double.toString(model.getTargetRPM()));

		targetCPUTemp.setText(Double.toString(model.getTargetCPUTemp()));
		targetGPUTemp.setText(Double.toString(model.getTargetGPUTemp()));

		minRPM.setText(Double.toString(model.getMinRPM()));

		updateProperties();

		t = new Thread(this);
		t.setDaemon(true);
		t.start();
	}

	/**
	 * This method updates the values of some UI elements and binds properties
	 * to others
	 *
	 */
	public void updateProperties() {
		DecimalFormat df = new DecimalFormat("#.##");

		CPULabel.setText(df.format(getModel().getSensor().getCPUTemp()) + " °C");
		PowerLabel.setText(df.format(getModel().getGrid().getTotalWattage()) + " W");
		CPULabelMax.setText(df.format(getModel().getSensor().getCpuMax()) + " °C Max");
		GPULabel.setText(df.format(getModel().getSensor().getGPUTemp()) + " °C");
		GPULabelMax.setText(df.format(getModel().getSensor().getGpuMax()) + " °C Max");

		String[] indices = IntStream.range(0, 6).mapToObj(Integer::toString).toArray(String[]::new);
		String[] voltages = Arrays.stream(getModel().getGrid().getVoltage()).mapToObj(df::format).toArray(String[]::new);
		String[] rpms = Arrays.stream(getModel().getGrid().getFanRPM()).mapToObj(df::format).toArray(String[]::new);
		String[] currents = Arrays.stream(getModel().getGrid().getFanAMP()).mapToObj(df::format).toArray(String[]::new);

		fanData = new String[][] { indices, voltages, rpms, currents };
		System.out.println(Arrays.deepToString(fanData));
	}

	@Override
	public void run() {
		while (!t.isInterrupted()) {
			getModel().poll();

			new Thread((() -> {
				updateProperties();
			})).start();
			
			if (!getModel().isManual()) {
				getModel().compute();
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				System.out.println("Thread got interrupted");
				return;
			}
		}

	}

	public ComputerModel getModel() {
		return model;
	}
}
