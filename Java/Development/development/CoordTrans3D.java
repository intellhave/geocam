package development;

import java.util.HashMap;
import java.util.LinkedList;

import Triangulation.Tetra;
import Triangulation.Vertex;
import Geoquant.*;

//note that the coordinates this geoquant gives do not take orientation into account

public class CoordTrans3D extends Geoquant {
  // Index map
  private static HashMap<TriPosition, CoordTrans3D> Index = new HashMap<TriPosition, CoordTrans3D>();

  //the common face between tetras t1 and t2 has vertices v1, v2, v3
  //this gives matrix moving tetra t1 onto its common face with t2
  private Coord3D cv1t1;
  private Coord3D cv1t2;
  private Coord3D cv2t1;
  private Coord3D cv2t2;
  private Coord3D cv3t1;
  private Coord3D cv3t2;

  private AffineTransformation affineTrans; 
  
  public CoordTrans3D(Tetra t1, Tetra t2) {
    super(t1,t2);
    
    //find common verts, must have retlist.size()==3
    LinkedList<Vertex> retlist = new LinkedList<Vertex>(t1.getLocalVertices());
    retlist.retainAll(t2.getLocalVertices());
    Vertex v1 = retlist.get(0);
    Vertex v2 = retlist.get(1);
    Vertex v3 = retlist.get(2);
    
    cv1t1 = Coord3D.At(v1, t1);
    cv2t1 = Coord3D.At(v2, t1);
    cv3t1 = Coord3D.At(v3, t1);
    cv1t2 = Coord3D.At(v1, t2);
    cv2t2 = Coord3D.At(v2, t2);
    cv3t2 = Coord3D.At(v3, t2);
    
    cv1t1.addObserver(this);
    cv2t1.addObserver(this);
    cv3t1.addObserver(this);
    cv1t2.addObserver(this);
    cv2t2.addObserver(this);
    cv3t2.addObserver(this);
  }
  
  protected void recalculate() {

    //COMPUTE THE MATRIX affineTrans HERE
    //affineTrans = new AffineTransformation(ci,cj)
    affineTrans = null;
    
    value = 0; //unused
  }

  public void remove() {
    deleteDependents();
    cv1t1.deleteObserver(this);
    cv2t1.deleteObserver(this);
    cv3t1.deleteObserver(this);
    cv1t2.deleteObserver(this);
    cv2t2.deleteObserver(this);
    cv3t2.deleteObserver(this);
    Index.remove(pos);
  }
  
  public static CoordTrans3D At(Tetra t1, Tetra t2) {
    TriPosition T = new TriPosition(t1.getSerialNumber(), t2.getSerialNumber());
    CoordTrans3D q = Index.get(T);
    if(q == null) {
      q = new CoordTrans3D(t1, t2);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static double valueAt(Tetra t1, Tetra t2) {
    return At(t1, t2).getValue();
  }
  
  //like getValue(), but returns affine transformation
  public AffineTransformation getAffineTrans() {
    double d = getValue(); //used to invoke recalculate if invalid
    return affineTrans; 
  }
  //like valueAt(), but returns affine transformation
  public static AffineTransformation affineTransAt(Tetra t1, Tetra t2) {
    return At(t1,t2).getAffineTrans();
  }
  
}
