package tests;

import inputOutput.TriangulationIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import triangulation.Boundary;
import triangulation.Triangulation;

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
  
