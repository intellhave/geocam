package objects;

import java.util.List;

import development.AffineTransformation;
import development.Coord2D;
import development.CoordTrans2D;
import development.DevelopmentComputations;
import development.Trail;
import development.Vector;
import triangulation.Edge;
import triangulation.Face;

//tuple of a face and a position in that face's coordinates

public class ManifoldPosition {

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
  
  public void setPosition(Face f, Vector pos){
    face = f; position = pos;
  }
    
  public Face getFace(){ return face; }
  public Vector getPosition(){ return position; }
  
  //moves position along the manifold in the direction of dx
  //optional parameter return_trail to show path taken (not operational yet)
  //optionally keep track of net transformation (useful for tangent vectors etc)
  //(Adapted from Kira's computeEnd)

  
  public void move(Vector dx, Vector...tangentVectors){
    move(dx,null,tangentVectors);
  }

  public void move(Vector dx, Trail trail, Vector...tangentVectors){
    
    Vector startPos = new Vector(position);
    position = Vector.add(position, dx);
    moveRecurse(startPos, null, trail, tangentVectors);
  }
    
  private void moveRecurse(Vector startPos, Edge lastEdgeCrossed, Trail trail, Vector...tangentVectors){
    
    //if position is in face, then quit
    Vector l = DevelopmentComputations.getBarycentricCoords(position,face);

    if( (l.getComponent(0) >= 0) && (l.getComponent(0) < 1) &&
        (l.getComponent(1) >= 0) && (l.getComponent(1) < 1) &&
        (l.getComponent(2) >= 0) && (l.getComponent(2) < 1)){
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
      Vector pointDiff = Vector.subtract(position, v2);

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
    Vector nextEndPos = affineTrans.affineTransPoint(position);
    setPosition(adjacentFace, nextEndPos);
    
    //flip over whatever tangent vectors are being carried along
    for(int i=0; i<tangentVectors.length; i++){
      tangentVectors[i].setEqualTo(affineTrans.affineTransVector(tangentVectors[i]));
    }
    
    moveRecurse(nextStartPos, intersectedEdge, trail, tangentVectors);
  }


}
