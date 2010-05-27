package tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import Geoquant.Radius;
import InputOutput.TriangulationIO;
import Triangulation.Edge;
import Triangulation.Face;
import Triangulation.Tetra;
import Triangulation.Triangulation;
import Triangulation.Vertex;

public class IOTest {

  public static void main(String[] args) {
    test2DLutz();
  }
    
  private static void testReplace() {
    Scanner scanner = null;
    String s;
    try {
      scanner = new Scanner(new File("Data/2DManifolds/LutzFormat/tetrahedron.txt"));
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    s = "";
    while(scanner.hasNextLine()) {
      s = s.concat(scanner.nextLine());
    }
    s = s.replaceAll("[^0-9],[^0-9]", "\n");
    s = s.replaceAll(",", " ");
    s = s.replaceAll("[^0-9 \n]", "");
    System.out.println(s);
  }
  
  private static void test2DReader() {
    TriangulationIO.read2DTriangulationFile("Data/2DManifolds/StandardFormat/tetrahedron mod.txt");
    
    for(Vertex v : Triangulation.vertexTable.values()) {
      System.out.println(v + ":\n\t" +
          v.getLocalVertices() + "\n\t" + v.getLocalEdges() + "\n\t" + v.getLocalFaces());
      if(v.getIndex() == 1) {
        System.out.println("Radius = " + Radius.valueAt(v));
      }
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
  
  private static void test2DLutz() {
    TriangulationIO.read2DLutzFile("Data/2DManifolds/LutzFormat/tetrahedron.txt");
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

}
