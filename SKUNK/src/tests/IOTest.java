package tests;

import java.io.File;

import InputOutput.TriangulationIO;
import Triangulation.Edge;
import Triangulation.Face;
import Triangulation.Triangulation;
import Triangulation.Vertex;

public class IOTest {

  public static void main(String[] args) {
    TriangulationIO.read2DTriangulationFile("Data/2DManifolds/StandardFormat/tetrahedron.txt");
    
    for(Vertex v : Triangulation.vertexTable.values()) {
      System.out.println(v + ":\n\t" +
          v.getLocalVertices() + "\n\t" + v.getLocalEdges() + "\n\t" + v.getLocalFaces());
    }
    for(Edge e : Triangulation.edgeTable.values()) {
      System.out.println(e + ":\n\t" +
          e.getLocalVertices() + "\n\t" + e.getLocalEdges() + "\n\t" + e.getLocalFaces());
    }
    for(Face f : Triangulation.faceTable.values()) {
      System.out.println(f + ":\n\t" +
          f.getLocalVertices() + "\n\t" + f.getLocalEdges() + "\n\t" + f.getLocalFaces());
    }
  }

}
