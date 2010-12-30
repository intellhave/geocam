package geoquant;

import java.util.HashMap;

import triangulation.Face;
import triangulation.StdFace;
import triangulation.Vertex;


public class Angle extends Geoquant {
  // Index map
  private static HashMap<TriPosition, Angle> Index = new HashMap<TriPosition, Angle>();
  
  // Needed geoquants
  private Length lenA;
  private Length lenB;
  private Length lenC;
  private Face f;
  
  public Angle(Vertex v, Face f) {
    super(v, f);
    
    this.f = f;
    StdFace sf = new StdFace(f, v);
 
    lenA = Length.at(sf.e12);
    lenB = Length.at(sf.e13);
    lenC = Length.at(sf.e23);
    
    lenA.addObserver(this);
    lenB.addObserver(this);
    lenC.addObserver(this);
  }
  
  protected void recalculate() {
    double a = lenA.getValue();
    double b = lenB.getValue();
    double c = lenC.getValue();
    if (f.isNegative()) {
      value = -1 * Math.acos((a*a + b*b - c*c)/ (2*a*b));
    }
    else {
      value = Math.acos((a*a + b*b - c*c)/ (2*a*b));
    }
  }
 
  public void remove() {
    deleteDependents();
    lenA.deleteObserver(this);
    lenB.deleteObserver(this);
    lenC.deleteObserver(this);
    Index.remove(pos);
  }
  
  public static Angle at(Vertex v, Face f) {
    TriPosition T = new TriPosition(v.getSerialNumber(), f.getSerialNumber());
    Angle q = Index.get(T);
    if(q == null) {
      q = new Angle(v, f);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static Angle At(Vertex v, Face f) {
    TriPosition T = new TriPosition(v.getSerialNumber(), f.getSerialNumber());
    Angle q = Index.get(T);
    if(q == null) {
      q = new Angle(v, f);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static double valueAt(Vertex v, Face f) {
    return at(v, f).getValue();
  }
}
