package Geoquant;

import java.util.HashMap;

import triangulation.Face;
import triangulation.StdTetra;
import triangulation.Tetra;


public class FaceHeight extends Geoquant {
  // Index map
  private static HashMap<TriPosition, FaceHeight> Index = new HashMap<TriPosition, FaceHeight>();
  
  // Needed geoquants
  private EdgeHeight hij_l;
  private EdgeHeight hij_k;
  private DihedralAngle beta_ij_kl;
  
  public FaceHeight(Face f, Tetra t) {
    super(f, t);
    
    StdTetra st = new StdTetra(t, f);
    hij_k = EdgeHeight.At(st.e12, st.f123);
    hij_k.addObserver(this);
    hij_l = EdgeHeight.At(st.e12, st.f124);
    hij_l.addObserver(this);
    beta_ij_kl = DihedralAngle.At(st.e12, t);
    beta_ij_kl.addObserver(this);
  }
  
  protected void recalculate() {
    double Hij_l = hij_l.getValue();
    double Hij_k = hij_k.getValue();
    double angle = beta_ij_kl.getValue();

    value = (Hij_l - Hij_k * Math.cos(angle))/Math.sin(angle);
  }
 
  public void remove() {
    deleteDependents();
    hij_l.deleteObserver(this);
    hij_k.deleteObserver(this);
    beta_ij_kl.deleteObserver(this);
    Index.remove(pos);
  }
  
  public static FaceHeight At(Face f, Tetra t) {
    TriPosition T = new TriPosition(f.getSerialNumber(), t.getSerialNumber());
    FaceHeight q = Index.get(T);
    if(q == null) {
      q = new FaceHeight(f, t);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static double valueAt(Face f, Tetra t) {
    return At(f, t).getValue();
  }

}
