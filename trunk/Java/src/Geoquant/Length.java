package Geoquant;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Set;

import Triangulation.*;

public class Length extends Geoquant {
  // Index map
  private static HashMap<TriPosition, Length> Index = new HashMap<TriPosition, Length>();
  
  // Needed geoquants
  private Radius rad1;
  private Radius rad2;
  private Alpha alpha1;
  private Alpha alpha2;
  private Eta eta;
  
  public Length(Edge e) {
    super(e);
    
    List<Vertex> verts = new ArrayList<Vertex>();
    verts.addAll(e.getLocalVertices());
    
    rad1 = Radius.At(verts.get(0));
    rad1.addObserver(this);
    alpha1 = Alpha.At(verts.get(0));
    alpha1.addObserver(this);
    rad2 = Radius.At(verts.get(1));
    rad2.addObserver(this);
    alpha2 = Alpha.At(verts.get(1));
    alpha2.addObserver(this);
    eta = Eta.At(e);
    eta.addObserver(this);
  }
  
  protected void recalculate() {
    double r1 = rad1.getValue();
    double r2 = rad2.getValue();
    double a1 = alpha1.getValue();
    double a2 = alpha2.getValue();
    double etaV = eta.getValue();

    value = Math.sqrt( a1* r1*r1 + a2 * r2*r2 + 2*r1*r2*etaV );

  }
 
  protected void remove() {
    deleteDependents();
    rad1.deleteObserver(this);
    rad2.deleteObserver(this);
    alpha1.deleteObserver(this);
    alpha2.deleteObserver(this);
    eta.deleteObserver(this);
    Index.remove(pos);
  }
  
  public static Length At(Edge e) {
    TriPosition T = new TriPosition(e.getSerialNumber());
    Length q = Index.get(T);
    if(q == null) {
      q = new Length(e);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static double valueAt(Edge e) {
    return At(e).getValue();
  }

}
