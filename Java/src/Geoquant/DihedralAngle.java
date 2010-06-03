package Geoquant;

import java.util.HashMap;
import java.util.LinkedList;

import Triangulation.*;

public class DihedralAngle extends Geoquant {
  //Index map
  private static HashMap<TriPosition, DihedralAngle> Index = new HashMap<TriPosition, DihedralAngle>();
  private HashMap<TriPosition, Partial> PartialIndex;
  // Needed geoquants
  private Angle angleA;
  private Angle angleB;
  private Angle angleC;
  private Tetra t;
  private Edge e;
  
  public DihedralAngle(Edge e, Tetra t) {
    super();
    PartialIndex = new HashMap<TriPosition, Partial>();
    
    this.t = t;
    this.e = e;
    
    StdTetra st = new StdTetra(t, e);  
        
    angleA = Angle.At(st.v1, st.f123);
    angleB = Angle.At(st.v1, st.f124);
    angleC = Angle.At(st.v1, st.f134);
   
    angleA.addDependent(this);
    angleB.addDependent(this);
    angleC.addDependent(this);
  }
  
  protected void recalculate() {
    double a = angleA.getValue();
    double b = angleB.getValue();
    double c = angleC.getValue();
    value =  Math.acos( (Math.cos(c)-Math.cos(a)*Math.cos(b)) / (Math.sin(a)*Math.sin(b)) );
  }
 
  protected void remove() {
    deleteDependents();
    angleA.removeDependent(this);
    angleB.removeDependent(this);
    angleC.removeDependent(this);
    Index.remove(pos);
  }
  
  public static DihedralAngle At(Edge e, Tetra t) {
    TriPosition T = new TriPosition(e.getSerialNumber(), t.getSerialNumber());
    DihedralAngle q = Index.get(T);
    if(q == null) {
      q = new DihedralAngle(e, t);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static double valueAt(Edge e, Tetra t) {
    return At(e, t).getValue();
  }
  
  public DihedralAngle.Partial partialAt(Edge f) {
    TriPosition T = new TriPosition(f.getSerialNumber());
    Partial q = PartialIndex.get(T);
    if(q == null) {
      q = new Partial(f);
      q.pos = T;
      PartialIndex.put(T, q);
    }
    return q;
  }
  
  public class Partial extends Geoquant {
    private Radius[] radii;
    private Alpha[] alphas;
    private Eta[] etas;
    private int locality;
    
    private Partial(Edge f) {
      super();
      
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
      
      if(e == f) {
        locality = 0;
      } else if(t.isAdjEdge(f)) {
        if(e.isAdjEdge(f)) {
          locality = 1;
        } else {
          locality = 2;
        }
      } else {
        locality = 3;
      }
    }
    
    protected void recalculate() {
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
      
      if(locality == 0) {
        value = (-((-(r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 - 2*Eta23*r2*r3))/
              (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))) -
              (r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
              (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))) +
              (r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
              (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
              (2.*Math.pow(alpha1*Math.pow(r1,2) +
              2*Eta12*r1*r2 + alpha2*Math.pow(r2,2),2)*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))))/
              (Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) +
              2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))))*
              Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) +
              2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2)))))) +
              ((-((r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
              ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2)))) +
              (r1*r2*Math.pow(2*alpha1*Math.pow(r1,2) +
              2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4,2))/
              (2.*Math.pow(alpha1*Math.pow(r1,2) +
              2*Eta12*r1*r2 + alpha2*Math.pow(r2,2),2)*
              (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))))*
              (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
              (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))) +
              (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
              (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2)))))/
              (2.*Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) +
              2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))))*
              Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) +
              2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))),1.5)) +
              ((-((r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 - 2*Eta23*r2*r3))/
              ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2)))) +
              (r1*r2*Math.pow(2*alpha1*Math.pow(r1,2) +
              2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3,2))/
              (2.*Math.pow(alpha1*Math.pow(r1,2) +
              2*Eta12*r1*r2 + alpha2*Math.pow(r2,2),2)*
              (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))))*
              (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
              (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))) +
              (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
              (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2)))))/
              (2.*Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) +
              2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))),1.5)*
              Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) +
              2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))))))/
              Math.sqrt(1 - Math.pow(-((2*alpha1*Math.pow(r1,2) +
              2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3)*
              (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))) +
              (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
              (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))),2)/
              ((1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))))*
              (1 - Math.pow(2*alpha1*Math.pow(r1,2) +
              2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))))));
      } else if(locality == 1) {
         value = (-(((r1*r3)/
              (Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))) +
              (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
              (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2),1.5)*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))) -
              (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
              (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))) -
              (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              2*Eta14*r1*r4 - 2*Eta34*r3*r4))/
              (2.*Math.pow(alpha1*Math.pow(r1,2) +
              2*Eta13*r1*r3 + alpha3*Math.pow(r3,2),1.5)*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))))/
              (Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) +
              2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))))*
              Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) +
              2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2)))))) +
              (((r1*r3*Math.pow(2*alpha1*Math.pow(r1,2) +
              2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3,2))/
              (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2),2)) -
              (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 - 2*Eta23*r2*r3))/
              ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))))*
              (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
              (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))) +
              (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
              (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2)))))/
              (2.*Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) +
              2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))),1.5)*
              Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) +
              2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))))))/
              Math.sqrt(1 - Math.pow(-((2*alpha1*Math.pow(r1,2) +
              2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3)*
              (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))) +
              (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
              (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))),2)/
              ((1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))))*
              (1 - Math.pow(2*alpha1*Math.pow(r1,2) +
              2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))))));
      } else if(locality == 2) {
         value = (r3*r4)/(Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
              Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) +
              2*Eta12*r1*r2 + 2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))*
              Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) +
              2*Eta12*r1*r2 + 2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))))*
              Math.sqrt(1 - Math.pow(-((2*alpha1*Math.pow(r1,2) +
              2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3)*
              (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))) +
              (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
              (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))),2)/
              ((1 - Math.pow(2*alpha1*Math.pow(r1,2) +
              2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))))*
              (1 - Math.pow(2*alpha1*Math.pow(r1,2) +
              2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2)))))));      
      } else {
         value = 0;       
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
    
  }
}

