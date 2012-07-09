package development;

import geoquant.Geoquant;
import geoquant.TriPosition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import triangulation.Edge;
import triangulation.Face;
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
 
  //These are new instance variables for the flip algorithm
  private Face newFace;
  private Edge edge;
  private Face oldFace;
  
  //This is an instance variable related to the flip algorithm
  //private AffineTransformation flip;
  private AffineTransformation affineTrans; 
  
  public CoordTrans2D(Face f, Edge e) {
    super(f,e);
   
    //find face incident to f along edge e
    Iterator<Face> fi = e.getLocalFaces().iterator();
    Face f2 = fi.next();
    if(f2 == f){ f2 = fi.next(); }
    newFace = f;
    oldFace = f2;
    edge = e;
    
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
    cw1 = Coord2D.At(w1, f);  
    cv1f2 = Coord2D.At(v1, f2);
    cv2f2 = Coord2D.At(v2, f2);       
    cw2 = Coord2D.At(w2, f2);
    
    /*************************************************
      Part of flip algorithm
     *************************************************/
    //StdFace stdF = new StdFace(newFace);
    //Length l = Length.at(stdF.e12);
    //double length = l.getValue();
    
    //double [][] reflectionArray = {{-1,0},{0,1}};
    //Vector translationVector = new Vector(length, 0);
    //Matrix reflection = new Matrix(reflectionArray);
    //try {
      //flip = new AffineTransformation(reflection, translationVector);
    //} catch (Exception e1) {
      //e1.printStackTrace();
    //}
    
    cv1f1.addObserver(this);
    cv1f2.addObserver(this);
    cv2f1.addObserver(this);
    cv2f2.addObserver(this);
    cw1.addObserver(this);
    cw2.addObserver(this);
  }
  
  protected void recalculate() {

    Vector cv1f1value = cv1f1.getCoord();
    Vector cv2f1value = cv2f1.getCoord();
    Vector cw1value = cw1.getCoord();
    Vector cv1f2value = cv1f2.getCoord();
    Vector cv2f2value = cv2f2.getCoord();
    Vector cw2value = cw2.getCoord();
    
    //check if the edge has opposite orientation on the two faces
    if (induceOrientation(newFace, edge) != -induceOrientation(oldFace, edge)){
      
      //if necessary, change the ordering of the first two vertices on the face
      List<Vertex> localVertices = newFace.getLocalVertices();
      Vertex temp = localVertices.get(0);
      localVertices.set(0, localVertices.get(1));
      localVertices.set(1, temp); 
      
      //make the 2DCoordinates update
      cv1f1.update(cv1f1, cv1f1value);
      cv2f1.update(cv2f1, cv2f1value);
      cw1.update(cw1, cw1value);
      
      cv1f1value = cv1f1.getCoord();
      cv2f1value = cv2f1.getCoord();
      cw1value = cw1.getCoord();
      
      //get the transformation
      Vector[] P = new Vector[] { cv1f1value, cv2f1value, cw1value };
      Vector[] Q = new Vector[] { cv1f2value, cv2f2value, cw2value };
      try {
        affineTrans = new AffineTransformation(P, Q);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    else{
      Vector[] P = new Vector[] { cv1f1value, cv2f1value, cw1value };
      Vector[] Q = new Vector[] { cv1f2value, cv2f2value, cw2value };
      try {
        affineTrans = new AffineTransformation(P, Q);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    

    /*****************************************************************************************************
     * 
     *   This is code that does the flipping through the transformation rather than changing
     *   the face's data. Needs debugging to deal with normal vectors in EmbeddedFace
     * 
     *****************************************************************************************************/
    /*****************************************************************************************************
    System.out.println("Orientation on face one is: " + induceOrientation(newFace, edge));
    System.out.println("Orientation on face two is: " + induceOrientation(oldFace, edge));
    
    if (induceOrientation(newFace, edge) != -induceOrientation(oldFace, edge)) {
      cv1f1value = flip.affineTransPoint(cv1f1value);
      cv2f1value = flip.affineTransPoint(cv2f1value);
      cw1value = flip.affineTransPoint(cw1value);
      Vector[] P = new Vector[] { cv1f1value, cv2f1value, cw1value };
      Vector[] Q = new Vector[] { cv1f2value, cv2f2value, cw2value };
      AffineTransformation connect;
      try {
        connect = new AffineTransformation(P, Q);
        flip.leftMultiply(connect);
        affineTrans = new AffineTransformation(flip);
      } catch (Exception e) {
        e.printStackTrace();
      }

    } else {
      Vector[] P = new Vector[] { cv1f1value, cv2f1value, cw1value };
      Vector[] Q = new Vector[] { cv1f2value, cv2f2value, cw2value };
      AffineTransformation connect;
      try {
        connect = new AffineTransformation(P, Q);
        affineTrans = new AffineTransformation(connect);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    /******************************************************************************************************/
 
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

  /*******************************************************************************************
   * InduceOrientation 
   * 
   * This is a method that takes a face and an edge and returns 1 or -1 to indicate the 
   * orientation of the edge on that face.
   ********************************************************************************************/
  public int induceOrientation(Face A,Edge e){
    
    List<Vertex> vertexList = A.getLocalVertices();
    List<Vertex> vertices = e.getLocalVertices();
    Vertex vertex1 = vertices.get(0);
    Vertex vertex2 = vertices.get(1);
    
    for(int ii = 0; ii < 3; ii++){
      Vertex v = vertexList.get(ii);
      if(v.equals(vertex1)){
        Vertex next = vertexList.get((ii + 1 + vertexList.size()) % vertexList.size());
        if(next.equals(vertex2))
          return 1;
      }
      if(v.equals(vertex2)){
        Vertex next = vertexList.get((ii + 1 + vertexList.size()) % vertexList.size());
        if(next.equals(vertex1))
          return -1;
      }
    }
    return 0;
  }
  
}
