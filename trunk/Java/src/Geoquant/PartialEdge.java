package Geoquant;

import java.util.HashMap;

import bsh.This;

import Triangulation.*;

public class PartialEdge extends Geoquant {
  // Index map
  private static HashMap<TriPosition, PartialEdge> Index = new HashMap<TriPosition, PartialEdge>();
  
  // Needed geoquants
  private Radius ri;
  private Radius rj;
  private Alpha ai;
  private Alpha aj;
  private Length Lij;
  
  public PartialEdge(Vertex vertex, Edge edge) {
    super();
    
    ri = Radius.At(vertex);
    ai = Alpha.At(vertex);
    for (Vertex v : edge.getLocalVertices()) {
      if (!v.equals(vertex)) {
        rj = Radius.At(v);
        aj = Alpha.At(v);
        break;
      }
    }
    
    Lij = Length.At(edge);
    
    ri.addDependent(this);
    rj.addDependent(this);
    ai.addDependent(this);
    aj.addDependent(this);
    Lij.addDependent(this);
    
  }
  
  protected void recalculate() {
    double length = Lij.getValue();
    double radi = ri.getValue();
    double radj = rj.getValue();
    double alphai = ai.getValue();
    double alphaj = aj.getValue();
    
    value = (length*length + alphai*radi*radi - alphaj*radj*radj) / (2 * length);
  }
 
  protected void remove() {
    deleteDependents();
    Lij.removeDependent(this);
    ri.removeDependent(this);
    rj.removeDependent(this);
    ai.removeDependent(this);
    aj.removeDependent(this);
  }
  
  public static PartialEdge At(Vertex vertex, Edge edge) {
    TriPosition T = new TriPosition(vertex.getSerialNumber(), edge.getSerialNumber());
    PartialEdge p = Index.get(T);
    if (p == null) {
      p = new PartialEdge(vertex, edge);
      p.pos = T;
      Index.put(T, p);
    }
    return p;
  }
  
  public static double valueAt(Vertex vertex, Edge edge) {
    return At(vertex, edge).getValue();
  }

}
