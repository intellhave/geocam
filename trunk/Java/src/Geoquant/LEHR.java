package Geoquant;

import java.util.HashMap;

import Triangulation.Face;
import Triangulation.StdTetra;
import Triangulation.Tetra;

public class LEHR extends Geoquant {
  // Index map
  private static HashMap<TriPosition, LEHR> Index = new HashMap<TriPosition, LEHR>();
  private static LEHR instance = null; 
  // Needed geoquants
  

  private Curvature3D.Sum totalK;
  private Length.Sum totalL;
  
//  private EdgeHeight hij_l;
//  private EdgeHeight hij_k;
//  private DihedralAngle beta_ij_kl;
  
  public LEHR() {
    super();
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
    if(instance == null) {
      instance = new LEHR();
    }
    return instance;
  }
}
