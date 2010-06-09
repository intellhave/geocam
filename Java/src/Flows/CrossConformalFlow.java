package Flows;

import Geoquant.Alpha;
import Geoquant.Eta;
import Geoquant.NEHR;
import Geoquant.Radius;
import InputOutput.TriangulationIO;
import Solvers.WrongDirectionException;
import Triangulation.Triangulation;
import Triangulation.Edge;
import Triangulation.Vertex;


public class CrossConformalFlow {
  public static void main(String[] args) {
    initializeQuantities();
    
    RadiusOptNEHR minRad = new RadiusOptNEHR();
    double[] radii = getLogRadii();
//    minRad.setStoppingCondition(0.0);
//    minRad.setStepRatio(1.0);
//    try {
//      double[] log_radii = getLogRadii();
//      for(int i = 0; i < 10; i++) {
//        minRad.step(log_radii);
//        setLogRadii(log_radii);
//      }
//    } catch (Exception e) {
//      return;
//    }
    
   
//    radii = getLogRadii();
//    for(int i = 0; i < radii.length; i++) {
//      System.out.print(Math.exp(radii[i]) + ", ");
//    }
//    System.out.println();
    
    minRad.setDefaults();
    minRad.setStoppingCondition(0.0000001);
    radii = getLogRadii();
    try {
      radii = minRad.minimize(radii);
    } catch (WrongDirectionException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    
    System.out.println(NEHR.value());
    
    
    for(int i = 0; i < radii.length; i++) {
      System.out.print(Math.exp(radii[i]) + ", ");
    }
    System.out.println();
    
    EtaOptNEHR min = new EtaOptNEHR();
   // min.setStoppingCondition(0.0);
    double[] etas = getEtas();
    try {
      while(min.stepMin(etas) > 0.00001) {
        printArray(etas);
      }
    } catch (WrongDirectionException e) {
      return;
    }
    printArray(etas);
  }
   
  public static void initializeQuantities() {
    TriangulationIO.read3DTriangulationFile("Data/3DManifolds/StandardFormat/pentachoron.txt");
 
    for(Vertex v : Triangulation.vertexTable.values()) {
      Radius.At(v).setValue(1.0);
      Alpha.At(v).setValue(1.0);
    }
    for(Edge e : Triangulation.edgeTable.values()) {
      Eta.At(e).setValue(1.0);
    }
    Eta.At(Triangulation.edgeTable.get(1)).setValue(1.1);
  }
  
  public static void printArray(double[] arr) {
    System.out.print("[");
    for(int i = 0; i < arr.length - 1; i++) {
      System.out.print(arr[i] + ", ");
    }
    System.out.println(arr[arr.length-1] + "]");
  }
  
  public static double[] getLogRadii() {
    double[] values = new double[Triangulation.vertexTable.size()];
    int i = 0;
    for(Vertex v : Triangulation.vertexTable.values()) {
      values[i] = Math.log(Radius.valueAt(v));
      i++;
    }
    return values;
  }
  
  public static void setLogRadii(double[] vars) {
    int i = 0;
    for(Vertex v : Triangulation.vertexTable.values()) {
      Radius.At(v).setValue(Math.exp(vars[i]));
      i++;
    }
  }
  
  public static double[] getEtas() {
    double[] values = new double[Triangulation.edgeTable.size()];
    int i = 0;
    for(Edge e: Triangulation.edgeTable.values()) {
      values[i] = Eta.valueAt(e);
      i++;
    }
    return values;
  }
}
