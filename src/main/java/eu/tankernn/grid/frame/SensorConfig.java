package eu.tankernn.grid.frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import eu.tankernn.grid.model.sensor.Sensor;

public class SensorConfig extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel(), buttonPanel = new JPanel(), zonePanel = new JPanel();
	private SensorZone availableList, cpuList , gpuList;
	private JButton cpuAdd = new JButton("->"), cpuRem = new JButton("<-"), gpuAdd = new JButton("->"),
			gpuRem = new JButton("<-");

	/**
	 * Create the dialog.
	 */
	public SensorConfig(Sensor sensor) {
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		availableList = new SensorZone("Available sensors", sensor.getSensorNames().stream().filter(s -> !sensor.getCpuSensors().contains(s))
				.filter(s -> !sensor.getGpuSensors().contains(s)).collect(Collectors.toList()));
		cpuList = new SensorZone("CPU sensors", sensor.getCpuSensors());
		gpuList = new SensorZone("GPU sensors", sensor.getGpuSensors());
		
		availableList.setPreferredSize(new Dimension(150, 400));
		cpuList.setPreferredSize(new Dimension(150, 200));
		gpuList.setPreferredSize(new Dimension(150, 200));
		
		zonePanel.setLayout(new BorderLayout());
		zonePanel.add(cpuList, BorderLayout.NORTH);
		zonePanel.add(gpuList, BorderLayout.SOUTH);
		
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.add(cpuAdd);
		buttonPanel.add(cpuRem);
		buttonPanel.add(gpuAdd);
		buttonPanel.add(gpuRem);
		
		cpuAdd.addActionListener(a -> move(availableList, cpuList));
		cpuRem.addActionListener(a -> move(cpuList, availableList));
		gpuAdd.addActionListener(a -> move(availableList, gpuList));
		gpuRem.addActionListener(a -> move(gpuList, availableList));
		
		contentPanel.add(availableList, BorderLayout.WEST);
		contentPanel.add(buttonPanel, BorderLayout.CENTER);
		contentPanel.add(zonePanel, BorderLayout.EAST);

		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(a -> {
					sensor.setCpuSensors(toList(cpuList.getSensors()));
					sensor.setGpuSensors(toList(gpuList.getSensors()));
					dispose();
				});
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(a -> dispose());
				buttonPane.add(cancelButton);
			}
		}
		pack();
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	
	private List<String> toList(JList<String> jlist) {
		List<String> list = new ArrayList<>();
		for (int i = 0; i < jlist.getModel().getSize(); i++)
			list.add(jlist.getModel().getElementAt(i));
		return list;
	}

	private void move(SensorZone origin, SensorZone target) {
		if (origin.getSensors().getSelectedIndex() < 0)
			return;
		String name = origin.getSensors().getSelectedValue();
		((DefaultListModel<String>) origin.getSensors().getModel()).remove(origin.getSensors().getSelectedIndex());
		((DefaultListModel<String>) target.getSensors().getModel()).addElement(name);
	}
}
