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
  // Needed geoquants
  private LinkedList<SectionalCurvature> sec_curvs;
  private LinkedList<PartialEdge> partials;
  private Vertex v;
  
  public Curvature3D(Vertex v) {
    super();
    
    this.v = v;
    PartialIndex = new HashMap<TriPosition, Partial>();
    
    sec_curvs = new LinkedList<SectionalCurvature>();
    partials = new LinkedList<PartialEdge>();
    SectionalCurvature sc;
    PartialEdge pe;
    for(Edge e : v.getLocalEdges()) {
      sc = SectionalCurvature.At(e);
      sc.addDependent(this);
      pe = PartialEdge.At(v, e);
      pe.addDependent(this);
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
 
  protected void remove() {
    deleteDependents();
    for(SectionalCurvature sc : sec_curvs) {
      sc.removeDependent(this);
    }
    for(PartialEdge pe : partials) {
      pe.removeDependent(this);
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
        k.addDependent(this);
        curvs.add(k);
      }
    }
    protected void recalculate() {
      value = 0;
      for(Curvature3D k : curvs) {
        value += k.getValue();
      }
    }

    protected void remove() {
      deleteDependents();
      for(Curvature3D k : curvs) {
        k.removeDependent(this);
      }
      curvs.clear();
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
  
  private class Partial extends Geoquant {
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
      super();
      type = PartialType.Radius;
      if(v == w) {
        locality  = 0;
      } else if(v.isAdjVertex(w)){
        locality = 1;
      }
      
      vRadius = Radius.At(v);
      vRadius.addDependent(this);
      vCurv = Curvature3D.At(v);
      vCurv.addDependent(this);
            
      DualArea da;
      ConeAngle ca;
      Length l;
      Eta eta;
      Radius r;
      StdEdge se;
      
      for(Edge e : v.getLocalEdges()) {
        if(w.isAdjEdge(e)) {
          da = DualArea.At(e);
          da.addDependent(this);
          duals.add(da);
          
          ca = ConeAngle.At(e);
          ca.addDependent(this);
          angles.add(ca);
          
          l = Length.At(e);
          l.addDependent(this);
          lengths.add(l);
          
          eta = Eta.At(e);
          eta.addDependent(this);
          etas.add(eta);
          
          se = new StdEdge(e, v);
          r = Radius.At(se.v2);
          r.addDependent(this);
          radii.add(r);
        }
      }
    }

    private Partial(Edge e) {
      super();
      type = PartialType.Eta;
      
      if(v.isAdjEdge(e)) {
        locality = 0;
      } else {
        locality = 1;
      }
      
      if(locality == 0) {
        dij_partial = PartialEdge.At(v, e).partialAt(e);
        dih_sum = ConeAngle.At(e);
        dij_partial.addDependent(this);
        dih_sum.addDependent(this);
      }
      
      DihedralAngle.Partial dih_partial;
      PartialEdge dij;
      LinkedList<DihedralAngle.Partial> list;
      for(Edge nm : v.getLocalEdges()) {
        list = new LinkedList<DihedralAngle.Partial>();
        for(Tetra t : nm.getLocalTetras()) {
          dih_partial = DihedralAngle.At(nm, t).partialAt(e);
          dih_partial.addDependent(this);
          list.add(dih_partial);
        }
        dih_partials.add(list);
        dij = PartialEdge.At(v, nm);
        dij.addDependent(this);
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
            * Math.pow(rW,2) * (1 - Math.pow(eta,2)) / Math.pow(len,3);
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
            *(1-Math.pow(eta,2))/Math.pow(l_vw, 3)); 
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
      Iterator<PartialEdge> dij_it = dijs.iterator();
      for(LinkedList<DihedralAngle.Partial> list : dih_partials) {
        dih_partial_sum = 0;
        for(DihedralAngle.Partial dih_partial : list) {
          dih_partial_sum -= dih_partial.getValue();
        }
        partial += dih_partial_sum * dij_it.next().getValue();
      }
      return partial;
    }
    
    protected void remove() {
      deleteDependents();
      switch(type) {
        case Radius:
          vRadius.removeDependent(this);
          vCurv.removeDependent(this);
          Iterator<DualArea> da_it = duals.iterator();
          Iterator<ConeAngle> ca_it = angles.iterator();
          Iterator<Length> l_it = lengths.iterator();
          Iterator<Eta> eta_it = etas.iterator();
          Iterator<Radius> r_it = radii.iterator();
          while(da_it.hasNext()) {
            da_it.next().removeDependent(this);
            ca_it.next().removeDependent(this);
            l_it.next().removeDependent(this);
            eta_it.next().removeDependent(this);
            r_it.next().removeDependent(this);
          }
          duals.clear();
          angles.clear();
          lengths.clear();
          etas.clear();
          radii.clear();
          break;
        
        case Eta:
          if(locality == 0) {
            dij_partial.removeDependent(this);
            dih_sum.removeDependent(this);
          }
          Iterator<PartialEdge> dij_it = dijs.iterator();
          for(LinkedList<DihedralAngle.Partial> list : dih_partials) {
            dij_it.next().removeDependent(this);
            for(DihedralAngle.Partial dij_partial : list) {
              dij_partial.removeDependent(this);
            }
            list.clear();
          }
          dih_partials.clear();
          break;
      }
      PartialIndex.remove(pos);
    }
    
    
  }
}
