package geoquant;

import java.util.HashMap;

import triangulation.Vertex;


public class VCSC extends Geoquant {
  // Index map
  private static HashMap<TriPosition, VCSC> Index = new HashMap<TriPosition, VCSC>();
  
  // Needed geoquants
  private Curvature3D.Sum totalK;
  private Volume.Sum totalV;
  private Curvature3D Curv;
  private Volume.PartialSum VolumePartial;
  
  public VCSC(Vertex v) {
    super(v);
    
    Curv = Curvature3D.at(v);
    Curv.addObserver(this);
    totalK = Curvature3D.sum();
    totalK.addObserver(this);
    totalV = Volume.sum();
    totalV.addObserver(this);
    VolumePartial = Volume.partialSumAt(v);
    VolumePartial.addObserver(this);
    
  }
  
  protected void recalculate() {
    double K_i = Curv.getValue();
    double K = totalK.getValue();
    double V = totalV.getValue();
    double Vpartial = VolumePartial.getValue(); 

    value = K_i - (K / (3.0*V)) * Vpartial;

  }
 
  public void remove() {
    deleteDependents();
    Curv.deleteObserver(this);
    totalK.deleteObserver(this);
    totalV.deleteObserver(this);
    VolumePartial.deleteObserver(this);
    Index.remove(pos);
  }
  
  public static VCSC at(Vertex v) {
    TriPosition T = new TriPosition(v.getSerialNumber());
    VCSC q = Index.get(T);
    if(q == null) {
      q = new VCSC(v);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static VCSC At(Vertex v) {
    TriPosition T = new TriPosition(v.getSerialNumber());
    VCSC q = Index.get(T);
    if(q == null) {
      q = new VCSC(v);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static double valueAt(Vertex v) {
    return at(v).getValue();
  }

}
