package Geoquant;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import Triangulation.Edge;
import Triangulation.Face;
import Triangulation.StdTetra;
import Triangulation.Tetra;
import Triangulation.Vertex;

public class DihedralAngle extends Geoquant {
//Index map
  private static HashMap<TriPosition, DihedralAngle> Index = new HashMap<TriPosition, DihedralAngle>();
  
  // Needed geoquants
  private Angle angleA;
  private Angle angleB;
  private Angle angleC;
    
  public DihedralAngle(Edge e, Tetra t) {
    super();
    
    StdTetra st = new StdTetra(t, e);  
        
    angleA = Angle.At(st.v1, st.f123);
    angleB = Angle.At(st.v1, st.f124);
    angleC = Angle.At(st.v1, st.f134);
   
    angleA.addDependent(this);
    angleB.addDependent(this);
    angleC.addDependent(this);
  }
  
  protected void recalculate() {
    double a = angleA.getValue();
    double b = angleB.getValue();
    double c = angleC.getValue();
    value =  Math.acos( (Math.cos(c)-Math.cos(a)*Math.cos(b)) / (Math.sin(a)*Math.sin(b)) );
  }
 
  protected void remove() {
    deleteDependents();
    angleA.removeDependent(this);
    angleB.removeDependent(this);
    angleC.removeDependent(this);
    Index.remove(pos);
  }
  
  public static DihedralAngle At(Edge e, Tetra t) {
    TriPosition T = new TriPosition(e.getSerialNumber(), t.getSerialNumber());
    DihedralAngle q = Index.get(T);
    if(q == null) {
      q = new DihedralAngle(e, t);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static double valueAt(Edge e, Tetra t) {
    return At(e, t).getValue();
  }
}

