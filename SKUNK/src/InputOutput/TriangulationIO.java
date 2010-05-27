package InputOutput;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

import Geoquant.Length;
import Geoquant.Radius;
import Geoquant.TriPosition;
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
        stringScanner = new Scanner(fileScanner.nextLine());
        if(type.compareTo("Vertex:") == 0) {
          Radius.At((Vertex) simplex).setValue(stringScanner.nextDouble());
        } else if(type.compareTo("Edge:") == 0) {
          Length.At((Edge) simplex).setValue(stringScanner.nextDouble());
        }
      }
    }
  }
  
  public static void read3DTriangulationFile(String filename) {
    read3DTriangulationFile(new File(filename));
  }
  
  public static void read3DTriangulationFile(File file) {
    Scanner fileScanner;
    Scanner stringScanner;
    String type;
    Simplex simplex;
    int index;
    Vertex v;
    Edge e;
    Face f;
    Tetra t;
    
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
      } else if(type.compareTo("Tetra:") == 0) {
        simplex = Triangulation.tetraTable.get(index);
        if(simplex == null) {
          simplex = new Tetra(index);
          Triangulation.putTetra((Tetra) simplex);
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
      
      // Local tetra
      stringScanner = new Scanner(fileScanner.nextLine());
      while(stringScanner.hasNextInt()) {
        index = stringScanner.nextInt();
        t = Triangulation.tetraTable.get(index);
        if(t == null) {
          t = new Tetra(index);
          Triangulation.putTetra(t);
        }
        simplex.addTetra(t);
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
  
  public static void read2DLutzFile(String filename) {
    read2DLutzFile(new File(filename));
  }
  
  public static void read2DLutzFile(File file) {
    String faces;
    Scanner scanner = null;
    Scanner line;
    HashMap<TriPosition, Edge> edgeList = new HashMap<TriPosition, Edge>();
    Vertex v;
    Edge e;
    Face f;
    Vertex[] verts = new Vertex[3];
    int index;
    TriPosition T;
    
    try {
      scanner = new Scanner(file);
    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
    }
    
    faces = "";
    while(scanner.hasNextLine()) {
      faces = faces.concat(scanner.nextLine());
    }
    
    faces = faces.replaceAll("[^0-9],[^0-9]", "\n");
    faces = faces.replaceAll(",", " ");
    faces = faces.replaceAll("[^0-9 \n]", "");
    
    scanner = new Scanner(faces);
    while(scanner.hasNextLine()) {
      line = new Scanner(scanner.nextLine());
      
      //Create face;
      f = new Face(Triangulation.greatestFace() + 1);
      Triangulation.putFace(f);
      
      // Fill out verts, create vertices, add to face
      for(int i = 0; i < verts.length; i++) {
        index = line.nextInt();
        v = Triangulation.vertexTable.get(index);
        if(v == null) {
          v = new Vertex(index);
          Triangulation.putVertex(v);
        }
        v.addFace(f);
        f.addVertex(v);
        verts[i] = v;
        // add to other vertices
        for(int j = 0; j < i; j++) {
          verts[j].addVertex(v);
          v.addVertex(verts[j]);
        }
      }
      
      // Build edges
      for(int i = 0; i < verts.length; i++) {
        for(int j = 0; j < i; j++) {
          T = new TriPosition(verts[i].getIndex(), verts[j].getIndex());
          e = edgeList.get(T);
          if(e == null) {
            e = new Edge(Triangulation.greatestEdge() + 1);
            Triangulation.putEdge(e);
            edgeList.put(T, e);
            for(Edge e2 : verts[i].getLocalEdges()) {
              e.addEdge(e2);
              e2.addEdge(e);
            }
            for(Edge e2 : verts[j].getLocalEdges()) {
              e.addEdge(e2);
              e2.addEdge(e);
            }
            verts[i].addEdge(e);
            verts[j].addEdge(e);
            e.addVertex(verts[j]);
            e.addVertex(verts[i]);
          } else {
            for(Face f2 : e.getLocalFaces()) {
              f.addFace(f2);
              f2.addFace(f);
            }
          }
          e.addFace(f);
          f.addEdge(e);
        }
      }
    }
  }
  
  
}
