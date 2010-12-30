package triangulation;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Boundary {
  public static HashMap<Integer, Vertex> boundaryVertexTable = new HashMap<Integer, Vertex>();
  public static HashMap<Integer, Edge> boundaryEdgeTable = new HashMap<Integer, Edge>();
  public static HashMap<Integer, Face> boundaryFaceTable = new HashMap<Integer, Face>();
 
  public Boundary(){
  }
  
  public static void makeBoundary(){
    int dim = Triangulation.getDimension();
//    System.out.println("yo");
    if (dim == 2){
//      System.out.println("hi");
      int edgeIndex = 0;
      int vertexIndex = 0;
      for(Face f: Triangulation.faceTable.values()) {
//        System.out.println(f.getLocalFaces().size());
        if (f.getLocalFaces().size() < 3) {
//          System.out.println("yesssss");
          for (Edge e: f.getLocalEdges()){
            if (e.getLocalFaces().size() == 1 && (! boundaryEdgeTable.containsValue(e))){
              boundaryEdgeTable.put(edgeIndex, e);
              edgeIndex++;
              for (Vertex v: e.getLocalVertices()){
                if (! boundaryVertexTable.containsValue(v)){
                  boundaryVertexTable.put(vertexIndex, v);
                  vertexIndex++;
                }
              }
            }
          }
        }
      }
//      System.out.println(vertexIndex);
//      System.out.println(edgeIndex);
    }
    else if (dim == 3 ){
      
    }
    else {
      // error handler
    }
  }
  
  public boolean inBoundary(Vertex v){
    return boundaryVertexTable.containsValue(v);
  }

  public boolean inBoundary(Edge e){
    return boundaryEdgeTable.containsValue(e);
  }
  
  public boolean inBoundary(Face f){
    return boundaryFaceTable.containsValue(f);
  }

  public static void reset() {
    boundaryVertexTable.clear();
    boundaryEdgeTable.clear();
    boundaryFaceTable.clear();
  }
}
