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
    ArrayList<Vector> efVerts = toClip.getVectors();
    ArrayList<Vector> texCoords = toClip.getTexCoords();

    ArrayList<Vector> newVerts = new ArrayList<Vector>();
    ArrayList<Vector> newTextureCoords = new ArrayList<Vector>();

    boolean endInside, startInside;
    Vector start, end, startTexCoord, endTexCoord;

    for (int i = 0; i < efVerts.size(); i++) {
      start = efVerts.get(i);
      end = efVerts.get((i + 1) % efVerts.size());
      startTexCoord = texCoords.get(i);
      endTexCoord = texCoords.get((i + 1) % texCoords.size());

      startInside = this.checkInterior(start);
      endInside = this.checkInterior(end);

      if (startInside) {
        newVerts.add(start);
        newTextureCoords.add(startTexCoord);
      }

      if (startInside && endInside)
        continue;

      double t = findIntersection(end, start, this.getLeft());
      double s = findIntersection(end, start, this.getRight());
      Vector p = makePoint(Math.min(t, s), start, end);
      Vector q = makePoint(Math.max(t, s), start, end);
      if (p != null) {
        newVerts.add(p);
        newTextureCoords.add(makePoint(Math.min(t, s), startTexCoord,
            endTexCoord));
      }
      if (q != null) {
        newVerts.add(q);
        newTextureCoords.add(makePoint(Math.max(t, s), startTexCoord,
            endTexCoord));
      }
    }

    return new EmbeddedFace(newVerts, newTextureCoords);
  }

  private double findIntersection(Vector end, Vector start, Vector ray){
    LineSegment efEdge = new LineSegment(end,start);
    Vector rayIntersection = LineSegment.intersectRayWithLineSegment(ray, efEdge);
    if(rayIntersection==null){
      return -1;
    }
    Vector differencefromstart=Vector.subtract(start, rayIntersection);
    Vector differencefromend=Vector.subtract(end, rayIntersection);
    if (differencefromstart.length()<epsilon||differencefromend.length()<epsilon){
      return -1;
    }
    double tt = -1;
    for(int jj = 0; jj < 2; jj++){
      if( end.getComponent(jj) != start.getComponent(jj) ){
        tt = (rayIntersection.getComponent(jj) - start.getComponent(jj)) / (end.getComponent(jj) - start.getComponent(jj));
        break;
      }
    }
    
    if( tt == -1 ){
      System.err.println("Error: Unable to compute intersection point.");
      System.exit(1);
    }
    return tt;
  }
  
  private Vector makePoint(double t, Vector start, Vector end){
    if (t<0||t>1){
      return null;
    }
    return Vector.add(Vector.scale(end, t),Vector.scale(start,1-t));
  }
  
//  private void computeIntersection(ArrayList<Vector> efVerts, ArrayList<Vector> texCoords, 
//                                   ArrayList<Vector> newVerts, ArrayList<Vector> newTextureCoords, Vector ray){
//    int efVertCount = efVerts.size();
//    for(int ii=0; ii<efVertCount; ii++){
//      Vector p = efVerts.get(ii);
//      Vector q = efVerts.get((ii+1) % efVertCount);
//      
//      LineSegment efEdge = new LineSegment(p,q);
//      Vector rayIntersection = LineSegment.intersectRayWithLineSegment(ray, efEdge);
//      
//      if(rayIntersection == null) continue;
//      if(Vector.closeToAnyOf(newVerts, rayIntersection, epsilon)) continue;
//      
//      double tt = -1;
//      for(int jj = 0; jj < 2; jj++){
//        if( p.getComponent(jj) != q.getComponent(jj) ){
//          tt = (rayIntersection.getComponent(jj) - q.getComponent(jj)) / (p.getComponent(jj) - q.getComponent(jj));
//          break;
//        }
//      }
//      
//      if( tt == -1 ){
//        System.err.println("Error: Unable to compute intersection point.");
//        System.exit(1);
//      }
//      
//      Vector pCoords = new Vector(texCoords.get(ii));
//      Vector qCoords = new Vector(texCoords.get((ii+1) % efVertCount));
//      Vector intersectionCoords = Vector.add(Vector.scale(pCoords, tt), Vector.scale(qCoords, 1-tt));
//      
//      newVerts.add( rayIntersection );
//      newTextureCoords.add( intersectionCoords );
//    }
//  }

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

