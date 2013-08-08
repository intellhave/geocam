package gui;

import geoquant.Alpha;
import geoquant.Eta;
import geoquant.GeoRecorder;
import geoquant.Geometry;
import geoquant.Radius;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import solvers.Solver;
import solvers.implemented.conformaldiskflow;
import triangulation.Boundary;
import triangulation.Edge;
import triangulation.Face;
import triangulation.Triangulation;
import triangulation.Vertex;

public class ConformalDiskFlowDialog extends JDialog {
	private JButton cancelButton;
	private JRadioButton numStepsButton;
	private JPanel stoppingCondPanel;
	private ButtonGroup stoppingCondButtonGroup;
	private JTextField precisionTextField;
	private JTextField numStepsTextField;
	private JRadioButton precisionButton;
	private JButton runButton;
	private GeoquantViewer owner;
	private JLabel stepsizeLabel;
	private JTextField stepsizeTextField;
	private static GeoRecorder rec;
	private int numSteps;
	private JCheckBox addVertex;
	private JCheckBox initialize;
	private JCheckBox multiplicative;

	public ConformalDiskFlowDialog(GeoquantViewer owner) {
		super(owner);
		this.owner = owner;
		rec = new GeoRecorder();
		initGUI();
	}

	private boolean checkBoundry(){
		ArrayList<Vertex> verticies = new ArrayList<Vertex>();
		int count = 0;
		for(Edge e : Triangulation.edgeTable.values()){
			if(e.getLocalFaces().size() == 1){
				verticies.addAll(e.getLocalVertices());
				count++;
			}
		}
		if(count < 3){
			JOptionPane.showMessageDialog(null, "Triangulation must have a boundry");
			this.dispose();
			return false;
		}
		
		for(Vertex v : Triangulation.vertexTable.values()){
			count = 0;
			for(Vertex vv : verticies){
				if(vv.equals(v))
					count++;
			}
			if( count != 2){
				JOptionPane.showMessageDialog(null, "Triangulation must have a boundry");
				this.dispose();
				return false;
			}
		}
		return true;
	}
	private void initGUI() {
		try {
			GroupLayout thisLayout = new GroupLayout(
					(JComponent) getContentPane());
			getContentPane().setLayout(thisLayout);
			{
				cancelButton = new JButton();
				cancelButton.setText("Cancel");
				cancelButton.setAction(new RunAction("Cancel"));
			}
			{
				runButton = new JButton();
				runButton.setText("Run");
				runButton.setAction(new RunAction("Run"));
			}
			{
				stoppingCondPanel = new JPanel();
				GroupLayout jPanel1Layout = new GroupLayout(
						(JComponent) stoppingCondPanel);
				stoppingCondPanel.setLayout(jPanel1Layout);
				stoppingCondPanel.setBorder(BorderFactory.createTitledBorder(
						null, "Choose Stopping Condition",
						TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION));
				{
					numStepsTextField = new JTextField();
					numStepsTextField.setText("100");
					numStepsTextField
							.setHorizontalAlignment(SwingConstants.RIGHT);
				}
				{
					numStepsButton = new JRadioButton();
					numStepsButton.setText("Number of Steps");
					numStepsButton.setSelected(true);
					numStepsButton.setActionCommand("NS");
					numStepsButton
							.addActionListener(new StoppingCondListener());
					getStoppingCondButtonGroup().add(numStepsButton);
				}
				{
					precisionButton = new JRadioButton();
					precisionButton.setText("Precision");
					precisionButton.setActionCommand("P");
					precisionButton
							.addActionListener(new StoppingCondListener());
					getStoppingCondButtonGroup().add(precisionButton);
				}
				{
					addVertex = new JCheckBox();
					addVertex.setText("Add Vertex");
					initialize = new JCheckBox();
					initialize.setText("Initialize Quantities");
					multiplicative = new JCheckBox();
					multiplicative.setText("Multiplicative Conformal Structure");

				}
				{
					precisionTextField = new JTextField();
					precisionTextField.setText("0.001");
					precisionTextField.setEnabled(false);
					precisionTextField
							.setHorizontalAlignment(SwingConstants.RIGHT);
				}
				{
					stepsizeTextField = new JTextField();
					stepsizeTextField.setText("0.1");
					stepsizeTextField
							.setHorizontalAlignment(SwingConstants.RIGHT);
				}
				thisLayout
						.setVerticalGroup(thisLayout
								.createSequentialGroup()
								.addContainerGap()
								.addComponent(stoppingCondPanel, 0, 99,
										Short.MAX_VALUE)
								.addPreferredGap(
										LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										thisLayout
											.createParallelGroup(
													GroupLayout.Alignment.CENTER)
															.addComponent(
																	addVertex,
																	GroupLayout.Alignment.CENTER,
																	GroupLayout.PREFERRED_SIZE,
																	29,
																	GroupLayout.PREFERRED_SIZE)
															.addComponent(
																	initialize,
																	GroupLayout.Alignment.CENTER,
																	GroupLayout.PREFERRED_SIZE,
																	29,
																	GroupLayout.PREFERRED_SIZE)
															.addComponent(
																	multiplicative,
																	GroupLayout.Alignment.CENTER,
																	GroupLayout.PREFERRED_SIZE,
																	29,
																	GroupLayout.PREFERRED_SIZE))					
								.addPreferredGap(
										LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(
										thisLayout
												.createParallelGroup(
														GroupLayout.Alignment.BASELINE)
												.addComponent(
														cancelButton,
														GroupLayout.Alignment.BASELINE,
														GroupLayout.PREFERRED_SIZE,
														32,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(
														stepsizeTextField,
														GroupLayout.Alignment.BASELINE,
														GroupLayout.PREFERRED_SIZE,
														29,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(
														runButton,
														GroupLayout.Alignment.BASELINE,
														GroupLayout.PREFERRED_SIZE,
														29,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(
														getStepsizeLabel(),
														GroupLayout.Alignment.BASELINE,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.PREFERRED_SIZE))
								.addContainerGap(15, 15));
				thisLayout
						.setHorizontalGroup(thisLayout
								.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										thisLayout
												.createParallelGroup()
												.addGroup(
														GroupLayout.Alignment.LEADING,
														thisLayout
																.createSequentialGroup()
																.addComponent(
																		getStepsizeLabel(),
																		GroupLayout.PREFERRED_SIZE,
																		80,
																		GroupLayout.PREFERRED_SIZE)
																.addPreferredGap(
																		LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		stepsizeTextField,
																		GroupLayout.PREFERRED_SIZE,
																		136,
																		GroupLayout.PREFERRED_SIZE)
																.addGap(28)
																.addComponent(
																		runButton,
																		GroupLayout.PREFERRED_SIZE,
																		82,
																		GroupLayout.PREFERRED_SIZE)		
																.addPreferredGap(
																		LayoutStyle.ComponentPlacement.RELATED)
																.addComponent(
																		cancelButton,
																		GroupLayout.PREFERRED_SIZE,
																		82,
																		GroupLayout.PREFERRED_SIZE))
													.addGroup(
															thisLayout
																.createSequentialGroup()
																.addComponent(
																	addVertex,
																	50,
																	90,
																	GroupLayout.PREFERRED_SIZE)	
															.addGap(5)
															.addComponent(
																	initialize,
																	50,
																	130,
																	GroupLayout.PREFERRED_SIZE)
															.addGap(5)
															.addComponent(
																	multiplicative,
																	50,
																	210,
																	210))		
													.addGroup(
														thisLayout
																.createSequentialGroup()
																.addComponent(
																		stoppingCondPanel,
																		GroupLayout.PREFERRED_SIZE,
																		425,
																		GroupLayout.PREFERRED_SIZE)))
								.addContainerGap(35, Short.MAX_VALUE));
				thisLayout.linkSize(SwingConstants.VERTICAL, new Component[] {
						stepsizeTextField, runButton });
				thisLayout.linkSize(SwingConstants.HORIZONTAL, new Component[] {
						cancelButton, runButton });
				jPanel1Layout
						.setHorizontalGroup(jPanel1Layout
								.createSequentialGroup()
								.addGroup(
										jPanel1Layout
												.createParallelGroup()
												.addComponent(
														numStepsTextField,
														GroupLayout.Alignment.LEADING,
														GroupLayout.PREFERRED_SIZE,
														183,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(
														numStepsButton,
														GroupLayout.Alignment.LEADING,
														GroupLayout.PREFERRED_SIZE,
														183,
														GroupLayout.PREFERRED_SIZE))
								.addGap(20)
								.addGroup(
										jPanel1Layout
												.createParallelGroup()
												.addGroup(
														jPanel1Layout
																.createSequentialGroup()
																.addComponent(
																		precisionButton,
																		GroupLayout.PREFERRED_SIZE,
																		178,
																		GroupLayout.PREFERRED_SIZE)
																.addGap(0,
																		0,
																		Short.MAX_VALUE))
												.addGroup(
														jPanel1Layout
																.createSequentialGroup()
																.addGap(0,
																		0,
																		Short.MAX_VALUE)
																.addComponent(
																		precisionTextField,
																		GroupLayout.PREFERRED_SIZE,
																		177,
																		GroupLayout.PREFERRED_SIZE)))
								.addContainerGap());
				jPanel1Layout
						.setVerticalGroup(jPanel1Layout
								.createSequentialGroup()
								.addGroup(
										jPanel1Layout
												.createParallelGroup(
														GroupLayout.Alignment.BASELINE)
												.addComponent(
														precisionButton,
														GroupLayout.Alignment.BASELINE,
														GroupLayout.PREFERRED_SIZE,
														21,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(
														numStepsButton,
														GroupLayout.Alignment.BASELINE,
														GroupLayout.PREFERRED_SIZE,
														20,
														GroupLayout.PREFERRED_SIZE))
								.addGap(14)
								.addGroup(
										jPanel1Layout
												.createParallelGroup(
														GroupLayout.Alignment.BASELINE)
												.addComponent(
														precisionTextField,
														GroupLayout.Alignment.BASELINE,
														GroupLayout.PREFERRED_SIZE,
														23,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(
														numStepsTextField,
														GroupLayout.Alignment.BASELINE,
														GroupLayout.PREFERRED_SIZE,
														22,
														GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(
										LayoutStyle.ComponentPlacement.RELATED));
			}
			{
				this.setSize(488, 200);
				this.setTitle("Conformal Disk Flow");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private ButtonGroup getStoppingCondButtonGroup() {
		if (stoppingCondButtonGroup == null) {
			stoppingCondButtonGroup = new ButtonGroup();
		}
		return stoppingCondButtonGroup;
	}

	private JLabel getStepsizeLabel() {
		if (stepsizeLabel == null) {
			stepsizeLabel = new JLabel();
			stepsizeLabel.setText("Stepsize:");
		}
		return stepsizeLabel;
	}

	private class StoppingCondListener implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			String name = evt.getActionCommand();
			if (name.equals("NS")) {
				precisionTextField.setEnabled(false);
				numStepsTextField.setEnabled(true);
			} else if (name.equals("P")) {
				precisionTextField.setEnabled(true);
				numStepsTextField.setEnabled(false);
			}
		}
	}


	private class RunAction extends AbstractAction {
		public RunAction(String text) {
			super(text, null);				
		}

		public void actionPerformed(ActionEvent evt) {
			if (evt.getSource().equals(runButton)) {
				if(initialize.isSelected()){
					if(multiplicative.isSelected())
						initializeForMultiplicativeConformalStructure();
					else
						initializeQuantities();
				}
				if(addVertex.isSelected())
					addNewVertex();
				
				
				Solver solver = new conformaldiskflow();

				double[] radii = new double[Triangulation.vertexTable.size()];
				int i = 0;
				for (Radius r : Geometry.getRadii()) {
					radii[i] = r.getValue();
					i++;
				}
				if (numStepsButton.isSelected()) {
					try {
						numSteps = Integer
								.parseInt(numStepsTextField.getText());
						double stepsize = Double.parseDouble(stepsizeTextField
								.getText());
						solver.setStepsize(stepsize);
						rec = owner.getRecorder();
						solver.addObserver(rec);
						solver.run(radii, numSteps);
						solver.deleteObserver(rec);

						owner.getPolygonPanel().setRecorder(rec);
						owner.newFlow();
						owner.getPolygonPanel().repaint();
					} catch (NumberFormatException ex) {

					}
				} else {
					try {
						double precision = Double
								.parseDouble(precisionTextField.getText());
						double stepsize = Double.parseDouble(stepsizeTextField
								.getText());
						solver.setStepsize(stepsize);
						solver.setStoppingCondition(precision);
						rec = owner.getRecorder();
						solver.addObserver(rec);
						solver.run(radii);
						solver.deleteObserver(rec);

						owner.getPolygonPanel().setRecorder(rec);
						owner.newFlow();
						owner.getPolygonPanel().repaint();
					} catch (NumberFormatException ex) {

					}
				}
			}
			if(addVertex.isSelected()){
				for(Vertex v : Triangulation.vertexTable.values()){
					if(v.getMultiplicity() == -1){
						v.remove();
						break;
					}
				}
			}
			ConformalDiskFlowDialog.this.dispose();
		}

	}

	private void initializeQuantities() {
		System.out.println("In initializeQuantities");			
		
		for (Radius r : Geometry.getRadii()) {
			r.setValue(1.0);
		}
		for (Alpha a : Geometry.getAlphas()) {
			a.setValue(1.0);
		}
		for (Eta e : Geometry.getEtas()) {
			e.setValue(1.0);
		}
		for (Vertex v : Triangulation.vertexTable.values()) {
			v.setMultiplicity(1);
		}
		for (Edge e : Triangulation.edgeTable.values()) {
			e.setMultiplicity(-1);
		}
		for (Face f : Triangulation.faceTable.values()) {
			f.setMultiplicity(1);
			f.setColor(Color.YELLOW);
		}

		Boundary.makeBoundary();

		for (Vertex v : Boundary.boundaryVertexTable.values()) {
			v.setMultiplicity(0);
		}

		for (Edge e : Boundary.boundaryEdgeTable.values()) {
			e.setMultiplicity(0);
		}
	}
	
	private void initializeForMultiplicativeConformalStructure() {
		System.out.println("In initializeForMultiplicativeConformalStructure");			
		
		for (Radius r : Geometry.getRadii()) {
			if(r.getValue() == 0)
				r.setValue(1.0);
		}
		for (Alpha a : Geometry.getAlphas()) {
			a.setValue(0);
		}
		for (Eta e : Geometry.getEtas()) {
			e.setValue(1.0);
		}
		for (Vertex v : Triangulation.vertexTable.values()) {
			v.setMultiplicity(1);
		}
		for (Edge e : Triangulation.edgeTable.values()) {
			e.setMultiplicity(-1);
		}
		for (Face f : Triangulation.faceTable.values()) {
			f.setMultiplicity(1);
			f.setColor(Color.YELLOW);
		}

		Boundary.makeBoundary();

		for (Vertex v : Boundary.boundaryVertexTable.values()) {
			v.setMultiplicity(0);
		}

		for (Edge e : Boundary.boundaryEdgeTable.values()) {
			e.setMultiplicity(0);
		}
	}
	
	private void addNewVertex(){
		System.out.println("In addNewVertex");

		// New Vertex
		Vertex newV = new Vertex(Triangulation.greatestVertex() + 1);
		newV.setMultiplicity(-1);
		Triangulation.putVertex(newV);

		Radius.at(newV).setValue(8);
		Alpha.at(newV).setValue(1);

		// New Edges

		for (Vertex v : Boundary.boundaryVertexTable.values()) {
			Edge newE = new Edge(Triangulation.greatestEdge() + 1);
			newE.setMultiplicity(1);
			newE.addVertex(v);
			newE.addVertex(newV);

			for (Edge e : v.getLocalEdges()) {
				e.addEdge(newE);
				newE.addEdge(e);
			}
			for (Edge e : newV.getLocalEdges()) {
				e.addEdge(newE);
				newE.addEdge(e);
			}
			System.out.println(newE);
			Triangulation.putEdge(newE);

			newV.addEdge(newE);
			newV.addVertex(v);
			v.addVertex(newV);
			v.addEdge(newE);
			
			if(multiplicative.isSelected())
				Eta.at(newE).setValue(0);
			else
				Eta.at(newE).setValue(-1);

		}
		// At this point, the vertex has neighbor vertices and edges
		// new edges have neighbor vertices, some neighbor edges

		// New Faces

		for (Edge e : Boundary.boundaryEdgeTable.values()) {
			Face newF = new Face(Triangulation.greatestFace() + 1);
			newF.setMultiplicity(-1);
			newF.addVertex(newV);
			newF.addEdge(e);
			for (Face f : e.getLocalFaces()) {
				newF.addFace(f);
				f.addFace(newF);
				newF.setColor(Color.RED);
			}
			e.addFace(newF);
			newV.addFace(newF);

			for (Vertex v : e.getLocalVertices()) {
				newF.addVertex(v);
				v.addFace(newF);

				for (Edge ed : v.getLocalEdges()) {
					if (ed.isAdjVertex(newV)) {
						newF.addEdge(ed);
						for (Face fa : ed.getLocalFaces()) {
							newF.addFace(fa);
							fa.addFace(newF);
						}
						ed.addFace(newF);
					}
				}
			}
			Triangulation.putFace(newF);

		}
	}
}
