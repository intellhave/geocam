package development;

import java.util.ArrayList;

public class Frustum3D {

  private static final double epsilon = Math.pow(10, -6);
  ArrayList<Vector3D> vectors = new ArrayList<Vector3D>();
  ArrayList<Vector3D> normals = new ArrayList<Vector3D>();

  // normals(i) is normal to plane formed by vectors{i) and vectors{i+1)

  // vectors should be listed in counter-clockwise order
  public Frustum3D(Vector3D... input) {
    for (int i = 0; i < input.length; i++) {
      vectors.add(input[i]);
    }
    findNormals();
  }

  public Frustum3D(ArrayList<Vector3D> input) {
    vectors = input;
    findNormals();
  }

  public int getNumberVectors() {
    return vectors.size();
  }

  private void findNormals() {
    for (int i = 0; i < vectors.size() - 1; i++) {
      Vector3D v1 = vectors.get(i);
      Vector3D v2 = vectors.get(i + 1);
      Vector3D norm = Vector3D.cross(v1, v2);
      norm.normalize();
      normals.add(norm);
    }
    Vector3D v1 = vectors.get(vectors.size() - 1);
    Vector3D v2 = vectors.get(0);
    Vector3D norm = Vector3D.cross(v1, v2);
    norm.normalize();
    normals.add(norm);
  }

  public ArrayList<Vector3D> getVectors() {
    return vectors;
  }

  public ArrayList<Vector3D> getNormals() {
    return normals;
  }

  // assumes normals are normalized
  public boolean checkInterior(Point point) throws Exception {
    for (int i = 0; i < normals.size(); i++) {
      if (Vector.dot(normals.get(i), point) < -epsilon)
        return false;
    }
    return true;
  }

  public boolean checkInterior(Vector vector) throws Exception {
    for (int i = 0; i < normals.size(); i++) {
      if (Vector.dot(normals.get(i), vector) < -epsilon)
        return false;
    }
    return true;
  }

  public int findOutsideVectorIndex(Frustum3D frustum) throws Exception {
    ArrayList<Vector3D> vectors = frustum.getVectors();
    for (int i = 0; i < vectors.size(); i++) {
      if (!this.checkInterior(vectors.get(i)))
        return i;
    }
    return -1;
  }

  public Vector getVectorAt(int index) {
    return vectors.get(index);
  }

  public static Frustum3D intersect(Frustum3D frustum1, Frustum3D frustum2)
      throws Exception {

    System.out.println();
    System.out.println();
    if (frustum1.findOutsideVectorIndex(frustum2) == -1)
      return frustum2;
    int outsideIndex = frustum2.findOutsideVectorIndex(frustum1);
    if (outsideIndex == -1)
      return frustum1;

    System.out.println("vertex index " + outsideIndex
        + " in frustum2 is outside frustum 1");

    ArrayList<Vector3D> newVectors = new ArrayList<Vector3D>();
    ArrayList<Vector3D> normals1 = frustum1.getNormals();
    ArrayList<Vector3D> normals2 = frustum2.getNormals();

    ArrayList<Vector3D> vectors1 = frustum1.getVectors();
    ArrayList<Vector3D> vectors2 = frustum2.getVectors();

    for (int i = 0; i < vectors1.size(); i++) {
      if (frustum2.checkInterior(vectors1.get(i))) {
        newVectors.add(vectors1.get(i));
        System.out.println("adding vector from frustum1");
      } else
        System.out.println("vector " + i + " from 1 is not contained in 2");

      Vector3D norm = normals1.get(i);

      ArrayList<Vector3D> intersections = new ArrayList<Vector3D>();
      int lastIntersectionIndex = 0;
      for (int j = 0; j < normals2.size(); j++) {
        Vector3D intersection = GeometryOperations.intersectPlanes(norm,
            normals2.get(j));
        boolean intersects = false;
        // check if intersection or its negation is contained in both frustums
        if (frustum1.checkInterior(intersection)
            && frustum2.checkInterior(intersection)) {
          intersects = true;
          System.out.println("found intersection of " + i + " from 1 and " + j
              + " from 2");
        } else {
          intersection.scale(-1);
          if (frustum1.checkInterior(intersection)
              && frustum2.checkInterior(intersection)) {
            // intersections.add(intersection);
            intersects = true;
            System.out.println("found negation of intersection of " + i
                + " from 1 and " + j + " from 2");
          }
        }
        System.out.println("length = " + intersection.length());
        if(intersection.length() < epsilon) intersects = false;

        if (intersects) {
          intersection.normalize();
          if (!closeTogether(intersection, vectors1.get(i))
              && !closeTogether(intersection, vectors1.get((i + 1)
                  % vectors1.size()))
              && !closeTogether(intersection, vectors2.get(j))
              && !closeTogether(intersection, vectors2.get((j + 1)
                  % vectors2.size()))) {
            if (intersections.size() == 0) {
              System.out.println("adding first");
              intersections.add(intersection);
              lastIntersectionIndex = j;
            } else {
              Vector difference = Vector.subtract(vectors1.get((i + 1)
                  % vectors1.size()), vectors1.get(i % vectors1.size()));
              double dot = Vector.dot(difference, normals2.get(j));
              if (dot < 0) {
                intersections.add(intersection);
                lastIntersectionIndex = j;
                System.out.println("adding at end");
              } else if (dot > 0) {
                intersections.add(0, intersection);
                System.out.println("adding at beginning");
              }
              else System.out.println("not adding");
            }
          }
        }
      }
      newVectors.addAll(intersections);
      System.out.println("found " + intersections.size() + " intersections");
      if (!intersections.isEmpty()) {
        // check next vertex in 2
        int k = (lastIntersectionIndex + 1) % vectors2.size();
        while (frustum1.checkInterior(vectors2.get(k))
            && !newVectors.contains(vectors2.get(k))) {
          System.out.println("adding vector " + k + " from 2");
          newVectors.add(vectors2.get(k));
          k = (k + 1) % vectors2.size();
        }
      }
    }

    System.out.println("found " + newVectors.size() + " vertices");
    if (newVectors.size() < 3)
      return null;
    return new Frustum3D(newVectors);
  }

  public static boolean closeTogether(Vector3D v1, Vector3D v2)
      throws Exception {
    Vector difference = Vector.subtract(v1, v2);
    double cos = (v1.lengthSquared() + v2.lengthSquared() - difference
        .lengthSquared())
        / (2 * v1.length() * v2.length());
    return Math.abs(cos) > 1 - epsilon;
  }
}
