package development;

import java.awt.Color;
import java.util.ArrayList;

import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.scene.Geometry;

public class Frustum2D {

  private static final double epsilon = Math.pow(10, -6);
  private Vector left, right;
  private Vector leftNormal, rightNormal;

  public Frustum2D(Vector l, Vector r) {
   Vector l3d = new Vector(l.getComponent(0), l.getComponent(1), 0);
   Vector r3d = new Vector(r.getComponent(0), r.getComponent(1), 0);
   Vector cross = Vector.cross(r3d, l3d);
   if(cross.getComponent(2) < 0) { // left and right are switched, so it is null
     left = new Vector(0,0);
     right = new Vector(0,0);
   }else {
    left = new Vector(l);
    right = new Vector(r);
   }
   findNormals();
  }

  public Frustum2D(double[] l, double[] r) {
    left = new Vector(l[0], l[1]);
    right = new Vector(r[0], r[1]);
    findNormals();
  }

  public Frustum2D(Frustum2D f) {
    left = new Vector(f.getLeft());
    right = new Vector(f.getRight());
    findNormals();
  }

  private void findNormals() {
    leftNormal = new Vector(left.getComponent(1), -left.getComponent(0));
    rightNormal = new Vector(-right.getComponent(1), right.getComponent(0));
  }

  public Vector getLeft() { return left; }
  public Vector getRight() { return right; }

  public void normalizeVectors() {
    left.normalize();
    right.normalize();
  }

  //check if vector is inside frustum
  public boolean checkInterior(Vector vector) {
    if(left.isZero()){ return false; }
    if(Vector.dot(leftNormal, vector) < -epsilon){ return false; }
    if(Vector.dot(rightNormal, vector) < -epsilon){ return false; }
    return true;
  }

  //return intersection of two frustums, or null if there is no intersection
  public static Frustum2D intersect(Frustum2D frustum1, Frustum2D frustum2) {
    if(frustum1.isNull() || frustum2.isNull()) return null;
    if (frustum1.checkInterior(frustum2.getLeft())) {
      if (frustum1.checkInterior(frustum2.getRight()))
        return frustum2;
      else
        return new Frustum2D(frustum2.getLeft(), frustum1.getRight());
    }

    if (frustum2.checkInterior(frustum1.getLeft())) {
      if (frustum2.checkInterior(frustum1.getRight()))
        return frustum1;
      else
        return new Frustum2D(frustum1.getLeft(), frustum2.getRight());
    }

    return null; // no intersection
  }
  
  public boolean isNull() {
    return ((left.getComponent(0) == 0) && (left.getComponent(1) == 0));
  }

  public EmbeddedFace clipFace(EmbeddedFace toClip) {

    int efVertCount = toClip.getNumberVertices();
    ArrayList<Vector> efVerts = toClip.getVectors();
    ArrayList<Vector> newVerts = new ArrayList<Vector>();
    
    //check for those vertices of the EmbeddedFace which are contained in the frustum
    for(Vector v : efVerts){
      if(this.checkInterior(v)){ newVerts.add(new Vector(v)); }
    }
    //if all of the vertices are already in the frustum, no clipping occurs
    if (newVerts.size() == efVertCount){
      return new EmbeddedFace(toClip); 
    }
    
    //check for intersections of each EmbeddedFace edge with the frustum's edges
    ArrayList<Vector> intersections = new ArrayList<Vector>();
    
    for(int i=0; i<efVertCount; i++){
      
      //get edge of EmbeddedFace as a LineSegment
      LineSegment efEdge = new LineSegment(efVerts.get(i), efVerts.get((i+1) % efVertCount));
      
      //intersect with left and right rays of frustum
      Vector intersectionLeft = LineSegment.intersectRayWithLineSegment(this.getLeft(), efEdge);
      Vector intersectionRight = LineSegment.intersectRayWithLineSegment(this.getRight(), efEdge);
      
      //add intersections to newVertList, if applicable
      if((intersectionLeft != null) && !Vector.closeToAnyOf(newVerts, intersectionLeft, epsilon)){ 
        intersections.add(intersectionLeft); 
      }
      if((intersectionRight != null) && !Vector.closeToAnyOf(newVerts, intersectionRight, epsilon)){ 
        intersections.add(intersectionRight); 
      }
    }
    
    newVerts.addAll(intersections);
    
    //intersection regarded as empty if it has fewer than 3 vertices
    if (newVerts.size() < 3){ return null; }
    
    //return the convex hull, as an EmbeddedFace, of all the points found above
    ConvexHull2D hull = new ConvexHull2D(newVerts);
    return new EmbeddedFace(hull.getPoints());
  }

  public Geometry getGeometry(Color color, double z){
    
    IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();
    
    ifsf.setVertexCount(3);
    ifsf.setEdgeCount(3);
    ifsf.setFaceCount(1);
    
    ifsf.setVertexCoordinates(new double[][] {
        new double[]{0,0,z}, 
        new double[]{right.getComponent(0),right.getComponent(1),z}, 
        new double[]{left.getComponent(0),left.getComponent(1),z}
    });
    
    ifsf.setEdgeIndices(new int[][] {
       new int[] {0,1},
       new int[] {1,2},
       new int[] {2,0}
    });
    
    ifsf.setFaceIndices(new int[][] {
       new int[] {0,1,2} 
    });
    
    ifsf.update();
    return ifsf.getGeometry();
  }
  
  /*public Trail clipTrail(Vector start, Vector end, Face face, Color color) {
    Vector a = findIntersection(start, end, this.left);
    Vector b = findIntersection(start, end, this.right);
    if(a == null && b == null && (!this.checkInterior(start) || !this.checkInterior(end)))
      return null;
    
    if(a != null && b != null)
      return new Trail(a, b, face, color);
    
    if(this.checkInterior(start)) {
      if(a == null) a = start;
      else b = start;
    }
      if(b == null) b = end;
      else if(a == null) a = end;
      
    return new Trail(a, b, face, color);
  }*/
}

