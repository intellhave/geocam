package tests;

import InputOutput.TriangulationIO;
import Triangulation.Face;
import Triangulation.StdFace;
import Triangulation.Triangulation;

public class OrientationTest {
  
  public static void main(String[] args) {
    TriangulationIO.read2DTriangulationFile("Data/flip_test/eight_triangles_redux.txt");
    StdFace.generateOrientation();
    
    for (Face face : Triangulation.faceTable.values()) {
      System.out.println("Face " + face.getIndex() + " : " + StdFace.getOrientedFace(face));
      StdFace s = StdFace.getOrientedFace(face);
      System.out.println("\t" + s.e12 + " " + s.e23 + " " + s.e13);
    }
  }

}
