package Geoquant;

public class NEHR extends Geoquant {
  private Curvature3D.Sum totalK;
  private Volume.Sum totalV;
  private static NEHR instance = null; 
  
  private NEHR() {
    super();
    totalK = Curvature3D.sum();
    totalV = Volume.sum();
    totalK.addDependent(this);
    totalV.addDependent(this);
  }
  
  protected void recalculate() {
    value = totalK.getValue() / Math.pow(totalV.getValue(), 1.0/3.0);
  }

  protected void remove() {
    deleteDependents();
    totalK.removeDependent(this);
    totalV.removeDependent(this);
  }
  
  public static NEHR getInstance() {
    if(instance == null) {
      instance = new NEHR();
    }
    return instance;
  }
  
  public static double value() {
    return getInstance().getValue();
  }

}
