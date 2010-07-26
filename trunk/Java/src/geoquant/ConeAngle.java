package geoquant;

import java.util.HashMap;
import java.util.LinkedList;

import triangulation.Edge;
import triangulation.Tetra;


public class ConeAngle extends Geoquant {
  private static HashMap<TriPosition, ConeAngle> Index = new HashMap<TriPosition, ConeAngle>();
  
  private LinkedList<DihedralAngle> angles = new LinkedList<DihedralAngle>();
  
  private ConeAngle(Edge e) {
    super(e);
    DihedralAngle beta;
    for(Tetra t : e.getLocalTetras()) {
      beta = DihedralAngle.at(e, t);
      beta.addObserver(this);
      angles.add(beta);
    }
  }
  
  protected void recalculate() {
    value = 0;
    for(DihedralAngle beta : angles) {
      value += beta.getValue();
    }

  }

  public void remove() {
    deleteDependents();
    for(DihedralAngle beta : angles) {
      beta.deleteObserver(this);
    }
    Index.remove(pos);
  }
  
  public static ConeAngle at(Edge e) {
    TriPosition T = new TriPosition(e.getSerialNumber());
    ConeAngle q = Index.get(T);
    if(q == null) {
      q = new ConeAngle(e);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }

  public static ConeAngle At(Edge e) {
    TriPosition T = new TriPosition(e.getSerialNumber());
    ConeAngle q = Index.get(T);
    if(q == null) {
      q = new ConeAngle(e);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static double valueAt(Edge e) {
    return at(e).getValue();
  }
}
