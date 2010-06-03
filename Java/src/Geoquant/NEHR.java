package Geoquant;

import java.util.HashMap;
import java.util.LinkedList;

import Triangulation.Edge;
import Triangulation.Vertex;

public class NEHR extends Geoquant {
  private Curvature3D.Sum totalK;
  private Volume.Sum totalV;
  private static NEHR instance = null; 
  private static HashMap<TriPosition, Partial> PartialIndex = new HashMap<TriPosition, Partial>();
  
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

  public static NEHR.Partial partialAt(Vertex v) {
    TriPosition T = new TriPosition(v.getSerialNumber());
    NEHR.Partial q = PartialIndex.get(T);
    if(q == null) {
      q = new Partial(v);
      q.pos = T;
      PartialIndex.put(T, q);
    }
    return q;
  }
  
  public static class Partial extends Geoquant {
    private PartialType type;
    
    private Volume.Sum totVolume;
    private Curvature3D.Sum totCurvature;
    private Volume.PartialSum volPartial;
    
    /* Radius type only */
    private Curvature3D localCurv;
    
    /* Eta type only */
    private LinkedList<Curvature3D.Partial> curvPartials;
    
    private Partial(Vertex v) {
      super();
      type = PartialType.Radius;
      
      totVolume = Volume.sum();
      totCurvature = Curvature3D.sum();
      volPartial = Volume.partialSumAt(v);
      totVolume.addDependent(this);
      totCurvature.addDependent(this);
      volPartial.addDependent(this);
      
      localCurv = Curvature3D.At(v);
      localCurv.addDependent(this);
    }
    
    private Partial(Edge e) {
      super();
      type = PartialType.Eta;
      
      totVolume = Volume.sum();
      totCurvature = Curvature3D.sum();
      volPartial = Volume.partialSumAt(e);
      totVolume.addDependent(this);
      totCurvature.addDependent(this);
      volPartial.addDependent(this);
      
      curvPartials = new LinkedList<Curvature3D.Partial>();
      Curvature3D.Partial partial;
      for(Vertex v : e.getLocalVertices()) {
        partial = Curvature3D.At(v).partialAt(e);
        partial.addDependent(this);
        curvPartials.add(partial);
      }
    }
    
    protected void recalculate() {
      switch(type) {
        case Radius:
          value = calculateRadiusCase();
          break;
        case Eta:
          value = calculateEtaCase();
          break;
      }
    }

    private double calculateRadiusCase() {
      double totV = totVolume.getValue();
      double totK = totCurvature.getValue();
      double K = localCurv.getValue();
      double volumePartial = volPartial.getValue();
      
      return Math.pow(totV, -4.0/3.0)*(K*totV - totK*volumePartial/3.0); 
    }

    private double calculateEtaCase() {
      double totV = totVolume.getValue();
      double totK = totCurvature.getValue();
      double volumePartial = volPartial.getValue();
      
      double curv_partial_sum = 0;
      for(Curvature3D.Partial partial : curvPartials) {
        curv_partial_sum += partial.getValue();
      }
      
      return Math.pow(totV, -4.0/3.0)*(curv_partial_sum*totV - totK*volumePartial/3.0);
    }
    
    protected void remove() {
      deleteDependents();
      totVolume.removeDependent(this);
      totCurvature.removeDependent(this);
      volPartial.removeDependent(this);
      switch(type) {
        case Radius:
          localCurv.removeDependent(this);
          break;
        case Eta:
          for(Curvature3D.Partial partial : curvPartials) {
            partial.removeDependent(this);
          }
          curvPartials.clear();
          break;
      }
      PartialIndex.remove(pos);
    }
    
  }
}
