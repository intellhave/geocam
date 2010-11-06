package development;

import geoquant.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import triangulation.Face;
import triangulation.Edge;
import triangulation.Vertex;

//value is det(affineTrans)

public class CoordTrans2D extends Geoquant {
  // Index map
  private static HashMap<TriPosition, CoordTrans2D> Index = new HashMap<TriPosition, CoordTrans2D>();

  //geoquant which gives the affine transformation gluing the face f
  //to the face f2 incident to f along the edge e
  //i.e., "change of coordinates" from f to f2
  
  //cvifj are coordinates for vertex vi on face fj; e.g. cv1f1 -> cv1f2 under 
  //the returned affine transformation.  cw1 is the non-common vertex in f, cw2 in f2.
  private Coord2D cv1f1,cv2f1,cw1;
  private Coord2D cv1f2,cv2f2,cw2;
  
  private AffineTransformation affineTrans; 
  
  public CoordTrans2D(Face f, Edge e) {
    super(f,e);
   
    //find face incident to f along edge e
    Iterator<Face> fi = e.getLocalFaces().iterator();
    Face f2 = fi.next();
    if(f2 == f){ f2 = fi.next(); }
    
    //get shared vertices of f and f2
    Iterator<Vertex> vi = e.getLocalVertices().iterator();
    Vertex v1 = vi.next();
    Vertex v2 = vi.next();
    
    //get non-common vertex on face 1
    LinkedList<Vertex> leftover1 = new LinkedList<Vertex>(f.getLocalVertices());
    leftover1.removeAll(e.getLocalVertices());
    Vertex w1 = leftover1.get(0);
    
    //get non-common vertex on face 2
    LinkedList<Vertex> leftover2 = new LinkedList<Vertex>(f2.getLocalVertices());
    leftover2.removeAll(e.getLocalVertices());
    Vertex w2 = leftover2.get(0);
    
    //set up geoquant dependencies
    cv1f1 = Coord2D.At(v1, f);
    cv2f1 = Coord2D.At(v2, f);
    cv1f2 = Coord2D.At(v1, f2);
    cv2f2 = Coord2D.At(v2, f2);
    cw1 = Coord2D.At(w1, f);
    cw2 = Coord2D.At(w2, f2);
    
    cv1f1.addObserver(this);
    cv1f2.addObserver(this);
    cv2f1.addObserver(this);
    cv2f2.addObserver(this);
    cw1.addObserver(this);
    cw2.addObserver(this);
  }
  
  protected void recalculate() {

    Vector[] P = new Vector[] {cv1f1.getCoord(), cv2f1.getCoord(), cw1.getCoord()};
    Vector[] Q = new Vector[] {cv1f2.getCoord(), cv2f2.getCoord(), cw2.getCoord()};
    try {
      affineTrans = new AffineTransformation(P,Q);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    value = affineTrans.determinant(); //unused
  }

  public void remove() {
    deleteDependents();
    cv1f1.deleteObserver(this);
    cv1f2.deleteObserver(this);
    cv2f1.deleteObserver(this);
    cv2f2.deleteObserver(this);
    cw1.deleteObserver(this);
    cw2.deleteObserver(this);
    Index.remove(pos);
  }
  
  public static CoordTrans2D At(Face f, Edge e) {
    TriPosition T = new TriPosition(f.getSerialNumber(), e.getSerialNumber());
    CoordTrans2D q = Index.get(T);
    if(q == null) {
      q = new CoordTrans2D(f, e);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static double valueAt(Face f, Edge e) {
    return At(f,e).getValue();
  }
  
  //like getValue(), but returns affine transformation
  public AffineTransformation getAffineTrans() {
    double d = getValue(); //used to invoke recalculate if invalid
    return affineTrans; 
  }
  
  //like valueAt(), but returns affine transformation
  public static AffineTransformation affineTransAt(Face f, Edge e) {
    return At(f,e).getAffineTrans();
  }

}
