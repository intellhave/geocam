package geoquant;

import java.util.HashMap;

import triangulation.Face;
import triangulation.StdFace;


public class Area extends Geoquant {
  // Index map
  private static HashMap<TriPosition, Area> Index = new HashMap<TriPosition, Area>();

  private Length lij;
  private Length ljk;
  private Length lik;
  
  public Area(Face f) {
    super(f);
    StdFace stdF = new StdFace(f);
    lij = Length.at(stdF.e12);
    ljk = Length.at(stdF.e23);
    lik = Length.at(stdF.e13);
    
    lij.addObserver(this);
    ljk.addObserver(this);
    lik.addObserver(this);
  }
  
  protected void recalculate() {
    double l1 = lij.getValue();
    double l2 = ljk.getValue();
    double l3 = lik.getValue();
    
    double s = (l1 + l2 + l3) * 0.5;
    value = Math.sqrt(s * (s - l1) * (s - l2) * (s - l3));

  }

  public void remove() {
    deleteDependents();
    lij.deleteObserver(this);
    ljk.deleteObserver(this);
    lik.deleteObserver(this);
    Index.remove(pos);
  }

  public static Area at(Face f) {
    TriPosition T = new TriPosition(f.getSerialNumber());
    Area q = Index.get(T);
    if(q == null) {
      q = new Area(f);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static Area At(Face f) {
    TriPosition T = new TriPosition(f.getSerialNumber());
    Area q = Index.get(T);
    if(q == null) {
      q = new Area(f);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static double valueAt(Face f) {
    return at(f).getValue();
  }
}
