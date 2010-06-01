package Geoquant;

import java.util.HashMap;
import java.util.LinkedList;

import Triangulation.Edge;
import Triangulation.Tetra;

public class SectionalCurvature extends Geoquant{
  //Index map
  private static HashMap<TriPosition, SectionalCurvature> Index = new HashMap<TriPosition, SectionalCurvature>();
  
  // Needed geoquants
  private LinkedList<DihedralAngle> dih_angles;
  
  public SectionalCurvature(Edge e) {
    super();
    dih_angles = new LinkedList<DihedralAngle>();
    DihedralAngle beta;
    for(Tetra t : e.getLocalTetras()) {
      beta = DihedralAngle.At(e, t);
      beta.addDependent(this);
      dih_angles.add(beta);
    }
  }
  
  protected void recalculate() {
    value = 2*Math.PI;
    for(DihedralAngle beta : dih_angles) {
      value -= beta.getValue();
    }
  }
 
  protected void remove() {
    deleteDependents();
    for(DihedralAngle beta : dih_angles) {
      beta.removeDependent(this);
    }
    dih_angles.clear();
    Index.remove(pos);
  }
  
  public static SectionalCurvature At(Edge e) {
    TriPosition T = new TriPosition(e.getSerialNumber());
    SectionalCurvature p = Index.get(T);
    if (p == null) {
      p = new SectionalCurvature(e);
      p.pos = T;
      Index.put(T, p);
    }
    return p;
  }
  
  public static double valueAt(Edge e) {
    return At(e).getValue();
  }

}
