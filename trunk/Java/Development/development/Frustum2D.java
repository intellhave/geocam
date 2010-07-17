package development;

import java.util.ArrayList;

public class Frustum2D {

  private static final double epsilon = Math.pow(10, -6);
  private Vector left, right;
  private Vector leftNormal, rightNormal;

  public Frustum2D(Vector l, Vector r) {
    left = l;
    right = r;
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

    if (Vector.dot(leftNormal, vector) < -epsilon)
      return false;
    if (Vector.dot(rightNormal, vector) < -epsilon)
      return false;
    return true;
  }

  public static Frustum2D intersect(Frustum2D frustum1, Frustum2D frustum2) {
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

  public Face clipFace(Face toClip) {
    ArrayList<Vector> vertices = new ArrayList<Vector>();
    for (int i = 0; i < toClip.getNumberVertices(); i++) {
      System.out.println("checking interior: " + toClip.getVectorAt(i));
      if (this.checkInterior(toClip.getVectorAt(i))) {
        vertices.add(new Vector(toClip.getVectorAt(i)));
        System.out.println("true");
      } else
        System.out.println("false");
    }
    if (vertices.size() == toClip.getNumberVertices())
      return new Face(toClip); // all vertices contained in frustum

    ArrayList<Vector> intersections = new ArrayList<Vector>();
    for (int i = 0; i < toClip.getNumberVertices(); i++) {
      Vector a = toClip.getVectorAt(i);
      Vector b = toClip.getVectorAt((i + 1) % toClip.getNumberVertices());
      System.out.println("checking edge " + i);
      Vector v = findIntersection(a, b, this.right);
      if (v != null && notTooCloseToAnyOf(vertices, v))
        intersections.add(v);
      else
        System.out.println("doesn't contain right");
      v = findIntersection(a, b, this.left);
      if (v != null && notTooCloseToAnyOf(vertices, v))
        intersections.add(v);
      else
        System.out.println("doesn't contain left");
    }
    vertices.addAll(intersections);
    System.out.println("found these intersections");
    for (int i = 0; i < intersections.size(); i++) {
      System.out.println(intersections.get(i));
    }
    System.out.println();
    if (vertices.isEmpty())
      return null;
    ConvexHull2D hull = new ConvexHull2D(vertices);
    return new Face(hull.getPoints());
  }

  /*
   * returns true if the coordinates of the given vector v are between the
   * coordinates of v1 and v2
   */
  private boolean isContained(Vector v1, Vector v2, Vector v) {
    double x = v.getComponent(0);
    double y = v.getComponent(1);
    double x1 = v1.getComponent(0);
    double y1 = v1.getComponent(1);
    double x2 = v2.getComponent(0);
    double y2 = v2.getComponent(1);

    return (between(x1, x2, x) && between(y1, y2, y));
  }

  // true if c is between a and b
  private boolean between(double a, double b, double c) {
    if ((a - epsilon > c && b - epsilon > c)
        || (a + epsilon < c && b + epsilon < c))
      return false;
    return true;
  }

  /*
   * Returns the intersection of the line formed by points a and b with the line
   * through v and the origin. ( y1 = (w2/w1)(x-s) + t, y2 = (v2/v1)x )
   */
  private Vector findIntersection(Vector a, Vector b, Vector v) {
    Vector w = Vector.subtract(b, a);
    System.out.println("v = " + v);
    System.out.println("w = " + w);
    double w1 = w.getComponent(0);
    double w2 = w.getComponent(1);
    double s = a.getComponent(0);
    double t = a.getComponent(1);
    double v1 = v.getComponent(0);
    double v2 = v.getComponent(1);

    if ((w2 / w1) == (v2 / v1)) { // slopes equal => parallel
      return null;
    }

    double x, y;
    if (w1 == 0) {
      x = 0;
      y = 0;
    } else if (v1 == 0) {
      x = 0;
      y = (w2 / w1) * (0 - s) + t;
    } else {
      x = (t - (w2 / w1) * s) / ((v2 / v1) - (w2 / w1));
      y = (v2 / v1) * x;
    }

    Vector intersection = new Vector(x, y);
    System.out.println("found intersection at " + intersection
        + " -- on edge? ");
    if (isContained(a, b, intersection)) {
      System.out.println("yes");
      System.out.println();
      return intersection;
    } else {
      System.out.println("no");
      System.out.println();
      return null;
    }
  }

  private boolean notTooCloseToAnyOf(ArrayList<Vector> vectors, Vector v) {
    for (int i = 0; i < vectors.size(); i++) {
      if (closeTogether(vectors.get(i), v)) {
        System.out.println("too close to " + vectors.get(i));
        return false;
      }
    }
    return true;
  }

  private boolean closeTogether(Vector v1, Vector v2) {
    return (Math.abs(v1.getComponent(0) - v2.getComponent(0)) < epsilon && Math
        .abs(v1.getComponent(1) - v2.getComponent(1)) < epsilon);
  }
}
