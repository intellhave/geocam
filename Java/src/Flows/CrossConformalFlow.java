package Flows;

import Geoquant.Alpha;
import Geoquant.Eta;
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

    try {
      while(minRad.stepMin(radii) > 0.00001) {
        printArray(radii);
      }
    } catch (WrongDirectionException e) {
      return;
    }
    printArray(radii);
    
    EtaOptNEHR min = new EtaOptNEHR();
    double[] etas = getEtas();
    try {
      while(min.stepMax(etas) > 0.00001) {
        printArray(etas);
      }
    } catch (WrongDirectionException e) {
      return;
    }
    printArray(etas);
  }
   
  private static void initializeQuantities() {
    TriangulationIO.read3DTriangulationFile("Data/3DManifolds/StandardFormat/pentachoron.txt");
 
    for(Vertex v : Triangulation.vertexTable.values()) {
      Radius.At(v).setValue(1.0);
      Alpha.At(v).setValue(1.0);
    }
    for(Edge e : Triangulation.edgeTable.values()) {
      Eta.At(e).setValue(1.0);
    }
    Eta.At(Triangulation.edgeTable.get(1)).setValue(1.01);
  }
  
  private static void printArray(double[] arr) {
    System.out.print("[");
    for(int i = 0; i < arr.length - 1; i++) {
      System.out.print(arr[i] + ", ");
    }
    System.out.println(arr[arr.length-1] + "]");
  }
  
  private static double[] getLogRadii() {
    double[] values = new double[Triangulation.vertexTable.size()];
    int i = 0;
    for(Vertex v : Triangulation.vertexTable.values()) {
      values[i] = Math.log(Radius.valueAt(v));
      i++;
    }
    return values;
  }
  
  private static double[] getEtas() {
    double[] values = new double[Triangulation.edgeTable.size()];
    int i = 0;
    for(Edge e: Triangulation.edgeTable.values()) {
      values[i] = Eta.valueAt(e);
      i++;
    }
    return values;
  }
}
