package objects;

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
 * The only data is a face and a position;
 * The position is a Vector which is in the coordinates given by the Coord2D geoquant
 * The most useful aspect of ManifoldPosition is the 'move' method, which can move
 * the position along a geodesic in the manifold, and can carry tangent vectors along
 */

public class ManifoldPosition{

  protected Face face;
  protected Vector position;
  
  public ManifoldPosition(Face f, Vector pos){
    face = f; position = pos;
  }
  
  public ManifoldPosition(ManifoldPosition mpos){
    //copy constructor
    face = mpos.getFace();
    position = new Vector(mpos.getPosition());
  }
  
  //this can be overridden to do something when the position changes faces (see VisibleObject)
  protected void reportFaceChange(Face oldFace){}
  
  public void setManifoldPosition(Face f, Vector pos){
    Face oldFace = face;
    face = f; position = pos;
    if(face != oldFace){ reportFaceChange(oldFace); }
  }
  
  public Face getFace(){ return face; }
  public Vector getPosition(){ return position; }
  
  /* moves position along the manifold in the direction of dx
   * optional parameter return_trail to show path taken (not operational yet)
   * optionally, carries along tangent vectors at the position
   * (Adapted from Kira's computeEnd)
   */
  public void move(Vector dx, Vector...tangentVectors){
    move(dx,null,tangentVectors);
  }

  public void move(Vector dx, GeodesicPath trail, Vector...tangentVectors){

    Vector startPos = new Vector(position);
    Vector endPos = Vector.add(position, dx);
    Face oldFace = face;
    moveRecurse(this,startPos, endPos, face, null, trail, tangentVectors);
    if(face != oldFace){ reportFaceChange(oldFace); }
  }
    
  private static void moveRecurse(ManifoldPosition posToUpdate, Vector startPos, Vector endPos, Face face, Edge lastEdgeCrossed, GeodesicPath trail, Vector...tangentVectors){
    
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
      System.err.println("(ManifoldPosition.moveRecurse) Error: No intersection found"); 
      return;
    }
    
    //get adjacent face and the AffineTransformation for the flip
    Face adjacentFace = DevelopmentComputations.getNewFace(face, intersectedEdge);
    AffineTransformation affineTrans = CoordTrans2D.affineTransAt(face, intersectedEdge);
    
    //flip start and end positions over intersectedEdge
    Vector nextStartPos = affineTrans.affineTransPoint(startPos);
    Vector nextEndPos = affineTrans.affineTransPoint(endPos);
    
    //flip over whatever tangent vectors are being carried along
    for(int i=0; i<tangentVectors.length; i++){
      tangentVectors[i].setEqualTo(affineTrans.affineTransVector(tangentVectors[i]));
    }
    
    moveRecurse(posToUpdate, nextStartPos, nextEndPos, adjacentFace, intersectedEdge, trail, tangentVectors);
  }


}
