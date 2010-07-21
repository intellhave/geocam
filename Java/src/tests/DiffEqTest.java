package tests;

import geoquant.Curvature2D;
import geoquant.Eta;
import geoquant.GeoRecorder;
import geoquant.Geometry;
import geoquant.Geoquant;
import geoquant.Radius;
import inputOutput.TriangulationIO;

import java.util.LinkedList;
import java.util.List;

import solver.DESystem;
import solver.EulerSolver;
import solver.Yamabe2DFlow;
import triangulation.Triangulation;


public class DiffEqTest {

  public static void main(String[] args) {
    initializeQuantities();
    testYamabe2DFlow();

  }
  
  private static void initializeQuantities() {
    TriangulationIO.readTriangulation("Data/Triangulations/2DManifolds/tetrahedron.xml");
    for(Radius r : Geometry.getRadii()) {
      r.setValue(1.0);
    }
    for(Eta e : Geometry.getEtas()) {
      e.setValue(1.0);
    }
  }

  private static void testYamabe2DFlow() {
    DESystem sys = new Yamabe2DFlow();
    EulerSolver solver = new EulerSolver(sys);
    
    List<Class<? extends Geoquant>> list = new LinkedList<Class<? extends Geoquant>>();
    list.add(Radius.class);
    list.add(Curvature2D.class);
    GeoRecorder rec = new GeoRecorder(list);
    solver.addObserver(rec);
    double[] radii = new double[Triangulation.vertexTable.size()];
    int i = 0;
    for(Radius r : Geometry.getRadii()) {
      radii[i] = r.getValue() + i/5.0;
      i++;
    }
    
    radii = solver.run(radii, 0.1, 50);
    
    System.out.println("RADII:");
    for(List<Double> values : rec.getValueHistory(Radius.class)) {
      System.out.println(values);
    }
    
    System.out.println("CURVATURES:");
    for(List<Double> values : rec.getValueHistory(Curvature2D.class)) {
      System.out.println(values);
    }
  }
}
