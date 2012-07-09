package development;

import java.util.List;

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
  
  //private static final boolean VERBOSE_PATH_RECORD = true; //for debugging

  //position
  protected Face face;
  protected Vector position;
  //orientation (should be normal and orthogonal, but this is not enforced)
  protected Vector forward;
  protected Vector left;
  
  public ManifoldPosition(Face f, Vector pos){
    face = f; position = new Vector(pos);
    setDefaultOrientation();
  }
  public ManifoldPosition(Face f, Vector pos, Vector directionForward, Vector directionLeft){
    face = f; position = new Vector(pos);
    if(!setOrientation(directionForward, directionLeft)){ setDefaultOrientation(); }
  }
  
  public ManifoldPosition(ManifoldPosition mpos){
    //copy constructor
    face = mpos.getFace();
    position = new Vector(mpos.getPosition());
    if(!setOrientation(mpos.getDirectionForward(), mpos.getDirectionLeft())){ setDefaultOrientation(); }
  }
  
  //face and position getters and setters
  //-----------------------------------------
  public void setManifoldPosition(Face f, Vector pos){
    Face oldFace = face;
    face = f; position = new Vector(pos);
    if(face != oldFace){ reportFaceChange(oldFace); }
  }
  
  public void setManifoldPosition(ManifoldPosition mpos){
    setManifoldPosition(mpos.getFace(), mpos.getPosition());
    if(!setOrientation(mpos.getDirectionForward(), mpos.getDirectionLeft())){ setDefaultOrientation(); }
  }
  
  public Face getFace(){ return face; }
  public Vector getPosition(){ return position; }
    
  //this is overridden in VisibleObject so that it can notify ManifoldObjectHandler
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
 
  //moving the position
  //-----------------------------------------
  /* moves position along the manifold in the specified direction
   * optional ManifoldPath version to record the path taken
   * carries along tangent vectors at the position, if specified
   * (Adapted from Kira's computeEnd)
   */
  public void move(Vector dx, Vector...tangentVectors){
    
    Vector startPos = new Vector(position);
    Vector endPos = Vector.add(position, dx);
    move(this, startPos, endPos, face, null, null, tangentVectors);
  } 

  public void moveWithTrail(Vector dx, ManifoldPath path, Vector...tangentVectors){

    Vector startPos = new Vector(position);
    Vector endPos = Vector.add(position, dx);
    //if(VERBOSE_PATH_RECORD){ System.out.println("\nMOVING: \n  Initial position:  Face " + face.getIndex() + ", " + position); }
    move(this, startPos, endPos, face, null, path, tangentVectors);
  }

  private void move(ManifoldPosition posToUpdate, Vector startPos, Vector endPos, 
      Face face, Edge lastEdgeCrossed, ManifoldPath path, Vector...tangentVectors){
    
    //if position is in face, then quit
    Vector l = DevelopmentComputations.getBarycentricCoords(endPos,face);

    if( (l.getComponent(0) >= 0) && (l.getComponent(0) < 1) &&
        (l.getComponent(1) >= 0) && (l.getComponent(1) < 1) &&
        (l.getComponent(2) >= 0) && (l.getComponent(2) < 1)){
      posToUpdate.setManifoldPosition(face,endPos);
      
      if(path != null){ 
        path.addSegment(new ManifoldPath.Segment(face, startPos, endPos));
        //if(VERBOSE_PATH_RECORD){ System.out.println("  Face " + face.getIndex() + ", " + startPos + " to " + endPos); }
      }
      
      return;
    }
    
    //if not, find the edge that the line intersects
    LineSegment pathSegment = new LineSegment(startPos,endPos);
    boolean foundEdge = false;
    Edge intersectedEdge = null;
    Vector intersection = null;

    List<Edge> edgeList = face.getLocalEdges();
    for(Edge e : edgeList){
      
      //don't check the edge that we just flipped over from
      if((lastEdgeCrossed != null) && e.equals(lastEdgeCrossed)){ continue; }
      
      LineSegment edgeSegment = new LineSegment(
          Coord2D.coordAt(e.getLocalVertices().get(0), face),
          Coord2D.coordAt(e.getLocalVertices().get(1), face)
      );
      intersection = LineSegment.intersectLineSegments(edgeSegment, pathSegment);
      
      if(intersection != null){
        intersectedEdge = e;
        foundEdge = true;
        break;
      }
    }
    
    if(!foundEdge){ 
      System.err.println("(ManifoldPosition.move) Error: No intersection found"); 
      return;
    }

    //add the new segment, if the path is being recorded
    if(path != null){ 
      path.addSegment(new ManifoldPath.Segment(face, startPos, intersection));
      //if(VERBOSE_PATH_RECORD){ System.out.println("  Face " + face.getIndex() + ", " + startPos + " to " + intersection); }
    }
        
    //get adjacent face and the AffineTransformation for the flip
    Face adjacentFace = DevelopmentComputations.getNewFace(face, intersectedEdge);
    AffineTransformation affineTrans = CoordTrans2D.affineTransAt(face, intersectedEdge);
    
    //flip start and end positions over intersectedEdge
    //(note: new start pos should be the intersection point, so that the correct path segment is added)
    Vector nextStartPos = affineTrans.affineTransPoint(intersection); 
    Vector nextEndPos = affineTrans.affineTransPoint(endPos);
    
    //flip over whatever tangent vectors are being carried along
    if(forward != null){ forward.setEqualTo(affineTrans.affineTransVector(forward)); }
    if(left != null){ left.setEqualTo(affineTrans.affineTransVector(left)); }
    for(int i=0; i<tangentVectors.length; i++){
      Vector v = tangentVectors[i];
      if(v != null){ v.setEqualTo(affineTrans.affineTransVector(v)); }
    }
    
    //recurse
    move(posToUpdate, nextStartPos, nextEndPos, adjacentFace, intersectedEdge, path, tangentVectors);
  }


}
