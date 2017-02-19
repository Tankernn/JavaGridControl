package eu.tankernn.grid.frame;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.util.stream.IntStream;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import eu.tankernn.grid.FanSpeedProfile;
import eu.tankernn.grid.model.ComputerModel;

public class GridControlPanel extends JPanel implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Thread t;

	private ComputerModel model;

	private FanPanel[] fanPanels;
	private JPanel gridPanel = new JPanel();

	private JTextField minRPM = new JTextField();

	private JComboBox<String> portMap = new JComboBox<>();

	private JLabel CPULabel = new JLabel("CPU preferred");

	private JLabel CPULabelMax = new JLabel("CPU max");

	private JLabel GPULabel = new JLabel("GPU preferred");

	private JLabel GPULabelMax = new JLabel("GPU max");

	private JLabel PowerLabel = new JLabel("Power");

	private FanSpeedProfile[] profiles;

	private void setMinRPM(ActionEvent event) {
		getModel().setMinRPM(Integer.parseInt(minRPM.getText()));
	}

	private void setPort(ItemEvent event) {
		String selectedPort = (String) portMap.getSelectedItem();

		getModel().getGrid().disconnect();

		getModel().setGrid(selectedPort);
	}

	public GridControlPanel(ComputerModel model) {
		setModel(model);
		this.setLayout(new BorderLayout());

		profiles = generateProfiles();
		fanPanels = model.getGrid().fanStream().map(f -> new FanPanel(f, profiles)).toArray(FanPanel[]::new);

		gridPanel.setLayout(new GridLayout(3, 2));
		for (FanPanel p : fanPanels)
			gridPanel.add(p);

		this.add(gridPanel, BorderLayout.CENTER);

		minRPM.addActionListener(this::setMinRPM);

		portMap.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				setPort(e);
			}
		});
		this.add(portMap, BorderLayout.NORTH);

		t = new Thread(this);
		t.setDaemon(true);
		t.start();
	}

	private FanSpeedProfile[] generateProfiles() {
		return IntStream.range(30 / 5, 100 / 5).map(i -> i * 5).mapToObj(i -> new FanSpeedProfile(i + "%", new int[] { i })).toArray(FanSpeedProfile[]::new);
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

		CPULabel.setText(df.format(getModel().getSensor().getCPUTemp()) + " °C");
		PowerLabel.setText(df.format(getModel().getGrid().getTotalWattage()) + " W");
		CPULabelMax.setText(df.format(getModel().getSensor().getCpuMax()) + " °C Max");
		GPULabel.setText(df.format(getModel().getSensor().getGPUTemp()) + " °C");
		GPULabelMax.setText(df.format(getModel().getSensor().getGpuMax()) + " °C Max");

		for (FanPanel p : fanPanels)
			p.update();
	}

	@Override
	public void run() {
		while (!t.isInterrupted()) {
			model.poll();
			model.compute();

			updateProperties();
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
