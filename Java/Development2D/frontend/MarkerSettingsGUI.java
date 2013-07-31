package frontend;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MarkerSettingsGUI extends Publisher<MarkerSettings> {

	private JPanel settingsPanel;

	private JFormattedTextField numMarkersTextbox;
	private JSlider speedSlider;
	private JSlider scalingSlider;
	private JCheckBox markerMobilityBox;
	private JCheckBox drawAvatarBox;
	private JCheckBox drawMarkersBox;

	private static int MAX_SPEED = 4000;
	private static int MAX_SIZE = 10;

	public MarkerSettingsGUI() {
		initSwingObjects();
		initListeners();
	}

	private void initSwingObjects() {
		settingsPanel = new JPanel();
		settingsPanel.setBorder(BorderFactory.createTitledBorder("Object Controls"));
		settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
		//settingsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		// Initialize Number of Objects Text Input
		numMarkersTextbox = new JFormattedTextField(NumberFormat.getIntegerInstance());
		numMarkersTextbox.setColumns(3);
		numMarkersTextbox.setValue(0);	
		JPanel numObjectsPanel = new JPanel();
		numObjectsPanel.setLayout(new FlowLayout());
		numObjectsPanel.add(new JLabel("Number of Objects"));
		numObjectsPanel.add(numMarkersTextbox);
		numObjectsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		settingsPanel.add(numObjectsPanel);

		JPanel sliderPanel = new JPanel();
		sliderPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
		sliderPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		settingsPanel.add(sliderPanel);
		
		
		// Initialize speed slider.		
		final DecimalFormat speedFormat = new DecimalFormat("0.00");		
		speedSlider = new JSlider();
		speedSlider.setMaximum(MAX_SPEED);
		speedSlider.setValue(0);
		TitledBorder speedBorder = BorderFactory.createTitledBorder("");
		speedSlider.setBorder(speedBorder);
		speedBorder.setTitle("Object Speed (" + speedFormat.format(.05) + ")");
		sliderPanel.add(speedSlider);
		
		// Initialize Scaling Slider.		
		scalingSlider = new JSlider();
		settingsPanel.add(scalingSlider);
		scalingSlider.setMaximum(MAX_SIZE);
		scalingSlider.setValue((int) 1.0 * 10);
		TitledBorder scaleBorder = BorderFactory.createTitledBorder("");
		scalingSlider.setBorder(scaleBorder);
		scaleBorder.setTitle("Object Size (" + scalingSlider.getValue()
				/ 10.0 + ")");
		sliderPanel.add(scalingSlider);
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder( BorderFactory
        .createEtchedBorder(EtchedBorder.LOWERED));
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));		
		
		// Initialize buttons and checkboxes		
		markerMobilityBox = new JCheckBox();
		markerMobilityBox.setText("Objects Move");
		markerMobilityBox.setSelected(true);
		buttonPanel.add(markerMobilityBox);
		
		drawAvatarBox = new JCheckBox();
		drawAvatarBox.setText("Draw Avatar");
		drawAvatarBox.setSelected(true);
		buttonPanel.add(drawAvatarBox);		

		drawMarkersBox = new JCheckBox();		
		drawMarkersBox.setText("Draw Objects");
		drawMarkersBox.setSelected(true);
		buttonPanel.add(drawMarkersBox);
		
		buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		settingsPanel.add(buttonPanel);
	}

	private void initListeners() {
		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {				
				updateSubscribers();
			}
		};
		ChangeListener cl = new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				// FIXME
				// Prevent keyboard arrow input from varying this slider
				// by moving the focus to a button.
				markerMobilityBox.requestFocus();
				updateSubscribers();
			}
		};

		markerMobilityBox.addActionListener(al);
		drawAvatarBox.addActionListener(al);
		drawMarkersBox.addActionListener(al);
		numMarkersTextbox.addActionListener(al);

		speedSlider.addChangeListener(cl);
		scalingSlider.addChangeListener(cl);
	}

	public JPanel getUIElement() {
		return settingsPanel;
	}

	@Override
	public MarkerSettings getPublishableData() {		
		Integer markerCount = Integer
				.parseInt(numMarkersTextbox.getValue().toString());
		if (markerCount < 0) {
			markerCount = 0;
			numMarkersTextbox.setValue(0);
		} else if (markerCount > 20) {
			markerCount = 20;
			numMarkersTextbox.setValue(20);
		}		
		double speed = speedSlider.getValue() / 1000.0;
		double scale = scalingSlider.getValue() / 10.0;
		boolean moveMarkers = markerMobilityBox.isSelected();
		boolean drawAvatar = drawAvatarBox.isSelected();
		boolean drawMarkers = drawMarkersBox.isSelected();
		return new MarkerSettings(markerCount, speed, scale, moveMarkers,
				drawAvatar, drawMarkers);
	}

}
