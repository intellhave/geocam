package triangulation;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Triangulation {
  public static HashMap<Integer, Vertex> vertexTable = new HashMap<Integer, Vertex>();
  public static HashMap<Integer, Edge> edgeTable = new HashMap<Integer, Edge>();
  public static HashMap<Integer, Face> faceTable = new HashMap<Integer, Face>();
  public static HashMap<Integer, Tetra> tetraTable = new HashMap<Integer, Tetra>();
  private static int id = 0;
  
  private Triangulation() {

  }

  public static int getTriangulationID() {
    return id;
  }
  
  // Put methods
  public static void putVertex(Vertex v) {
    vertexTable.put(v.getIndex(), v);
  }

  public static void putEdge(Edge e) {
    edgeTable.put(e.getIndex(), e);
  }

  public static void putFace(Face f) {
    faceTable.put(f.getIndex(), f);
  }

  public static void putTetra(Tetra t) {
    tetraTable.put(t.getIndex(), t);
  }

  // Remove methods
  public static void removeVertex(int v) {
    vertexTable.remove(v);
  }

  public static void removeEdge(int e) {
    edgeTable.remove(e);
  }

  public static void removeFace(int f) {
    faceTable.remove(f);
  }

  public static void removeTetra(int t) {
    tetraTable.remove(t);
  }

  // Contains methods
  public static boolean containsVertex(int v) {
    return vertexTable.containsKey(v);
  }

  public static boolean containsVertex(Vertex v) {
    return vertexTable.containsValue(v);
  }

  public static boolean containsEdge(int e) {
    return edgeTable.containsKey(e);
  }

  public static boolean containsEdge(Edge e) {
    return edgeTable.containsValue(e);
  }

  public static boolean containsFace(int f) {
    return faceTable.containsKey(f);
  }

  public static boolean containsFace(Face f) {
    return faceTable.containsValue(f);
  }

  public static boolean containsTetra(Tetra t) {
    return tetraTable.containsValue(t);
  }

  // Greatest methods: Returns the largest integer in the table
  public static int greatestVertex() {
    int max = 0;
    for (Integer v : vertexTable.keySet()) {
      if (max < v) {
        max = v;
      }
    }
    return max;
  }

  public static int greatestEdge() {
    int max = 0;
    for (Integer e : edgeTable.keySet()) {
      if (max < e) {
        max = e;
      }
    }
    return max;
  }

  public static int greatestFace() {
    int max = 0;
    for (Integer f : faceTable.keySet()) {
      if (max < f) {
        max = f;
      }
    }
    return max;
  }

  public static int greatestTetra() {
    int max = 0;
    for (Integer t : tetraTable.keySet()) {
      if (max < t) {
        max = t;
      }
    }
    return max;
  }

  public static boolean isManifold() {
    for (Edge e : edgeTable.values()) {
      if (e.getLocalFaces().size() != 2) {
        System.out.println("Edge " + e.getIndex() + " does not have two adjacent faces");
        return false;
      }
    }

    for (Vertex v : vertexTable.values()) {
      if (!isCyclic(v))
        System.out.println("Vertex " + v.getIndex() + " does not have a cyclic ordering of adjacent faces");
        return false;
    }
    return true;
  }

  public static boolean isCyclic(Vertex v) {
    List<Face> faceList = v.getLocalFaces();
    if (faceList.size() == 0) {
      return false;
    }
     
    Face currentFace = faceList.get(0);
    HashMap<Face, Boolean> visited = new HashMap<Face, Boolean>();
    visited.put(currentFace, true);
    int counted = 1;
    boolean faceFound = true;
    while (faceFound) {
      faceFound = false;
      for (Face localFace : currentFace.getLocalFaces()) {
        for (Vertex localVertex : localFace.getLocalVertices()) {
          if (localVertex == v && visited.get(localFace) == false) {
            visited.put(localFace, true);
            currentFace = localFace;
            counted++;
            faceFound = true;
            break;
          }
        }
        if (faceFound) {
          break;
        }
      }
    }
    
    if(counted == v.getLocalFaces().size()){  
      return true;
    }
    else{
      return false;     
    } 
}

  public static void reset() {
    vertexTable.clear();
    edgeTable.clear();
    faceTable.clear();
    tetraTable.clear();
    id++;
  }
}
