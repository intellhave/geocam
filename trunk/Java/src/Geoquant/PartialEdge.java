package Geoquant;

import java.util.HashMap;
import Triangulation.*;

public class PartialEdge extends Geoquant {
  // Index map
  private static HashMap<TriPosition, PartialEdge> Index = new HashMap<TriPosition, PartialEdge>();
  private HashMap<TriPosition, Partial> PartialIndex;
  // Needed geoquants
  private Radius ri;
  private Radius rj;
  private Alpha ai;
  private Alpha aj;
  private Length Lij;
  private Edge e;
  
  public PartialEdge(Vertex vertex, Edge edge) {
    super();
    PartialIndex = new HashMap<TriPosition, Partial>();
    
    this.e = edge;
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
    Index.remove(pos);
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
  
  public PartialEdge.Partial partialAt(Edge f) {
    TriPosition T = new TriPosition(f.getSerialNumber());
    Partial q = PartialIndex.get(T);
    if(q == null) {
      q = new Partial(f);
      q.pos = T;
      PartialIndex.put(T, q);
    }
    return q;
  }
  
  public class Partial extends Geoquant {
    private boolean equal;
    private Eta eij;
    
    private Partial(Edge f) {
      equal = e == f;
      if(equal) {
        ri.addDependent(this);
        rj.addDependent(this);
        ai.addDependent(this);
        aj.addDependent(this);
        eij = Eta.At(e);
        eij.addDependent(this);
      }
    }
    
    protected void recalculate() {
      value = 0;
      if(equal) {
        double radi = ri.getValue();
        double radj = rj.getValue();
        double alphi = ai.getValue();
        double alphj = aj.getValue();
        double eta = eij.getValue();

        value = radi * Math.pow(radj, 2) * (eta * radi + alphj * radj) 
           / Math.pow(alphi * Math.pow(radi, 2) + alphj * Math.pow(radj, 2) + 2*eta*radi*radj, 1.5);
      }
    }

    protected void remove() {
      deleteDependents();
      if(equal) {
        ri.removeDependent(this);
        rj.removeDependent(this);
        ai.removeDependent(this);
        aj.removeDependent(this);
        eij.removeDependent(this);
      }
      PartialIndex.remove(pos);
    }
    
  }

}
