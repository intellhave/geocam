package Geoquant;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Set;

import triangulation.*;


public class VEinstein extends Geoquant {
  // Index map
  private static HashMap<TriPosition, VEinstein> Index = new HashMap<TriPosition, VEinstein>();
  
  // Needed geoquants
  private Curvature3D.Sum totalK;
  private Volume.Sum totalV;
  private SectionalCurvature Curv;
  private Length leng;
  private LinkedList<DihedralAngle> dihs;
  private LinkedList<Length> lengths;
  
  public VEinstein(Edge e) {
    super(e);
    
    dihs = new LinkedList<DihedralAngle>();
    lengths = new LinkedList<Length>();
    Curv = SectionalCurvature.At(e);
    Curv.addObserver(this);
    totalK = Curvature3D.sum();
    totalK.addObserver(this);
    totalV = Volume.sum();
    totalV.addObserver(this);
    leng = Length.At(e);
    leng.addObserver(this);
    DihedralAngle dih;
    Length L34;
    for(Tetra t : e.getLocalTetras()) {
      StdTetra st = new StdTetra(t, e);
      L34 = Length.At(st.e34);
      L34.addObserver(this);
      lengths.add(L34);
      dih = DihedralAngle.At(e, t);
      dih.addObserver(this);
      dihs.add(dih);
    }
    
  }
  
  protected void recalculate() {
    double K_ij = Curv.getValue();
    double K = totalK.getValue();
    double V = totalV.getValue();
    double L_ij = leng.getValue();
    double Vpartial = 0; 

    Iterator<Length> l_it= lengths.iterator();
    Iterator<DihedralAngle> dih_it= dihs.iterator();
    
    while(l_it.hasNext()) {
      Vpartial += L_ij * l_it.next().getValue()/Math.tan(dih_it.next().getValue());
    }
    Vpartial /= 6;
    
    value = K_ij*L_ij - L_ij * (K / (3.0*V)) * Vpartial;

  }
 
  public void remove() {
    deleteDependents();
    Curv.deleteObserver(this);
    totalV.deleteObserver(this);
    totalK.deleteObserver(this);
    leng.deleteObserver(this);
    Iterator<Length> l_it= lengths.iterator();
    Iterator<DihedralAngle> dih_it= dihs.iterator();
    while(l_it.hasNext()) {
      l_it.next().deleteObserver(this);
      dih_it.next().deleteObserver(this);
    }
    Index.remove(pos);
  }
  
  public static VEinstein At(Edge e) {
    TriPosition T = new TriPosition(e.getSerialNumber());
    VEinstein q = Index.get(T);
    if(q == null) {
      q = new VEinstein(e);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static double valueAt(Edge e) {
    return At(e).getValue();
  }

}
