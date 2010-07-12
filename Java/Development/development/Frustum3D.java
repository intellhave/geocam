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

  public int getNumberVectors() {
    return vectors.size();
  }

  public ArrayList<Vector3D> getVectors() {
    return vectors;
  }

  public ArrayList<Vector3D> getNormals() {
    return normals;
  }

  // checkInterior methods assume normals are normalized
  public boolean checkInterior(Point point) throws Exception {
    for (int i = 0; i < normals.size(); i++) {
      if (Vector3D.dot(normals.get(i), point) < -epsilon)
        return false;
    }
    return true;
  }

  public boolean checkInterior(Vector3D vector) {
    for (int i = 0; i < normals.size(); i++) {
      if (Vector3D.dot(normals.get(i), vector) < -epsilon)
        return false;
    }
    return true;
  }

  public boolean contains(Frustum3D frustum) {
    ArrayList<Vector3D> vectors = frustum.getVectors();
    for (int i = 0; i < vectors.size(); i++) {
      if (!this.checkInterior(vectors.get(i)))
        return false;
    }
    return true;
  }

  public Vector getVectorAt(int index) {
    return vectors.get(index);
  }

  public static Frustum3D intersect(Frustum3D frustum1, Frustum3D frustum2) {

    if (frustum1.contains(frustum2))
      return frustum2;
    if (frustum2.contains(frustum1))
      return frustum1;

    ArrayList<Vector3D> newVectors = new ArrayList<Vector3D>(); 
    ArrayList<Vector3D> normals1 = frustum1.getNormals();
    ArrayList<Vector3D> normals2 = frustum2.getNormals();
    ArrayList<Vector3D> vectors1 = frustum1.getVectors();
    ArrayList<Vector3D> vectors2 = frustum2.getVectors();

    // walk around vectors in frustum1
    for (int i = 0; i < vectors1.size(); i++) {

      // check whether current vector in frustum 1 is contained in frustum 2
      if (frustum2.checkInterior(vectors1.get(i)))
        newVectors.add(vectors1.get(i));

      Vector3D norm = normals1.get(i);
      ArrayList<Vector3D> intersections = new ArrayList<Vector3D>();

      int lastIntersectionIndex = 0; 
      // will be index of last sector from
      // frustum2 that intersects the 
      // current sector of frustum1
      
      for (int j = 0; j < normals2.size(); j++) {
        boolean intersects;
        Vector3D intersection = GeometryOperations.intersectPlanes(norm,
            normals2.get(j));

        if (intersection.length() < epsilon)
          intersects = false;
        else
          intersects = checkInIntersection(intersection, frustum1, frustum2);

        if (intersects) {
          intersection.normalize();
          if (!closeTogether(intersection, vectors1.get(i))
              && !closeTogether(intersection, vectors1.get((i + 1)
                  % vectors1.size()))
              && !closeTogether(intersection, vectors2.get(j))
              && !closeTogether(intersection, vectors2.get((j + 1)
                  % vectors2.size()))) {
            if (intersections.isEmpty()) {
              intersections.add(intersection);
              lastIntersectionIndex = j;
            } else {
              Vector3D difference = Vector3D.subtract(vectors1.get((i + 1)
                  % vectors1.size()), vectors1.get(i % vectors1.size()));
              double dot = Vector3D.dot(difference, normals2.get(j));
              if (dot < 0) {
                intersections.add(intersection);
                lastIntersectionIndex = j;
              }else if (dot > 0)
                intersections.add(0, intersection);
            }
          }
        }
      }
      newVectors.addAll(intersections);
      
      // walk around frustum2 until a vector outside frustum1 is encountered,
      if (!intersections.isEmpty()) {
        int k = (lastIntersectionIndex + 1) % vectors2.size();
        while (frustum1.checkInterior(vectors2.get(k))
            && !newVectors.contains(vectors2.get(k))) {
          newVectors.add(vectors2.get(k));
          k = (k + 1) % vectors2.size();
        }
      }
    }

    if (newVectors.size() < 3) // degenerate case
      return null;
    
    return new Frustum3D(newVectors);
  }

  private static boolean checkInIntersection(Vector3D vector, Frustum3D f1,
      Frustum3D f2) {
    if (f1.checkInterior(vector) && f2.checkInterior(vector))
      return true;
    else {
      vector.scale(-1);
      if (f1.checkInterior(vector) && f2.checkInterior(vector))
        return true;
    }
    return false;
  }

  private static boolean closeTogether(Vector3D v1, Vector3D v2) {
    Vector difference = Vector.subtract(v1, v2);
    double cos = (v1.lengthSquared() + v2.lengthSquared() - difference
        .lengthSquared())
        / (2 * v1.length() * v2.length());
    return Math.abs(cos) > 1 - epsilon;
  }
}
