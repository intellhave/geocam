package geoquant;

import java.util.HashMap;

import triangulation.Triangulation;


public class LEHR extends Geoquant {
  // Index map
  private static HashMap<TriPosition, LEHR> Index = new HashMap<TriPosition, LEHR>();
  private static LEHR instance = null;
  private int id;
  // Needed geoquants
  

  private Curvature3D.Sum totalK;
  private Length.Sum totalL;
  
//  private EdgeHeight hij_l;
//  private EdgeHeight hij_k;
//  private DihedralAngle beta_ij_kl;
  
  public LEHR() {
    super();
    id = Triangulation.getTriangulationID();
    totalK = Curvature3D.sum();
    totalK.addObserver(this);
    totalL = Length.sum();
    totalL.addObserver(this);
  }
  
  protected void recalculate() {
    value = totalK.getValue() / totalL.getValue();
  }
 
  public void remove() {
    deleteDependents();
    totalK.deleteObserver(this);
    totalL.deleteObserver(this);
  }

  
  public static double value() {
    return getInstance().getValue();
  }

  public static LEHR getInstance() {
    if(instance == null || instance.id != Triangulation.getTriangulationID()) {
      instance = new LEHR();
    }
    return instance;
  }
}
