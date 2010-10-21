package view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import triangulation.Face;
import triangulation.Triangulation;
import triangulation.Vertex;
import view.ColorScheme.schemes;
import development.Coord2D;
import development.EmbeddedTriangulation;
import development.Vector;

public class DevelopmentGUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		JFrame window = new DevelopmentGUI();
		window.setVisible(true);
	}

	private int maxDepth = 25;
	private int currentDepth = maxDepth;

	private static Development development;
	private static Vector sourcePoint;
	private static Face sourceFace;
	private static ColorScheme colorScheme;
	private static DevelopmentView2D view2D;
	private static DevelopmentView3D view3D;

	private JPanel sliderPanel;
	private JSlider depthSlider;
	private JLabel depthLabel = new JLabel("Max Recursion Depth ("
			+ currentDepth + ")");
	private JPanel colorPanel;

	public DevelopmentGUI() {
		EmbeddedTriangulation.readEmbeddedSurface("models/cone.off");

		Iterator<Integer> i = null;
		// pick some arbitrary face and source point
		i = Triangulation.faceTable.keySet().iterator();
		sourceFace = Triangulation.faceTable.get(i.next());

		sourcePoint = new Vector(0, 0);
		Iterator<Vertex> iv = sourceFace.getLocalVertices().iterator();
		while (iv.hasNext()) {
			sourcePoint.add(Coord2D.coordAt(iv.next(), sourceFace));
		}
		sourcePoint.scale(1.0f / 3.0f);

		colorScheme = new ColorScheme(schemes.DEPTH);
		development = new Development(sourceFace, sourcePoint, maxDepth,
				currentDepth);
		view2D = new DevelopmentView2D(development, colorScheme);
		development.addObserver(view2D);

		view3D = new DevelopmentView3D(development, colorScheme);
		development.addObserver(view3D);

		layoutGUI();
	}

	private void layoutGUI() {
		this.setSize(220, 200);
		this.setResizable(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		// this.setLayout(new BorderLayout());

		sliderPanel = new JPanel();
		sliderPanel.setLayout(new GridLayout(2, 1));
		depthSlider = new JSlider(1, maxDepth, currentDepth);
		depthSlider.setMaximumSize(new Dimension(400, 100));
		depthSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int val = ((JSlider) e.getSource()).getValue();
				currentDepth = val;
				depthLabel.setText("Recursion Depth (" + currentDepth + ")");
				development.setDesiredDepth(currentDepth);
			}
		});
		sliderPanel.add(depthLabel);
		sliderPanel.add(depthSlider);

		colorPanel = new JPanel();
		JButton depthSchemeButton = new JButton("Depth");
		depthSchemeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (colorScheme.getSchemeType() != schemes.DEPTH) {
					colorScheme = new ColorScheme(schemes.DEPTH);
					view2D.setColorScheme(colorScheme);
					view3D.setColorScheme(colorScheme);
				}
			}
		});
		JButton faceSchemeButton = new JButton("Face");
		faceSchemeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (colorScheme.getSchemeType() != schemes.FACE) {
					colorScheme = new ColorScheme(schemes.FACE);
					view2D.setColorScheme(colorScheme);
					view3D.setColorScheme(colorScheme);
				}
			}
		});
		colorPanel.setLayout(new GridLayout(3, 1));
		;
		colorPanel.add(new JLabel("Set Color Scheme"));
		colorPanel.add(depthSchemeButton);
		colorPanel.add(faceSchemeButton);

		this.setLayout(new FlowLayout());
		this.add(sliderPanel);
		this.add(colorPanel);
	}

}
