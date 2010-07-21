package geoquant;

import java.util.HashMap;
import java.util.LinkedList;

import triangulation.Edge;
import triangulation.Triangulation;
import triangulation.Vertex;


public class VEHR extends Geoquant {
  private Curvature3D.Sum totalK;
  private Volume.Sum totalV;
  private static VEHR instance = null; 
  private static HashMap<TriPosition, Partial> PartialIndex = new HashMap<TriPosition, Partial>();
  private static HashMap<TriPosition, SecondPartial> SecondPartialIndex = new HashMap<TriPosition, SecondPartial>();
  
  private VEHR() {
    super();
    totalK = Curvature3D.sum();
    totalV = Volume.sum();
    totalK.addObserver(this);
    totalV.addObserver(this);
  }
  
  protected void recalculate() {
    value = totalK.getValue() / Math.pow(totalV.getValue(), 1.0/3.0);
  }

  public void remove() {
    deleteDependents();
    totalK.deleteObserver(this);
    totalV.deleteObserver(this);
  }
  
  public static VEHR getInstance() {
    if(instance == null) {
      instance = new VEHR();
    }
    return instance;
  }
  
  public static double value() {
    return getInstance().getValue();
  }

  public static VEHR.Partial partialAt(Vertex v) {
    TriPosition T = new TriPosition(v.getSerialNumber());
    VEHR.Partial q = PartialIndex.get(T);
    if(q == null) {
      q = new Partial(v);
      q.pos = T;
      PartialIndex.put(T, q);
    }
    return q;
  }
  
  public static VEHR.Partial partialAt(Edge e) {
    TriPosition T = new TriPosition(e.getSerialNumber());
    VEHR.Partial q = PartialIndex.get(T);
    if(q == null) {
      q = new Partial(e);
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
      super(v);
      type = PartialType.Radius;
      
      totVolume = Volume.sum();
      totCurvature = Curvature3D.sum();
      volPartial = Volume.partialSumAt(v);
      totVolume.addObserver(this);
      totCurvature.addObserver(this);
      volPartial.addObserver(this);
      
      localCurv = Curvature3D.At(v);
      localCurv.addObserver(this);
    }
    
    private Partial(Edge e) {
      super(e);
      type = PartialType.Eta;
      
      totVolume = Volume.sum();
      totCurvature = Curvature3D.sum();
      volPartial = Volume.partialSumAt(e);
      totVolume.addObserver(this);
      totCurvature.addObserver(this);
      volPartial.addObserver(this);
      
      curvPartials = new LinkedList<Curvature3D.Partial>();
      Curvature3D.Partial partial;
      for(Vertex v : Triangulation.vertexTable.values()) {
        partial = Curvature3D.At(v).partialAt(e);
        partial.addObserver(this);
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
    
    public void remove() {
      deleteDependents();
      totVolume.deleteObserver(this);
      totCurvature.deleteObserver(this);
      volPartial.deleteObserver(this);
      switch(type) {
        case Radius:
          localCurv.deleteObserver(this);
          break;
        case Eta:
          for(Curvature3D.Partial partial : curvPartials) {
            partial.deleteObserver(this);
          }
          curvPartials.clear();
          break;
      }
      PartialIndex.remove(pos);
    }
  
    public String toString() {
      return "NEHR@[]" + "w.r.t" + location + "=" + getValue();
    }
  }
  
  public static VEHR.SecondPartial secondPartialAt(Vertex v, Vertex w) {
    TriPosition T = new TriPosition(v.getSerialNumber(), w.getSerialNumber());
    SecondPartial q = SecondPartialIndex.get(T);
    if(q == null) {
      q = new SecondPartial(v, w);
      q.pos = T;
      SecondPartialIndex.put(T, q);
    }
    return q;
  }
  
  public static VEHR.SecondPartial secondPartialAt(Vertex v, Edge e) {
    TriPosition T = new TriPosition(v.getSerialNumber(), e.getSerialNumber());
    SecondPartial q = SecondPartialIndex.get(T);
    if(q == null) {
      q = new SecondPartial(v, e);
      q.pos = T;
      SecondPartialIndex.put(T, q);
    }
    return q;
  }
  
  public static VEHR.SecondPartial secondPartialAt(Edge e, Edge f) {
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
      super(v, w);
      type = SecondPartialType.RadiusRadius;
      totVolume = Volume.sum();
      totVolume.addObserver( this );

      totCurvature = Curvature3D.sum();
      totVolume.addObserver( this );

      curvature_i = Curvature3D.At( v );
      curvature_i.addObserver( this );

      vps_i = Volume.partialSumAt(v);
      vps_i.addObserver( this );
      
      if( v == w ){
        curvature_j = curvature_i;
        vps_j = vps_i;
        locality = 0;
      } else {
        curvature_j = Curvature3D.At( w );
        curvature_j.addObserver( this );

        vps_j = Volume.partialSumAt( w );
        vps_j.addObserver( this );
        
        locality = 1;
      }

      curvPartial_ij = curvature_i.partialAt(w);
      curvPartial_ij.addObserver( this );

      vps_ij = Volume.secondPartialSumAt(v, w);
      vps_ij.addObserver(this);
    }
    
    private SecondPartial(Vertex v, Edge nm) {
      super(v, nm);
      type = SecondPartialType.RadiusEta;
      
      totVolume = Volume.sum();
      totVolume.addObserver( this );

      totCurvature = Curvature3D.sum();
      totVolume.addObserver( this );

      curvature_i = Curvature3D.At( v );
      curvature_i.addObserver( this );

      vps_i = Volume.partialSumAt(v);
      vps_i.addObserver( this );
      
      vps_nm = Volume.partialSumAt(nm);
      vps_nm.addObserver(this);
      
      curvPartial_inm = curvature_i.partialAt(nm);
      curvPartial_inm.addObserver(this);
      
      vps_inm = Volume.secondPartialSumAt(v, nm);
      vps_inm.addObserver(this);
      
      curvPartials_nm = new LinkedList<Curvature3D.Partial>();
      
      Curvature3D.Partial cp;
      for(Vertex w : Triangulation.vertexTable.values()) {
        cp = Curvature3D.At(w).partialAt(nm);
        cp.addObserver( this );
        curvPartials_nm.add( cp );
      } 
    }
    
    private SecondPartial(Edge nm, Edge op) {
      super(nm, op);
      type = SecondPartialType.EtaEta;
      
      totVolume = Volume.sum();
      totVolume.addObserver( this );

      totCurvature = Curvature3D.sum();
      totVolume.addObserver( this );

      vps_nm = Volume.partialSumAt(nm);
      vps_nm.addObserver(this);
      vps_op = Volume.partialSumAt(op);
      vps_op.addObserver(this);
      
      vps_nm_op = Volume.secondPartialSumAt(nm, op);
      
      curvPartials_nm = new LinkedList<Curvature3D.Partial>();
      curvPartials_op = new LinkedList<Curvature3D.Partial>();
      curvSecPartials = new LinkedList<Curvature3D.SecondPartial>();
      
      Curvature3D.Partial cp;
      Curvature3D.SecondPartial csp;
      
      for(Vertex v : Triangulation.vertexTable.values()) {
        cp = Curvature3D.At(v).partialAt(nm);
        cp.addObserver( this );
        curvPartials_nm.add( cp );
        
        cp = Curvature3D.At(v).partialAt(op);
        cp.addObserver( this );
        curvPartials_op.add( cp );
        
        csp = Curvature3D.At(v).secondPartialAt(nm, op);
        csp.addObserver(this);
        curvSecPartials.add( csp );
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
      result = result*(3 * totV * curvPartial - Ki * VPS_j - Kj * VPS_i
          + (4.0/3.0) * ( totK / totV ) * VPS_i * VPS_j 
          - totK * VolSecondPartial);
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
    
    public void remove() {
      deleteDependents();
      totVolume.deleteObserver(this);
      totCurvature.deleteObserver(this);

      switch(type) {
        case RadiusRadius:
          vps_i.deleteObserver(this);
          curvature_i.deleteObserver(this);
          vps_ij.deleteObserver(this);
          curvPartial_ij.deleteObserver(this);
          if(locality == 1) {
            vps_j.deleteObserver(this);
            curvature_j.deleteObserver(this);
          }
          break;
        case RadiusEta:
          vps_i.deleteObserver(this);
          curvature_i.deleteObserver(this);
          vps_nm.deleteObserver(this);
          curvPartial_inm.deleteObserver(this);
          vps_inm.deleteObserver(this);
          for(Curvature3D.Partial cp : curvPartials_nm) {
            cp.deleteObserver(this);
          }
          curvPartials_nm.clear();
          break;
        case EtaEta:
          vps_nm.deleteObserver(this);
          vps_op.deleteObserver(this);
          vps_nm_op.deleteObserver(this);
          for(Curvature3D.Partial cp : curvPartials_nm) {
            cp.deleteObserver(this);
          }
          for(Curvature3D.Partial cp : curvPartials_op) {
            cp.deleteObserver(this);
          }
          for(Curvature3D.SecondPartial csp : curvSecPartials) {
            csp.deleteObserver(this);
          }
          curvPartials_nm.clear();
          curvPartials_op.clear();
          curvSecPartials.clear();
      }      
      SecondPartialIndex.remove(pos);
    }
    
    public String toString() {
      return "NEHR@[]" + "w.r.t" + location + "=" + getValue();
    }
  }
}
