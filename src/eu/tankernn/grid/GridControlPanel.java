package eu.tankernn.grid;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

import eu.tankernn.grid.model.ComputerModel;

public class GridControlPanel extends JPanel implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Thread t;

	private ComputerModel model;

	private FanPanel[] fanPanels;

	private JTextField minRPM = new JTextField();

	private JComboBox<String> portMap = new JComboBox<>();

	private JSlider manualSlider = new JSlider();

	private JLabel CPULabel = new JLabel("CPU preferred");

	private JLabel CPULabelMax = new JLabel("CPU max");

	private JLabel GPULabel = new JLabel("GPU preferred");

	private JLabel GPULabelMax = new JLabel("GPU max");

	private JLabel PowerLabel = new JLabel("Power");

	private void setMinRPM(ActionEvent event) {
		getModel().setMinRPM(Integer.parseInt(minRPM.getText()));
	}

	private void setPort(ItemEvent event) {
		String selectedPort = (String) portMap.getSelectedItem();

		getModel().getGrid().disconnect();

		getModel().setGrid(selectedPort);
	}

	public GridControlPanel() {
		this.setLayout(new GridLayout(3, 2));

		for (FanPanel p : fanPanels)
			this.add(p);

		minRPM.addActionListener(this::setMinRPM);

		manualSlider.setMaximum(100);

		manualSlider.setMinimum(0);

		// manualSlider.addChangeListener(new ChangeListener() {
		//
		// @Override
		// public void stateChanged(ChangeEvent e) {
		// manualSpeed(e);
		// }
		// });

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
	}

	@Override
	public void run() {
		while (!t.isInterrupted()) {
			model.poll();

			updateProperties();

			getModel().compute();

			try {
				Thread.sleep(1000);
			} catch (InterruptedException ex) {
				System.out.println("Thread was interrupted.");
				return;
			}
		}

	}

	public ComputerModel getModel() {
		return model;
	}
}
