package development;

import java.util.HashMap;

import Triangulation.Tetra;
import Triangulation.Vertex;
import Triangulation.StdTetra;
import Geoquant.*;

//note that the coordinates this geoquant gives do not take orientation into account

public class Coord3D extends Geoquant {
  // Index map
  private static HashMap<TriPosition, Coord3D> Index = new HashMap<TriPosition, Coord3D>();

  private Length L_ij;
  private Length L_ik;
  private Length L_il;
  private Length L_jk;
  private Length L_jl;
  private Length L_kl;
  private Angle A_jik;
  private Angle A_jil;
  private DihedralAngle D_ij; 

  private int stdTIndex; //coordinate computed differently depending on index
  private Vector coord; //3d point holding coords for this vertex/tetra pair
  
  public Coord3D(Vertex v, Tetra t) {
    super(v,t);
    StdTetra st = new StdTetra(t);
    L_ij = Length.At(st.e12);
    L_ik = Length.At(st.e13);
    L_il = Length.At(st.e14);
    L_jk = Length.At(st.e23);
    L_jl = Length.At(st.e24);
    L_kl = Length.At(st.e34);
    A_jik = Angle.At(st.v1, st.f123);
    A_jil = Angle.At(st.v1, st.f124);
    D_ij = DihedralAngle.At(st.e12, t);
        
    //figure out index of v in stdTetra vertex list
    if(v == st.v1){ stdTIndex = 0; }
    else if(v == st.v2){ stdTIndex = 1; }
    else if(v == st.v3){ stdTIndex = 2; }
    else if(v == st.v4){ stdTIndex = 3; }
    
    L_ij.addObserver(this);
    L_ik.addObserver(this);
    L_il.addObserver(this);
    L_jk.addObserver(this);
    L_jl.addObserver(this);
    L_kl.addObserver(this);
    A_jik.addObserver(this);
    A_jil.addObserver(this);
    D_ij.addObserver(this);
  }
  
  protected void recalculate() {
    
    if(stdTIndex == 0){
      
      coord = new Vector(new double[] {0, 0, 0});
     
    }else if(stdTIndex == 1){
      
      double L = L_ij.getValue();
      coord = new Vector(new double[] {L, 0, 0});
      
    }else if(stdTIndex == 2){
      
      double L = L_ik.getValue();
      double t = A_jik.getValue();
      coord = new Vector(new double[] {L*Math.cos(t), L*Math.sin(t), 0});
      
    }else if(stdTIndex == 3){
      
      double L = L_il.getValue();
      double a = A_jil.getValue();
      double b = D_ij.getValue();
      coord = new Vector(new double[] {L*Math.cos(a), L*Math.sin(a)*Math.cos(b), L*Math.sin(a)*Math.sin(b)});
    }

    value = 0; //unused
  }

  public void remove() {
    deleteDependents();
    L_ij.deleteObserver(this);
    L_ik.deleteObserver(this);
    L_il.deleteObserver(this);
    L_jk.deleteObserver(this);
    L_jl.deleteObserver(this);
    L_kl.deleteObserver(this);
    A_jik.deleteObserver(this);
    A_jil.deleteObserver(this);
    D_ij.deleteObserver(this);
    Index.remove(pos);
  }
  
  public static Coord3D At(Vertex v, Tetra t) {
    TriPosition T = new TriPosition(v.getSerialNumber(), t.getSerialNumber());
    Coord3D q = Index.get(T);
    if(q == null) {
      q = new Coord3D(v, t);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static double valueAt(Vertex v, Tetra t) {
    return At(v, t).getValue();
  }
  
  //like getValue(), but returns coordinate point
  public Vector getCoord() {
    double d = getValue(); //used to invoke recalculate if invalid
    return coord; 
  }
  //like valueAt(), but returns coordinate point
  public static Vector coordAt(Vertex v, Tetra t) {
    return At(v,t).getCoord();
  }
  
  
  

}
