package frontend;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import geoquant.Geometry;
import geoquant.Radius;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import development.Development;

import solvers.Solver;
import solvers.implemented.Yamabe2DFlow;
import triangulation.Triangulation;
import view.View;

public class FlowButtonPanel extends JPanel implements ViewController {

	private static final long serialVersionUID = 1L;

	private Development development;
	private List<View> views;

	public FlowButtonPanel(Development dev) {
		this.development = dev;
		this.views = new LinkedList<View>();

		JButton flowButton = new JButton();
		flowButton.setText("Run 2D Yamabe Flow");
		
		super.setLayout(new FlowLayout());
		super.setBorder(BorderFactory.createTitledBorder("Geometric Flow Controls"));
		super.add(flowButton);

		flowButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				// As of now, the yamabe flow only works with xml files and only
				// xml files with length data
				runFlow();
			}			
		});
	}

	public void runFlow() {
		double[] radiiLengths = new double[Triangulation.vertexTable.size()];
		Radius[] radii = new Radius[Triangulation.vertexTable.size()];

		int i = 0;

		for (Radius r : Geometry.getRadii()) {
			radiiLengths[i] = r.getValue();
			radii[i] = r;
			i++;
		}

		Solver solver = new Yamabe2DFlow();
		solver.setStoppingCondition(0.001);
		solver.setStepsize(0.002);
		for (int j = 0; j < 100; j++) {
			radiiLengths = solver.run(radiiLengths, j);
			for (i = 0; i < Triangulation.vertexTable.size(); i++) {
				radii[i].setValue(radiiLengths[i]);
			}

			development.rebuild();
			updateViews();
		}

		System.out.println("runFlow()");
	}

	private void updateViews() {
		for(View v : views){
			v.update(null, null);
		}
		
	}

	@Override
	public void addView(View v) {
		views.add(v);
	}

	@Override
	public void removeView(View v) {
		views.remove(v);
	}

	@Override
	public void removeAllViews() {
		views.clear();
	}
}
