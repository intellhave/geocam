package tests;

import geoquant.Curvature2D;
import geoquant.LKCurvature;
import inputOutput.TriangulationIO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import triangulation.Edge;
import triangulation.Face;
import triangulation.Triangulation;
import triangulation.Vertex;


public class lkcurvtest {
  
  public static void main(String[] args) {
   TriangulationIO.readTriangulation("Data/Triangulations/2DManifolds/tetrahedron2.xml");
   for(Vertex v: Triangulation.vertexTable.values()) {
     v.setMultiplicity(1);
   }
   for(Edge e: Triangulation.edgeTable.values()) {
     e.setMultiplicity(-1);
   }
   for(Face f: Triangulation.faceTable.values()) {
     f.setMultiplicity(1);
   }
   
   testLKCurvature();

  }

  private static void testLKCurvature() {
    PrintStream out = null;
    try {
      out = new PrintStream(new File("Data/Tests/LKCurvature.txt"));
    } catch (FileNotFoundException e1) {
      return;
    }
    
    Curvature2D cu2;
    LKCurvature lk;
    for (Vertex v : Triangulation.vertexTable.values()){
      cu2 = Curvature2D.at(v);
      lk = LKCurvature.at(v);
      out.println(cu2);
      out.println(lk);
    }
    out.close();
    System.out.println("Done with Curvatures.");
  }
}