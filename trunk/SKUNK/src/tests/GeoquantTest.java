package tests;

import Geoquant.Alpha;
import Geoquant.Eta;
import Geoquant.Length;
import Geoquant.Radius;
import InputOutput.TriangulationIO;
import Triangulation.*;

public class GeoquantTest {

  /**
   * @param args
   */
  public static void main(String[] args) {
    initializeQuantities();
    
    testLengths();
  }
  
  private static void initializeQuantities() {
    TriangulationIO.read2DTriangulationFile("Data/2DManifolds/StandardFormat/tetrahedron.txt");
    for(Vertex v : Triangulation.vertexTable.values()) {
      Radius.At(v).setValue(1.0);
      Alpha.At(v).setValue(1.0);
    }
    for(Edge e : Triangulation.edgeTable.values()) {
      Eta.At(e).setValue(1.0);
      System.out.println(Eta.At(e) + " at " + e + " = " + Eta.valueAt(e));
    }
  }
  
  private static void testLengths() {
    for(Edge e: Triangulation.edgeTable.values()) {
      System.out.println(Eta.At(e) + " at " + e + " = " + Eta.valueAt(e));
      System.out.println("Length at " + e + " = " + Length.valueAt(e));
    }
  }

}
