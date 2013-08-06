package development;

import util.Vector;

/* LineSegment is a pair of vectors representing a line segment
 * 
 * Some useful geometric routines are included here (for 2D segments):
 * - intersecting a ray with a line segment
 * - intersecting two line segments
 * - clipping a line segment by a frustum
 */

public class LineSegment {

  private Vector p0;
  private Vector p1;
  
  public LineSegment(Vector start, Vector end){
    p0 = new Vector(start);
    p1 = new Vector(end);
  }
  
  public LineSegment(LineSegment ls){
    //copy constructor
    p0 = new Vector(ls.getStart());
    p1 = new Vector(ls.getEnd());
  }
  
  public Vector getStart(){ return p0; }
  public Vector getEnd(){ return p1; }
  
  //some useful geometric routines
  //----------------------------------------------
  
  //return intersection of line segment with frustum, or null if no intersetcion (assumes vectors are 2D)
  public static LineSegment clipWithFrustum(Frustum2D frust, LineSegment ls){
    
    Vector p0 = ls.getStart();
    Vector p1 = ls.getEnd();
    
    //first case: frustum is null, i.e. 'full' frustum
    if(frust == null){ return new LineSegment(p0,p1); }
    
    //now see which line segment endpoints are inside the frustum
    boolean p0inside = frust.checkInterior(p0);
    boolean p1inside = frust.checkInterior(p1);
    
    //if both points inside, then no clipping occurs
    if(p0inside && p1inside){ return new LineSegment(p0, p1); }

    //if at least one point outside, record which, and check intersections with frust edges
    Vector pInside = p0;
    if(p1inside){ pInside = p1; }
    
    Vector lineIntersectionL =  intersectRayWithLineSegment(frust.getLeft(), ls);
    Vector lineIntersectionR =  intersectRayWithLineSegment(frust.getRight(), ls);
    
    //several cases:
    if(lineIntersectionL == null){
      if(lineIntersectionR == null){ return null; }
      else{ return new LineSegment(pInside, lineIntersectionR); }
    }else{
      if(lineIntersectionR == null){ return new LineSegment(pInside, lineIntersectionL); }
      else{ return new LineSegment(lineIntersectionL, lineIntersectionR); }
    }
  }

  //return intersection of ray with line segment, or null if no intersection (assumes vectors are 2D)
  public static Vector intersectRayWithLineSegment(Vector v, LineSegment ls){

    Vector a = ls.getStart();
    Vector b = ls.getEnd();
    Vector l = Vector.subtract(b,a);
    
    Vector nv = new Vector(-v.getComponent(1), v.getComponent(0)); //normal to ray
    Vector nl = new Vector(-l.getComponent(1), l.getComponent(0)); //normal to line segment
    
    double d = Vector.dot(v,nl);
    if(d == 0){ return null; } //line segment is parallel to ray
    
    //segment is bt+a(1-t), t in [0,1]; the ray is vs, s > 0.  intersection of lines in coords s, t are:
    double s = Vector.dot(a,nl) / d;
    double t = Vector.dot(a,nv) / d;
    
    if((s < 0) || (t < 0) || (t > 1)){ return null; }
    return Vector.scale(v,s);
  }
  
  //return intersection of two line segments, or null if no intersection (assumes vectors are 2D)
  public static Vector intersectLineSegments(LineSegment ls0, LineSegment ls1){
    
    Vector a = ls0.getStart();
    Vector b = ls0.getEnd();
    Vector l0 = Vector.subtract(b,a);
    Vector n0 = new Vector(-l0.getComponent(1), l0.getComponent(0));
    
    Vector c = ls1.getStart();
    Vector d = ls1.getEnd();
    Vector l1 = Vector.subtract(d,c);
    Vector n1 = new Vector(-l1.getComponent(1), l1.getComponent(0));
    
    double det = Vector.dot(l1,n0);
    if(det == 0){ return null; } //lines are parallel
    
    //segments are bt+a(1-t) and ds+c(1-s), with s,t in [0,1].  intersection of lines in coords s,t are:
    Vector w = Vector.subtract(a,c);
    double s = Vector.dot(w,n0) / det;
    double t = Vector.dot(w,n1) / det;
    
    if((s < 0) || (s > 1) || (t < 0) || (t > 1)){ return null; }
    return Vector.add(Vector.scale(b,t), Vector.scale(a,1-t));
  }
}
