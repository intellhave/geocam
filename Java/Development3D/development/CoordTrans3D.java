package development;

import geoquant.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import triangulation.Tetra;
import triangulation.Face;
import triangulation.Vertex;

//value is det(affineTrans)

public class CoordTrans3D extends Geoquant {
  // Index map
  private static HashMap<TriPosition, CoordTrans3D> Index = new HashMap<TriPosition, CoordTrans3D>();

  //geoquant which gives the affine transformation gluing the tetra t
  //to the tetra t2 incident to t along the face f
  //i.e., "change of coordinates" from t to t2
  
  //cvitj are coordinates for vertex vi on tetra tj; e.g. cv1t1 -> cv1t2 under 
  //the returned affine transformation.  cw1 is the non-common vertex in t, cw2 in t2.
  private Coord3D cv1t1,cv2t1,cv3t1,cw1;
  private Coord3D cv1t2,cv2t2,cv3t2,cw2;

  private AffineTransformation affineTrans; 
  
  public CoordTrans3D(Tetra t, Face f) {
    super(t,f);
    
    //get tetra incident to t along face f
    Iterator<Tetra> ti = f.getLocalTetras().iterator();
    Tetra t2 = ti.next();
    if(t2 == t){ t2 = ti.next(); }
    
    //get shared vertices of t and t2
    Iterator<Vertex> vi = f.getLocalVertices().iterator();
    Vertex v1 = vi.next();
    Vertex v2 = vi.next();
    Vertex v3 = vi.next();
    
    //get non-common vertex on tetra 1
    LinkedList<Vertex> leftover1 = new LinkedList<Vertex>(t.getLocalVertices());
    leftover1.removeAll(f.getLocalVertices());
    Vertex w1 = leftover1.get(0);
    
    //get non-common vertex on tetra 2
    LinkedList<Vertex> leftover2 = new LinkedList<Vertex>(t2.getLocalVertices());
    leftover2.removeAll(f.getLocalVertices());
    Vertex w2 = leftover2.get(0);
    
    cv1t1 = Coord3D.At(v1, t);
    cv2t1 = Coord3D.At(v2, t);
    cv3t1 = Coord3D.At(v3, t);
    cv1t2 = Coord3D.At(v1, t2);
    cv2t2 = Coord3D.At(v2, t2);
    cv3t2 = Coord3D.At(v3, t2);
    cw1 = Coord3D.At(w1, t);
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
    
    value = affineTrans.determinant();
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
  
  public static CoordTrans3D At(Tetra t, Face f) {
    TriPosition T = new TriPosition(t.getSerialNumber(), f.getSerialNumber());
    CoordTrans3D q = Index.get(T);
    if(q == null) {
      q = new CoordTrans3D(t, f);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static double valueAt(Tetra t, Face f) {
    return At(t, f).getValue();
  }
  
  //like getValue(), but returns affine transformation
  public AffineTransformation getAffineTrans() {
    double d = getValue(); //used to invoke recalculate if invalid
    return affineTrans; 
  }
  //like valueAt(), but returns affine transformation
  public static AffineTransformation affineTransAt(Tetra t, Face f) {
    return At(t,f).getAffineTrans();
  }
  
}
