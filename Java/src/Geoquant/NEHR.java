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
  private static HashMap<TriPosition, SecondPartial> SecondPartialIndex = new HashMap<TriPosition, SecondPartial>();
  
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
  
  public static NEHR.SecondPartial secondPartialAt(Edge e, Edge f) {
    TriPosition T = new TriPosition(e.getSerialNumber(), f.getSerialNumber());
    SecondPartial q = SecondPartialIndex.get(T);
    if(q == null) {
      q = new SecondPartial(e, f);
      q.pos = T;
      SecondPartialIndex.put(T, q);
    }
    return q;
  }
  
  public static class SecondPartial extends Geoquant {
    private SecondPartialType type;
    
    private Volume.Sum totVolume;
    private Curvature3D.Sum totCurvature;
    int locality;
    
    /* Radius_ only */
    private Curvature3D curvature_i;
    private Volume.PartialSum vps_i;
    
    /* RadiusRadius only */
    private Curvature3D curvature_j;
    private Curvature3D.Partial curvPartial_ij;
    private Volume.PartialSum vps_j;
    private Volume.SecondPartialSum vps_ij; 
    
    /* RadiusEta only */
    private Curvature3D.Partial curvPartial_inm;
    private Volume.SecondPartialSum vps_inm;
      
    /* Eta_ only */
    private Volume.PartialSum vps_nm;
    private LinkedList<Curvature3D.Partial> curvPartials_nm;
    
    /* EtaEta only */
    private Volume.PartialSum vps_op;
    private LinkedList<Curvature3D.Partial> curvPartials_op;
    private Volume.SecondPartialSum vps_nm_op;
    private LinkedList<Curvature3D.SecondPartial> curvSecPartials;
    
    private SecondPartial(Vertex v, Vertex w) {
      super();
      type = SecondPartialType.RadiusRadius;
      totVolume = Volume.sum();
      totVolume.addDependent( this );

      totCurvature = Curvature3D.sum();
      totVolume.addDependent( this );

      curvature_i = Curvature3D.At( v );
      curvature_i.addDependent( this );

      vps_i = Volume.partialSumAt(v);
      vps_i.addDependent( this );
      
      if( v == w ){
        curvature_j = curvature_i;
        vps_j = vps_i;
        locality = 0;
      } else {
        curvature_j = Curvature3D.At( w );
        curvature_j.addDependent( this );

        vps_j = Volume.partialSumAt( w );
        vps_j.addDependent( this );
        
        locality = 1;
      }

      curvPartial_ij = curvature_i.partialAt(w);
      curvPartial_ij.addDependent( this );

      vps_ij = Volume.secondPartialSumAt(v, w);
      vps_ij.addDependent(this);
    }
    
    private SecondPartial(Vertex v, Edge nm) {
      super();
      type = SecondPartialType.RadiusEta;
      
      totVolume = Volume.sum();
      totVolume.addDependent( this );

      totCurvature = Curvature3D.sum();
      totVolume.addDependent( this );

      curvature_i = Curvature3D.At( v );
      curvature_i.addDependent( this );

      vps_i = Volume.partialSumAt(v);
      vps_i.addDependent( this );
      
      vps_nm = Volume.partialSumAt(nm);
      vps_nm.addDependent(this);
      
      curvPartial_inm = curvature_i.partialAt(nm);
      curvPartial_inm.addDependent(this);
      
      vps_inm = Volume.secondPartialSumAt(v, nm);
      vps_inm.addDependent(this);
      
      curvPartials_nm = new LinkedList<Curvature3D.Partial>();
      
      Curvature3D.Partial cp;
      for(Vertex w : nm.getLocalVertices()) {
        cp = Curvature3D.At(w).partialAt(nm);
        cp.addDependent( this );
        curvPartials_nm.add( cp );
      } 
    }
    
    private SecondPartial(Edge nm, Edge op) {
      super();
      type = SecondPartialType.EtaEta;
      
      totVolume = Volume.sum();
      totVolume.addDependent( this );

      totCurvature = Curvature3D.sum();
      totVolume.addDependent( this );

      vps_nm = Volume.partialSumAt(nm);
      vps_nm.addDependent(this);
      vps_op = Volume.partialSumAt(op);
      vps_op.addDependent(this);
      
      vps_nm_op = Volume.secondPartialSumAt(nm, op);
      
      curvPartials_nm = new LinkedList<Curvature3D.Partial>();
      curvPartials_op = new LinkedList<Curvature3D.Partial>();
      curvSecPartials = new LinkedList<Curvature3D.SecondPartial>();
      
      Curvature3D.Partial cp;
      Curvature3D.SecondPartial csp;
      
      for(Vertex v : nm.getLocalVertices()) {
        cp = Curvature3D.At(v).partialAt(nm);
        cp.addDependent( this );
        curvPartials_nm.add( cp );
        
        if(op.isAdjVertex(v)) {
          csp = Curvature3D.At(v).secondPartialAt(nm, op);
          csp.addDependent(this);
          curvSecPartials.add( csp );
        }
      }
      for(Vertex v : op.getLocalVertices()) {
        cp = Curvature3D.At(v).partialAt(op);
        cp.addDependent( this );
        curvPartials_op.add( cp );
      }
    }
    
    protected void recalculate() {
      switch(type) {
        case RadiusRadius:
          value = calculateRadRadCase();
          break;
        case RadiusEta:
          value = calculateRadEtaCase();
          break;
        case EtaEta:
          value = calculateEtaEtaCase();
          break;
      }
    }

    private double calculateRadRadCase() {
      // Calculates the second partial of the EHR (with respect to log radii).
      double totV = totVolume.getValue();
      double totK = totCurvature.getValue();
      double VPS_i = vps_i.getValue();
      double VPS_j = vps_j.getValue();

      double curvPartial = curvPartial_ij.getValue();

      double Ki = curvature_i.getValue();
      double Kj = curvature_j.getValue();

      double VolSecondPartial = vps_ij.getValue();
      
      double result;

      result = Math.pow(totV, (-4.0/3.0)) * (1.0 / 3.0);
      result *= (3 * totV * curvPartial - Ki * VPS_j - Kj * VPS_i
          + (4.0/3.0) * ( totK / totV ) *
          VPS_i * VPS_j - totK * VolSecondPartial);

      return result;
    }
    
    private double calculateRadEtaCase() {
      double totV = totVolume.getValue();
      double totK = totCurvature.getValue();
      double VPS_i = vps_i.getValue();
      double Ki = curvature_i.getValue();

      double VPS_nm = vps_nm.getValue();
      double CP_inm = curvPartial_inm.getValue();
      double VolSecondPartial = vps_inm.getValue();
      
      double curvPartialSum = 0;
      for(Curvature3D.Partial cp : curvPartials_nm) {
        curvPartialSum += cp.getValue();
      }
      
      double result;

      result = Math.pow(totV, (-4.0/3.0)) * (1.0 / 3.0);
      result *= (3 * totV * CP_inm - Ki * VPS_nm - curvPartialSum * VPS_i
          + (4.0/3.0) * ( totK / totV ) * VPS_i * VPS_nm
            - totK * VolSecondPartial);
          
      return result;
    }

    private double calculateEtaEtaCase() {
      double totV = totVolume.getValue();
      double totK = totCurvature.getValue();
      double VPS_nm = vps_nm.getValue();
      double VPS_op = vps_op.getValue();
      double VolSecondPartial = vps_nm_op.getValue();

      double curvPartialSum_nm = 0;
      for(Curvature3D.Partial cp : curvPartials_nm) {
        curvPartialSum_nm += cp.getValue();
      }
      double curvPartialSum_op = 0;
      for(Curvature3D.Partial cp : curvPartials_op) {
        curvPartialSum_op += cp.getValue();
      }
      double curvSecPartialSum = 0;
      for(Curvature3D.SecondPartial csp : curvSecPartials) {
        curvSecPartialSum +=  csp.getValue();
      }
      
      double result;

      result = Math.pow(totV, (-4.0/3.0)) * (1.0 / 3.0);
      result *= (3 * totV * curvSecPartialSum - curvPartialSum_nm * VPS_op
              - curvPartialSum_op * VPS_nm
              + (4.0/3.0) * ( totK / totV ) * VPS_op * VPS_nm
              - totK * VolSecondPartial);

      return result;
    }
    
    protected void remove() {
      deleteDependents();
      totVolume.removeDependent(this);
      totCurvature.removeDependent(this);

      switch(type) {
        case RadiusRadius:
          vps_i.removeDependent(this);
          curvature_i.removeDependent(this);
          vps_ij.removeDependent(this);
          curvPartial_ij.removeDependent(this);
          if(locality == 1) {
            vps_j.removeDependent(this);
            curvature_j.removeDependent(this);
          }
          break;
        case RadiusEta:
          vps_i.removeDependent(this);
          curvature_i.removeDependent(this);
          vps_nm.removeDependent(this);
          curvPartial_inm.removeDependent(this);
          vps_inm.removeDependent(this);
          for(Curvature3D.Partial cp : curvPartials_nm) {
            cp.removeDependent(this);
          }
          curvPartials_nm.clear();
          break;
        case EtaEta:
          vps_nm.removeDependent(this);
          vps_op.removeDependent(this);
          vps_nm_op.removeDependent(this);
          for(Curvature3D.Partial cp : curvPartials_nm) {
            cp.removeDependent(this);
          }
          for(Curvature3D.Partial cp : curvPartials_op) {
            cp.removeDependent(this);
          }
          for(Curvature3D.SecondPartial csp : curvSecPartials) {
            csp.removeDependent(this);
          }
          curvPartials_nm.clear();
          curvPartials_op.clear();
          curvSecPartials.clear();
      }      
      SecondPartialIndex.remove(pos);
    }
  }
}
