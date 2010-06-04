package Geoquant;

import java.util.HashMap;

import Triangulation.Edge;
import Triangulation.Face;
import Triangulation.StdFace;

public class EdgeHeight extends Geoquant {
  // Index map
  private static HashMap<TriPosition, EdgeHeight> Index = new HashMap<TriPosition, EdgeHeight>();
  
  // Needed geoquants
  private PartialEdge dij;
  private PartialEdge dik;
  private Angle theta_i;
  
  public EdgeHeight(Edge e, Face f) {
    super(e, f);
    
    StdFace sf = new StdFace(f, e);
    dij = PartialEdge.At(sf.v1, sf.e12);
    dik = PartialEdge.At(sf.v1, sf.e13);
    theta_i = Angle.At(sf.v1, f);
    
    dij.addObserver(this);
    dik.addObserver(this);
    theta_i.addObserver(this);
  }
  
  protected void recalculate() {
    double d_ij = dij.getValue();
    double d_ik = dik.getValue();
    double theta = theta_i.getValue();

    value = (d_ik - d_ij * Math.cos(theta))/Math.sin(theta);
  }
 
  protected void remove() {
    deleteDependents();
    dij.deleteObserver(this);
    dik.deleteObserver(this);
    theta_i.deleteObserver(this);
    Index.remove(pos);
  }
  
  public static EdgeHeight At(Edge e, Face f) {
    TriPosition T = new TriPosition(e.getSerialNumber(), f.getSerialNumber());
    EdgeHeight q = Index.get(T);
    if(q == null) {
      q = new EdgeHeight(e, f);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static double valueAt(Edge e, Face f) {
    return At(e, f).getValue();
  }
  
}
