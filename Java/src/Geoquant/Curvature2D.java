package Geoquant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import Triangulation.Edge;
import Triangulation.Face;
import Triangulation.Vertex;

public class Curvature2D extends Geoquant {
  // Index map
  private static HashMap<TriPosition, Curvature2D> Index = new HashMap<TriPosition, Curvature2D>();
  
  // Needed geoquants
  private LinkedList<Angle> angles;
  
  public Curvature2D(Vertex v) {
    super();
    
    angles = new LinkedList<Angle>();
    Angle a;
    for(Face f : v.getLocalFaces()) {
      a = Angle.At(v, f);
      a.addDependent(this);
      angles.add(a);
    }
  }
  
  protected void recalculate() {
    value = 2 * Math.PI;

    for(Angle a : angles) {
      value -= a.getValue();
    }
  }
 
  protected void remove() {
    deleteDependents();
    for(Angle a : angles) {
      a.removeDependent(this);
    }
    angles.clear();
    Index.remove(pos);
  }
  
  public static Curvature2D At(Vertex v) {
    TriPosition T = new TriPosition(v.getSerialNumber());
    Curvature2D q = Index.get(T);
    if(q == null) {
      q = new Curvature2D(v);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static double valueAt(Vertex v) {
    return At(v).getValue();
  }

}
