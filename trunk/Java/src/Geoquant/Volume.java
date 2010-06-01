package Geoquant;

import java.util.HashMap;
import java.util.LinkedList;

import Triangulation.StdTetra;
import Triangulation.Tetra;
import Triangulation.Triangulation;

public class Volume extends Geoquant {
  // Index map
  private static HashMap<TriPosition, Volume> Index = new HashMap<TriPosition, Volume>();

  private Length L_ij;
  private Length L_ik;
  private Length L_il;
  private Length L_jk;
  private Length L_jl;
  private Length L_kl;
  
  public Volume(Tetra t) {
    super();
    StdTetra st = new StdTetra(t);
    L_ij = Length.At(st.e12);
    L_ik = Length.At(st.e13);
    L_il = Length.At(st.e14);
    L_jk = Length.At(st.e23);
    L_jl = Length.At(st.e24);
    L_kl = Length.At(st.e34);
    
    L_ij.addDependent(this);
    L_ik.addDependent(this);
    L_il.addDependent(this);
    L_jk.addDependent(this);
    L_jl.addDependent(this);
    L_kl.addDependent(this);
  }
  
  protected void recalculate() {
    double CayleyMenger;
    double L12 = L_ij.getValue();
    double L13 = L_ik.getValue();
    double L14 = L_il.getValue();
    double L23 = L_jk.getValue();
    double L24 = L_jl.getValue();
    double L34 = L_jk.getValue();
     
    CayleyMenger = (-1)*( Math.pow(L12, 4.0)*Math.pow(L34,2.0) + Math.pow(L13, 4.0)*Math.pow(L24,2.0) 
       + Math.pow(L14, 4.0)*Math.pow(L23,2.0) + Math.pow(L23, 4.0)*Math.pow(L14,2.0)
       + Math.pow(L24, 4.0)*Math.pow(L13,2.0) + Math.pow(L34, 4.0)*Math.pow(L12,2.0) );

    CayleyMenger += (-1)*( Math.pow(L12, 2.0)*Math.pow(L13,2.0)*Math.pow(L23,2.0) 
         + Math.pow(L12, 2.0)*Math.pow(L14,2.0)*Math.pow(L24,2.0)
         + Math.pow(L13, 2.0)*Math.pow(L14,2.0)*Math.pow(L34,2.0)
         + Math.pow(L23, 2.0)*Math.pow(L24,2.0)*Math.pow(L34,2.0) );

    CayleyMenger += Math.pow(L12, 2.0)*Math.pow(L13,2.0)*Math.pow(L24,2.0) 
                           + Math.pow(L12, 2.0)*Math.pow(L13,2.0)*Math.pow(L34,2.0)
                           + Math.pow(L12, 2.0)*Math.pow(L14,2.0)*Math.pow(L23,2.0) 
                           + Math.pow(L12, 2.0)*Math.pow(L14,2.0)*Math.pow(L34,2.0);

    CayleyMenger += Math.pow(L13, 2.0)*Math.pow(L14,2.0)*Math.pow(L23,2.0) 
                           + Math.pow(L13, 2.0)*Math.pow(L14,2.0)*Math.pow(L24,2.0)
                           + Math.pow(L13, 2.0)*Math.pow(L23,2.0)*Math.pow(L24,2.0)
                           + Math.pow(L14, 2.0)*Math.pow(L23,2.0)*Math.pow(L24,2.0);

    CayleyMenger += Math.pow(L12, 2.0)*Math.pow(L23,2.0)*Math.pow(L34,2.0)
                           + Math.pow(L14, 2.0)*Math.pow(L23,2.0)*Math.pow(L34,2.0) 
                           + Math.pow(L12, 2.0)*Math.pow(L24,2.0)*Math.pow(L34,2.0) 
                           + Math.pow(L13, 2.0)*Math.pow(L24,2.0)*Math.pow(L34,2.0);
     
    value = Math.sqrt(CayleyMenger / 144.0);

  }

  protected void remove() {
    deleteDependents();
    L_ij.removeDependent(this);
    L_ik.removeDependent(this);
    L_il.removeDependent(this);
    L_jk.removeDependent(this);
    L_jl.removeDependent(this);
    L_kl.removeDependent(this);
    Index.remove(pos);
  }

  public static Volume At(Tetra t) {
    TriPosition T = new TriPosition(t.getSerialNumber());
    Volume q = Index.get(T);
    if(q == null) {
      q = new Volume(t);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static double valueAt(Tetra t) {
    return At(t).getValue();
  }
  
  public static class Sum extends Geoquant {
    LinkedList<Volume> volumes = new LinkedList<Volume>();
    private static Sum total = null;
    
    private Sum() {
      super();
      Volume v;
      for(Tetra t : Triangulation.tetraTable.values()) {
        v = Volume.At(t);
        v.addDependent(this);
        volumes.add(v);
      }
    }
    protected void recalculate() {
      value = 0;
      for(Volume v : volumes) {
        value += v.getValue();
      }
    }

    protected void remove() {
      deleteDependents();
      for(Volume v : volumes) {
        v.removeDependent(this);
      }
      volumes.clear();
    }
    
    public static Sum get() {
      if(total == null) {
        total = new Sum();
      }
      return total;
    }
    
    public static double getSum() {
      return total.getValue();
    }
  }

}
