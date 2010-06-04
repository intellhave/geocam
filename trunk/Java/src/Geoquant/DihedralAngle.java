package Geoquant;

import java.util.HashMap;
import java.util.LinkedList;

import Triangulation.*;

public class DihedralAngle extends Geoquant {
  //Index map
  private static HashMap<TriPosition, DihedralAngle> Index = new HashMap<TriPosition, DihedralAngle>();
  private HashMap<TriPosition, Partial> PartialIndex;
  private HashMap<TriPosition, SecondPartial> SecondPartialIndex;
  // Needed geoquants
  private Angle angleA;
  private Angle angleB;
  private Angle angleC;
  private Tetra t;
  private Edge e;
  
  public DihedralAngle(Edge e, Tetra t) {
    super(e, t);
    PartialIndex = new HashMap<TriPosition, Partial>();
    SecondPartialIndex = new HashMap<TriPosition, SecondPartial>();
    
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
      super(f);
      
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
    
    public String toString() {
      return "DihedralAngle@[" + e + ", " + t + "]" + "w.r.t" + location + "=" + getValue();
    }
    
  }

  public DihedralAngle.SecondPartial secondPartialAt(Edge nm, Edge op) {
    TriPosition T = new TriPosition(nm.getSerialNumber(), op.getSerialNumber());
    SecondPartial q = SecondPartialIndex.get(T);
    if(q == null) {
      q = new SecondPartial(nm, op);
      q.pos = T;
      SecondPartialIndex.put(T, q);
    }
    return q;
  }
  
  public class SecondPartial extends Geoquant {
    private Radius[] radii;
    private Alpha[] alphas;
    private Eta[] etas;
    private int locality;
    
    private SecondPartial(Edge nm, Edge op) {
      super(nm, op);
      radii = new Radius[4];
      alphas = new Alpha[4];
      etas = new Eta[6];
      
      StdTetra st;
      
      if(!t.isAdjEdge(nm) || !t.isAdjEdge(op) ) {
        locality = 0;
        st = new StdTetra( t, e);
      } else if(e == nm) {
        st = new StdTetra(t, nm, op);
        if(nm == op) {
          locality = 1; // A (nm = ij, op = ij)
        } else if(op.isAdjEdge(nm)) {
          locality = 6; // G , I (nm = ij, op = ik)
        } else {
          locality = 4; // D, E (nm = ij, op = kl)
        }
      } else if(e.isAdjEdge(nm)) {
        st = new StdTetra(t, e, nm);
        if(nm == op) {
          locality = 3; // C (nm = ik, op = ik)
        } else if(e == op) {
          locality = 6; // G, I (nm = ik, op = ij)
        } else if(op.isAdjEdge(e) && op.isAdjEdge(nm)) {
          if(op.isAdjVertex(st.v1)) {
            locality = 8; // K (nm = ik, op = il)
          } else {
            locality = 9; // L (nm = ik, op = jk)
          }
        } else if(op.isAdjEdge(e)) {
          locality = 5; // F (nm = ik, op = jl)
        } else {
          locality = 7; // H, J (nm = ik, op = kl)
        }
      } else {
        st = new StdTetra(t, e, op);
        if(nm == op) {
          locality = 2; // B (nm = kl, op = kl)
        } else if(e == op) {
          locality = 4; // D, E (nm = kl, op = ij)
        } else if(op.isAdjEdge(e)) {
          locality = 7; // H, J (nm = kl, op = ik)
        }
      }
      
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
      if(locality == 0) {
        value = 0;
        
        
     /*****************************************************************************/
     /******************************_____________**********************************/
     /*****************************|   _______   |*********************************/
     /*****************************|  |*******|  |*********************************/
     /*****************************|  |_______|  |*********************************/
     /*****************************|   _______   |*********************************/
     /*****************************|  |*******|  |*********************************/
     /*****************************|  |*******|  |*********************************/
     /*****************************|__|*******| _|*********************************/
     /*****************************************************************************/
      } else if(locality == 1) {
         value = caseA();


     /*****************************************************************************/
     /******************************_____________**********************************/
     /*****************************|   ________  |*********************************/
     /*****************************|  |********| |*********************************/
     /*****************************|  |________| |*********************************/
     /*****************************|   ________ |**********************************/
     /*****************************|  |********| |*********************************/
     /*****************************|  |________| |*********************************/
     /*****************************|_____________|*********************************/
     /*****************************************************************************/
      } else if(locality == 2) {
         value = caseB();


     /*****************************************************************************/
     /******************************_____________**********************************/
     /*****************************|             |*********************************/
     /*****************************|     ________|*********************************/
     /*****************************|    |******************************************/
     /*****************************|    |******************************************/
     /*****************************|    |________**********************************/
     /*****************************|             |*********************************/
     /*****************************|_____________|*********************************/
     /*****************************************************************************/
      } else if(locality == 3){
         value = caseC();
            
     /*****************************************************************************/
     /******************************____________***********************************/
     /*****************************|   _________|**********************************/
     /*****************************|  |********************************************/
     /*****************************|  |______**************************************/
     /*****************************|   ______|*************************************/
     /*****************************|  |********************************************/
     /*****************************|  |_________***********************************/
     /*****************************|____________|**********************************/
     /*****************************************************************************/
      } else if(locality == 4) {
        value = caseD_E();

     /*****************************************************************************/
     /******************************_____________**********************************/
     /*****************************|   __________|*********************************/
     /*****************************|  |********************************************/
     /*****************************|  |______**************************************/
     /*****************************|   ______|*************************************/
     /*****************************|  |********************************************/
     /*****************************|  |********************************************/
     /*****************************|__|********************************************/
     /*****************************************************************************/
      } else if(locality == 5) {
        value = caseF();
            
     /*****************************************************************************/
     /******************************_____________**********************************/
     /*****************************|   __________|*********************************/
     /*****************************|  |********************************************/
     /*****************************|  |********************************************/
     /*****************************|  |****______**********************************/
     /*****************************|  |***|___   |*********************************/
     /*****************************|  |_______|  |*********************************/
     /*****************************|_____________|*********************************/
     /*****************************************************************************/
      } else if(locality == 6) {
        value = caseG_I();

     /*****************************************************************************/
     /******************************__*********__**********************************/
     /*****************************|  |*******|  |*********************************/
     /*****************************|  |*******|  |*********************************/
     /*****************************|  |_______|  |*********************************/
     /*****************************|   _______   |*********************************/
     /*****************************|  |*******|  |*********************************/
     /*****************************|  |*******|  |*********************************/
     /*****************************|__|*******|__|*********************************/
     /*****************************************************************************/
      } else if(locality == 7) {
        value = caseH_J();


     /*****************************************************************************/
     /******************************__*******__************************************/
     /*****************************|  |****_|  |***********************************/
     /*****************************|  |*__|   /************************************/
     /*****************************|  |/   _/**************************************/
     /*****************************|   _  |****************************************/
     /*****************************|  |*\ \_***************************************/
     /*****************************|  |**\_ \__************************************/
     /*****************************|__|****\___|***********************************/
     /*****************************************************************************/
      } else if(locality == 8) {
        value = caseK();

     /*****************************************************************************/
     /******************************__*********************************************/
     /*****************************|  |*******************************************/
     /*****************************|  |*******************************************/
     /*****************************|  |*******************************************/
     /*****************************|  |*******************************************/
     /*****************************|  |********************************************/
     /*****************************|  |________************************************/
     /*****************************|___________|***********************************/
     /*****************************************************************************/
      } else if(locality == 9) {
        value = caseL();
      }
      
    }

    private double caseA() {
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
      
      return -((-((-(r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3))/
          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) -
          (r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)* (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/ (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2),2)* Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))* Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2))))/ (Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/(4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2))* (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))))* Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/(4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2))* (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2)))))) +
          ((-((r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/ ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))) +
          (r1*r2*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta14*r1*r4 - 2*Eta24*r2*r4,2))/
          (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2),2)*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))*
          (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)* (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2))) +
          (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
          2*Eta34*r3*r4)/ (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))* Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2)))))/
          (2.*Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
          Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))),1.5)) +
          ((-((r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3))/
          ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2)))) +
          (r1*r2*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3,2))/
          (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2),2)*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
          (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)* (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
          2*Eta34*r3*r4)/ (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))* Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2)))))/
          (2.*Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))),1.5)*
          Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))))*
          ((-2*(-(r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3))/
          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) -
          (r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/(2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2),2)*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2))))*
          (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
          2*Eta34*r3*r4)/(2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2)))))/
          ((1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
          (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))) +
          ((-((r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))) +
          (r1*r2*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta14*r1*r4 - 2*Eta24*r2*r4,2))/
          (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2),2)*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))*
          Math.pow(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
          (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))),2))/
          ((1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
          Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))),2)) +
          ((-((r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3))/
          ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2)))) +
          (r1*r2*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3,2))/
          (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2),2)*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
          Math.pow(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*
          (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
          (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))),2))/
          (Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))),2)*
          (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))))))/
          (2.*Math.pow(1 - Math.pow(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
          (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
          2*Eta34*r3*r4)/(2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2))),2)/
          ((1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
          (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))),1.5)) +
          (-(((-2*Math.pow(r1,2)*Math.pow(r2,2))/
          ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (2*Math.pow(r1,2)*Math.pow(r2,2)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3))/(Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2),2)*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (2*Math.pow(r1,2)*Math.pow(r2,2)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta14*r1*r4 - 2*Eta24*r2*r4))/(Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2),2)*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) -
          (2*Math.pow(r1,2)*Math.pow(r2,2)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
          (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/(Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2),3)*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))/
          (Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
          - 2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
          Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))))) +
          ((-((r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2)))) + (r1*r2*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta14*r1*r4 - 2*Eta24*r2*r4,2))/
          (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2),2)*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))*
          (-(r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3))/
          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) -
          (r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/(2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2),2)*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))))/
          (Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
          Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
          - 2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))),1.5)) +
          ((-((r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3))/((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2)))) + (r1*r2*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3,2))/
          (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2),2)*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
          (-(r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3))/
          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) -
          (r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*
          (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
          (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2),2)*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))))/
          (Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
          - 2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))),1.5)*
          Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
          - 2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))) -
          (3*Math.pow(-((r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
          - 2*Eta24*r2*r4))/((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
          ) + (r1*r2*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta14*r1*r4 - 2*Eta24*r2*r4,2))/
          (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2),2)*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))),2)*
          (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
          (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))))/
          (4.*Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
          Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
          - 2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))),2.5)) +
          (((-2*Math.pow(r1,2)*Math.pow(r2,2))/((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2))) + (4*Math.pow(r1,2)*Math.pow(r2,2)*(2*alpha1*Math.pow(r1,2) +
          2*Eta12*r1*r2 + 2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
          (Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2),2)*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) -
          (2*Math.pow(r1,2)*Math.pow(r2,2)*Math.pow(2*alpha1*Math.pow(r1,2) +
          2*Eta12*r1*r2 + 2*Eta14*r1*r4 - 2*Eta24*r2*r4,2))/
          (Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2),3)*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))*
          (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
          (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
          (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))))/
          (2.*Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
          Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
          - 2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))),1.5)) -
          ((-((r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3))/((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2)))
          ) + (r1*r2*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3,2))/
          (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2),2)*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
          (-((r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
          ) + (r1*r2*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta14*r1*r4 - 2*Eta24*r2*r4,2))/
          (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2),2)*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))*
          (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
          (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
          2*Eta34*r3*r4)/(2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2)))))/(2.*Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))),1.5)*
          Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
          - 2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))),1.5)) -
          (3*Math.pow(-((r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
          - 2*Eta23*r2*r3))/((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2)))
          ) + (r1*r2*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3,2))/
          (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2),2)*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))),2)*
          (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
          (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
          (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))))/
          (4.*Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))),2.5)*
          Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
          - 2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))) +
          (((-2*Math.pow(r1,2)*Math.pow(r2,2))/((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2)))
          + (4*Math.pow(r1,2)*Math.pow(r2,2)*(2*alpha1*Math.pow(r1,2) +
          2*Eta12*r1*r2 + 2*Eta13*r1*r3 - 2*Eta23*r2*r3))/
          (Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2),2)*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))) -
          (2*Math.pow(r1,2)*Math.pow(r2,2)*Math.pow(2*alpha1*Math.pow(r1,2) +
          2*Eta12*r1*r2 + 2*Eta13*r1*r3 - 2*Eta23*r2*r3,2))/
          (Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2),3)*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
          (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*
          (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
          2*Eta34*r3*r4)/(2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2)))))/(2.*Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))),1.5)*
          Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
          - 2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))))/
          Math.sqrt(1 - Math.pow(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
          (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))),2)/
          ((1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
          (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))));
    }
    
    private double caseB() {
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
      
      return -((Math.pow(r3,2)*Math.pow(r4,2)*(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
          (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
          (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))))/
          ((alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3,2)/(4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))),
          1.5)*(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))*
          Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4,2)/(4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2))),
          1.5)*Math.pow(1 - Math.pow(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
          (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
          (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))),2)/
          ((1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
          (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2))))),1.5)));
    }
    
    private double caseC() {
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
      
      return -((-(((r1*r3)/(Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2),1.5)*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2))) -
          (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) -
          (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
          2*Eta34*r3*r4))/(2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2),1.5)*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2))))/(Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/(4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))))*Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/(4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2)))))) +
          (((r1*r3*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
          - 2*Eta23*r2*r3,2))/
          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2),2)) -
          (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3))/((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
          (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
          (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))))/
          (2.*Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))),1.5)*
          Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))))*
          ((-2*((r1*r3)/(Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2))) +
          (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/(4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2),1.5)*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) -
          (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) -
          (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
          2*Eta34*r3*r4))/(2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2),1.5)*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2))))*
          (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
          2*Eta34*r3*r4)/(2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2)))))/
          ((1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
          (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))) +
          (((r1*r3*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
          - 2*Eta23*r2*r3,2))/
          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2),2)) -
          (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3))/
          ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
          Math.pow(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
          2*Eta34*r3*r4)/(2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2))),2))/(Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))),2)*
          (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))))))/
          (2.*Math.pow(1 - Math.pow(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
          (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
          (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))),2)/
          ((1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
          (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))),1.5)) +
          (-(((-2*Math.pow(r1,2)*Math.pow(r3,2))/(Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2),1.5)*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) -
          (3*Math.pow(r1,2)*Math.pow(r3,2)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
          (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2),2.5)*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (Math.pow(r1,2)*Math.pow(r3,2)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
          ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2),1.5)*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (3*Math.pow(r1,2)*Math.pow(r3,2)*(2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          2*Eta14*r1*r4 - 2*Eta34*r3*r4))/
          (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +alpha3*Math.pow(r3,2),2.5)*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))/
          (Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
          - 2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
          Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))))) +
          (((r1*r3*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3,2))/
          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2),2)) -
          (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3))/((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))))*((r1*r3)/(Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2),1.5)*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) -
          (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) -
          (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
          2*Eta34*r3*r4))/ (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2),1.5)* Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2)))))/
          (Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
          - 2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))),1.5)*
          Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
          - 2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))) -
          (3*Math.pow((r1*r3*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3,2))/
          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2),2)) -
          (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3))/ ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))),
          2)*(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
          2*Eta34*r3*r4)/(2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2)))))/
          (4.*Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))),2.5)*
          Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
          - 2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))) +
          (((-2*Math.pow(r1,2)*Math.pow(r3,2)*Math.pow(2*alpha1*Math.pow(r1,2) +
          2*Eta12*r1*r2 + 2*Eta13*r1*r3 - 2*Eta23*r2*r3,2))/
          ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2),3)) +
          (4*Math.pow(r1,2)*Math.pow(r3,2)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3))/
          ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2),2)) -
          (2*Math.pow(r1,2)*Math.pow(r3,2))/((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))))*
          (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
          2*Eta34*r3*r4)/(2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2)))))/
          (2.*Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))),1.5)*
          Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
          - 2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))))/
          Math.sqrt(1 - Math.pow(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*
          (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
          (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))),2)/
          ((1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
          (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))));
    }
    
    private double caseD_E() {
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
      
      return -((r3*r4*(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
              (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
              2*Eta34*r3*r4)/
              (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))*
              (-((-(r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3))/(2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))) -
              (r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/(2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))) +
              (r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/(2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2),2)*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))))/
              (Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
              Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))))) +
              ((-((r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2)))) + (r1*r2*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta14*r1*r4 - 2*Eta24*r2*r4,2))/
              (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2),2)*(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))))*
              (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/(4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))) +
              (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
              2*Eta34*r3*r4)/(2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2)))))/
              (2.*Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/(4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))))*Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/(4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))),1.5)) +
              ((-((r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3))/((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2)))) +
              (r1*r2*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 - 2*Eta23*r2*r3,2))/
              (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2),2)*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))))*
              (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/(4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))) +
              (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
              2*Eta34*r3*r4)/(2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2)))))/
              (2.*Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))),1.5)*
              Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))))))/
              (Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
              (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3,2)/(4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2)))
              )*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))*
              (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4,2)/(4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))))*Math.pow(1 - Math.pow(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
              (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
              (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
              (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))),2)/
              ((1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3,2)/(4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))))*
              (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
              - 2*Eta24*r2*r4,2)/(4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))))),1.5))) +
              (-(r3*r4*(-((r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
              ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))) +
              (r1*r2*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
              - 2*Eta24*r2*r4,2))/(2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2),2)*
              (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))))/
              (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
              Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
              - 2*Eta23*r2*r3,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))*
              Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
              - 2*Eta24*r2*r4,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))),1.5)) -
              (r3*(-((r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3))/((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2)))) + (r1*r2*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 - 2*Eta23*r2*r3,2))/
              (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2),2)*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))))*r4)/
              (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
              Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
              - 2*Eta23*r2*r3,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))),1.5)*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))*
              Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
              - 2*Eta24*r2*r4,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))))/
              Math.sqrt(1 - Math.pow(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
              (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
              (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))),2)/
              ((1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
              (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
              (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))));
    }

    private double caseF() {
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
      
      return -((-(((r1*r3)/(Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2),1.5)*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) -
          (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) -
          (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
          2*Eta34*r3*r4))/(2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2),1.5)*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2))))/(Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/(4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))))*Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/(4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2)))))) +
          (((r1*r3*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
          - 2*Eta23*r2*r3,2))/
          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2),2)) -
          (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3))/((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
          (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
          (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))))/
          (2.*Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))),1.5)*
          Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))))*
          (-((r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*r4*
          (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/(4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2))*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2))) +
          (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
          2*Eta34*r3*r4)/(2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2)))))/
          ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
          - 2*Eta23*r2*r3,2)/(4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))*
          (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
          - 2*Eta24*r2*r4,2)/(4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2)))))) +
          (r2*r4*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4)*
          Math.pow(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
          2*Eta34*r3*r4)/(2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2))),2))/
          ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))*
          Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))),2))))/
          (2.*Math.pow(1 - Math.pow(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
          (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
          2*Eta34*r3*r4)/(2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2))),2)/
          ((1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
          (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))),1.5)) +
          ((r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*
          ((r1*r3*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3,2))/
          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2),2)) -
          (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3))/((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))))*
          r4)/(4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
          - 2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))),1.5)*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))*
          Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
          - 2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))) -
          (-(r1*r2*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*r4)/
          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2),1.5)*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (r1*r2*r3*r4)/((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))/
          (Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
          Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
          - 2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))) +
          (r2*r4*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4)*((r1*r3)/(Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2),1.5)*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) -
          (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) -
          (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
          2*Eta34*r3*r4))/(2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2),1.5)*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2)))))/
          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
          - 2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))*
          Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
          - 2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))),1.5)) -
          (r2*((r1*r3*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
          - 2*Eta23*r2*r3,2))/
          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2),2)) -
          (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3))/((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
          alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))))*
          r4*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4)*(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
          2*Eta34*r3*r4)/(2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
          alpha4*Math.pow(r4,2)))))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
          - 2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))),1.5)*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))*
          Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
          - 2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))),1.5)))/
          Math.sqrt(1 - Math.pow(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4))/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
          (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 - 2*Eta34*r3*r4)/
          (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))),2)/
          ((1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
          (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
          2*Eta24*r2*r4,2)/
          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))));
    }
    
    private double caseG_I() {
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
      
      return -((-((-(r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
          2*Eta23*r2*r3))/
          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                   Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))*
                   Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2))) -
                (r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
  2*Eta24*r2*r4))/
                 (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                   Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))*
                   Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2))) +
                (r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
  2*Eta23*r2*r3)*
                   (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
  2*Eta24*r2*r4))/
                 (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  alpha2*Math.pow(r2,2),2)*
                   Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))*
                   Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2))))/
              (Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
                   (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  alpha2*Math.pow(r2,2))*
                     (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))))*
                Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
                   (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  alpha2*Math.pow(r2,2))*
                     (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2)))))) +
           ((-((r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
  2*Eta24*r2*r4))/
                   ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                     (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2)))) +
                (r1*r2*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  2*Eta14*r1*r4 - 2*Eta24*r2*r4,2))/
                 (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  alpha2*Math.pow(r2,2),2)*
                   (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))*
              (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
  2*Eta23*r2*r3)*
                    (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
  2*Eta24*r2*r4))/
                 (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                   Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))*
                   Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2))) +
                (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
  2*Eta34*r3*r4)/
                 (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))*
                   Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2)))))/
            (2.*Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
                 (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                   (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
              Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
                 (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                   (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2))),1.5)) +
           ((-((r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
  2*Eta23*r2*r3))/
                   ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                     (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2)))) +
                (r1*r2*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  2*Eta13*r1*r3 - 2*Eta23*r2*r3,2))/
                 (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  alpha2*Math.pow(r2,2),2)*
                   (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
              (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
  2*Eta23*r2*r3)*
                    (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
  2*Eta24*r2*r4))/
                 (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                   Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))*
                   Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2))) +
                (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
  2*Eta34*r3*r4)/
                 (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))*
                   Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2)))))/
            (2.*Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
                 (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                   (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))),1.5)*
              Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
                 (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                   (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2))))))*
         ((-2*((r1*r3)/(Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))*
                   Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2))) +
                (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
  2*Eta23*r2*r3)*
                   (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
  2*Eta24*r2*r4))/
                 (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                   Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2),1.5)*
                   Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2))) -
                (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
  2*Eta24*r2*r4))/
                 (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                   Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))*
                   Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2))) -
                (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
  2*Eta34*r3*r4))/
                 (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2),1.5)*
                   Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2))))*
              (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
  2*Eta23*r2*r3)*
                    (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
  2*Eta24*r2*r4))/
                 (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                   Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))*
                   Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2))) +
                (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
  2*Eta34*r3*r4)/
                 (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))*
                   Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2)))))/
            ((1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
  2*Eta23*r2*r3,2)/
                 (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                   (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
              (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
  2*Eta24*r2*r4,2)/
                 (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                   (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))))
  +
           (((r1*r3*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
  - 2*Eta23*r2*r3,2))/
                 (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                   Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2),2)) -
                (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
  2*Eta23*r2*r3))/
                 ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                   (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
              Math.pow(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
  2*Eta23*r2*r3)*
                    (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
  2*Eta24*r2*r4))/
                 (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                   Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))*
                   Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2))) +
                (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
  2*Eta34*r3*r4)/
                 (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))*
                   Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2))),2))/
            (Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
                 (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                   (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))),2)*
              (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
  2*Eta24*r2*r4,2)/
                 (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                   (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2)))))))/
      (2.*Math.pow(1 - Math.pow(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
                 (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
  2*Eta24*r2*r4))/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
  +
             (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
  2*Eta34*r3*r4)/
              (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2))),2)/
           ((1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
  2*Eta23*r2*r3,2)/
                (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                  (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
             (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
  2*Eta24*r2*r4,2)/
                (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                  (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2))))),1.5)) +
     (-(((Math.pow(r1,2)*r2*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  2*Eta13*r1*r3 - 2*Eta23*r2*r3))/
              (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2),1.5)*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
  -
             (Math.pow(r1,2)*r2*r3)/
              ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
  +
             (Math.pow(r1,2)*r2*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
              (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2),1.5)*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
  -
             (Math.pow(r1,2)*r2*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
                (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
  2*Eta24*r2*r4))/
              (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  alpha2*Math.pow(r2,2),2)*
                Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2),1.5)*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
  +
             (Math.pow(r1,2)*r2*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
              (Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  alpha2*Math.pow(r2,2),2)*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2))))/
           (Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
  - 2*Eta23*r2*r3,2)/
                (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                  (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
             Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
                (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                  (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))))
  +
        (((r1*r3*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
  2*Eta23*r2*r3,2))/
              (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2),2)) -
             (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
  2*Eta23*r2*r3))/
              ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))))*
           (-(r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
  2*Eta23*r2*r3))/
              (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
  -
             (r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
  2*Eta24*r2*r4))/
              (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
  +
             (r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
  2*Eta23*r2*r3)*
                (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
  2*Eta24*r2*r4))/
              (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  alpha2*Math.pow(r2,2),2)*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2)))))/
         (2.*Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))),1.5)*
           Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
  - 2*Eta24*r2*r4,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))) +
        ((-((r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
  2*Eta24*r2*r4))/
                ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2)))
                ) + (r1*r2*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  2*Eta14*r1*r4 - 2*Eta24*r2*r4,2))/
              (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  alpha2*Math.pow(r2,2),2)*
                (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))*
           ((r1*r3)/(Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
  +
             (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
  2*Eta23*r2*r3)*
                (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
  2*Eta24*r2*r4))/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2),1.5)*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
  -
             (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
  2*Eta24*r2*r4))/
              (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
  -
             (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
  2*Eta34*r3*r4))/
              (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2),1.5)*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2)))))/
         (2.*Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
           Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
  - 2*Eta24*r2*r4,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2))),1.5)) +
        ((-((r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
  2*Eta23*r2*r3))/
                ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2)))
                ) + (r1*r2*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  2*Eta13*r1*r3 - 2*Eta23*r2*r3,2))/
              (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  alpha2*Math.pow(r2,2),2)*
                (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
           ((r1*r3)/(Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
  +
             (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
  2*Eta23*r2*r3)*
                (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
  2*Eta24*r2*r4))/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2),1.5)*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
  -
             (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
  2*Eta24*r2*r4))/
              (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
  -
             (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
  2*Eta34*r3*r4))/
              (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2),1.5)*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2)))))/
         (2.*Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))),1.5)*
           Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
  - 2*Eta24*r2*r4,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))) -
        (((r1*r3*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
  2*Eta23*r2*r3,2))/
              (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2),2)) -
             (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
  2*Eta23*r2*r3))/
              ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))))*
           (-((r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
  2*Eta24*r2*r4))/
                ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2)))
                ) + (r1*r2*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  2*Eta14*r1*r4 - 2*Eta24*r2*r4,2))/
              (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  alpha2*Math.pow(r2,2),2)*
                (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))*
           (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
  2*Eta23*r2*r3)*
                 (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
  2*Eta24*r2*r4))/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
  +
             (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
  2*Eta34*r3*r4)/
              (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2)))))/
         (4.*Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))),1.5)*
           Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
  - 2*Eta24*r2*r4,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2))),1.5)) -
        (3*((r1*r3*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
  2*Eta23*r2*r3,2))/
              (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2),2)) -
             (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
  2*Eta23*r2*r3))/
              ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))))*
           (-((r1*r2*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
  2*Eta23*r2*r3))/
                ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2)))
                ) + (r1*r2*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  2*Eta13*r1*r3 - 2*Eta23*r2*r3,2))/
              (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  alpha2*Math.pow(r2,2),2)*
                (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
           (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
  2*Eta23*r2*r3)*
                 (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
  2*Eta24*r2*r4))/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
  +
             (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
  2*Eta34*r3*r4)/
              (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2)))))/
         (4.*Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))),2.5)*
           Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
  - 2*Eta24*r2*r4,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))) +
        (((2*Math.pow(r1,2)*r2*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  2*Eta13*r1*r3 - 2*Eta23*r2*r3))/
              ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2),2)) -
             (Math.pow(r1,2)*r2*r3*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  2*Eta13*r1*r3 - 2*Eta23*r2*r3,2))/
              (Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  alpha2*Math.pow(r2,2),2)*
                Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2),2)) -
             (2*Math.pow(r1,2)*r2*r3)/
              ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2)))
              + (2*Math.pow(r1,2)*r2*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  2*Eta13*r1*r3 - 2*Eta23*r2*r3))/
              (Math.pow(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  alpha2*Math.pow(r2,2),2)*
                (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
           (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
  2*Eta23*r2*r3)*
                 (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
  2*Eta24*r2*r4))/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
  +
             (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
  2*Eta34*r3*r4)/
              (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))*
                Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2)))))/
         (2.*Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
  2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
  alpha3*Math.pow(r3,2))),1.5)*
           Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
  - 2*Eta24*r2*r4,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))))/
      Math.sqrt(1 - Math.pow(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
  2*Eta23*r2*r3)*
               (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
  2*Eta24*r2*r4))/
            (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
           (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
  2*Eta34*r3*r4)/
            (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
              Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
  alpha4*Math.pow(r4,2))),2)/
         ((1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
  2*Eta23*r2*r3,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
           (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
  2*Eta24*r2*r4,2)/
              (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))));
    }
      
    private double caseH_J() {
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
      
      return -(r3*r4*((-2*((r1*r3)/(Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
          alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
alpha4*Math.pow(r4,2))) +
       (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
2*Eta23*r2*r3)*
          (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
2*Eta24*r2*r4))/
        (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
alpha3*Math.pow(r3,2),1.5)*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
alpha4*Math.pow(r4,2))) -
       (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
2*Eta24*r2*r4))/
        (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
alpha4*Math.pow(r4,2))) -
       (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
2*Eta34*r3*r4))/
        (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
alpha3*Math.pow(r3,2),1.5)*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
alpha4*Math.pow(r4,2))))*
     (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
2*Eta23*r2*r3)*
           (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
2*Eta24*r2*r4))/
        (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
alpha4*Math.pow(r4,2))) +
       (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
2*Eta34*r3*r4)/
        (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
alpha4*Math.pow(r4,2)))))/
   ((1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
2*Eta23*r2*r3,2)/
        (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
     (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
2*Eta24*r2*r4,2)/
        (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))))
+
  (((r1*r3*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
- 2*Eta23*r2*r3,2))/
        (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
alpha3*Math.pow(r3,2),2)) -
       (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
2*Eta23*r2*r3))/
        ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
     Math.pow(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
2*Eta23*r2*r3)*
           (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
2*Eta24*r2*r4))/
        (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
alpha4*Math.pow(r4,2))) +
       (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
2*Eta34*r3*r4)/
        (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
alpha4*Math.pow(r4,2))),2))/
   (Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
        (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
alpha3*Math.pow(r3,2))),2)*
     (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
2*Eta24*r2*r4,2)/
        (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
          (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
alpha4*Math.pow(r4,2)))))))/
(2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
2*Eta23*r2*r3,2)/
  (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
alpha3*Math.pow(r3,2))))*
Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))*
Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
2*Eta24*r2*r4,2)/
  (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
alpha4*Math.pow(r4,2))))*
Math.pow(1 - Math.pow(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
- 2*Eta23*r2*r3)*
        (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
2*Eta24*r2*r4))/
     (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
       Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
       Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
+
    (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
2*Eta34*r3*r4)/
     (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
alpha3*Math.pow(r3,2))*
       Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
alpha4*Math.pow(r4,2))),2)/
  ((1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
2*Eta23*r2*r3,2)/
       (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
         (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
    (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
2*Eta24*r2*r4,2)/
       (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
         (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
alpha4*Math.pow(r4,2))))),1.5)) -
(r3*((r1*r3*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
2*Eta23*r2*r3,2))/
  (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
    Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2),2))
-
 (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
2*Eta23*r2*r3))/
  ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
alpha3*Math.pow(r3,2))))*r4)
/(2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
2*Eta23*r2*r3,2)/
  (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
alpha3*Math.pow(r3,2))),
1.5)*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))*
Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
2*Eta24*r2*r4,2)/
  (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
alpha4*Math.pow(r4,2))))*
Math.sqrt(1 - Math.pow(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
- 2*Eta23*r2*r3)*
        (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
2*Eta24*r2*r4))/
     (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
       Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
       Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
+
    (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
2*Eta34*r3*r4)/
     (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
alpha3*Math.pow(r3,2))*
       Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
alpha4*Math.pow(r4,2))),2)/
  ((1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
2*Eta23*r2*r3,2)/
       (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
         (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
    (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
2*Eta24*r2*r4,2)/
       (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
         (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
alpha4*Math.pow(r4,2))))))) -
(r1*Math.pow(r3,2)*r4)/(Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
alpha3*Math.pow(r3,2),1.5)*
Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
2*Eta23*r2*r3,2)/
  (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
alpha3*Math.pow(r3,2))))*
Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))*
Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
2*Eta24*r2*r4,2)/
  (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
alpha2*Math.pow(r2,2))*(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
alpha4*Math.pow(r4,2))))*
Math.sqrt(1 - Math.pow(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
- 2*Eta23*r2*r3)*
        (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
2*Eta24*r2*r4))/
     (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
       Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
       Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
+
    (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
2*Eta34*r3*r4)/
     (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
alpha3*Math.pow(r3,2))*
       Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
alpha4*Math.pow(r4,2))),2)/
  ((1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
2*Eta23*r2*r3,2)/
       (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
         (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
    (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
2*Eta24*r2*r4,2)/
       (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
         (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
alpha4*Math.pow(r4,2)))))));
    }
    
    private double caseK() {
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
      
      return -((-(((r1*r3)/(Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))) +
                            (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3)*
                               (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
                             (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                               Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2),1.5)*
                               Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))) -
                            (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
                             (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                               Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
                               Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))) -
                            (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
              2*Eta34*r3*r4))/
                             (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2),1.5)*
                               Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))))/
                          (Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 - 2*Eta23*r2*r3,
                                2)/
                               (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
                                 (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))))*
                            Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta14*r1*r4 - 2*Eta24*r2*r4,
                                2)/
                               (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
                                 (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2)))))) +
                       (((r1*r3*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
              - 2*Eta23*r2*r3,2))/
                             (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                               Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2),2)) -
                            (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3))/
                             ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                               (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
                          (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3)*
                                (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
                             (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                               Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
                               Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))) +
                            (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
              2*Eta34*r3*r4)/
                             (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
                               Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2)))))/
                        (2.*Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 - 2*Eta23*r2*r3,
                              2)/(4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
                               (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))),1.5)*
                          Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
                             (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                               (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))))))*
                     ((-2*((r1*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3)*r4*
                               (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
                             (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                               Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
                               Math.pow(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2),1.5)) -
                            (r1*r4*(2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
              2*Eta34*r3*r4))/
                             (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
                               Math.pow(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2),1.5)) +
                            (r1*r4)/
                             (Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
                               Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))) -
                            (r1*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3)*r4)/
                             (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                               Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
                               Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))))*
                          (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3)*
                                (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
                             (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                               Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
                               Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))) +
                            (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
              2*Eta34*r3*r4)/
                             (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
                               Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2)))))/
                        ((1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3,2)/
                             (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                               (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
                          (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4,2)/
                             (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                               (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))))
              +
                       (((r1*r4*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
              - 2*Eta24*r2*r4,2))/
                             (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                               Math.pow(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2),2)) -
                            (r1*r4*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
                             ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                               (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))*
                          Math.pow(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3)*
                                (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
                             (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                               Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
                               Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))) +
                            (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
              2*Eta34*r3*r4)/
                             (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
                               Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))),2))/
                        ((1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3,2)/
                             (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                               (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
                          Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta14*r1*r4 - 2*Eta24*r2*r4,
                              2)/(4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
                               (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))),2))))/
                  (2.*Math.pow(1 - Math.pow(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
                             (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
                          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
              +
                         (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
              2*Eta34*r3*r4)/
                          (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))),2)/
                       ((1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3,2)/
                            (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                              (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
                         (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4,2)/
                            (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                              (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))))),1.5)) +
                 (-((-((Math.pow(r1,2)*r3*r4)/
                            (Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                              Math.pow(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2),1.5))) -
                         (Math.pow(r1,2)*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
                            r4*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
                          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2),1.5)*
                            Math.pow(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2),1.5)) +
                         (Math.pow(r1,2)*r3*r4*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta14*r1*r4 -
                              2*Eta24*r2*r4))/
                          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                            Math.pow(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2),1.5)) +
                         (Math.pow(r1,2)*r3*r4*(2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              2*Eta14*r1*r4 -
                              2*Eta34*r3*r4))/
                          (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2),1.5)*
                            Math.pow(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2),1.5)) -
                         (Math.pow(r1,2)*r3*r4)/
                          (Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2),1.5)*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
              +
                         (Math.pow(r1,2)*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
                            r4)/(2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
                            Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2),1.5)*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
              -
                         (Math.pow(r1,2)*r3*r4)/
                          ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))))/
                       (Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
              - 2*Eta23*r2*r3,2)/
                            (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                              (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
                         Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
                            (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                              (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))))
              +
                    (((r1*r3*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3,2))/
                          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2),2)) -
                         (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3))/
                          ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
                       ((r1*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3)*r4*
                            (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
                          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                            Math.pow(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2),1.5)) -
                         (r1*r4*(2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
              2*Eta34*r3*r4))/
                          (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
                            Math.pow(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2),1.5)) +
                         (r1*r4)/(Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
              -
                         (r1*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3)*r4)/
                          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2)))))/
                     (2.*Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
                          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))),1.5)*
                       Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
              - 2*Eta24*r2*r4,2)/
                          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))) +
                    (((r1*r4*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4,2))/
                          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            Math.pow(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2),2)) -
                         (r1*r4*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
                          ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))*
                       ((r1*r3)/(Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
              +
                         (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3)*
                            (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
                          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2),1.5)*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
              -
                         (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
                          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
              -
                         (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
              2*Eta34*r3*r4))/
                          (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2),1.5)*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2)))))/
                     (2.*Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
                          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
                       Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
              - 2*Eta24*r2*r4,2)/
                          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))),1.5)) -
                    (((r1*r3*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3,2))/
                          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2),2)) -
                         (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3))/
                          ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
                       ((r1*r4*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4,2))/
                          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            Math.pow(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2),2)) -
                         (r1*r4*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
                          ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))*
                       (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3)*
                             (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
                          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
              +
                         (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
              2*Eta34*r3*r4)/
                          (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2)))))/
                     (4.*Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
                          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))),1.5)*
                       Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
              - 2*Eta24*r2*r4,2)/
                          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))),1.5)))/
                  Math.sqrt(1 - Math.pow(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3)*
                           (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
                        (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
                       (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
              2*Eta34*r3*r4)/
                        (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))),2)/
                     ((1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3,2)/
                          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
                       (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4,2)/
                          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))));
    }
    
    private double caseL() {
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
      
      return -((-(((r1*r3)/(Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))) +
                            (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3)*
                               (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
                             (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                               Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2),1.5)*
                               Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))) -
                            (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
                             (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                               Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
                               Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))) -
                            (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
              2*Eta34*r3*r4))/
                             (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2),1.5)*
                               Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))))/
                          (Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 - 2*Eta23*r2*r3,
                                2)/
                               (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
                                 (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))))*
                            Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta14*r1*r4 - 2*Eta24*r2*r4,
                                2)/
                               (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
                                 (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2)))))) +
                       (((r1*r3*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
              - 2*Eta23*r2*r3,2))/
                             (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                               Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2),2)) -
                            (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3))/
                             ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                               (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
                          (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3)*
                                (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
                             (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                               Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
                               Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))) +
                            (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
              2*Eta34*r3*r4)/
                             (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
                               Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2)))))/
                        (2.*Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 - 2*Eta23*r2*r3,
                              2)/(4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
                               (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))),1.5)*
                          Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta14*r1*r4 - 2*Eta24*r2*r4,2)/
                             (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                               (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))))))*
                     (-((r2*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4)*
                            (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3)*
                                  (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
                               (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
                                 Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
                                 Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))) +
                              (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
              2*Eta34*r3*r4)/
                               (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
                                 Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2)))))/
                          ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                            (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
              - 2*Eta23*r2*r3,2)/
                               (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
                                 (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))*
                            (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
              - 2*Eta24*r2*r4,2)/
                               (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
                                 (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2)))))) +
                       (r2*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3)*
                          Math.pow(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3)*
                                (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
                             (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                               Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
                               Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))) +
                            (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
              2*Eta34*r3*r4)/
                             (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
                               Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))),2))/
                        ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                          (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                          Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 - 2*Eta23*r2*r3,
                              2)/(4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              alpha2*Math.pow(r2,2))*
                               (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))),2)*
                          (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4,2)/
                             (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                               (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2)))))))/
                  (2.*Math.pow(1 - Math.pow(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 - 2*Eta23*r2*r3)*
                             (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
                          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
              +
                         (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
              2*Eta34*r3*r4)/
                          (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))),2)/
                       ((1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3,2)/
                            (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                              (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
                         (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4,2)/
                            (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                              (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))))),1.5)) +
                 ((r2*r3*((r1*r3*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
              - 2*Eta23*r2*r3,2))/
                          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2),2)) -
                         (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3))/
                          ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
                       (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
                     (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                       Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                       Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
              - 2*Eta23*r2*r3,2)/
                          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))),1.5)*
                       Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))*
                       Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
              - 2*Eta24*r2*r4,2)/
                          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))) +
                    (r1*r2*Math.pow(r3,2)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta14*r1*r4 - 2*Eta24*r2*r4))/
                     (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                       Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2),1.5)*
                       Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
              - 2*Eta23*r2*r3,2)/
                          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
                       Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))*
                       Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
              - 2*Eta24*r2*r4,2)/
                          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))) +
                    (r2*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3)*
                       ((r1*r3)/(Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
              +
                         (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3)*
                            (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
                          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2),1.5)*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
              -
                         (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
                          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
              -
                         (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
              2*Eta34*r3*r4))/
                          (2.*Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2),1.5)*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2)))))/
                     (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                       (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                       Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
              - 2*Eta23*r2*r3,2)/
                          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))),1.5)*
                       Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
              - 2*Eta24*r2*r4,2)/
                          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))) -
                    (3*r2*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3)*
                       ((r1*r3*Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3,2))/
                          (2.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2),2)) -
                         (r1*r3*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3))/
                          ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
                       (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3)*
                             (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
                          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
              +
                         (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
              2*Eta34*r3*r4)/
                          (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2)))))/
                     (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                       (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                       Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3
              - 2*Eta23*r2*r3,2)/
                          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))),2.5)*
                       Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
              - 2*Eta24*r2*r4,2)/
                          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))) +
                    (((-2*r1*r2*Math.pow(r3,2)*(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 -
                              2*Eta23*r2*r3))/
                          ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            Math.pow(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2),2)) +
                         (2*r1*r2*Math.pow(r3,2))/
                          ((alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
                       (-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3)*
                             (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
                          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2)))
              +
                         (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
              2*Eta34*r3*r4)/
                          (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))*
                            Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2)))))/
                     (2.*Math.pow(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 +
              2*Eta13*r1*r3 - 2*Eta23*r2*r3,2)/
                          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 +
              alpha3*Math.pow(r3,2))),1.5)*
                       Math.sqrt(1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4
              - 2*Eta24*r2*r4,2)/
                          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))))/
                  Math.sqrt(1 - Math.pow(-((2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3)*
                           (2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4))/
                        (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))) +
                       (2*alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + 2*Eta14*r1*r4 -
              2*Eta34*r3*r4)/
                        (2.*Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))*
                          Math.sqrt(alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 +
              alpha4*Math.pow(r4,2))),2)/
                     ((1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta13*r1*r3 -
              2*Eta23*r2*r3,2)/
                          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            (alpha1*Math.pow(r1,2) + 2*Eta13*r1*r3 + alpha3*Math.pow(r3,2))))*
                       (1 - Math.pow(2*alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + 2*Eta14*r1*r4 -
              2*Eta24*r2*r4,2)/
                          (4.*(alpha1*Math.pow(r1,2) + 2*Eta12*r1*r2 + alpha2*Math.pow(r2,2))*
                            (alpha1*Math.pow(r1,2) + 2*Eta14*r1*r4 + alpha4*Math.pow(r4,2))))));
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
      SecondPartialIndex.remove(pos);
    }
    
    public String toString() {
      return "DihedralAngle@[" + e + ", " + t + "]" + "w.r.t" + location + "=" + getValue();
    }
  }
}

