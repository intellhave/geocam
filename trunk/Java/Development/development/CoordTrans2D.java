package development;

import java.util.HashMap;
import java.util.LinkedList;

import Triangulation.Face;
import Triangulation.Vertex;
import Geoquant.*;

//note that the coordinates this geoquant gives do not take orientation into account

public class CoordTrans2D extends Geoquant {
  // Index map
  private static HashMap<TriPosition, CoordTrans2D> Index = new HashMap<TriPosition, CoordTrans2D>();

  //the common edge between faces f1 and f2 has vertices v1 and v2
  //this gives matrix moving face f1 onto its common edge with f2
  private Coord2D cv1f1;
  private Coord2D cv1f2;
  private Coord2D cv2f1;
  private Coord2D cv2f2;

  private AffineTransformation affineTrans; 
  
  public CoordTrans2D(Face f1, Face f2) {
    super(f1,f2);
    
    //find common verts, must have retlist.size()==2
    LinkedList<Vertex> retlist = new LinkedList<Vertex>(f1.getLocalVertices());
    retlist.retainAll(f2.getLocalVertices());
    Vertex v1 = retlist.get(0);
    Vertex v2 = retlist.get(1);
    
    cv1f1 = Coord2D.At(v1, f1);
    cv2f1 = Coord2D.At(v2, f1);
    cv1f2 = Coord2D.At(v1, f2);
    cv2f2 = Coord2D.At(v2, f2);
    
    cv1f1.addObserver(this);
    cv1f2.addObserver(this);
    cv2f1.addObserver(this);
    cv2f2.addObserver(this);
  }
  
  protected void recalculate() {

    //COMPUTE THE MATRIX affineTrans HERE
    //affineTrans = new AffineTransformation(ci,cj)
    affineTrans = null;
    
    value = 0; //unused
  }

  public void remove() {
    deleteDependents();
    cv1f1.deleteObserver(this);
    cv1f2.deleteObserver(this);
    cv2f1.deleteObserver(this);
    cv2f2.deleteObserver(this);
    Index.remove(pos);
  }
  
  public static CoordTrans2D At(Face f1, Face f2) {
    TriPosition T = new TriPosition(f1.getSerialNumber(), f2.getSerialNumber());
    CoordTrans2D q = Index.get(T);
    if(q == null) {
      q = new CoordTrans2D(f1, f2);
      q.pos = T;
      Index.put(T, q);
    }
    return q;
  }
  
  public static double valueAt(Face f1, Face f2) {
    return At(f1, f2).getValue();
  }
  
  //like getValue(), but returns affine transformation
  public AffineTransformation getAffineTrans() {
    double d = getValue(); //used to invoke recalculate if invalid
    return affineTrans; 
  }
  //like valueAt(), but returns affine transformation
  public static AffineTransformation affineTransAt(Face f1, Face f2) {
    return At(f1,f2).getAffineTrans();
  }

}
