package tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import geoquant.Curvature2D;
import geoquant.LKCurvature;
import inputOutput.TriangulationIO;
import triangulation.Triangulation;
import triangulation.Vertex;
import triangulation.Boundary;

public class boundarytest {
  
  public static void main(String[] args) {
    TriangulationIO.readTriangulation("Data/Triangulations/2DManifolds/domain1.xml");
    Boundary.makeBoundary();
    testBoundary();
  }
 
  private static void testBoundary() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/Boundary.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    out.println(Triangulation.vertexTable.values());
    out.println(Triangulation.faceTable.values());
    out.println(Boundary.boundaryVertexTable.values());
    out.println(Boundary.boundaryEdgeTable.values());
    
//    for (Vertex v : Boundary.boundaryVertexTable.values()){
//      System.out.println(v.getIndex());
//      System.out.println("go");
//    }
    out.close();
 //   System.out.println("Done with Boundary.");
  }
}
  
