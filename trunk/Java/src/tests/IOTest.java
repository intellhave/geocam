package tests;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import triangulation.Edge;
import triangulation.Face;
import triangulation.Tetra;
import triangulation.Triangulation;
import triangulation.Vertex;

import Geoquant.Radius;
import InputOutput.TriangulationIO;

public class IOTest {

  public static void main(String[] args) {
    testXMLReader();
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
  
  private static void test3DLutz() {
    TriangulationIO.read3DLutzFile("Data/3DManifolds/LutzFormat/pentachoron.txt");
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
  
  public static void test2DWrite() {
    TriangulationIO.read2DLutzFile("Data/2DManifolds/LutzFormat/tetrahedron.txt");
    TriangulationIO.write2DTriangulationFile("Data/2DManifolds/StandardFormat/tetra_test.txt");
  }
  
  public static void testXMLReader() {
    TriangulationIO.readTriangulation("Data/Triangulations/3DManifolds/pentachoron.xml");
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

  public static void testXMLWriter() {
    TriangulationIO.readTriangulation("Data/xml/3DManifolds/pentachoron_regular.xml");
    Radius.At(Triangulation.vertexTable.get(1)).setValue(2.0);
    TriangulationIO.writeTriangulation("Data/xml/3DManifolds/pentachoron_mod.xml");
  }
}
