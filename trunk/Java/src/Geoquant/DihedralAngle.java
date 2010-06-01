package Geoquant;

import java.util.HashMap;
import java.util.LinkedList;

import Triangulation.*;

public class DihedralAngle extends Geoquant {
//Index map
  private static HashMap<TriPosition, DihedralAngle> Index = new HashMap<TriPosition, DihedralAngle>();
  private static Sum sum = null;
  
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
  
  public static Geoquant sum() {
    if(sum == null) {
      sum = new Sum();
    }
    return sum;
  }
  
  public static double getSum() {
    return sum.getValue();
  }
  
  private static class Sum extends Geoquant {
    LinkedList<DihedralAngle> angles = new LinkedList<DihedralAngle>();
    
    Sum() {
      super();
      for(Tetra t : Triangulation.tetraTable.values()) {
        for(Edge e : t.getLocalEdges()) {
          DihedralAngle beta = DihedralAngle.At(e, t);
          beta.addDependent(this);
          angles.add(beta);
        }
      }
    }
    protected void recalculate() {
      value = 0;
      for(DihedralAngle beta : angles) {
        value += beta.getValue();
      }
    }

    protected void remove() {
      deleteDependents();
      for(DihedralAngle beta : angles) {
        beta.removeDependent(this);
      }
      angles.clear();
    }
  }
}

