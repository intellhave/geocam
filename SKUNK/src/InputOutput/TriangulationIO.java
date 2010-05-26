package InputOutput;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import Geoquant.Length;
import Geoquant.Radius;
import Triangulation.*;

public class TriangulationIO {
  private TriangulationIO() {
    
  }
  
  public static void read2DTriangulationFile(String filename) {
    read2DTriangulationFile(new File(filename));
  }
  
  public static void read2DTriangulationFile(File file) {
    Scanner fileScanner;
    Scanner stringScanner;
    String type;
    Simplex simplex;
    int index;
    Vertex v;
    Edge e;
    Face f;
    
    try {
      fileScanner = new Scanner(file);
    } catch (FileNotFoundException ex) {
      // TODO Auto-generated catch block
      System.err.print("File given by " + file + " could not be found\n");
      ex.printStackTrace();
      return;
    }
    
    while(fileScanner.hasNextLine()) {
      stringScanner = new Scanner(fileScanner.nextLine());
      type = stringScanner.next();
      index = stringScanner.nextInt();
      
      if(type.compareTo("Vertex:") == 0) {
        simplex = Triangulation.vertexTable.get(index);
        if(simplex == null) {
          simplex = new Vertex(index);
          Triangulation.putVertex((Vertex) simplex);
        }
      } else if(type.compareTo("Edge:") == 0) {
          simplex = Triangulation.edgeTable.get(index);
          if(simplex == null) {
            simplex = new Edge(index);
            Triangulation.putEdge((Edge) simplex);
          }       
      } else if(type.compareTo("Face:") == 0) {
          simplex = Triangulation.faceTable.get(index);
          if(simplex == null) {
            simplex = new Face(index);
            Triangulation.putFace((Face) simplex);
          }   
      } else {
        System.err.print("Invald simplex type " + type + "\n");
        Triangulation.reset();
        return;
      }
      
      // Local vertices
      stringScanner = new Scanner(fileScanner.nextLine());
      while(stringScanner.hasNextInt()) {
        index = stringScanner.nextInt();
        v = Triangulation.vertexTable.get(index);
        if(v == null) {
          v = new Vertex(index);
          Triangulation.putVertex(v);
        }
        simplex.addVertex(v);
      }
      
      // Local edges
      stringScanner = new Scanner(fileScanner.nextLine());
      while(stringScanner.hasNextInt()) {
        index = stringScanner.nextInt();
        e = Triangulation.edgeTable.get(index);
        if(e == null) {
          e = new Edge(index);
          Triangulation.putEdge(e);
        }
        simplex.addEdge(e);
      }
      
      // Local faces
      stringScanner = new Scanner(fileScanner.nextLine());
      while(stringScanner.hasNextInt()) {
        index = stringScanner.nextInt();
        f = Triangulation.faceTable.get(index);
        if(f == null) {
          f = new Face(index);
          Triangulation.putFace(f);
        }
        simplex.addFace(f);
      }
      
      // Check for radii / lengths
      if(fileScanner.hasNextDouble()) {
        if(type.compareTo("Vertex:") == 0) {
          Radius.At((Vertex) simplex).setValue(fileScanner.nextDouble());
        } else if(type.compareTo("Edge:") == 0) {
          Length.At((Edge) simplex).setValue(fileScanner.nextDouble());
        }
      }
    }
  }
}
