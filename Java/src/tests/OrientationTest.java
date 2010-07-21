package tests;

import io.TriangulationIO;
import triangulation.Face;
import triangulation.StdFace;
import triangulation.Triangulation;

public class OrientationTest {
  
  public static void main(String[] args) {
    TriangulationIO.read2DTriangulationFile("Data/flip_test/eight_triangles_redux.txt");
    StdFace.generateOrientation();
    
    for (Face embeddedFace : Triangulation.faceTable.values()) {
      System.out.println("Face " + embeddedFace.getIndex() + " : " + StdFace.getOrientedFace(embeddedFace));
      StdFace s = StdFace.getOrientedFace(embeddedFace);
      System.out.println("\t" + s.e12 + " " + s.e23 + " " + s.e13);
    }
  }

}
