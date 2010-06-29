package Flows;

import Geoquant.Alpha;
import Geoquant.Eta;
import Geoquant.Geometry;
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
        
    EtaOptNEHR min = new EtaOptNEHR();
    double[] etas = getEtas();
    while(min.step(etas) > 0.00001) {
      printArray(etas);
    }
    printArray(etas);
    System.out.println("DONE!");
  }
   
  public static void initializeQuantities() {
    TriangulationIO.readTriangulation("Data/Triangulations/CommonManifolds/pentachoron_test.xml");
    for(Eta e : Geometry.getEtas()) {
      e.setValue(1.0);
    }
    Eta.At(Triangulation.edgeTable.get(1)).setValue(1.01);
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
