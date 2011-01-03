package development;

import java.awt.Color;
import java.util.ArrayList;

import triangulation.Face;
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
    left = l;
    right = r;
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

  public Vector getLeft() {
    return left;
  }

  public Vector getRight() {
    return right;
  }

  public void normalizeVectors() {
    left.normalize();
    right.normalize();
  }

  public boolean checkInterior(Vector vector) {
    if(left.isZero()) return false;
    if (Vector.dot(leftNormal, vector) < -epsilon)
      return false;
    if (Vector.dot(rightNormal, vector) < -epsilon)
      return false;
    return true;
  }

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
    return left.getComponent(0) == 0 && left.getComponent(1) == 0;
  }

  public EmbeddedFace clipFace(EmbeddedFace toClip) {
    
    boolean verbose = false;
    
    int left_intersections = 0;
    ArrayList<Vector> vertices = new ArrayList<Vector>();
    
    for (int i = 0; i < toClip.getNumberVertices(); i++) {
      if(verbose){ System.out.println("checking interior: " + toClip.getVectorAt(i)); }
      if (this.checkInterior(toClip.getVectorAt(i))) {
        vertices.add(new Vector(toClip.getVectorAt(i)));
        if(verbose){ System.out.println("true"); }
      } else
        if(verbose){ System.out.println("false"); }
    }
    if (vertices.size() == toClip.getNumberVertices())
      return new EmbeddedFace(toClip); // all vertices contained in frustum

    ArrayList<Vector> intersections = new ArrayList<Vector>();
    for (int i = 0; i < toClip.getNumberVertices(); i++) {
      Vector a = toClip.getVectorAt(i);
      Vector b = toClip.getVectorAt((i + 1) % toClip.getNumberVertices());
      if(verbose){ System.out.println("checking edge " + i); }
      Vector v = findIntersection(a, b, this.right);
      if (v != null && notTooCloseToAnyOf(vertices, v))
        intersections.add(v);

      v = findIntersection(a, b, this.left);
      if (v != null && notTooCloseToAnyOf(vertices, v)) {
        left_intersections++;
        intersections.add(v);
      }
    }
    vertices.addAll(intersections);
    //if(left_intersections == 1) vertices.add(new Vector(0, 0));

    if (vertices.size() < 3)
      return null;
    ConvexHull2D hull = new ConvexHull2D(vertices);
    return new EmbeddedFace(hull.getPoints());
  }

  /*
   * returns true if the coordinates of the given vector v are between the
   * coordinates of v1 and v2 and contained in frustum f
   */
  private boolean isContained(Frustum2D f, Vector v1, Vector v2, Vector v) {
    double x = v.getComponent(0);
    double y = v.getComponent(1);
    double x1 = v1.getComponent(0);
    double y1 = v1.getComponent(1);
    double x2 = v2.getComponent(0);
    double y2 = v2.getComponent(1);

    return (between(x1, x2, x) && between(y1, y2, y) && f.checkInterior(v));
  }

  // true if c is between a and b
  private static boolean between(double a, double b, double c) {
    if ((a - epsilon > c && b - epsilon > c)
        || (a + epsilon < c && b + epsilon < c))
      return false;
    return true;
  }

  /*
   * Returns the intersection of the line formed by points a and b with the ray
   * from the origin through v. ( ( y1 = (w2/w1)(x-s) + t, y2 = (v2/v1)x )
   */
  private Vector findIntersection(Vector a, Vector b, Vector v) {
    Vector w = Vector.subtract(b, a);
    double w1 = w.getComponent(0);
    double w2 = w.getComponent(1);
    double s = a.getComponent(0);
    double t = a.getComponent(1);
    double v1 = v.getComponent(0);
    double v2 = v.getComponent(1);
    
    if((w1 == 0 && v1 == 0) || (w2 / w1) == (v2 / v1)) { // slopes equal => parallel
      return null;
    }

    double x, y;
    if (w1 == 0) {
      x = s;
      y = (v2/v1)*s;
    } else if (v1 == 0) {
      x = 0;
      y = (w2 / w1) * (0 - s) + t;
    } else {
      x = (t - (w2 / w1) * s) / ((v2 / v1) - (w2 / w1));
      y = (v2 / v1) * x;
    }

    Vector intersection = new Vector(x, y);

    if (isContained(this, a, b, intersection))
      return intersection;
    else
      return null;
  }

  private boolean notTooCloseToAnyOf(ArrayList<Vector> vectors, Vector v) {
    for (int i = 0; i < vectors.size(); i++) {
      if (closeTogether(vectors.get(i), v))
        return false;
    }
    return true;
  }

  private boolean closeTogether(Vector v1, Vector v2) {
    return (Math.abs(v1.getComponent(0) - v2.getComponent(0)) < epsilon && Math
        .abs(v1.getComponent(1) - v2.getComponent(1)) < epsilon);
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
  
  public Trail clipTrail(Vector start, Vector end, Face face, Color color) {
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
      
    System.out.println("a = " + a);
    System.out.println("b = " + b);
    System.out.println();
    return new Trail(a, b, face, color);
  }
}

