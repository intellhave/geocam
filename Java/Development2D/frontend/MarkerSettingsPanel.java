package frontend;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;
import java.util.Set;

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

import marker.Marker;
import marker.MarkerAppearance;
import marker.MarkerHandler;
import development.ManifoldPosition;
import development.Vector;

public class MarkerSettingsPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private MarkerHandler markerHandler;

	private JFormattedTextField numMarkersTextbox;
	private int currentMarkerCount;

	private JSlider speedSlider;
	private TitledBorder speedBorder;
	private double currentSpeed;

	private JSlider sizeSlider;
	private TitledBorder sizeBorder;
	private double currentSize;

	private JCheckBox markerMobilityBox;
	private JCheckBox drawAvatarBox;
	private JCheckBox drawMarkersBox;

	private static int MAX_SPEED = 100;
	private static int MAX_SIZE = 10;

	static final DecimalFormat speedFormat = new DecimalFormat("0.00");

	public MarkerSettingsPanel(MarkerHandler mh) {
		initSwingObjects(); 	// Setup menus and record all preset values.
		initListeners(); 		// Prepare for user input.
		setMarkerHandler(mh); 	// Record the given marker handler and push
								// the settings in the menus onto the
								// markerhandler.		
	}

	private void initSwingObjects() {
		super.setBorder(BorderFactory.createTitledBorder("Object Controls"));
		super.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		// settingsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// Initialize Number of Objects Text Input
		numMarkersTextbox = new JFormattedTextField(NumberFormat.getIntegerInstance());
		numMarkersTextbox.setColumns(3);
		numMarkersTextbox.setValue(0);
		currentMarkerCount = 0;

		JPanel numObjectsPanel = new JPanel();
		numObjectsPanel.setLayout(new FlowLayout());
		numObjectsPanel.add(new JLabel("Number of Objects"));
		numObjectsPanel.add(numMarkersTextbox);
		numObjectsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		super.add(numObjectsPanel);

		JPanel sliderPanel = new JPanel();
		sliderPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
		sliderPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		super.add(sliderPanel);

		// Initialize speed slider.
		final DecimalFormat speedFormat = new DecimalFormat("0.00");
		speedSlider = new JSlider();
		speedSlider.setFocusable(false);
		speedSlider.setMaximum(MAX_SPEED);
		speedSlider.setValue(MAX_SPEED / 4);
		currentSpeed = 0.05 * (MAX_SPEED / 4);

		speedBorder = BorderFactory.createTitledBorder("");
		speedSlider.setBorder(speedBorder);
		speedBorder.setTitle("Object Speed (" + speedFormat.format(MAX_SPEED / 4) + ")");
		sliderPanel.add(speedSlider);

		// Initialize Scaling Slider.
		sizeSlider = new JSlider();
		sizeSlider.setFocusable(false); // Mouse input only.
		sizeSlider.setMaximum(MAX_SIZE);
		sizeSlider.setValue((int) 1.0 * 10);
		sizeBorder = BorderFactory.createTitledBorder("");
		sizeSlider.setBorder(sizeBorder);
		sizeBorder.setTitle("Object Size (" + sizeSlider.getValue() / 10.0 + ")");
		sliderPanel.add(sizeSlider);
		currentSize = sizeSlider.getValue() / 10.0;

		JPanel buttonPanel = new JPanel();
		buttonPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

		// Initialize buttons and checkboxes
		markerMobilityBox = new JCheckBox();
		markerMobilityBox.setText("Objects Move");
		markerMobilityBox.setSelected(false);
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
		super.add(buttonPanel);
	}

	private void initListeners() {
		numMarkersTextbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Integer numMarkers = Integer.parseInt(numMarkersTextbox.getValue().toString());
				if (numMarkers < 0) {
					numMarkers = 0;
				} else if (numMarkers > 20) {
					numMarkers = 20;
				}
				numMarkersTextbox.setValue(numMarkers);
				setMovingMarkerCount(numMarkers);
			}
		});

		markerMobilityBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				markerHandler.pauseSimulation(!markerMobilityBox.isSelected());
			}
		});

		drawAvatarBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean sourceVisible = drawAvatarBox.isSelected();
				markerHandler.getSourceMarker().setVisible(sourceVisible);
			}
		});

		drawMarkersBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean vis = drawMarkersBox.isSelected();
				setMarkerVisible(vis);
			}
		});

		sizeSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				currentSize = sizeSlider.getValue() / 10.0;
				sizeBorder.setTitle("Object Size (" + sizeSlider.getValue() / 10.0 + ")");
				setMarkerSize(currentSize);
			}
		});

		speedSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				double speed = speedSlider.getValue();
				String str = speedFormat.format(speed);
				speedBorder.setTitle("Object Speed (" + str + ")");
				setMarkerSpeed(speed * 0.05);
			}
		});
	}

	private void setMovingMarkerCount(int numMarkers) {
		currentMarkerCount = numMarkers;
		Set<Marker> markers = markerHandler.getAllMarkers();
		// Get the current number of moving markers.
		int currentMarkers = 0;
		for (Marker m : markers) {
			if (m.getMarkerType() == Marker.MarkerType.MOVING) {
				currentMarkers++;
			}
		}

		// If necessary, add moving markers.
		if (currentMarkers < numMarkers) {
			Random rand = new Random();
			ManifoldPosition pos;
			MarkerAppearance app;

			for (int ii = 0; ii < numMarkers - currentMarkers; ii++) {
				pos = new ManifoldPosition(markerHandler.getSourceMarker().getPosition());
				app = new MarkerAppearance(MarkerAppearance.ModelType.ANT, currentSize);
				double a = rand.nextDouble() * Math.PI * 2;
				Vector vel = new Vector(Math.cos(a), Math.sin(a));
				// Move the new marker away from the source point.
				vel.scale(0.25);
				pos.move(vel);

				Marker m = new Marker(pos, app, Marker.MarkerType.MOVING, vel);
				m.setSpeed(currentSpeed);
				markerHandler.addMarker(m);
			}
			setMarkerVisible(true); // Markers were added, so make them visible.
			drawMarkersBox.setSelected(true);
			markerHandler.updateMarkers(100);
		}

		// If necessary, remove moving markers.
		if (currentMarkers > numMarkers) {
			int counter = 0;
			for (Marker m : markers) {
				if (m.getMarkerType() == Marker.MarkerType.MOVING) {
					m.flagForRemoval();
					counter++;
				}
				if (counter == currentMarkers - numMarkers)
					break;
			}
		}
	}

	private void setMarkerSpeed(double speed) {
		currentSpeed = speed;
		for (Marker m : markerHandler.getAllMarkers())
			if (m.getMarkerType() == Marker.MarkerType.MOVING)
				m.setSpeed(speed);
	}

	private void setMarkerSize(double size) {
		currentSize = size;
		for (Marker m : markerHandler.getAllMarkers())
			if (m.getMarkerType() == Marker.MarkerType.MOVING)
				m.getAppearance().setSize(size);
	}

	private void setMarkerVisible(boolean visible) {
		for (Marker m : markerHandler.getAllMarkers())
			if (m.getMarkerType() == Marker.MarkerType.MOVING)
				m.setVisible(visible);

	}

	public void setMarkerHandler(MarkerHandler other) {
		markerHandler = other;

		setMovingMarkerCount(currentMarkerCount);
		setMarkerSpeed(currentSpeed);
		setMarkerSize(currentSize);

		markerHandler.getSourceMarker().setVisible(drawAvatarBox.isVisible());
		markerHandler.pauseSimulation(!markerMobilityBox.isSelected());
		setMarkerVisible(drawMarkersBox.isSelected());
	}
}
