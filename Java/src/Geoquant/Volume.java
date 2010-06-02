package Geoquant;

import java.util.HashMap;
import java.util.LinkedList;

import Triangulation.Edge;
import Triangulation.StdTetra;
import Triangulation.Tetra;
import Triangulation.Triangulation;
import Triangulation.Vertex;

public class Volume extends Geoquant {
  // Index map
  private static HashMap<TriPosition, Volume> Index = new HashMap<TriPosition, Volume>();
  private static Sum total;
  private HashMap<TriPosition, Partial> PartialIndex;
  private HashMap<TriPosition, SecondPartial> SecondPartialIndex;
  
  private Length L_ij;
  private Length L_ik;
  private Length L_il;
  private Length L_jk;
  private Length L_jl;
  private Length L_kl;
  
  // Location
  private Tetra t;
  
  public Volume(Tetra t) {
    super();
    
    PartialIndex = new HashMap<TriPosition, Partial>();
    SecondPartialIndex = new HashMap<TriPosition, SecondPartial>();
    this.t = t;
    
    StdTetra st = new StdTetra(t);
    L_ij = Length.At(st.e12);
    L_ik = Length.At(st.e13);
    L_il = Length.At(st.e14);
    L_jk = Length.At(st.e23);
    L_jl = Length.At(st.e24);
    L_kl = Length.At(st.e34);
    
    L_ij.addDependent(this);
    L_ik.addDependent(this);
    L_il.addDependent(this);
    L_jk.addDependent(this);
    L_jl.addDependent(this);
    L_kl.addDependent(this);
  }
  
  protected void recalculate() {
    double CayleyMenger;
    double L12 = L_ij.getValue();
    double L13 = L_ik.getValue();
    double L14 = L_il.getValue();
    double L23 = L_jk.getValue();
    double L24 = L_jl.getValue();
    double L34 = L_jk.getValue();
     
    CayleyMenger = (-1)*( Math.pow(L12, 4.0)*Math.pow(L34,2.0) + Math.pow(L13, 4.0)*Math.pow(L24,2.0) 
       + Math.pow(L14, 4.0)*Math.pow(L23,2.0) + Math.pow(L23, 4.0)*Math.pow(L14,2.0)
       + Math.pow(L24, 4.0)*Math.pow(L13,2.0) + Math.pow(L34, 4.0)*Math.pow(L12,2.0) );

    CayleyMenger += (-1)*( Math.pow(L12, 2.0)*Math.pow(L13,2.0)*Math.pow(L23,2.0) 
         + Math.pow(L12, 2.0)*Math.pow(L14,2.0)*Math.pow(L24,2.0)
         + Math.pow(L13, 2.0)*Math.pow(L14,2.0)*Math.pow(L34,2.0)
         + Math.pow(L23, 2.0)*Math.pow(L24,2.0)*Math.pow(L34,2.0) );

    CayleyMenger += Math.pow(L12, 2.0)*Math.pow(L13,2.0)*Math.pow(L24,2.0) 
                           + Math.pow(L12, 2.0)*Math.pow(L13,2.0)*Math.pow(L34,2.0)
                           + Math.pow(L12, 2.0)*Math.pow(L14,2.0)*Math.pow(L23,2.0) 
                           + Math.pow(L12, 2.0)*Math.pow(L14,2.0)*Math.pow(L34,2.0);

    CayleyMenger += Math.pow(L13, 2.0)*Math.pow(L14,2.0)*Math.pow(L23,2.0) 
                           + Math.pow(L13, 2.0)*Math.pow(L14,2.0)*Math.pow(L24,2.0)
                           + Math.pow(L13, 2.0)*Math.pow(L23,2.0)*Math.pow(L24,2.0)
                           + Math.pow(L14, 2.0)*Math.pow(L23,2.0)*Math.pow(L24,2.0);

    CayleyMenger += Math.pow(L12, 2.0)*Math.pow(L23,2.0)*Math.pow(L34,2.0)
                           + Math.pow(L14, 2.0)*Math.pow(L23,2.0)*Math.pow(L34,2.0) 
                           + Math.pow(L12, 2.0)*Math.pow(L24,2.0)*Math.pow(L34,2.0) 
                           + Math.pow(L13, 2.0)*Math.pow(L24,2.0)*Math.pow(L34,2.0);
     
    value = Math.sqrt(CayleyMenger / 144.0);

  }

  protected void remove() {
    deleteDependents();
    L_ij.removeDependent(this);
    L_ik.removeDependent(this);
    L_il.removeDependent(this);
    L_jk.removeDependent(this);
    L_jl.removeDependent(this);
    L_kl.removeDependent(this);
    Index.remove(pos);
  }

  public static Volume At(Tetra t) {
    TriPosition T = new TriPosition(t.getSerialNumber());
    Volume q = Index.get(T);
    if(q == null) {
      q = new Volume(t);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static double valueAt(Tetra t) {
    return At(t).getValue();
  }
  
  
  public static Volume.Sum sum() {
    if(total == null) {
      total = new Sum();
    }
    return total;
  }
    
  public static class Sum extends Geoquant {
    LinkedList<Volume> volumes = new LinkedList<Volume>();
    
    private Sum() {
      super();
      Volume v;
      for(Tetra t : Triangulation.tetraTable.values()) {
        v = Volume.At(t);
        v.addDependent(this);
        volumes.add(v);
      }
    }
    protected void recalculate() {
      value = 0;
      for(Volume v : volumes) {
        value += v.getValue();
      }
    }

    protected void remove() {
      deleteDependents();
      for(Volume v : volumes) {
        v.removeDependent(this);
      }
      volumes.clear();
    }
  }

  public Volume.Partial partialAt(Vertex v) {
    TriPosition T = new TriPosition(v.getSerialNumber());
    Partial q = PartialIndex.get(T);
    if(q == null) {
      q = new Partial(v);
      q.pos = T;
      PartialIndex.put(T, q);
    }
    return q;
  }
  
  public Volume.Partial partialAt(Edge e) {
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
    private Radius[] radii;
    private Alpha[] alphas;
    private Eta[] etas;
    PartialType type;
    
    
    private Partial(Vertex v) {
      super();
      
      type = PartialType.Radius;
      radii = new Radius[4];
      alphas = new Alpha[4];
      etas = new Eta[6];
      
      StdTetra st = new StdTetra(t, v);
      radii[0] = Radius.At(st.v1);
      radii[1] = Radius.At(st.v2);
      radii[2] = Radius.At(st.v3);
      radii[3] = Radius.At(st.v4);
      
      alphas[0] = Alpha.At(st.v1);
      alphas[1] = Alpha.At(st.v2);
      alphas[2] = Alpha.At(st.v3);
      alphas[3] = Alpha.At(st.v4); 
      
      etas[0] = Eta.At(st.e12);
      etas[1] = Eta.At(st.e13);
      etas[2] = Eta.At(st.e14);
      etas[3] = Eta.At(st.e23);
      etas[4] = Eta.At(st.e24);
      etas[5] = Eta.At(st.e34);
      
      for(int i = 0; i < 4; i++) {
        radii[i].addDependent(this);
        alphas[i].addDependent(this);
      }
      
      for(int i = 0; i < 6; i++) {
        etas[i].addDependent(this);
      }
      
    }
    
    private Partial(Edge e) {
      super();
      
      type = PartialType.Eta;
      radii = new Radius[4];
      alphas = new Alpha[4];
      etas = new Eta[6];
      
      StdTetra st = new StdTetra(t, e);
      radii[0] = Radius.At(st.v1);
      radii[1] = Radius.At(st.v2);
      radii[2] = Radius.At(st.v3);
      radii[3] = Radius.At(st.v4);
      
      alphas[0] = Alpha.At(st.v1);
      alphas[1] = Alpha.At(st.v2);
      alphas[2] = Alpha.At(st.v3);
      alphas[3] = Alpha.At(st.v4); 
      
      etas[0] = Eta.At(st.e12);
      etas[1] = Eta.At(st.e13);
      etas[2] = Eta.At(st.e14);
      etas[3] = Eta.At(st.e23);
      etas[4] = Eta.At(st.e24);
      etas[5] = Eta.At(st.e34);
      
      for(int i = 0; i < 4; i++) {
        radii[i].addDependent(this);
        alphas[i].addDependent(this);
      }
      
      for(int i = 0; i < 6; i++) {
        etas[i].addDependent(this);
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

    protected void remove() {
      deleteDependents();
      for(int i = 0; i < 4; i++) {
        radii[i].removeDependent(this);
        alphas[i].removeDependent(this);
      }
      
      for(int i = 0; i < 6; i++) {
        etas[i].removeDependent(this);
      }
      PartialIndex.remove(pos);
    }
    
    private double calculateRadiusCase() {
      double r1, r2, r3, r4, alpha1, alpha2, alpha3, alpha4, 
      Eta12, Eta13, Eta14, Eta23, Eta24, Eta34;
    
      r1 = radii[0].getValue();
      r2 = radii[1].getValue();
      r3 = radii[2].getValue();
      r4 = radii[3].getValue();

      alpha1 = alphas[0].getValue();
      alpha2 = alphas[1].getValue();
      alpha3 = alphas[2].getValue();
      alpha4 = alphas[3].getValue();

      Eta12 = etas[0].getValue();  
      Eta13 = etas[1].getValue();
      Eta14 = etas[2].getValue(); 
      Eta23 = etas[3].getValue();
      Eta24 = etas[4].getValue();  
      Eta34 = etas[5].getValue();
            
      double result = (-2*(alpha3*Math.pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23) +
          alpha1*(-(alpha2*alpha3) + Math.pow(Eta23,2)))*r1*Math.pow(r2,2)*
          Math.pow(r3,2) + 2*r2*r3*(alpha1*Eta23*Eta24*r1*r2 + Eta12*(Eta14*Eta23 +
          Eta13*Eta24)*r1*r2 - Math.pow(Eta12,2)*Eta34*r1*r2 +
          Eta13*Eta14*Eta23*r1*r3 + alpha1*alpha3*Eta24*r1*r3 -
          Math.pow(Eta13,2)*Eta24*r1*r3 + alpha1*Eta23*Eta34*r1*r3 -
          Eta14*Math.pow(Eta23,2)*r2*r3 + Eta13*Eta23*Eta24*r2*r3 +
          Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 + alpha3*Eta24*r2 +
          Eta23*Eta34*r2)*r3 +
          alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 + alpha3*Eta14*r3 +
          Eta13*Eta34*r3))*r4 +
          2*r1*r2*r3*(alpha1*Eta23*Eta24*r2 - Math.pow(Eta12,2)*Eta34*r2 +
     alpha2*(Eta13*Eta14 + alpha1*Eta34)*r2 + Eta13*Eta14*Eta23*r3 +
     alpha1*alpha3*Eta24*r3 - Math.pow(Eta13,2)*Eta24*r3 +
     alpha1*Eta23*Eta34*r3 +
     Eta12*(Eta14*Eta23*r2 + Eta13*Eta24*r2 + alpha3*Eta14*r3 +
     Eta13*Eta34*r3))*r4 -
     (-2*(alpha1*alpha2*alpha4 - alpha4*Math.pow(Eta12,2) -
     alpha2*Math.pow(Eta14,2) - 2*Eta12*Eta14*Eta24 - alpha1*Math.pow(Eta24,2))*r1*
     Math.pow(r2,2) - 2*(alpha4*Eta12*Eta13 + alpha1*alpha4*Eta23 -
         Math.pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 +
     Eta12*Eta14*Eta34 + alpha1*Eta24*Eta34)*r1*r2*r3 -
     2*r2*((alpha4*Eta12*Eta13 + alpha1*alpha4*Eta23 -
         Math.pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta12*Eta14*Eta34 +
     alpha1*Eta24*Eta34)*r1 + (alpha4*Eta12*Eta23 +
     Eta24*(Eta14*Eta23 - Eta13*Eta24 + Eta12*Eta34) +
     alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*r3 +
     (-2*alpha1*(alpha3*alpha4 - Math.pow(Eta34,2))*r1 +
     2*(alpha4*Math.pow(Eta13,2) + Eta14*(alpha3*Eta14 + 2*Eta13*Eta34))*r1 -
     2*(alpha4*Eta13*Eta23 + alpha3*(alpha4*Eta12 + Eta14*Eta24) +
     Eta34*(Eta14*Eta23 + Eta13*Eta24 - Eta12*Eta34))*r2)*
     Math.pow(r3,2))*Math.pow(r4,2))/
     (12.*Math.sqrt(-((alpha3*Math.pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23)
     + alpha1*(-(alpha2*alpha3) + Math.pow(Eta23,2)))*
     Math.pow(r1,2)*Math.pow(r2,2)*Math.pow(r3,2)) + 2*r1*r2*r3*
     (alpha1*Eta23*Eta24*r1*r2 + Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
         Math.pow(Eta12,2)*Eta34*r1*r2 +
     Eta13*Eta14*Eta23*r1*r3 + alpha1*alpha3*Eta24*r1*r3 -
     Math.pow(Eta13,2)*Eta24*r1*r3 + alpha1*Eta23*Eta34*r1*r3 -
     Eta14*Math.pow(Eta23,2)*r2*r3 + Eta13*Eta23*Eta24*r2*r3 +
     Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 + alpha3*Eta24*r2 +
     Eta23*Eta34*r2)*r3 +
     alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 + alpha3*Eta14*r3 +
     Eta13*Eta34*r3))*r4 -
     (-((alpha1*alpha2*alpha4 - alpha4*Math.pow(Eta12,2) -
     alpha2*Math.pow(Eta14,2) - 2*Eta12*Eta14*Eta24 - alpha1*Math.pow(Eta24,2))*
     Math.pow(r1,2)*Math.pow(r2,2)) - 2*r1*r2*((alpha4*Eta12*Eta13 +
     alpha1*alpha4*Eta23 - Math.pow(Eta14,2)*Eta23 +
     Eta13*Eta14*Eta24 + Eta12*Eta14*Eta34 +
     alpha1*Eta24*Eta34)*r1 +
     (alpha4*Eta12*Eta23 + Eta24*(Eta14*Eta23 - Eta13*Eta24 +
     Eta12*Eta34) + alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*r3 +
     (-(alpha1*(alpha3*alpha4 - Math.pow(Eta34,2))*Math.pow(r1,2)) +
     (alpha4*Math.pow(Eta13,2) + Eta14*(alpha3*Eta14 +
     2*Eta13*Eta34))*Math.pow(r1,2) -
     2*(alpha4*Eta13*Eta23 + alpha3*(alpha4*Eta12 + Eta14*Eta24) +
     Eta34*(Eta14*Eta23 + Eta13*Eta24 - Eta12*Eta34))*r1*
     r2 + (alpha4*Math.pow(Eta23,2) + Eta24*(alpha3*Eta24 +
     2*Eta23*Eta34) + alpha2*(-(alpha3*alpha4) + Math.pow(Eta34,2)))*
     Math.pow(r2,2))*Math.pow(r3,2))*Math.pow(r4,2)));  

 return result * r1;
    }
    
    private double calculateEtaCase() {
      double r1, r2, r3, r4, alpha1, alpha2, alpha3, alpha4, 
      Eta12, Eta13, Eta14, Eta23, Eta24, Eta34;
    
      r1 = radii[0].getValue();
      r2 = radii[1].getValue();
      r3 = radii[2].getValue();
      r4 = radii[3].getValue();

      alpha1 = alphas[0].getValue();
      alpha2 = alphas[1].getValue();
      alpha3 = alphas[2].getValue();
      alpha4 = alphas[3].getValue();

      Eta12 = etas[0].getValue();  
      Eta13 = etas[1].getValue();
      Eta14 = etas[2].getValue(); 
      Eta23 = etas[3].getValue();
      Eta24 = etas[4].getValue();  
      Eta34 = etas[5].getValue();
      
      double result = (r1*r2*(alpha3*Math.pow(r3,2)*(-(Eta12*r1*r2) + r4*(Eta14*r1 + Eta24*r2 +
          alpha4*r4)) +
          Eta13*r1*r3*(-(Eta23*r2*r3) + r4*(Eta24*r2 + Eta34*r3 + alpha4*r4)) +
          r4*(-(Eta12*r1*r2*(2*Eta34*r3 + alpha4*r4)) + Eta14*r1*(Eta23*r2*r3 -
          Eta24*r2*r4 + Eta34*r3*r4) +
          r3*(Eta34*(Eta24*r2 - Eta34*r3)*r4 + Eta23*r2*(Eta34*r3 +
          alpha4*r4)))))/
          (6.*Math.sqrt(-((alpha3*Math.pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23) +
          alpha1*(-(alpha2*alpha3) + Math.pow(Eta23,2)))*
          Math.pow(r1,2)*Math.pow(r2,2)*Math.pow(r3,2)) + 2*r1*r2*r3*
          (alpha1*Eta23*Eta24*r1*r2 + Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
          Math.pow(Eta12,2)*Eta34*r1*r2 +
          Eta13*Eta14*Eta23*r1*r3 + alpha1*alpha3*Eta24*r1*r3 -
          Math.pow(Eta13,2)*Eta24*r1*r3 + alpha1*Eta23*Eta34*r1*r3 -
          Eta14*Math.pow(Eta23,2)*r2*r3 + Eta13*Eta23*Eta24*r2*r3 +
          Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 + alpha3*Eta24*r2 +
          Eta23*Eta34*r2)*r3 +
          alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 + alpha3*Eta14*r3 +
          Eta13*Eta34*r3))*r4 -
          (-((alpha1*alpha2*alpha4 - alpha4*Math.pow(Eta12,2) -
          alpha2*Math.pow(Eta14,2) - 2*Eta12*Eta14*Eta24 - alpha1*Math.pow(Eta24,2))*
          Math.pow(r1,2)*Math.pow(r2,2)) - 2*r1*r2*((alpha4*Eta12*Eta13 +
          alpha1*alpha4*Eta23 - Math.pow(Eta14,2)*Eta23 +
          Eta13*Eta14*Eta24 + Eta12*Eta14*Eta34 +
          alpha1*Eta24*Eta34)*r1 +
          (alpha4*Eta12*Eta23 + Eta24*(Eta14*Eta23 - Eta13*Eta24 +
          Eta12*Eta34) + alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*r3 +
          (-(alpha1*(alpha3*alpha4 - Math.pow(Eta34,2))*Math.pow(r1,2)) +
          (alpha4*Math.pow(Eta13,2) + Eta14*(alpha3*Eta14 +
          2*Eta13*Eta34))*Math.pow(r1,2) -
          2*(alpha4*Eta13*Eta23 + alpha3*(alpha4*Eta12 + Eta14*Eta24) +
          Eta34*(Eta14*Eta23 + Eta13*Eta24 - Eta12*Eta34))*r1*
          r2 + (alpha4*Math.pow(Eta23,2) + Eta24*(alpha3*Eta24 +
          2*Eta23*Eta34) + alpha2*(-(alpha3*alpha4) + Math.pow(Eta34,2)))*
          Math.pow(r2,2))*Math.pow(r3,2))*Math.pow(r4,2)));
          
      return result;
    }
  }

  public Volume.SecondPartial secondPartialAt(Vertex v, Vertex w) {
    TriPosition T = new TriPosition(v.getSerialNumber(), w.getSerialNumber());
    SecondPartial q = SecondPartialIndex.get(T);
    if(q == null) {
      q = new SecondPartial(v, w);
      q.pos = T;
      SecondPartialIndex.put(T, q);
    }
    return q;
  }
  
  public Volume.SecondPartial secondPartialAt(Vertex v, Edge e) {
    TriPosition T = new TriPosition(v.getSerialNumber(), e.getSerialNumber());
    SecondPartial q = SecondPartialIndex.get(T);
    if(q == null) {
      q = new SecondPartial(v, e);
      q.pos = T;
      SecondPartialIndex.put(T, q);
    }
    return q;
  }
  
  public Volume.SecondPartial secondPartialAt(Edge e, Edge f) {
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

    private Radius[] radii;
    private Alpha[] alphas;
    private Eta[] etas;
    private Partial volumePartial;
    SecondPartialType type;
    int locality;
    
    private SecondPartial(Vertex v, Vertex w) {
      super();
      type = SecondPartialType.RadiusRadius;
      if(v == w) {
        locality = 0;
        volumePartial = partialAt(v);
        volumePartial.addDependent(this);
      } else {
        locality = 1;
      }
      StdTetra st = new StdTetra(t, v, w);
      radii[0] = Radius.At(st.v1);
      radii[1] = Radius.At(st.v2);
      radii[2] = Radius.At(st.v3);
      radii[3] = Radius.At(st.v4);
      
      alphas[0] = Alpha.At(st.v1);
      alphas[1] = Alpha.At(st.v2);
      alphas[2] = Alpha.At(st.v3);
      alphas[3] = Alpha.At(st.v4); 
      
      etas[0] = Eta.At(st.e12);
      etas[1] = Eta.At(st.e13);
      etas[2] = Eta.At(st.e14);
      etas[3] = Eta.At(st.e23);
      etas[4] = Eta.At(st.e24);
      etas[5] = Eta.At(st.e34);
      
      for(int i = 0; i < 4; i++) {
        radii[i].addDependent(this);
        alphas[i].addDependent(this);
      }
      
      for(int i = 0; i < 6; i++) {
        etas[i].addDependent(this);
      }
    }
    
    private SecondPartial(Vertex v, Edge e) {
      super();
      type = SecondPartialType.RadiusEta;
      
      if(e.isAdjVertex(v)) {
        locality = 0;
      } else {
        locality = 1;
      }
      
      StdTetra st = new StdTetra(t, v, e);
      radii[0] = Radius.At(st.v1);
      radii[1] = Radius.At(st.v2);
      radii[2] = Radius.At(st.v3);
      radii[3] = Radius.At(st.v4);
      
      alphas[0] = Alpha.At(st.v1);
      alphas[1] = Alpha.At(st.v2);
      alphas[2] = Alpha.At(st.v3);
      alphas[3] = Alpha.At(st.v4); 
      
      etas[0] = Eta.At(st.e12);
      etas[1] = Eta.At(st.e13);
      etas[2] = Eta.At(st.e14);
      etas[3] = Eta.At(st.e23);
      etas[4] = Eta.At(st.e24);
      etas[5] = Eta.At(st.e34);
      
      for(int i = 0; i < 4; i++) {
        radii[i].addDependent(this);
        alphas[i].addDependent(this);
      }
      
      for(int i = 0; i < 6; i++) {
        etas[i].addDependent(this);
      }
    }
    
    private SecondPartial(Edge e, Edge f) {
      super();
      type = SecondPartialType.EtaEta;
      
      if(e == f) {
        locality = 0;
      } else if(e.isAdjEdge(f)) {
        locality = 1;
      } else {
        locality = 2;
      }
      
      StdTetra st = new StdTetra(t, e, f);
      radii[0] = Radius.At(st.v1);
      radii[1] = Radius.At(st.v2);
      radii[2] = Radius.At(st.v3);
      radii[3] = Radius.At(st.v4);
      
      alphas[0] = Alpha.At(st.v1);
      alphas[1] = Alpha.At(st.v2);
      alphas[2] = Alpha.At(st.v3);
      alphas[3] = Alpha.At(st.v4); 
      
      etas[0] = Eta.At(st.e12);
      etas[1] = Eta.At(st.e13);
      etas[2] = Eta.At(st.e14);
      etas[3] = Eta.At(st.e23);
      etas[4] = Eta.At(st.e24);
      etas[5] = Eta.At(st.e34);
      
      for(int i = 0; i < 4; i++) {
        radii[i].addDependent(this);
        alphas[i].addDependent(this);
      }
      
      for(int i = 0; i < 6; i++) {
        etas[i].addDependent(this);
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
      double r1, r2, r3, r4, alpha1, alpha2, alpha3, alpha4, 
      Eta12, Eta13, Eta14, Eta23, Eta24, Eta34;
    
      r1 = radii[0].getValue();
      r2 = radii[1].getValue();
      r3 = radii[2].getValue();
      r4 = radii[3].getValue();

      alpha1 = alphas[0].getValue();
      alpha2 = alphas[1].getValue();
      alpha3 = alphas[2].getValue();
      alpha4 = alphas[3].getValue();

      Eta12 = etas[0].getValue();  
      Eta13 = etas[1].getValue();
      Eta14 = etas[2].getValue(); 
      Eta23 = etas[3].getValue();
      Eta24 = etas[4].getValue();  
      Eta34 = etas[5].getValue();
      
      double result = 0;
      if(locality == 0) {
        result = -((alpha2*alpha4*Math.pow(Eta13,2) + 2*alpha4*Eta12*Eta13*Eta23 -
            Math.pow(Eta14,2)*Math.pow(Eta23,2) + 2*Eta13*Eta14*Eta23*Eta24 -
            Math.pow(Eta13,2)*Math.pow(Eta24,2) +
            alpha3*(alpha4*Math.pow(Eta12,2) + alpha2*Math.pow(Eta14,2) +
            2*Eta12*Eta14*Eta24) + 2*alpha2*Eta13*Eta14*Eta34 +
            2*Eta12*Eta14*Eta23*Eta34 + 2*Eta12*Eta13*Eta24*Eta34 -
            Math.pow(Eta12,2)*Math.pow(Eta34,2) +
            alpha1*(alpha4*Math.pow(Eta23,2) + alpha3*Math.pow(Eta24,2) +
            2*Eta23*Eta24*Eta34 + alpha2*(-(alpha3*alpha4) +
            Math.pow(Eta34,2))))*Math.pow(r1,2)*Math.pow(r2,2)*Math.pow(r3,2)*Math.pow(r4,2)*
            (-(Math.pow(Eta23,2)*Math.pow(r2,2)*Math.pow(r3,2)) +
            2*Eta23*r2*r3*r4*(Eta24*r2 + Eta34*r3 + alpha4*r4) +
            r4*(-(Math.pow(Eta24*r2 - Eta34*r3,2)*r4) +
            alpha3*Math.pow(r3,2)*(2*Eta24*r2 + alpha4*r4)) +
            alpha2*Math.pow(r2,2)*(alpha3*Math.pow(r3,2) + r4*(2*Eta34*r3 +
            alpha4*r4))))/
            (6.*Math.pow(-((alpha3*Math.pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23)
            + alpha1*(-(alpha2*alpha3) + Math.pow(Eta23,2)))*Math.pow(r1,2)*Math.pow(r2,2)*
            Math.pow(r3,2)) + 2*r1*r2*r3*
            (alpha1*Eta23*Eta24*r1*r2 + Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
            Math.pow(Eta12,2)*Eta34*r1*r2 + Eta13*Eta14*Eta23*r1*r3 +
            alpha1*alpha3*Eta24*r1*r3 - Math.pow(Eta13,2)*Eta24*r1*r3 +
            alpha1*Eta23*Eta34*r1*r3 - Eta14*Math.pow(Eta23,2)*r2*r3 +
            Eta13*Eta23*Eta24*r2*r3 +
            Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 + alpha3*Eta24*r2 +
            Eta23*Eta34*r2)*r3 +
            alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 + alpha3*Eta14*r3 +
            Eta13*Eta34*r3))*r4 -
            (-((alpha1*alpha2*alpha4 - alpha4*Math.pow(Eta12,2) -
            alpha2*Math.pow(Eta14,2) - 2*Eta12*Eta14*Eta24 -
            alpha1*Math.pow(Eta24,2))*Math.pow(r1,2)*Math.pow(r2,2)) -
            2*r1*r2*((alpha4*Eta12*Eta13 + alpha1*alpha4*Eta23 -
            Math.pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta12*Eta14*Eta34
            + alpha1*Eta24*Eta34)*r1 + (alpha4*Eta12*Eta23 +
            Eta24*(Eta14*Eta23 - Eta13*Eta24 + Eta12*Eta34) +
            alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*r3 +
            (-(alpha1*(alpha3*alpha4 - Math.pow(Eta34,2))*Math.pow(r1,2)) +
            (alpha4*Math.pow(Eta13,2) + Eta14*(alpha3*Eta14 + 2*Eta13*Eta34))*
            Math.pow(r1,2) - 2*(alpha4*Eta13*Eta23 +
            alpha3*(alpha4*Eta12 + Eta14*Eta24) +
            Eta34*(Eta14*Eta23 + Eta13*Eta24 - Eta12*Eta34))*r1*r2 +
            (alpha4*Math.pow(Eta23,2) + Eta24*(alpha3*Eta24 + 2*Eta23*Eta34) +
            alpha2*(-(alpha3*alpha4) + Math.pow(Eta34,2)))*Math.pow(r2,2))*
            Math.pow(r3,2))*Math.pow(r4,2),1.5));
            
            result += volumePartial.getValue();
      } else {
        result = r1*r2*(-((-2*(alpha3*Math.pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23) +
            alpha1*(-(alpha2*alpha3) + Math.pow(Eta23,2)))*r1*Math.pow(r2,2)*
            Math.pow(r3,2) + 2*r2*r3*
            (alpha1*Eta23*Eta24*r1*r2 +
            Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
            Math.pow(Eta12,2)*Eta34*r1*r2 + Eta13*Eta14*Eta23*r1*r3 +
            alpha1*alpha3*Eta24*r1*r3 - Math.pow(Eta13,2)*Eta24*r1*r3 +
            alpha1*Eta23*Eta34*r1*r3 - Eta14*Math.pow(Eta23,2)*r2*r3 +
            Eta13*Eta23*Eta24*r2*r3 +
            Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 + alpha3*Eta24*r2 +
            Eta23*Eta34*r2)*r3 +
            alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 + alpha3*Eta14*r3
            + Eta13*Eta34*r3))*r4 +
            2*r1*r2*r3*(alpha1*Eta23*Eta24*r2 - Math.pow(Eta12,2)*Eta34*r2 +
            alpha2*(Eta13*Eta14 + alpha1*Eta34)*r2 + Eta13*Eta14*Eta23*r3
            + alpha1*alpha3*Eta24*r3 - Math.pow(Eta13,2)*Eta24*r3 +
            alpha1*Eta23*Eta34*r3 +
            Eta12*(Eta14*Eta23*r2 + Eta13*Eta24*r2 + alpha3*Eta14*r3 +
            Eta13*Eta34*r3))*r4 -
            (-2*(alpha1*alpha2*alpha4 - alpha4*Math.pow(Eta12,2) -
            alpha2*Math.pow(Eta14,2) - 2*Eta12*Eta14*Eta24 -
            alpha1*Math.pow(Eta24,2))*r1*Math.pow(r2,2) -
            2*(alpha4*Eta12*Eta13 + alpha1*alpha4*Eta23 -
            Math.pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 +
            Eta12*Eta14*Eta34 + alpha1*Eta24*Eta34)*r1*r2*r3 -
            2*r2*((alpha4*Eta12*Eta13 + alpha1*alpha4*Eta23 -
            Math.pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 +
            Eta12*Eta14*Eta34 + alpha1*Eta24*Eta34)*r1 +
            (alpha4*Eta12*Eta23 +
            Eta24*(Eta14*Eta23 - Eta13*Eta24 + Eta12*Eta34) +
            alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*r3 +
            (-2*alpha1*(alpha3*alpha4 - Math.pow(Eta34,2))*r1 +
            2*(alpha4*Math.pow(Eta13,2) +
            Eta14*(alpha3*Eta14 + 2*Eta13*Eta34))*r1 -
            2*(alpha4*Eta13*Eta23 + alpha3*(alpha4*Eta12 + Eta14*Eta24)
            + Eta34*(Eta14*Eta23 + Eta13*Eta24 - Eta12*Eta34))*r2)*
            Math.pow(r3,2))*Math.pow(r4,2))*
            (-2*(alpha3*Math.pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23) +
            alpha1*(-(alpha2*alpha3) + Math.pow(Eta23,2)))*Math.pow(r1,2)*r2*
            Math.pow(r3,2) + 2*r1*r3*(alpha1*Eta23*Eta24*r1*r2 +
            Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
            Math.pow(Eta12,2)*Eta34*r1*r2 + Eta13*Eta14*Eta23*r1*r3 +
            alpha1*alpha3*Eta24*r1*r3 - Math.pow(Eta13,2)*Eta24*r1*r3 +
            alpha1*Eta23*Eta34*r1*r3 - Eta14*Math.pow(Eta23,2)*r2*r3 +
            Eta13*Eta23*Eta24*r2*r3 +
            Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 + alpha3*Eta24*r2 +
            Eta23*Eta34*r2)*r3 +
            alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 + alpha3*Eta14*r3
            + Eta13*Eta34*r3))*r4 + 2*r1*r2*r3*(-(Math.pow(Eta12,2)*Eta34*r1) +
            Eta23*(alpha1*Eta24*r1 - Eta14*Eta23*r3 + Eta13*Eta24*r3) +
            alpha2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 + alpha3*Eta14*r3 +
            Eta13*Eta34*r3) +
            Eta12*(Eta14*Eta23*r1 + Eta13*Eta24*r1 + alpha3*Eta24*r3 +
            Eta23*Eta34*r3))*r4 -
            (-2*(alpha1*alpha2*alpha4 - alpha4*Math.pow(Eta12,2) -
            alpha2*Math.pow(Eta14,2) - 2*Eta12*Eta14*Eta24 -
            alpha1*Math.pow(Eta24,2))*Math.pow(r1,2)*r2 -
            2*(alpha4*Eta12*Eta23 +
            Eta24*(Eta14*Eta23 - Eta13*Eta24 + Eta12*Eta34) +
            alpha2*(alpha4*Eta13 + Eta14*Eta34))*r1*r2*r3 -
            2*r1*((alpha4*Eta12*Eta13 + alpha1*alpha4*Eta23 -
            Math.pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 +
            Eta12*Eta14*Eta34 + alpha1*Eta24*Eta34)*r1 +
            (alpha4*Eta12*Eta23 +
            Eta24*(Eta14*Eta23 - Eta13*Eta24 + Eta12*Eta34) +
            alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*r3 +
            (-2*(alpha4*Eta13*Eta23 + alpha3*(alpha4*Eta12 + Eta14*Eta24)
            + Eta34*(Eta14*Eta23 + Eta13*Eta24 - Eta12*Eta34))*r1 +
            2*(alpha4*Math.pow(Eta23,2) +
            Eta24*(alpha3*Eta24 + 2*Eta23*Eta34) + alpha2*(-(alpha3*alpha4)
            + Math.pow(Eta34,2)))*r2)*Math.pow(r3,2))*Math.pow(r4,2))) +
            4*(-((alpha3*Math.pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23) +
            alpha1*(-(alpha2*alpha3) + Math.pow(Eta23,2)))*Math.pow(r1,2)*
            Math.pow(r2,2)*Math.pow(r3,2)) +
            2*r1*r2*r3*(alpha1*Eta23*Eta24*r1*r2 +
            Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
            Math.pow(Eta12,2)*Eta34*r1*r2 + Eta13*Eta14*Eta23*r1*r3 +
            alpha1*alpha3*Eta24*r1*r3 - Math.pow(Eta13,2)*Eta24*r1*r3 +
            alpha1*Eta23*Eta34*r1*r3 - Eta14*Math.pow(Eta23,2)*r2*r3 +
            Eta13*Eta23*Eta24*r2*r3 +
            Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 + alpha3*Eta24*r2 +
            Eta23*Eta34*r2)*r3 +
            alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 + alpha3*Eta14*r3 +
            Eta13*Eta34*r3))*r4 -
            (-((alpha1*alpha2*alpha4 - alpha4*Math.pow(Eta12,2) -
            alpha2*Math.pow(Eta14,2) - 2*Eta12*Eta14*Eta24 -
            alpha1*Math.pow(Eta24,2))*Math.pow(r1,2)*Math.pow(r2,2)) -
            2*r1*r2*((alpha4*Eta12*Eta13 + alpha1*alpha4*Eta23 -
            Math.pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 +
            Eta12*Eta14*Eta34 + alpha1*Eta24*Eta34)*r1 +
            (alpha4*Eta12*Eta23 +
            Eta24*(Eta14*Eta23 - Eta13*Eta24 + Eta12*Eta34) +
            alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*r3 +
            (-(alpha1*(alpha3*alpha4 - Math.pow(Eta34,2))*Math.pow(r1,2)) +
            (alpha4*Math.pow(Eta13,2) + Eta14*(alpha3*Eta14 +
            2*Eta13*Eta34))*Math.pow(r1,2) - 2*
            (alpha4*Eta13*Eta23 + alpha3*(alpha4*Eta12 + Eta14*Eta24) +
            Eta34*(Eta14*Eta23 + Eta13*Eta24 - Eta12*Eta34))*r1*r2 +
            (alpha4*Math.pow(Eta23,2) + Eta24*(alpha3*Eta24 + 2*Eta23*Eta34)
            + alpha2*(-(alpha3*alpha4) + Math.pow(Eta34,2)))*Math.pow(r2,2))*
            Math.pow(r3,2))*Math.pow(r4,2))*
            (-2*alpha2*Math.pow(Eta13,2)*r1*r2*Math.pow(r3,2) -
            4*Eta12*Eta13*Eta23*r1*r2*Math.pow(r3,2) +
            4*alpha2*Eta13*Eta14*r1*r2*r3*r4 + 4*Eta12*Eta14*Eta23*r1*r2*r3*r4
            + 4*Eta12*Eta13*Eta24*r1*r2*r3*r4 -
            4*Math.pow(Eta12,2)*Eta34*r1*r2*r3*r4 +
            2*Eta13*Eta14*Eta23*r1*Math.pow(r3,2)*r4 -
            2*Math.pow(Eta13,2)*Eta24*r1*Math.pow(r3,2)*r4 +
            2*Eta12*Eta13*Eta34*r1*Math.pow(r3,2)*r4 -
            2*Eta14*Math.pow(Eta23,2)*r2*Math.pow(r3,2)*r4 +
            2*Eta13*Eta23*Eta24*r2*Math.pow(r3,2)*r4 +
            2*alpha2*Eta13*Eta34*r2*Math.pow(r3,2)*r4 +
            2*Eta12*Eta23*Eta34*r2*Math.pow(r3,2)*r4 -
            2*alpha4*Math.pow(Eta12,2)*r1*r2*Math.pow(r4,2) -
            2*alpha2*Math.pow(Eta14,2)*r1*r2*Math.pow(r4,2) -
            4*Eta12*Eta14*Eta24*r1*r2*Math.pow(r4,2) +
            2*alpha4*Eta12*Eta13*r1*r3*Math.pow(r4,2) -
            2*Math.pow(Eta14,2)*Eta23*r1*r3*Math.pow(r4,2) +
            2*Eta13*Eta14*Eta24*r1*r3*Math.pow(r4,2) +
            2*Eta12*Eta14*Eta34*r1*r3*Math.pow(r4,2) +
            2*alpha2*alpha4*Eta13*r2*r3*Math.pow(r4,2) +
            2*alpha4*Eta12*Eta23*r2*r3*Math.pow(r4,2) +
            2*Eta14*Eta23*Eta24*r2*r3*Math.pow(r4,2) -
            2*Eta13*Math.pow(Eta24,2)*r2*r3*Math.pow(r4,2) +
            2*alpha2*Eta14*Eta34*r2*r3*Math.pow(r4,2) +
            2*Eta12*Eta24*Eta34*r2*r3*Math.pow(r4,2) +
            alpha4*Eta13*Eta23*Math.pow(r3,2)*Math.pow(r4,2) +
            Eta14*Eta23*Eta34*Math.pow(r3,2)*Math.pow(r4,2) +
            Eta13*Eta24*Eta34*Math.pow(r3,2)*Math.pow(r4,2) -
            Eta12*Math.pow(Eta34,2)*Math.pow(r3,2)*Math.pow(r4,2) +
            alpha3*Math.pow(r3,2)*(-2*Math.pow(Eta12,2)*r1*r2 +
            Eta12*r4*(2*Eta14*r1 + 2*Eta24*r2 + alpha4*r4) +
            Eta14*r4*(2*alpha2*r2 + Eta24*r4)) +
            2*alpha1*r1*(-(Math.pow(Eta23,2)*r2*Math.pow(r3,2)) +
            Eta23*r3*r4*(2*Eta24*r2 + Eta34*r3 + alpha4*r4) +
            Eta24*r4*(alpha3*Math.pow(r3,2) - Eta24*r2*r4 + Eta34*r3*r4) +
            alpha2*r2*(alpha3*Math.pow(r3,2) + r4*(2*Eta34*r3 +
            alpha4*r4)))))/
            (24.*Math.pow(-((alpha3*Math.pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23)
            + alpha1*(-(alpha2*alpha3) + Math.pow(Eta23,2)))*Math.pow(r1,2)*Math.pow(r2,2)*
            Math.pow(r3,2)) + 2*r1*r2*r3*
            (alpha1*Eta23*Eta24*r1*r2 + Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
            Math.pow(Eta12,2)*Eta34*r1*r2 + Eta13*Eta14*Eta23*r1*r3 +
            alpha1*alpha3*Eta24*r1*r3 - Math.pow(Eta13,2)*Eta24*r1*r3 +
            alpha1*Eta23*Eta34*r1*r3 - Eta14*Math.pow(Eta23,2)*r2*r3 +
            Eta13*Eta23*Eta24*r2*r3 +
            Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 + alpha3*Eta24*r2 +
            Eta23*Eta34*r2)*r3 +
            alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 + alpha3*Eta14*r3 +
            Eta13*Eta34*r3))*r4 -
            (-((alpha1*alpha2*alpha4 - alpha4*Math.pow(Eta12,2) -
            alpha2*Math.pow(Eta14,2) - 2*Eta12*Eta14*Eta24 -
            alpha1*Math.pow(Eta24,2))*Math.pow(r1,2)*Math.pow(r2,2)) -
            2*r1*r2*((alpha4*Eta12*Eta13 + alpha1*alpha4*Eta23 -
            Math.pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta12*Eta14*Eta34
            + alpha1*Eta24*Eta34)*r1 + (alpha4*Eta12*Eta23 +
            Eta24*(Eta14*Eta23 - Eta13*Eta24 + Eta12*Eta34) +
            alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*r3 +
            (-(alpha1*(alpha3*alpha4 - Math.pow(Eta34,2))*Math.pow(r1,2)) +
            (alpha4*Math.pow(Eta13,2) + Eta14*(alpha3*Eta14 + 2*Eta13*Eta34))*
            Math.pow(r1,2) - 2*(alpha4*Eta13*Eta23 +
            alpha3*(alpha4*Eta12 + Eta14*Eta24) +
            Eta34*(Eta14*Eta23 + Eta13*Eta24 - Eta12*Eta34))*r1*r2 +
            (alpha4*Math.pow(Eta23,2) + Eta24*(alpha3*Eta24 + 2*Eta23*Eta34) +
            alpha2*(-(alpha3*alpha4) + Math.pow(Eta34,2)))*Math.pow(r2,2))*
            Math.pow(r3,2))*Math.pow(r4,2),1.5));    
      }
      return result;
    }

    private double calculateRadEtaCase() {
      double r1, r2, r3, r4, alpha1, alpha2, alpha3, alpha4, 
      Eta12, Eta13, Eta14, Eta23, Eta24, Eta34;
    
      r1 = radii[0].getValue();
      r2 = radii[1].getValue();
      r3 = radii[2].getValue();
      r4 = radii[3].getValue();

      alpha1 = alphas[0].getValue();
      alpha2 = alphas[1].getValue();
      alpha3 = alphas[2].getValue();
      alpha4 = alphas[3].getValue();

      Eta12 = etas[0].getValue();  
      Eta13 = etas[1].getValue();
      Eta14 = etas[2].getValue(); 
      Eta23 = etas[3].getValue();
      Eta24 = etas[4].getValue();  
      Eta34 = etas[5].getValue();
      
      double result = 0;
      if(locality == 0) {
        result = (-2*r1*r2*(-2*(alpha3*Math.pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23) +
            alpha1*(-(alpha2*alpha3) + Math.pow(Eta23,2)))*r1*
            Math.pow(r2,2)*Math.pow(r3,2) + 2*r2*r3*(alpha1*Eta23*Eta24*r1*r2 +
            Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
            Math.pow(Eta12,2)*Eta34*r1*r2 + Eta13*Eta14*Eta23*r1*r3 +
            alpha1*alpha3*Eta24*r1*r3 - Math.pow(Eta13,2)*Eta24*r1*r3 +
            alpha1*Eta23*Eta34*r1*r3 - Eta14*Math.pow(Eta23,2)*r2*r3 +
            Eta13*Eta23*Eta24*r2*r3 +
            Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 + alpha3*Eta24*r2 +
            Eta23*Eta34*r2)*r3 +
            alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 + alpha3*Eta14*r3 +
            Eta13*Eta34*r3))*r4 +
            2*r1*r2*r3*(alpha1*Eta23*Eta24*r2 - Math.pow(Eta12,2)*Eta34*r2 +
            alpha2*(Eta13*Eta14 + alpha1*Eta34)*r2 +
            Eta13*Eta14*Eta23*r3 + alpha1*alpha3*Eta24*r3 -
            Math.pow(Eta13,2)*Eta24*r3 + alpha1*Eta23*Eta34*r3 +
            Eta12*(Eta14*Eta23*r2 + Eta13*Eta24*r2 + alpha3*Eta14*r3 +
            Eta13*Eta34*r3))*r4 -
            (-2*(alpha1*alpha2*alpha4 - alpha4*Math.pow(Eta12,2) -
            alpha2*Math.pow(Eta14,2) - 2*Eta12*Eta14*Eta24 - alpha1*Math.pow(Eta24,2))*
            r1*Math.pow(r2,2) - 2*(alpha4*Eta12*Eta13 + alpha1*alpha4*Eta23 -
            Math.pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 +
            Eta12*Eta14*Eta34 + alpha1*Eta24*Eta34)*r1*r2*r3 -
            2*r2*((alpha4*Eta12*Eta13 + alpha1*alpha4*Eta23 -
            Math.pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta12*Eta14*Eta34 +
            alpha1*Eta24*Eta34)*r1 + (alpha4*Eta12*Eta23 +
            Eta24*(Eta14*Eta23 - Eta13*Eta24 + Eta12*Eta34) +
            alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*r3 +
            (-2*alpha1*(alpha3*alpha4 - Math.pow(Eta34,2))*r1 +
            2*(alpha4*Math.pow(Eta13,2) + Eta14*(alpha3*Eta14 + 2*Eta13*Eta34))*r1 -
            2*(alpha4*Eta13*Eta23 + alpha3*(alpha4*Eta12 + Eta14*Eta24) +
            Eta34*(Eta14*Eta23 + Eta13*Eta24 - Eta12*Eta34))*r2)*
            Math.pow(r3,2))*Math.pow(r4,2))*(-((alpha3*Eta12 + Eta13*Eta23)*r1*r2*Math.pow(r3,2)) +
            r3*((Eta14*Eta23 + Eta13*Eta24)*r1*r2 - 2*Eta12*Eta34*r1*r2 +
            (alpha3*Eta14*r1 + Eta13*Eta34*r1 + alpha3*Eta24*r2 +
            Eta23*Eta34*r2)*r3)*r4 -
            ((Eta14*r1 - Eta34*r3)*(Eta24*r2 - Eta34*r3) + alpha4*(Eta12*r1*r2 -
            r3*(Eta13*r1 + Eta23*r2 + alpha3*r3)))*Math.pow(r4,2)) -
            4*r2*(-((alpha3*Math.pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23) +
            alpha1*(-(alpha2*alpha3) + Math.pow(Eta23,2)))*
            Math.pow(r1,2)*Math.pow(r2,2)*Math.pow(r3,2)) + 2*r1*r2*r3*
            (alpha1*Eta23*Eta24*r1*r2 + Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2
            - Math.pow(Eta12,2)*Eta34*r1*r2 +
            Eta13*Eta14*Eta23*r1*r3 + alpha1*alpha3*Eta24*r1*r3 -
            Math.pow(Eta13,2)*Eta24*r1*r3 + alpha1*Eta23*Eta34*r1*r3 -
            Eta14*Math.pow(Eta23,2)*r2*r3 + Eta13*Eta23*Eta24*r2*r3 +
            Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 + alpha3*Eta24*r2 +
            Eta23*Eta34*r2)*r3 +
            alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 + alpha3*Eta14*r3 +
            Eta13*Eta34*r3))*r4 -
            (-((alpha1*alpha2*alpha4 - alpha4*Math.pow(Eta12,2) -
            alpha2*Math.pow(Eta14,2) - 2*Eta12*Eta14*Eta24 - alpha1*Math.pow(Eta24,2))*
            Math.pow(r1,2)*Math.pow(r2,2)) - 2*r1*r2*((alpha4*Eta12*Eta13 +
            alpha1*alpha4*Eta23 - Math.pow(Eta14,2)*Eta23 +
            Eta13*Eta14*Eta24 + Eta12*Eta14*Eta34 +
            alpha1*Eta24*Eta34)*r1 +
            (alpha4*Eta12*Eta23 + Eta24*(Eta14*Eta23 - Eta13*Eta24 +
            Eta12*Eta34) + alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*r3
            + (-(alpha1*(alpha3*alpha4 - Math.pow(Eta34,2))*Math.pow(r1,2)) +
            (alpha4*Math.pow(Eta13,2) + Eta14*(alpha3*Eta14 + 2*Eta13*Eta34))*Math.pow(r1,2) -
            2*(alpha4*Eta13*Eta23 + alpha3*(alpha4*Eta12 + Eta14*Eta24) +
            Eta34*(Eta14*Eta23 + Eta13*Eta24 - Eta12*Eta34))*r1*
            r2 + (alpha4*Math.pow(Eta23,2) + Eta24*(alpha3*Eta24 +
            2*Eta23*Eta34) + alpha2*(-(alpha3*alpha4) + Math.pow(Eta34,2)))*
            Math.pow(r2,2))*Math.pow(r3,2))*Math.pow(r4,2))*
            (alpha3*Math.pow(r3,2)*(2*Eta12*r1*r2 - r4*(2*Eta14*r1 + Eta24*r2 +
            alpha4*r4)) +
            2*Eta13*r1*r3*(Eta23*r2*r3 - r4*(Eta24*r2 + Eta34*r3 + alpha4*r4)) +
            r4*(2*Eta12*r1*r2*(2*Eta34*r3 + alpha4*r4) - 2*Eta14*r1*(Eta23*r2*r3
            - Eta24*r2*r4 + Eta34*r3*r4) -
            r3*(Eta34*(Eta24*r2 - Eta34*r3)*r4 + Eta23*r2*(Eta34*r3 +
            alpha4*r4)))))/
            (24.*Math.pow(-((alpha3*Math.pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23)
            + alpha1*(-(alpha2*alpha3) + Math.pow(Eta23,2)))*
            Math.pow(r1,2)*Math.pow(r2,2)*Math.pow(r3,2)) + 2*r1*r2*r3*
            (alpha1*Eta23*Eta24*r1*r2 + Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
            Math.pow(Eta12,2)*Eta34*r1*r2 +
            Eta13*Eta14*Eta23*r1*r3 + alpha1*alpha3*Eta24*r1*r3 -
            Math.pow(Eta13,2)*Eta24*r1*r3 + alpha1*Eta23*Eta34*r1*r3 -
            Eta14*Math.pow(Eta23,2)*r2*r3 + Eta13*Eta23*Eta24*r2*r3 +
            Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 + alpha3*Eta24*r2 +
            Eta23*Eta34*r2)*r3 +
            alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 + alpha3*Eta14*r3 +
            Eta13*Eta34*r3))*r4 -
            (-((alpha1*alpha2*alpha4 - alpha4*Math.pow(Eta12,2) -
            alpha2*Math.pow(Eta14,2) - 2*Eta12*Eta14*Eta24 - alpha1*Math.pow(Eta24,2))*
            Math.pow(r1,2)*Math.pow(r2,2)) - 2*r1*r2*((alpha4*Eta12*Eta13 +
            alpha1*alpha4*Eta23 - Math.pow(Eta14,2)*Eta23 +
            Eta13*Eta14*Eta24 + Eta12*Eta14*Eta34 +
            alpha1*Eta24*Eta34)*r1 +
            (alpha4*Eta12*Eta23 + Eta24*(Eta14*Eta23 - Eta13*Eta24 +
            Eta12*Eta34) + alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*r3 +
            (-(alpha1*(alpha3*alpha4 - Math.pow(Eta34,2))*Math.pow(r1,2)) +
            (alpha4*Math.pow(Eta13,2) + Eta14*(alpha3*Eta14 + 2*Eta13*Eta34))*Math.pow(r1,2) -
            2*(alpha4*Eta13*Eta23 + alpha3*(alpha4*Eta12 + Eta14*Eta24) +
            Eta34*(Eta14*Eta23 + Eta13*Eta24 - Eta12*Eta34))*r1*
            r2 + (alpha4*Math.pow(Eta23,2) + Eta24*(alpha3*Eta24 +
            2*Eta23*Eta34) + alpha2*(-(alpha3*alpha4) + Math.pow(Eta34,2)))*
            Math.pow(r2,2))*Math.pow(r3,2))*Math.pow(r4,2),1.5));
      } else {
        result = (-2*r2*r3*(-2*(alpha3*Math.pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23) +
            alpha1*(-(alpha2*alpha3) + Math.pow(Eta23,2)))*r1*
            Math.pow(r2,2)*Math.pow(r3,2) + 2*r2*r3*(alpha1*Eta23*Eta24*r1*r2 +
            Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
            Math.pow(Eta12,2)*Eta34*r1*r2 + Eta13*Eta14*Eta23*r1*r3 +
            alpha1*alpha3*Eta24*r1*r3 - Math.pow(Eta13,2)*Eta24*r1*r3 +
            alpha1*Eta23*Eta34*r1*r3 - Eta14*Math.pow(Eta23,2)*r2*r3 +
            Eta13*Eta23*Eta24*r2*r3 +
            Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 + alpha3*Eta24*r2 +
            Eta23*Eta34*r2)*r3 +
            alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 + alpha3*Eta14*r3 +
            Eta13*Eta34*r3))*r4 +
            2*r1*r2*r3*(alpha1*Eta23*Eta24*r2 - Math.pow(Eta12,2)*Eta34*r2 +
            alpha2*(Eta13*Eta14 + alpha1*Eta34)*r2 +
            Eta13*Eta14*Eta23*r3 + alpha1*alpha3*Eta24*r3 -
            Math.pow(Eta13,2)*Eta24*r3 + alpha1*Eta23*Eta34*r3 +
            Eta12*(Eta14*Eta23*r2 + Eta13*Eta24*r2 + alpha3*Eta14*r3 +
            Eta13*Eta34*r3))*r4 - (-2*(alpha1*alpha2*alpha4 - alpha4*Math.pow(Eta12,2) -
            alpha2*Math.pow(Eta14,2) - 2*Eta12*Eta14*Eta24 - alpha1*Math.pow(Eta24,2))*
            r1*Math.pow(r2,2) - 2*(alpha4*Eta12*Eta13 + alpha1*alpha4*Eta23 -
            Math.pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 +
            Eta12*Eta14*Eta34 + alpha1*Eta24*Eta34)*r1*r2*r3 -
            2*r2*((alpha4*Eta12*Eta13 + alpha1*alpha4*Eta23 -
            Math.pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta12*Eta14*Eta34 +
            alpha1*Eta24*Eta34)*r1 + (alpha4*Eta12*Eta23 +
            Eta24*(Eta14*Eta23 - Eta13*Eta24 + Eta12*Eta34) +
            alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*r3 +
            (-2*alpha1*(alpha3*alpha4 - Math.pow(Eta34,2))*r1 +
            2*(alpha4*Math.pow(Eta13,2) + Eta14*(alpha3*Eta14 + 2*Eta13*Eta34))*r1 -
            2*(alpha4*Eta13*Eta23 + alpha3*(alpha4*Eta12 + Eta14*Eta24) +
            Eta34*(Eta14*Eta23 + Eta13*Eta24 - Eta12*Eta34))*r2)*
            Math.pow(r3,2))*Math.pow(r4,2))*(Eta12*r1*r2*(-(Eta13*r1*r3) +
            r4*(Eta14*r1 + Eta34*r3 + alpha4*r4)) +
            alpha1*Math.pow(r1,2)*(-(Eta23*r2*r3) + r4*(Eta24*r2 + Eta34*r3 +
            alpha4*r4)) + r4*(-(Math.pow(Eta14,2)*Math.pow(r1,2)*r4) - (alpha4*Eta23 +
            Eta24*Eta34)*r2*r3*r4 + Eta13*r1*r3*(Eta14*r1 + Eta24*r2 + alpha4*r4) +
            Eta14*r1*(-2*Eta23*r2*r3 + Eta24*r2*r4 + Eta34*r3*r4))) -
            4*r2*r3*(-((alpha3*Math.pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23)
            + alpha1*(-(alpha2*alpha3) + Math.pow(Eta23,2)))*
            Math.pow(r1,2)*Math.pow(r2,2)*Math.pow(r3,2)) + 2*r1*r2*r3*
            (alpha1*Eta23*Eta24*r1*r2 + Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2
            - Math.pow(Eta12,2)*Eta34*r1*r2 +
            Eta13*Eta14*Eta23*r1*r3 + alpha1*alpha3*Eta24*r1*r3 -
            Math.pow(Eta13,2)*Eta24*r1*r3 + alpha1*Eta23*Eta34*r1*r3 -
            Eta14*Math.pow(Eta23,2)*r2*r3 + Eta13*Eta23*Eta24*r2*r3 +
            Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 + alpha3*Eta24*r2 +
            Eta23*Eta34*r2)*r3 +
            alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 + alpha3*Eta14*r3 +
            Eta13*Eta34*r3))*r4 -
            (-((alpha1*alpha2*alpha4 - alpha4*Math.pow(Eta12,2) -
            alpha2*Math.pow(Eta14,2) - 2*Eta12*Eta14*Eta24 - alpha1*Math.pow(Eta24,2))*
            Math.pow(r1,2)*Math.pow(r2,2)) - 2*r1*r2*((alpha4*Eta12*Eta13 +
            alpha1*alpha4*Eta23 - Math.pow(Eta14,2)*Eta23 +
            Eta13*Eta14*Eta24 + Eta12*Eta14*Eta34 + alpha1*Eta24*Eta34)*r1 +
            (alpha4*Eta12*Eta23 + Eta24*(Eta14*Eta23 - Eta13*Eta24 +
            Eta12*Eta34) + alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*r3
            + (-(alpha1*(alpha3*alpha4 - Math.pow(Eta34,2))*Math.pow(r1,2)) +
            (alpha4*Math.pow(Eta13,2) + Eta14*(alpha3*Eta14 + 2*Eta13*Eta34))*Math.pow(r1,2) -
            2*(alpha4*Eta13*Eta23 + alpha3*(alpha4*Eta12 + Eta14*Eta24) +
            Eta34*(Eta14*Eta23 + Eta13*Eta24 - Eta12*Eta34))*r1*
            r2 + (alpha4*Math.pow(Eta23,2) + Eta24*(alpha3*Eta24 +
            2*Eta23*Eta34) + alpha2*(-(alpha3*alpha4) + Math.pow(Eta34,2)))*
            Math.pow(r2,2))*Math.pow(r3,2))*Math.pow(r4,2))*
            (Eta12*r2*(2*Eta13*r1*r3 - r4*(2*Eta14*r1 + Eta34*r3 + alpha4*r4)) +
            2*alpha1*r1*(Eta23*r2*r3 - r4*(Eta24*r2 + Eta34*r3 + alpha4*r4)) -
            r4*(Eta13*r3*(2*Eta14*r1 + Eta24*r2 + alpha4*r4) +
            Eta14*(-2*Eta23*r2*r3 - 2*Eta14*r1*r4 + Eta24*r2*r4 + Eta34*r3*r4))))/
            (24.*Math.pow(-((alpha3*Math.pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23)
            + alpha1*(-(alpha2*alpha3) + Math.pow(Eta23,2)))*
            Math.pow(r1,2)*Math.pow(r2,2)*Math.pow(r3,2)) + 2*r1*r2*r3*
            (alpha1*Eta23*Eta24*r1*r2 + Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
            Math.pow(Eta12,2)*Eta34*r1*r2 +
            Eta13*Eta14*Eta23*r1*r3 + alpha1*alpha3*Eta24*r1*r3 -
            Math.pow(Eta13,2)*Eta24*r1*r3 + alpha1*Eta23*Eta34*r1*r3 -
            Eta14*Math.pow(Eta23,2)*r2*r3 + Eta13*Eta23*Eta24*r2*r3 +
            Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 + alpha3*Eta24*r2 +
            Eta23*Eta34*r2)*r3 +
            alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 + alpha3*Eta14*r3 +
            Eta13*Eta34*r3))*r4 - (-((alpha1*alpha2*alpha4 - alpha4*Math.pow(Eta12,2) -
            alpha2*Math.pow(Eta14,2) - 2*Eta12*Eta14*Eta24 - alpha1*Math.pow(Eta24,2))*
            Math.pow(r1,2)*Math.pow(r2,2)) - 2*r1*r2*((alpha4*Eta12*Eta13 +
            alpha1*alpha4*Eta23 - Math.pow(Eta14,2)*Eta23 +
            Eta13*Eta14*Eta24 + Eta12*Eta14*Eta34 +
            alpha1*Eta24*Eta34)*r1 +
            (alpha4*Eta12*Eta23 + Eta24*(Eta14*Eta23 - Eta13*Eta24 +
            Eta12*Eta34) + alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*r3 +
            (-(alpha1*(alpha3*alpha4 - Math.pow(Eta34,2))*Math.pow(r1,2)) +
            (alpha4*Math.pow(Eta13,2) + Eta14*(alpha3*Eta14 + 2*Eta13*Eta34))*Math.pow(r1,2) -
            2*(alpha4*Eta13*Eta23 + alpha3*(alpha4*Eta12 + Eta14*Eta24) +
            Eta34*(Eta14*Eta23 + Eta13*Eta24 - Eta12*Eta34))*r1*
            r2 + (alpha4*Math.pow(Eta23,2) + Eta24*(alpha3*Eta24 +
            2*Eta23*Eta34) + alpha2*(-(alpha3*alpha4) + Math.pow(Eta34,2)))*
            Math.pow(r2,2))*Math.pow(r3,2))*Math.pow(r4,2),1.5));
      }
      
      return result * r1;
    }
    
    private double calculateEtaEtaCase() {
      double r1, r2, r3, r4, alpha1, alpha2, alpha3, alpha4, 
      Eta12, Eta13, Eta14, Eta23, Eta24, Eta34;
    
      r1 = radii[0].getValue();
      r2 = radii[1].getValue();
      r3 = radii[2].getValue();
      r4 = radii[3].getValue();

      alpha1 = alphas[0].getValue();
      alpha2 = alphas[1].getValue();
      alpha3 = alphas[2].getValue();
      alpha4 = alphas[3].getValue();

      Eta12 = etas[0].getValue();  
      Eta13 = etas[1].getValue();
      Eta14 = etas[2].getValue(); 
      Eta23 = etas[3].getValue();
      Eta24 = etas[4].getValue();  
      Eta34 = etas[5].getValue();
      
      double result = 0;
      if(locality == 0) {
        result = -(Math.pow(r1,2)*Math.pow(r2,2)* (-(Math.pow(Eta13,2)*Math.pow(r1,2)*Math.pow(r3,2)) +
            2*Eta13*r1*r3*r4*(Eta14*r1 + Eta34*r3 + alpha4*r4) +
            r4*(-(Math.pow(Eta14*r1 - Eta34*r3,2)*r4) +
            alpha3*Math.pow(r3,2)*(2*Eta14*r1 + alpha4*r4)) + alpha1*Math.pow(r1,2)*
            (alpha3*Math.pow(r3,2) + r4*(2*Eta34*r3 + alpha4*r4)))*
            (-(Math.pow(Eta23,2)*Math.pow(r2,2)*Math.pow(r3,2)) + 2*Eta23*r2*r3*r4*
            (Eta24*r2 + Eta34*r3 + alpha4*r4) +
            r4*(-(Math.pow(Eta24*r2 - Eta34*r3,2)*r4) +
            alpha3*Math.pow(r3,2)*(2*Eta24*r2 + alpha4*r4)) + alpha2*Math.pow(r2,2)*
            (alpha3*Math.pow(r3,2) + r4*(2*Eta34*r3 + alpha4*r4))))/
            (6.*Math.pow(-((alpha3*Math.pow(Eta12,2) + Eta13*(alpha2*Eta13 + 2*Eta12*Eta23) +
            alpha1*(-(alpha2*alpha3) + Math.pow(Eta23,2)))*
            Math.pow(r1,2)*Math.pow(r2,2)*Math.pow(r3,2)) +
            2*r1*r2*r3*(alpha1*Eta23*Eta24*r1*r2 +
            Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
            Math.pow(Eta12,2)*Eta34*r1*r2 + Eta13*Eta14*Eta23*r1*r3 +
            alpha1*alpha3*Eta24*r1*r3 - Math.pow(Eta13,2)*Eta24*r1*r3 +
            alpha1*Eta23*Eta34*r1*r3 - Eta14*Math.pow(Eta23,2)*r2*r3 +
            Eta13*Eta23*Eta24*r2*r3 + Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 +
            alpha3*Eta24*r2 + Eta23*Eta34*r2)*r3 +
            alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 +
            alpha3*Eta14*r3 + Eta13*Eta34*r3))*r4 -
            (-((alpha1*alpha2*alpha4 - alpha4*Math.pow(Eta12,2) - alpha2*Math.pow(Eta14,2) -
            2*Eta12*Eta14*Eta24 - alpha1*Math.pow(Eta24,2))*Math.pow(r1,2)*Math.pow(r2,2)) -
            2*r1*r2*((alpha4*Eta12*Eta13 + alpha1*alpha4*Eta23 -
            Math.pow(Eta14,2)*Eta23 + Eta13*Eta14*Eta24 + Eta12*Eta14*Eta34 +
            alpha1*Eta24*Eta34)*r1 + (alpha4*Eta12*Eta23 +
            Eta24*(Eta14*Eta23 - Eta13*Eta24 + Eta12*Eta34) +
            alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*r3 + (-(alpha1*
            (alpha3*alpha4 - Math.pow(Eta34,2))* Math.pow(r1,2)) + (alpha4*Math.pow(Eta13,2) +
            Eta14*(alpha3*Eta14 + 2*Eta13*Eta34))*Math.pow(r1,2) -
            2*(alpha4*Eta13*Eta23 + alpha3*(alpha4*Eta12 + Eta14*Eta24) +
            Eta34*(Eta14*Eta23 + Eta13*Eta24 - Eta12*Eta34))*r1*r2 +
            (alpha4*Math.pow(Eta23,2) + Eta24*(alpha3*Eta24 + 2*Eta23*Eta34) +
            alpha2*(-(alpha3*alpha4) + Math.pow(Eta34,2))
            )*Math.pow(r2,2))*Math.pow(r3,2))*Math.pow(r4,2),1.5));
      } else if(locality == 1) {
        result = -(Math.pow(r1,2)*r2*r3*(-(Math.pow(Eta23,2)*Math.pow(r2,2)*
               Math.pow(r3,2)) +
            2*Eta23*r2*r3*r4*
             (Eta24*r2 + Eta34*r3 + alpha4*r4) +
            r4*(-(Math.pow(Eta24*r2 - Eta34*r3,2)*r4) +
               alpha3*Math.pow(r3,2)*(2*Eta24*r2 + alpha4*r4)) +
            alpha2*Math.pow(r2,2)*
             (alpha3*Math.pow(r3,2) +
               r4*(2*Eta34*r3 + alpha4*r4)))*
          (Eta12*r1*r2*(Eta13*r1*r3 -
               r4*(Eta14*r1 + Eta34*r3 + alpha4*r4)) +
            alpha1*Math.pow(r1,2)*
             (Eta23*r2*r3 -
               r4*(Eta24*r2 + Eta34*r3 + alpha4*r4)) -
            r4*(-(Math.pow(Eta14,2)*Math.pow(r1,2)*r4) -
               (alpha4*Eta23 + Eta24*Eta34)*r2*r3*r4 +
               Eta13*r1*r3*
                (Eta14*r1 + Eta24*r2 + alpha4*r4) +
               Eta14*r1*(-2*Eta23*r2*r3 + Eta24*r2*r4 +
                  Eta34*r3*r4))))/
       (6.*Math.pow(-((alpha3*Math.pow(Eta12,2) +
                Eta13*(alpha2*Eta13 + 2*Eta12*Eta23) +
                alpha1*(-(alpha2*alpha3) + Math.pow(Eta23,2)))*
              Math.pow(r1,2)*Math.pow(r2,2)*Math.pow(r3,2)) +
           2*r1*r2*r3*(alpha1*Eta23*Eta24*r1*r2 +
              Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
              Math.pow(Eta12,2)*Eta34*r1*r2 +
              Eta13*Eta14*Eta23*r1*r3 +
              alpha1*alpha3*Eta24*r1*r3 -
              Math.pow(Eta13,2)*Eta24*r1*r3 +
              alpha1*Eta23*Eta34*r1*r3 -
              Eta14*Math.pow(Eta23,2)*r2*r3 +
              Eta13*Eta23*Eta24*r2*r3 +
              Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 +
                 alpha3*Eta24*r2 + Eta23*Eta34*r2)*r3 +
              alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 +
                 alpha3*Eta14*r3 + Eta13*Eta34*r3))*r4 -
           (-((alpha1*alpha2*alpha4 - alpha4*Math.pow(Eta12,2) -
                   alpha2*Math.pow(Eta14,2) -
                   2*Eta12*Eta14*Eta24 - alpha1*Math.pow(Eta24,2)
                   )*Math.pow(r1,2)*Math.pow(r2,2)) -
              2*r1*r2*((alpha4*Eta12*Eta13 +
                    alpha1*alpha4*Eta23 -
                    Math.pow(Eta14,2)*Eta23 +
                    Eta13*Eta14*Eta24 + Eta12*Eta14*Eta34 +
                    alpha1*Eta24*Eta34)*r1 +
                 (alpha4*Eta12*Eta23 +
                    Eta24*(Eta14*Eta23 - Eta13*Eta24 +
                       Eta12*Eta34) +
                    alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*
               r3 + (-(alpha1*
                    (alpha3*alpha4 - Math.pow(Eta34,2))*
                    Math.pow(r1,2)) +
                 (alpha4*Math.pow(Eta13,2) +
                    Eta14*(alpha3*Eta14 + 2*Eta13*Eta34))*
                  Math.pow(r1,2) -
                 2*(alpha4*Eta13*Eta23 +
                    alpha3*(alpha4*Eta12 + Eta14*Eta24) +
                    Eta34*(Eta14*Eta23 + Eta13*Eta24 -
                       Eta12*Eta34))*r1*r2 +
                 (alpha4*Math.pow(Eta23,2) +
                    Eta24*(alpha3*Eta24 + 2*Eta23*Eta34) +
                    alpha2*(-(alpha3*alpha4) + Math.pow(Eta34,2))
                    )*Math.pow(r2,2))*Math.pow(r3,2))*Math.pow(r4,2),
          1.5));
      } else {
        result = (r1*r2*r4*(-2*r3*(2*Eta12*r1*r2 - Eta13*r1*r3 -
              Eta23*r2*r3 - Eta14*r1*r4 - Eta24*r2*r4 +
              2*Eta34*r3*r4)*
            (-((alpha3*Math.pow(Eta12,2) +
                   Eta13*(alpha2*Eta13 + 2*Eta12*Eta23) +
                   alpha1*(-(alpha2*alpha3) + Math.pow(Eta23,2)))
                  *Math.pow(r1,2)*Math.pow(r2,2)*Math.pow(r3,2)) +
              2*r1*r2*r3*(alpha1*Eta23*Eta24*r1*r2 +
                 Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
                 Math.pow(Eta12,2)*Eta34*r1*r2 +
                 Eta13*Eta14*Eta23*r1*r3 +
                 alpha1*alpha3*Eta24*r1*r3 -
                 Math.pow(Eta13,2)*Eta24*r1*r3 +
                 alpha1*Eta23*Eta34*r1*r3 -
                 Eta14*Math.pow(Eta23,2)*r2*r3 +
                 Eta13*Eta23*Eta24*r2*r3 +
                 Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 +
                    alpha3*Eta24*r2 + Eta23*Eta34*r2)*r3 +
                 alpha2*r2*(Eta13*Eta14*r1 +
                    alpha1*Eta34*r1 + alpha3*Eta14*r3 +
                    Eta13*Eta34*r3))*r4 -
              (-((alpha1*alpha2*alpha4 -
                      alpha4*Math.pow(Eta12,2) -
                      alpha2*Math.pow(Eta14,2) -
                      2*Eta12*Eta14*Eta24 -
                      alpha1*Math.pow(Eta24,2))*Math.pow(r1,2)*
                    Math.pow(r2,2)) -
                 2*r1*r2*((alpha4*Eta12*Eta13 +
                       alpha1*alpha4*Eta23 -
                       Math.pow(Eta14,2)*Eta23 +
                       Eta13*Eta14*Eta24 +
                       Eta12*Eta14*Eta34 + alpha1*Eta24*Eta34)
                      *r1 +
                    (alpha4*Eta12*Eta23 +
                       Eta24*
                        (Eta14*Eta23 - Eta13*Eta24 +
                         Eta12*Eta34) +
                       alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2
                    )*r3 + (-(alpha1*
                       (alpha3*alpha4 - Math.pow(Eta34,2))*
                       Math.pow(r1,2)) +
                    (alpha4*Math.pow(Eta13,2) +
                       Eta14*(alpha3*Eta14 + 2*Eta13*Eta34))*
                     Math.pow(r1,2) -
                    2*(alpha4*Eta13*Eta23 +
                       alpha3*(alpha4*Eta12 + Eta14*Eta24) +
                       Eta34*
                        (Eta14*Eta23 + Eta13*Eta24 -
                         Eta12*Eta34))*r1*r2 +
                    (alpha4*Math.pow(Eta23,2) +
                       Eta24*(alpha3*Eta24 + 2*Eta23*Eta34) +
                       alpha2*
                        (-(alpha3*alpha4) + Math.pow(Eta34,2)))*
                     Math.pow(r2,2))*Math.pow(r3,2))*Math.pow(r4,2)) -
           (2*r1*r2*r3*(-(Math.pow(Eta12,2)*r1*r2) +
                 alpha2*Eta13*r2*r3 +
                 Eta12*(Eta13*r1 + Eta23*r2)*r3 +
                 alpha1*r1*(alpha2*r2 + Eta23*r3)) -
              r3*(-2*r1*r2*(Eta12*Eta14*r1 +
                    alpha1*Eta24*r1 + alpha2*Eta14*r2 +
                    Eta12*Eta24*r2) +
                 2*(alpha1*Eta34*Math.pow(r1,2) +
                    Eta13*r1*(Eta14*r1 - Eta24*r2) +
                    r2*(-(Eta14*Eta23*r1) +
                       2*Eta12*Eta34*r1 + Eta23*Eta24*r2 +
                       alpha2*Eta34*r2))*r3)*r4)*
            (-((alpha3*Eta12 + Eta13*Eta23)*r1*r2*
                 Math.pow(r3,2)) +
              r3*((Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
                 2*Eta12*Eta34*r1*r2 +
                 (alpha3*Eta14*r1 + Eta13*Eta34*r1 +
                    alpha3*Eta24*r2 + Eta23*Eta34*r2)*r3)*r4
               - ((Eta14*r1 - Eta34*r3)*
                  (Eta24*r2 - Eta34*r3) +
                 alpha4*(Eta12*r1*r2 -
                    r3*(Eta13*r1 + Eta23*r2 + alpha3*r3)))*
               Math.pow(r4,2))))/
       (12.*Math.pow(-((alpha3*Math.pow(Eta12,2) +
                Eta13*(alpha2*Eta13 + 2*Eta12*Eta23) +
                alpha1*(-(alpha2*alpha3) + Math.pow(Eta23,2)))*
              Math.pow(r1,2)*Math.pow(r2,2)*Math.pow(r3,2)) +
           2*r1*r2*r3*(alpha1*Eta23*Eta24*r1*r2 +
              Eta12*(Eta14*Eta23 + Eta13*Eta24)*r1*r2 -
              Math.pow(Eta12,2)*Eta34*r1*r2 +
              Eta13*Eta14*Eta23*r1*r3 +
              alpha1*alpha3*Eta24*r1*r3 -
              Math.pow(Eta13,2)*Eta24*r1*r3 +
              alpha1*Eta23*Eta34*r1*r3 -
              Eta14*Math.pow(Eta23,2)*r2*r3 +
              Eta13*Eta23*Eta24*r2*r3 +
              Eta12*(alpha3*Eta14*r1 + Eta13*Eta34*r1 +
                 alpha3*Eta24*r2 + Eta23*Eta34*r2)*r3 +
              alpha2*r2*(Eta13*Eta14*r1 + alpha1*Eta34*r1 +
                 alpha3*Eta14*r3 + Eta13*Eta34*r3))*r4 -
           (-((alpha1*alpha2*alpha4 - alpha4*Math.pow(Eta12,2) -
                   alpha2*Math.pow(Eta14,2) -
                   2*Eta12*Eta14*Eta24 - alpha1*Math.pow(Eta24,2)
                   )*Math.pow(r1,2)*Math.pow(r2,2)) -
              2*r1*r2*((alpha4*Eta12*Eta13 +
                    alpha1*alpha4*Eta23 -
                    Math.pow(Eta14,2)*Eta23 +
                    Eta13*Eta14*Eta24 + Eta12*Eta14*Eta34 +
                    alpha1*Eta24*Eta34)*r1 +
                 (alpha4*Eta12*Eta23 +
                    Eta24*(Eta14*Eta23 - Eta13*Eta24 +
                       Eta12*Eta34) +
                    alpha2*(alpha4*Eta13 + Eta14*Eta34))*r2)*
               r3 + (-(alpha1*
                    (alpha3*alpha4 - Math.pow(Eta34,2))*
                    Math.pow(r1,2)) +
                 (alpha4*Math.pow(Eta13,2) +
                    Eta14*(alpha3*Eta14 + 2*Eta13*Eta34))*
                  Math.pow(r1,2) -
                 2*(alpha4*Eta13*Eta23 +
                    alpha3*(alpha4*Eta12 + Eta14*Eta24) +
                    Eta34*(Eta14*Eta23 + Eta13*Eta24 -
                       Eta12*Eta34))*r1*r2 +
                 (alpha4*Math.pow(Eta23,2) +
                    Eta24*(alpha3*Eta24 + 2*Eta23*Eta34) +
                    alpha2*(-(alpha3*alpha4) + Math.pow(Eta34,2))
                    )*Math.pow(r2,2))*Math.pow(r3,2))*Math.pow(r4,2),
          1.5));
      }
      return result;
    }
    
    protected void remove() {
      deleteDependents();
      for(int i = 0; i < 4; i++) {
        radii[i].removeDependent(this);
        alphas[i].removeDependent(this);
      }
      
      for(int i = 0; i < 6; i++) {
        etas[i].removeDependent(this);
      }
      if(type == SecondPartialType.RadiusRadius && locality == 0) {
        volumePartial.removeDependent(this);
      }
      SecondPartialIndex.remove(pos);
    }
    
  }
}
