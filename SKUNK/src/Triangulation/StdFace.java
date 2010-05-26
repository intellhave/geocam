package Triangulation;

import java.util.Iterator;

public class StdFace {
  public Vertex v1;
  public Vertex v2;
  public Vertex v3;
  
  public Edge e12;
  public Edge e13;
  public Edge e23;
  
  public StdFace(Face f, Vertex v) {
    v1 = v;

    Vertex[] otherVertices = new Vertex[2];
    int count = 0;
    for(Vertex w : f.getLocalVertices()) {
      if(w != v) {
        otherVertices[count] = w;
        count++;
      }
    }
    v2 = otherVertices[0];
    v3 = otherVertices[1];
    
    fixFaceEdges( f );
  }
  
  public StdFace(Face f, Edge e) {
    Iterator<Vertex> localVertices = e.getLocalVertices().iterator();

    v1 = localVertices.next();
    v2 = localVertices.next();

    for(Vertex v : f.getLocalVertices()) {
      if(v != v1 && v != v2) {
        v3 = v;
      }
    }
     
    fixFaceEdges( f );
  }
  
  private void fixFaceEdges(Face f) {
    for(Edge e : f.getLocalEdges()) {
      boolean b1 = e.isAdjVertex(v1);
      boolean b2 = e.isAdjVertex(v2);
      boolean b3 = e.isAdjVertex(v3);
      
      if( b1 && b2 ){ e12 = e; }
      else if( b1 && b3 ){ e13 = e; }
      else if( b2 && b3 ){ e23 = e; }
    }      
  }
}
