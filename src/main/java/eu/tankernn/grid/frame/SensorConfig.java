package eu.tankernn.grid.frame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import eu.tankernn.grid.model.sensor.Sensor;

public class SensorConfig extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final JPanel contentPanel = new JPanel(), buttonPanel = new JPanel(), zonePanel = new JPanel();
	private JList<String> availableList = new JList<>(), cpuList = new JList<>(), gpuList = new JList<>();
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

		{
			DefaultListModel<String> model = new DefaultListModel<>();
			for (String s : sensor.getCpuSensors())
				model.addElement(s);
			cpuList.setModel(model);
		}
		{
			DefaultListModel<String> model = new DefaultListModel<>();
			for (String s : sensor.getGpuSensors())
				model.addElement(s);
			gpuList.setModel(model);
		}
		{
			DefaultListModel<String> model = new DefaultListModel<>();
			for (String s : sensor.getSensorNames().stream().filter(s -> !sensor.getCpuSensors().contains(s))
					.filter(s -> !sensor.getGpuSensors().contains(s)).toArray(String[]::new))
				model.addElement(s);
			availableList.setModel(model);
		}

		availableList.setBorder(new TitledBorder("Available sensors:"));
		cpuList.setBorder(new TitledBorder("CPU sensors:"));
		gpuList.setBorder(new TitledBorder("GPU sensors:"));
		
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
					sensor.setCpuSensors(toList(cpuList));
					sensor.setGpuSensors(toList(gpuList));
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

	private void move(JList<String> origin, JList<String> target) {
		if (origin.getSelectedIndex() < 0)
			return;
		String name = origin.getSelectedValue();
		((DefaultListModel<String>) origin.getModel()).remove(origin.getSelectedIndex());
		((DefaultListModel<String>) target.getModel()).addElement(name);
	}
}
