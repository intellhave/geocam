package tests;

import java.io.File;

import InputOutput.TriangulationIO;
import Triangulation.Edge;
import Triangulation.Face;
import Triangulation.Tetra;
import Triangulation.Triangulation;
import Triangulation.Vertex;

public class IOTest {

  public static void main(String[] args) {
    test3DReader();
  }
  
  private static void test2DReader() {
    TriangulationIO.read2DTriangulationFile("Data/2DManifolds/StandardFormat/tetrahedron.txt");
    
    for(Vertex v : Triangulation.vertexTable.values()) {
      System.out.println(v + ":\n\t" +
          v.getLocalVertices() + "\n\t" + v.getLocalEdges() + "\n\t" + v.getLocalFaces());
    }
    System.out.println();
    for(Edge e : Triangulation.edgeTable.values()) {
      System.out.println(e + ":\n\t" +
          e.getLocalVertices() + "\n\t" + e.getLocalEdges() + "\n\t" + e.getLocalFaces());
    }
    System.out.println();
    for(Face f : Triangulation.faceTable.values()) {
      System.out.println(f + ":\n\t" +
          f.getLocalVertices() + "\n\t" + f.getLocalEdges() + "\n\t" + f.getLocalFaces());
    }
  }
  
  private static void test3DReader() {
    TriangulationIO.read3DTriangulationFile("Data/3DManifolds/StandardFormat/pentachoron.txt");
    
    for(Vertex v : Triangulation.vertexTable.values()) {
      System.out.println(v + ":\n\t" +
          v.getLocalVertices() + "\n\t" + v.getLocalEdges() + "\n\t" + v.getLocalFaces() +
          "\n\t" + v.getLocalTetras());
    }
    System.out.println();
    for(Edge e : Triangulation.edgeTable.values()) {
      System.out.println(e + ":\n\t" +
          e.getLocalVertices() + "\n\t" + e.getLocalEdges() + "\n\t" + e.getLocalFaces() +
          "\n\t" + e.getLocalTetras());
    }
    System.out.println();
    for(Face f : Triangulation.faceTable.values()) {
      System.out.println(f + ":\n\t" +
          f.getLocalVertices() + "\n\t" + f.getLocalEdges() + "\n\t" + f.getLocalFaces() +
          "\n\t" + f.getLocalTetras());
    }
    System.out.println();
    for(Tetra t : Triangulation.tetraTable.values()) {
      System.out.println(t + ":\n\t" +
          t.getLocalVertices() + "\n\t" + t.getLocalEdges() + "\n\t" + t.getLocalFaces() +
          "\n\t" + t.getLocalTetras());
    }
  }

}
