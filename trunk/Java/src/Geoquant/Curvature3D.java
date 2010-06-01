package Geoquant;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import Triangulation.Edge;
import Triangulation.Triangulation;
import Triangulation.Vertex;

public class Curvature3D extends Geoquant {
  // Index map
  private static HashMap<TriPosition, Curvature3D> Index = new HashMap<TriPosition, Curvature3D>();
  
  // Needed geoquants
  private LinkedList<SectionalCurvature> sec_curvs;
  private LinkedList<PartialEdge> partials;
  
  public Curvature3D(Vertex v) {
    super();
    sec_curvs = new LinkedList<SectionalCurvature>();
    partials = new LinkedList<PartialEdge>();
    SectionalCurvature sc;
    PartialEdge pe;
    for(Edge e : v.getLocalEdges()) {
      sc = SectionalCurvature.At(e);
      sc.addDependent(this);
      pe = PartialEdge.At(v, e);
      pe.addDependent(this);
      sec_curvs.add(sc);
      partials.add(pe);
    }
  }
  
  protected void recalculate() {
    value = 0;
    Iterator<PartialEdge> pe_it = partials.iterator();
    for(SectionalCurvature sc : sec_curvs) {
      value += sc.getValue() * pe_it.next().getValue();
    }
  }
 
  protected void remove() {
    deleteDependents();
    for(SectionalCurvature sc : sec_curvs) {
      sc.removeDependent(this);
    }
    for(PartialEdge pe : partials) {
      pe.removeDependent(this);
    }
    sec_curvs.clear();
    partials.clear();
    Index.remove(pos);
  }
  
  public static Curvature3D At(Vertex v) {
    TriPosition T = new TriPosition(v.getSerialNumber());
    Curvature3D q = Index.get(T);
    if(q == null) {
      q = new Curvature3D(v);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static double valueAt(Vertex v) {
    return At(v).getValue();
  }
    
  public static class Sum extends Geoquant {
    LinkedList<Curvature3D> curvs = new LinkedList<Curvature3D>();
    
    Sum() {
      super();
      Curvature3D k;
      for(Vertex v : Triangulation.vertexTable.values()) {
        k = Curvature3D.At(v);
        k.addDependent(this);
        curvs.add(k);
      }
    }
    protected void recalculate() {
      value = 0;
      for(Curvature3D k : curvs) {
        value += k.getValue();
      }
    }

    protected void remove() {
      deleteDependents();
      for(Curvature3D k : curvs) {
        k.removeDependent(this);
      }
      curvs.clear();
    }
  }

}
