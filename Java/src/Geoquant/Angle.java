package Geoquant;

import java.util.HashMap;

import Triangulation.Face;
import Triangulation.StdFace;
import Triangulation.Vertex;

public class Angle extends Geoquant {
  // Index map
  private static HashMap<TriPosition, Angle> Index = new HashMap<TriPosition, Angle>();
  
  // Needed geoquants
  private Length lenA;
  private Length lenB;
  private Length lenC;
  private Face f;
  
  public Angle(Vertex v, Face f) {
    super();
    
    this.f = f;
    StdFace sf = new StdFace(f, v);
    lenA = Length.At(sf.e12);
    lenB = Length.At(sf.e13);
    lenC = Length.At(sf.e23);
    
    lenA.addDependent(this);
    lenB.addDependent(this);
    lenC.addDependent(this);
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
 
  protected void remove() {
    deleteDependents();
    lenA.removeDependent(this);
    lenB.removeDependent(this);
    lenC.removeDependent(this);
    Index.remove(pos);
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
    return At(v, f).getValue();
  }
}
