package Visualization;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import Geoquant.Length;
import Triangulation.Edge;
import Triangulation.Face;
import Triangulation.Triangulation;
import Triangulation.Vertex;

public class PlanarDevelopment {

  private Hashtable<Vertex, Point> points;
  private Hashtable<Edge, Line> lines;

  public PlanarDevelopment() {
    points = new Hashtable<Vertex, Point>();
    lines = new Hashtable<Edge, Line>();
  }

  public void clearSystem() {
    points.clear();
    lines.clear();
  }

  public Point getPoint(Vertex vertex) {
    return points.get(vertex);
  }

  public Line getLine(Edge edge) {
    return lines.get(edge);
  }

  public void generatePlane() {
    if (Triangulation.faceTable.isEmpty()) {
      return;
    } else {

      // perform a breadth first search of the vertices
      LinkedList<Vertex> vertsToMap = new LinkedList<Vertex>();
      HashSet<Vertex> vertsAlreadyMapped = new HashSet<Vertex>();

      vertsToMap.addLast(Triangulation.vertexTable.values().iterator().next());

      while (!vertsToMap.isEmpty()) {
        // we know this will succeed because the looping condition
        Vertex vertex = vertsToMap.getFirst();
        
        //this will happen everytime after the first vertex
        if (points.containsKey(vertex)) {
          
        } else { //the first vertex will be handled here
          Edge startEdge = vertex.getLocalEdges().iterator().next();
        }
        

        // we don't want to map the same vertex twice
        vertsAlreadyMapped.add(vertex);
      }
    }
  }

  //mutates the edges and faces parameters
  private static void generateCyclicOrderings(Vertex vertex, Edge startEdge,
      Face startFace, LinkedList<Edge> edges, LinkedList<Face> faces) {
    
    HashSet<Face> visitedFaces = new HashSet<Face>();
    HashSet<Edge> visitedEdges = new HashSet<Edge>();
    Edge currEdge = startEdge;
    Face currFace = startFace;
    
    while(!visitedEdges.contains(currEdge)) {
      edges.addLast(currEdge);
      faces.addLast(currFace);
      
      visitedEdges.add(currEdge);
      visitedFaces.add(currFace);
      
      //find the other edge on this face/vertex
      for (Edge edge : currFace.getLocalEdges()) {
        if (vertex.isAdjEdge(edge) && !currEdge.equals(edge) ) {
          currEdge = edge;
          break;
        }
      }
      
      //find the next face
      for (Face face : currEdge.getLocalFaces()) {
        if (!visitedFaces.contains(face)) {
          currFace = face;
          break;
        }
      }
    }
    
    //might want the first edge to appear again at the end
    //not sure about this though TODO
    edges.addLast(currEdge);
  }

  private void developEdge(Edge currEdge, Edge prevEdge, Face face,
      Vertex vertex) {

  }

  // public void generatePlaneHelper(LinkedList<Face> facesToMap, HashSet<Face>
  // facesAlreadyMapped) {
  //  
  // }

}
