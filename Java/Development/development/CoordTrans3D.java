package development;

import geoquant.*;

import java.util.HashMap;
import java.util.LinkedList;

import triangulation.Tetra;
import triangulation.Vertex;

//note that the coordinates this geoquant gives do not take orientation into account

public class CoordTrans3D extends Geoquant {
  // Index map
  private static HashMap<TriPosition, CoordTrans3D> Index = new HashMap<TriPosition, CoordTrans3D>();

  //the common face between tetras t1 and t2 has vertices v1, v2, v3
  //this gives matrix moving tetra t1 onto its common face with t2
  //to get the orientation right, we need the non-common vertices, w1 and w2
  private Coord3D cv1t1,cv2t1,cv3t1,cw1;
  private Coord3D cv1t2,cv2t2,cv3t2,cw2;

  private AffineTransformation affineTrans; 
  
  public CoordTrans3D(Tetra t1, Tetra t2) {
    super(t1,t2);

    //get list of common vertices
    LinkedList<Vertex> vertscommon = new LinkedList<Vertex>(t1.getLocalVertices());
    vertscommon.retainAll(t2.getLocalVertices());
    Vertex v1 = vertscommon.get(0);
    Vertex v2 = vertscommon.get(1);
    Vertex v3 = vertscommon.get(2);
    
    //get non-common vertex on tetra 1
    LinkedList<Vertex> leftover1 = new LinkedList<Vertex>(t1.getLocalVertices());
    leftover1.removeAll(vertscommon);
    Vertex w1 = leftover1.get(0);
    
    //get non-common vertex on tetra 2
    LinkedList<Vertex> leftover2 = new LinkedList<Vertex>(t2.getLocalVertices());
    leftover2.removeAll(vertscommon);
    Vertex w2 = leftover2.get(0);
    
    cv1t1 = Coord3D.At(v1, t1);
    cv2t1 = Coord3D.At(v2, t1);
    cv3t1 = Coord3D.At(v3, t1);
    cv1t2 = Coord3D.At(v1, t2);
    cv2t2 = Coord3D.At(v2, t2);
    cv3t2 = Coord3D.At(v3, t2);
    cw1 = Coord3D.At(w1, t1);
    cw2 = Coord3D.At(w2, t2);
    
    cv1t1.addObserver(this);
    cv2t1.addObserver(this);
    cv3t1.addObserver(this);
    cv1t2.addObserver(this);
    cv2t2.addObserver(this);
    cv3t2.addObserver(this);
    cw1.addObserver(this);
    cw2.addObserver(this);
  }
  
  protected void recalculate() {

    Vector[] P = new Vector[] {cv1t1.getCoord(), cv2t1.getCoord(), cv3t1.getCoord(), cw1.getCoord()};
    Vector[] Q = new Vector[] {cv1t2.getCoord(), cv2t2.getCoord(), cv3t2.getCoord(), cw2.getCoord()};
    try {
      affineTrans = new AffineTransformation(P,Q);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
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
    cw1.deleteObserver(this);
    cw2.deleteObserver(this);
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
