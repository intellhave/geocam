package Geoquant;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import Triangulation.Edge;
import Triangulation.StdEdge;
import Triangulation.Tetra;
import Triangulation.Triangulation;
import Triangulation.Vertex;

public class Curvature3D extends Geoquant {
  // Index map
  private static HashMap<TriPosition, Curvature3D> Index = new HashMap<TriPosition, Curvature3D>();
  private static Sum total = null;
  private HashMap<TriPosition, Partial> PartialIndex;
  private HashMap<TriPosition, SecondPartial> SecondPartialIndex;
  // Needed geoquants
  private LinkedList<SectionalCurvature> sec_curvs;
  private LinkedList<PartialEdge> partials;
  private Vertex v;
  
  public Curvature3D(Vertex v) {
    super(v);
    
    this.v = v;
    PartialIndex = new HashMap<TriPosition, Partial>();
    SecondPartialIndex = new HashMap<TriPosition, SecondPartial>();
    
    sec_curvs = new LinkedList<SectionalCurvature>();
    partials = new LinkedList<PartialEdge>();
    SectionalCurvature sc;
    PartialEdge pe;
    for(Edge e : v.getLocalEdges()) {
      sc = SectionalCurvature.At(e);
      sc.addObserver(this);
      pe = PartialEdge.At(v, e);
      pe.addObserver(this);
      sec_curvs.add(sc);
      partials.add(pe);
    }
  }
  
  protected void recalculate() {
    value = 0;
    Iterator<PartialEdge> pe_it = partials.iterator();
    for(SectionalCurvature sc : sec_curvs) {
      value += sc.getValue() * pe_it.next().getValue();
    }
  }
 
  public void remove() {
    deleteDependents();
    for(SectionalCurvature sc : sec_curvs) {
      sc.deleteObserver(this);
    }
    for(PartialEdge pe : partials) {
      pe.deleteObserver(this);
    }
    sec_curvs.clear();
    partials.clear();
    Index.remove(pos);
  }
  
  public static Curvature3D At(Vertex v) {
    TriPosition T = new TriPosition(v.getSerialNumber());
    Curvature3D q = Index.get(T);
    if(q == null) {
      q = new Curvature3D(v);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static double valueAt(Vertex v) {
    return At(v).getValue();
  }
  
  public static Curvature3D.Sum sum() {
    if(total == null) {
      total = new Sum();
    }
    return total;
  }
   
  public static class Sum extends Geoquant {
    LinkedList<Curvature3D> curvs = new LinkedList<Curvature3D>();
       
    private Sum() {
      super();
      Curvature3D k;
      for(Vertex v : Triangulation.vertexTable.values()) {
        k = Curvature3D.At(v);
        k.addObserver(this);
        curvs.add(k);
      }
    }
    protected void recalculate() {
      value = 0;
      for(Curvature3D k : curvs) {
        value += k.getValue();
      }
    }

    public void remove() {
      deleteDependents();
      for(Curvature3D k : curvs) {
        k.deleteObserver(this);
      }
      curvs.clear();
      total = null;
    }
  }

  public Curvature3D.Partial partialAt(Vertex w) {
    TriPosition T = new TriPosition(w.getSerialNumber());
    Partial q = PartialIndex.get(T);
    if(q == null) {
      q = new Partial(w);
      q.pos = T;
      PartialIndex.put(T, q);
    }
    return q;
  }
  
  public Curvature3D.Partial partialAt(Edge e) {
    TriPosition T = new TriPosition(e.getSerialNumber());
    Partial q = PartialIndex.get(T);
    if(q == null) {
      q = new Partial(e);
      q.pos = T;
      PartialIndex.put(T, q);
    }
    return q;
  }
  
  public class Partial extends Geoquant {
    private PartialType type;
    private int locality;
    /* Radius type variables */
    private LinkedList<DualArea> duals;
    private LinkedList<ConeAngle> angles;
    private LinkedList<Length> lengths;
    private LinkedList<Eta> etas;
    private LinkedList<Radius> radii;
    private Radius vRadius;
    private Curvature3D vCurv;
        
    /* Eta type variables */
    private LinkedList<LinkedList<DihedralAngle.Partial>> dih_partials;
    private LinkedList<PartialEdge> dijs;
    private ConeAngle dih_sum;
    private PartialEdge.Partial dij_partial;
    
    private Partial(Vertex w) {
      super(w);
      type = PartialType.Radius;
      if(v == w) {
        locality  = 0;
      } else if(v.isAdjVertex(w)){
        locality = 1;
      } else {
        locality = 2;
      }
      
      vRadius = Radius.At(v);
      vRadius.addObserver(this);
      vCurv = Curvature3D.At(v);
      vCurv.addObserver(this);
            
      DualArea da;
      ConeAngle ca;
      Length l;
      Eta eta;
      Radius r;
      StdEdge se;
      
      duals = new LinkedList<DualArea>();
      angles = new LinkedList<ConeAngle>();
      lengths = new LinkedList<Length>();
      etas = new LinkedList<Eta>();
      radii = new LinkedList<Radius>();
      
      for(Edge e : v.getLocalEdges()) {
        if(w.isAdjEdge(e)) {
          da = DualArea.At(e);
          da.addObserver(this);
          duals.add(da);
          
          ca = ConeAngle.At(e);
          ca.addObserver(this);
          angles.add(ca);
          
          l = Length.At(e);
          l.addObserver(this);
          lengths.add(l);
          
          eta = Eta.At(e);
          eta.addObserver(this);
          etas.add(eta);
          
          se = new StdEdge(e, v);
          
          r = Radius.At(se.v2);
          r.addObserver(this);
          radii.add(r);
        }
      }
    }

    private Partial(Edge e) {
      super(e);
      type = PartialType.Eta;
            
      if(v.isAdjEdge(e)) {
        locality = 0;
      } else {
        locality = 1;
      }
      
      if(locality == 0) {
        dij_partial = PartialEdge.At(v, e).partialAt(e);
        dih_sum = ConeAngle.At(e);
        dij_partial.addObserver(this);
        dih_sum.addObserver(this);
      }
      
      dih_partials = new LinkedList<LinkedList<DihedralAngle.Partial>>();
      dijs = new LinkedList<PartialEdge>();
      DihedralAngle.Partial dih_partial;
      PartialEdge dij;
      LinkedList<DihedralAngle.Partial> list;
      for(Edge ij : v.getLocalEdges()) {
        list = new LinkedList<DihedralAngle.Partial>();
        for(Tetra t : ij.getLocalTetras()) {
          dih_partial = DihedralAngle.At(ij, t).partialAt(e);
          dih_partial.addObserver(this);
          list.add(dih_partial);
        }
        dih_partials.add(list);
        dij = PartialEdge.At(v, ij);
        dij.addObserver(this);
        dijs.add(dij);
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
      if(locality == 0) {
        
        double rV = vRadius.getValue();
        double curv = vCurv.getValue();
        double dih_sum, len, Lij_star, eta, rW;
      
        double sum = 0.0;

        Iterator<DualArea> da_it = duals.iterator();
        Iterator<ConeAngle> ca_it = angles.iterator();
        Iterator<Length> l_it = lengths.iterator();
        Iterator<Eta> eta_it = etas.iterator();
        Iterator<Radius> r_it = radii.iterator();
      
        while(da_it.hasNext()) {
          Lij_star = da_it.next().getValue();
          dih_sum = ca_it.next().getValue();
          len = l_it.next().getValue();
          eta = eta_it.next().getValue();
          rW = r_it.next().getValue();
        
          sum += 2.0*Lij_star/len - (2*Math.PI - dih_sum)* Math.pow(rV,2) 
            * Math.pow(rW,2) * (Math.pow(eta,2) - 1) / Math.pow(len,3);
        }

        sum += curv;

        return sum;
      } else if(locality == 1){
        double vr = vRadius.getValue();
        double wr = radii.get(0).getValue();
        double Lvw_star = duals.get(0).getValue();
        double dih_sum = angles.get(0).getValue();
        double l_vw = lengths.get(0).getValue();
        double eta = etas.get(0).getValue();
                      
        return -2*Lvw_star/l_vw + (2*Math.PI - dih_sum)*(Math.pow(vr,2)*Math.pow(wr,2)
            *(Math.pow(eta,2) - 1)/Math.pow(l_vw, 3)); 
      } else {
        return 0;
      }
    }

    private double calculateEtaCase() {
      double partial;
      if( locality == 1 ) {
        partial = 0;
      } else {
        partial = (2 * Math.PI - dih_sum.getValue()) * dij_partial.getValue();
      }
      double dih_partial_sum;
      double dij_val;
      Iterator<PartialEdge> dij_it = dijs.iterator();
      for(LinkedList<DihedralAngle.Partial> list : dih_partials) {
        dih_partial_sum = 0;
        for(DihedralAngle.Partial dih_partial : list) {
          dih_partial_sum -= dih_partial.getValue();
        }
        dij_val = dij_it.next().getValue();
        partial += dih_partial_sum * dij_val;
      }
      return partial;
    }
    
    public void remove() {
      deleteDependents();
      switch(type) {
        case Radius:
          vRadius.deleteObserver(this);
          vCurv.deleteObserver(this);
          Iterator<DualArea> da_it = duals.iterator();
          Iterator<ConeAngle> ca_it = angles.iterator();
          Iterator<Length> l_it = lengths.iterator();
          Iterator<Eta> eta_it = etas.iterator();
          Iterator<Radius> r_it = radii.iterator();
          while(da_it.hasNext()) {
            da_it.next().deleteObserver(this);
            ca_it.next().deleteObserver(this);
            l_it.next().deleteObserver(this);
            eta_it.next().deleteObserver(this);
            r_it.next().deleteObserver(this);
          }
          duals.clear();
          angles.clear();
          lengths.clear();
          etas.clear();
          radii.clear();
          break;
        
        case Eta:
          if(locality == 0) {
            dij_partial.deleteObserver(this);
            dih_sum.deleteObserver(this);
          }
          Iterator<PartialEdge> dij_it = dijs.iterator();
          for(LinkedList<DihedralAngle.Partial> list : dih_partials) {
            dij_it.next().deleteObserver(this);
            for(DihedralAngle.Partial dij_partial : list) {
              dij_partial.deleteObserver(this);
            }
            list.clear();
          }
          dih_partials.clear();
          break;
      }
      PartialIndex.remove(pos);
    }
    
    public String toString() {
      return "Curvature3D@[" + v + "]" + "w.r.t" + location + "=" + getValue();
    }
  }

  public Curvature3D.SecondPartial secondPartialAt(Edge e, Edge f) {
    TriPosition T = new TriPosition(e.getSerialNumber(), f.getSerialNumber());
    SecondPartial q = SecondPartialIndex.get(T);
    if(q == null) {
      q = new SecondPartial(e, f);
      q.pos = T;
      SecondPartialIndex.put(T, q);
    }
    return q;
  }
  
  public class SecondPartial extends Geoquant {
    private LinkedList<LinkedList<DihedralAngle.SecondPartial>> dih_sec_partials;
    private LinkedList<LinkedList<DihedralAngle.Partial>> dih_partials_e;
    private LinkedList<LinkedList<DihedralAngle.Partial>> dih_partials_f;
    private LinkedList<LinkedList<DihedralAngle>> dihs;
    
    private LinkedList<PartialEdge.SecondPartial> dij_sec_partials;
    private LinkedList<PartialEdge.Partial> dij_partials_e;
    private LinkedList<PartialEdge.Partial> dij_partials_f;
    private LinkedList<PartialEdge> dijs;
    
    public SecondPartial(Edge e, Edge f) {
      super(e, f);
            
      dih_sec_partials = new LinkedList<LinkedList<DihedralAngle.SecondPartial>>();
      dih_partials_e = new LinkedList<LinkedList<DihedralAngle.Partial>>();
      dih_partials_f = new LinkedList<LinkedList<DihedralAngle.Partial>>();
      dihs = new LinkedList<LinkedList<DihedralAngle>>();
      
      dij_sec_partials = new LinkedList<PartialEdge.SecondPartial>();
      dij_partials_e = new LinkedList<PartialEdge.Partial>();
      dij_partials_f = new LinkedList<PartialEdge.Partial>();
      dijs = new LinkedList<PartialEdge>();
      
      DihedralAngle.SecondPartial dih_sec_partial;
      DihedralAngle.Partial dih_partial;
      DihedralAngle dih;
      
      PartialEdge.SecondPartial dij_sec_partial;
      PartialEdge.Partial dij_partial;
      PartialEdge dij;
      
      LinkedList<DihedralAngle.SecondPartial> dih_sec_list;
      LinkedList<DihedralAngle.Partial> dih_partial_e_list;
      LinkedList<DihedralAngle.Partial> dih_partial_f_list;
      LinkedList<DihedralAngle> dih_list;
      
      for(Edge ij : v.getLocalEdges()) {
        /* Dihedral Angles */
        dih_sec_list = new LinkedList<DihedralAngle.SecondPartial>();
        dih_partial_e_list = new LinkedList<DihedralAngle.Partial>();
        dih_partial_f_list = new LinkedList<DihedralAngle.Partial>();
        dih_list = new LinkedList<DihedralAngle>();
        
        /* PartialEdge */
        dij = PartialEdge.At(v, ij);
        dij.addObserver(this);
        dijs.add(dij);
        
        /* PartialEdgeSecondPartial */
        dij_sec_partial = dij.secondPartialAt(e, f);
        dij_sec_partial.addObserver(this);
        dij_sec_partials.add(dij_sec_partial);
        
        /* PartialEdgePartial */
        dij_partial = dij.partialAt(e);
        dij_partial.addObserver(this);
        dij_partials_e.add(dij_partial);
        dij_partial = dij.partialAt(f);
        dij_partial.addObserver(this);
        dij_partials_f.add(dij_partial);
        
        for(Tetra t : ij.getLocalTetras()) {
          /* DihedralAngle */
          dih = DihedralAngle.At(ij, t);
          dih.addObserver(this);
          dih_list.add(dih);
          
          /* DihedralAngleSecondPartial */
          dih_sec_partial = dih.secondPartialAt(e, f);
          dih_sec_partial.addObserver(this);
          dih_sec_list.add(dih_sec_partial);
          
          /* DihedralAnglePartial */
          dih_partial = dih.partialAt(e);
          dih_partial.addObserver(this);
          dih_partial_e_list.add(dih_partial);
          dih_partial = dih.partialAt(f);
          dih_partial.addObserver(this);
          dih_partial_f_list.add(dih_partial);
        }
        
        dih_sec_partials.add(dih_sec_list);
        dih_partials_e.add(dih_partial_e_list);
        dih_partials_f.add(dih_partial_f_list);
        dihs.add(dih_list);
      }
    }
    
    protected void recalculate() {
      value = 0;
      double dih_sec_partial_sum;
      double dih_partial_e_sum;
      double dih_partial_f_sum;
      double dih_sum;
      double temp;
      
      Iterator<LinkedList<DihedralAngle.SecondPartial>> dih_sec_list = dih_sec_partials.iterator();
      Iterator<LinkedList<DihedralAngle.Partial>> dih_e_list = dih_partials_e.iterator();
      Iterator<LinkedList<DihedralAngle.Partial>> dih_f_list = dih_partials_f.iterator();
      Iterator<LinkedList<DihedralAngle>> dih_list = dihs.iterator();
      Iterator<PartialEdge.SecondPartial> dij_sec_it = dij_sec_partials.iterator();
      Iterator<PartialEdge.Partial> dij_e_it = dij_partials_e.iterator();
      Iterator<PartialEdge.Partial> dij_f_it = dij_partials_f.iterator();
      Iterator<PartialEdge> dij_it = dijs.iterator();
      
      
      Iterator<DihedralAngle.SecondPartial> dih_sec_it;
      Iterator<DihedralAngle.Partial> dih_e_it;
      Iterator<DihedralAngle.Partial> dih_f_it;
      Iterator<DihedralAngle> dih_it;
      
      while(dij_it.hasNext()) {
        dih_sec_partial_sum = 0;
        dih_partial_e_sum = 0;
        dih_partial_f_sum = 0;
        dih_sum = 2 * Math.PI;
        
        dih_sec_it = dih_sec_list.next().iterator();
        dih_e_it = dih_e_list.next().iterator();
        dih_f_it = dih_f_list.next().iterator();
        dih_it = dih_list.next().iterator();
        while(dih_it.hasNext()) {
          dih_sec_partial_sum -= dih_sec_it.next().getValue();
          dih_partial_e_sum -= dih_e_it.next().getValue();
          dih_partial_f_sum -= dih_f_it.next().getValue();
          dih_sum -= dih_it.next().getValue();          
        }
        temp = dih_sec_partial_sum * dij_it.next().getValue()
          + dih_partial_e_sum * dij_f_it.next().getValue()
          + dih_partial_f_sum * dij_e_it.next().getValue()
          + dih_sum * dij_sec_it.next().getValue();
        value += temp;
      }      
    }

    public void remove() {
      deleteDependents();
      Iterator<LinkedList<DihedralAngle.SecondPartial>> dih_sec_list = dih_sec_partials.iterator();
      Iterator<LinkedList<DihedralAngle.Partial>> dih_e_list = dih_partials_e.iterator();
      Iterator<LinkedList<DihedralAngle.Partial>> dih_f_list = dih_partials_f.iterator();
      Iterator<LinkedList<DihedralAngle>> dih_list = dihs.iterator();
      Iterator<PartialEdge.SecondPartial> dij_sec_it = dij_sec_partials.iterator();
      Iterator<PartialEdge.Partial> dij_e_it = dij_partials_e.iterator();
      Iterator<PartialEdge.Partial> dij_f_it = dij_partials_f.iterator();
      Iterator<PartialEdge> dij_it = dijs.iterator();
      
      
      Iterator<DihedralAngle.SecondPartial> dih_sec_it;
      Iterator<DihedralAngle.Partial> dih_e_it;
      Iterator<DihedralAngle.Partial> dih_f_it;
      Iterator<DihedralAngle> dih_it;
      
      while(dij_it.hasNext()) {
        dih_sec_it = dih_sec_list.next().iterator();
        dih_e_it = dih_e_list.next().iterator();
        dih_f_it = dih_f_list.next().iterator();
        dih_it = dih_list.next().iterator();
        while(dih_it.hasNext()) {
          dih_sec_it.next().deleteObserver(this);
          dih_e_it.next().deleteObserver(this);
          dih_f_it.next().deleteObserver(this);
          dih_it.next().deleteObserver(this);          
        }
        dij_it.next().deleteObserver(this);
        dij_f_it.next().deleteObserver(this);
        dij_e_it.next().deleteObserver(this);
        dij_sec_it.next().deleteObserver(this);
      }
      dih_sec_partials.clear();
      dih_partials_e.clear();
      dih_partials_f.clear();
      dihs.clear();
      dij_sec_partials.clear();
      dij_partials_e.clear();
      dij_partials_f.clear();
      dijs.clear();
      SecondPartialIndex.remove(pos);      
    }
    
    public String toString() {
      return "Curvature3D@[" + v + "]" + "w.r.t" + location + "=" + getValue();
    }
  }
}
