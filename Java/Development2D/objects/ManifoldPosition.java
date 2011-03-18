package objects;

import java.util.ArrayList;
import java.util.List;

import development.AffineTransformation;
import development.Coord2D;
import development.CoordTrans2D;
import development.DevelopmentComputations;
import development.Vector;

import triangulation.Edge;
import triangulation.Face;

/* Represents a position on the Triangulation
 * 
 * The data is a face, position, and orientation
 * The position is a Vector which is in the coordinates given by the Coord2D geoquant
 * The most useful aspect of ManifoldPosition is the 'move' method, which can move
 * the position along a geodesic in the manifold, and can carry tangent vectors along
 */

public class ManifoldPosition{

  //position
  protected Face face;
  protected Vector position;
  //orientation (should be normal and orthogonal, but this is not enforced)
  protected Vector forward;
  protected Vector left;
  
  public ManifoldPosition(Face f, Vector pos){
    face = f; position = pos;
    setDefaultOrientation();
  }
  public ManifoldPosition(Face f, Vector pos, Vector directionForward, Vector directionLeft){
    face = f; position = pos;
    if(!setOrientation(directionForward, directionLeft)){ setDefaultOrientation(); }
  }
  
  public ManifoldPosition(ManifoldPosition mpos){
    //copy constructor
    face = mpos.getFace();
    position = new Vector(mpos.getPosition());
    if(!setOrientation(mpos.getDirectionForward(), mpos.getDirectionLeft())){ setDefaultOrientation(); }
  }
    
  //this can be overridden to do something when the position changes faces (see VisibleObject)
  //-----------------------------------------
  protected void reportFaceChange(Face oldFace){}
  
  //tangent vectors getters and setters
  //-----------------------------------------
  private void setDefaultOrientation(){
    //some arbitrary orientation
    forward = new Vector(1,0);
    left = new Vector(0,1);
  }
  
  public boolean setOrientation(Vector directionForward, Vector directionLeft){
    //set forward and left vectors
    if((directionForward == null) || (directionLeft == null)){ return false; }
    forward = new Vector(directionForward); 
    left = new Vector(directionLeft);
    return true;
  }
  public boolean setOrientation(Vector directionForward){
    //if no left vector is specified, choose an arbitrary one that is normal to it
    if(directionForward == null){ return false; }
    double x = directionForward.getComponent(0);
    double y = directionForward.getComponent(1);
    return setOrientation(directionForward, new Vector(-y,x));
  }
  
  public void rotateOrientation(double angle) {
    
    double cos = Math.cos(-angle), sin = Math.sin(-angle);
    Vector m1 = new Vector(cos, -sin);
    Vector m2 = new Vector(sin, cos);
    Vector F = getDirectionForward();
    Vector L = getDirectionLeft();
    setOrientation(
        new Vector(Vector.dot(m1,F), Vector.dot(m2,F)),
        new Vector(Vector.dot(m1,L), Vector.dot(m2,L)));
  }
  
  public Vector getDirectionForward(){ return forward; }
  public Vector getDirectionLeft(){ return left; }
  
  public Vector getDirection(double componentForward, double componentLeft){ 
    Vector v = new Vector(0,0);
    v.add(Vector.scale(getDirectionForward(),componentForward));
    v.add(Vector.scale(getDirectionLeft(),componentLeft));
    return v;
  }
  
  //face and position getters and setters
  //-----------------------------------------
  public void setManifoldPosition(Face f, Vector pos){
    Face oldFace = face;
    face = f; position = pos;
    if(face != oldFace){ reportFaceChange(oldFace); }
  }
  
  public Face getFace(){ return face; }
  public Vector getPosition(){ return position; }
  
  /* moves position along the manifold in the direction of dx
   * optional parameter return_trail to show path taken (not operational yet)
   * carries along tangent vectors at the position
   * (Adapted from Kira's computeEnd)
   */
  public void move(double dForward, double dLeft, Vector...tangentVectors){
    move(dForward, dLeft, null, tangentVectors);
  } 

  public void move(double dForward, double dLeft, GeodesicPath trail, Vector...tangentVectors){

    Vector startPos = new Vector(position);
    Vector endPos = new Vector(position);
    endPos.add(Vector.scale(getDirectionForward(), dForward));
    endPos.add(Vector.scale(getDirectionLeft(), dLeft));
    move(this, startPos, endPos, face, null, trail, tangentVectors);
  }

  private void move(ManifoldPosition posToUpdate, Vector startPos, Vector endPos, Face face, Edge lastEdgeCrossed, GeodesicPath trail, Vector...tangentVectors){
    
    //if position is in face, then quit
    Vector l = DevelopmentComputations.getBarycentricCoords(endPos,face);

    if( (l.getComponent(0) >= 0) && (l.getComponent(0) < 1) &&
        (l.getComponent(1) >= 0) && (l.getComponent(1) < 1) &&
        (l.getComponent(2) >= 0) && (l.getComponent(2) < 1)){
      posToUpdate.setManifoldPosition(face,endPos);
      return;
    }
    
    //if not, find the edge that the line intersects
    boolean foundEdge = false;
    Edge intersectedEdge = null;

    List<Edge> edgeList = face.getLocalEdges();
    for(Edge e : edgeList){
      
      //don't check the edge that we just flipped over from
      if((lastEdgeCrossed != null) && e.equals(lastEdgeCrossed)){ continue; }
      
      Vector v1 = Coord2D.coordAt(e.getLocalVertices().get(0), face);
      Vector v2 = Coord2D.coordAt(e.getLocalVertices().get(1), face);

      Vector edgeDiff = Vector.subtract(v1, v2);
      Vector sourceDiff = Vector.subtract(startPos, v2);
      Vector pointDiff = Vector.subtract(endPos, v2);

      //the intersection
      Vector intersection = Vector.findIntersection(sourceDiff, pointDiff, edgeDiff);
      if(intersection == null) { continue; }
      
      //if an intersection is found, add intersection to return_trail (if applicable)
      intersectedEdge = e;
      foundEdge = true; 
      
      break;
    }
    
    if(!foundEdge){ 
      System.err.println("(ManifoldPosition.move) Error: No intersection found"); 
      return;
    }
    
    //get adjacent face and the AffineTransformation for the flip
    Face adjacentFace = DevelopmentComputations.getNewFace(face, intersectedEdge);
    AffineTransformation affineTrans = CoordTrans2D.affineTransAt(face, intersectedEdge);
    
    //flip start and end positions over intersectedEdge
    Vector nextStartPos = affineTrans.affineTransPoint(startPos);
    Vector nextEndPos = affineTrans.affineTransPoint(endPos);
    
    //flip over whatever tangent vectors are being carried along
    if(forward != null){ forward.setEqualTo(affineTrans.affineTransVector(forward)); }
    if(left != null){ left.setEqualTo(affineTrans.affineTransVector(left)); }
    for(int i=0; i<tangentVectors.length; i++){
      Vector v = tangentVectors[i];
      if(v != null){ v.setEqualTo(affineTrans.affineTransVector(v)); }
    }
    
    move(posToUpdate, nextStartPos, nextEndPos, adjacentFace, intersectedEdge, trail, tangentVectors);
  }


}
