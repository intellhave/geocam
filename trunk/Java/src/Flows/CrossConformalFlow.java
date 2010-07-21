package Flows;

import triangulation.Edge;
import triangulation.Triangulation;
import triangulation.Vertex;
import Geoquant.Alpha;
import Geoquant.Eta;
import Geoquant.Geometry;
import Geoquant.VEHR;
import Geoquant.Radius;
import InputOutput.TriangulationIO;
import Solvers.WrongDirectionException;


public class CrossConformalFlow {
  public static void main(String[] args) {
    initializeQuantities();
    
    // Comment out all others but desired
    runMinRadii();
//    runMaxRadii();
//    runRadii();
//    runMinEtas();
//    runMaxEtas();
//    runEtas();
  }
   
  public static void initializeQuantities() {
    TriangulationIO.readTriangulation("Data/Triangulations/CommonManifolds/pentachoron_test.xml");
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
  
  public static void runMinRadii() {
    RadiusOptNEHR flow = new RadiusOptNEHR();
    double[] log_radii = flow.getLogRadii();
    try {
      while(flow.stepMin(log_radii) > 0.00001) {
        printArray(log_radii);
      }
    } catch (WrongDirectionException e) {
    }
    printArray(log_radii);
    System.out.println("DONE!");
  }
  
  public static void runMaxRadii() {
    RadiusOptNEHR flow = new RadiusOptNEHR();
    double[] log_radii = flow.getLogRadii();
    try {
      while(flow.stepMax(log_radii) > 0.00001) {
        printArray(log_radii);
      }
    } catch (WrongDirectionException e) {
    }
    printArray(log_radii);
    System.out.println("DONE!");
  }
  
  public static void runRadii() {
    RadiusOptNEHR flow = new RadiusOptNEHR();
    double[] log_radii = flow.getLogRadii();
      while(flow.step(log_radii) > 0.00001) {
        printArray(log_radii);
      }
    printArray(log_radii);
    System.out.println("DONE!");
  }
  
  public static void runMinEtas() {
    EtaOptNEHR flow = new EtaOptNEHR();
    double[] etas = flow.getEtas();
    try {
      while(flow.stepMin(etas) > 0.00001) {
        printArray(etas);
      }
    } catch (WrongDirectionException e) {
    }
    printArray(etas);
    System.out.println("DONE!");
  }
  
  public static void runMaxEtas() {
    EtaOptNEHR flow = new EtaOptNEHR();
    double[] etas = flow.getEtas();
    try {
      while(flow.stepMax(etas) > 0.00001) {
        printArray(etas);
      }
    } catch (WrongDirectionException e) {
    }
    printArray(etas);
    System.out.println("DONE!");
  }
  
  public static void runEtas() {
    EtaOptNEHR flow = new EtaOptNEHR();
    double[] etas = flow.getEtas();
      while(flow.step(etas) > 0.00001) {
        printArray(etas);
      }
    printArray(etas);
    System.out.println("DONE!");
  }
}
