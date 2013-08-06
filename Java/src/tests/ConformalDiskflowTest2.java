package tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.awt.Color;

import javax.swing.JFrame;

import geoquant.Alpha;
import geoquant.LKCurvature;
import geoquant.Eta;
import geoquant.GeoRecorder;
import geoquant.Geometry;
import geoquant.Geoquant;
import geoquant.Length;
import geoquant.Radius;
import inputOutput.TriangulationIO;

import solvers.Solver;
import solvers.implemented.conformaldiskflow;
import triangulation.Boundary;
import triangulation.Edge;
import triangulation.Face;
import triangulation.Triangulation;
import triangulation.Vertex;
import development.*;
import visualization.*;
import de.jreality.geometry.Primitives;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.DirectionalLight;
import de.jreality.scene.SceneGraphComponent;

public class ConformalDiskflowTest2 {
	
	public static void main(String[] args) {
		TriangulationIO
				.readTriangulation("Data/Triangulations/2DManifolds/domain.xml");

		System.out.println("Before flow:");
		for (Vertex v : Triangulation.vertexTable.values()) {
			LKCurvature lk = new LKCurvature(v);
			System.out.println(lk);
		}

		for (Edge e : Triangulation.edgeTable.values()) {
			System.out.println("len=" + Length.valueAt(e));
			double radsum = 0;
			for (Vertex v : e.getLocalVertices()) {
				radsum += Radius.valueAt(v);
			}
			System.out.println("radsum=" + radsum);
		}
		
		testFlow();
		
		System.out.println("\n\n\nAfter flow:");
		for (Vertex v : Triangulation.vertexTable.values()) {
			LKCurvature lk = new LKCurvature(v);
			System.out.println(lk);
		}

		for (Edge e : Triangulation.edgeTable.values()) {
			System.out.println("len=" + Length.valueAt(e));
			double radsum = 0;
			for (Vertex v : e.getLocalVertices()) {
				radsum += Radius.valueAt(v);
			}
			System.out.println("radsum=" + radsum);
		}
	}
	
	private static void testFlow() {
		Solver solver = new conformaldiskflow();

		List<Class<? extends Geoquant>> list = new LinkedList<Class<? extends Geoquant>>();
		list.add(Radius.class);
		list.add(LKCurvature.class);
		GeoRecorder rec = new GeoRecorder(list);
		solver.addObserver(rec);
		double[] radii = new double[Triangulation.vertexTable.size()];
		int i = 0;
		for (Radius r : Geometry.getRadii()) {
			radii[i] = r.getValue();
			i++;
		}

		solver.setStepsize(0.05);

		radii = solver.run(radii, 100);

		int j = 0;
		for (Vertex v : Triangulation.vertexTable.values()) {
			Radius.at(v).setValue(radii[j]);
			j++;
		}

		PrintStream out = null;
		try {
			out = new PrintStream(new File("Data/Tests/flowdata.txt"));
		} catch (FileNotFoundException e1) {
			return;
		}
		System.err.println(out);

		System.out.println("CURVATURES:");
		for (List<Double> values : rec.getValueHistory(LKCurvature.class)) {
			System.out.println(values);
		}
	}

}
