package development;

import java.util.ArrayList;

public class Frustum3D {

  private static final double epsilon = 10 ^ (-6);
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
    for(int i = 0; i < vectors.size(); i++) {
      if(!this.checkInterior(vectors.get(i))) return i;
    }
    return -1;
  }
  
  public Vector getVectorAt(int index) {
    return vectors.get(index);
  }

  public static Frustum3D intersect(Frustum3D frustum1, Frustum3D frustum2)
      throws Exception {
    
    if(frustum1.findOutsideVectorIndex(frustum2) == -1) return frustum2;
    int outsideIndex = frustum2.findOutsideVectorIndex(frustum1);
    if (outsideIndex == -1) return frustum1;    
    
    ArrayList<Vector3D> newVectors = new ArrayList<Vector3D>();
    ArrayList<Vector3D> normals1 = frustum1.getNormals();
    ArrayList<Vector3D> normals2 = frustum2.getNormals();

    ArrayList<Vector3D> vectors1 = frustum1.getVectors();

    for (int i = 0; i < vectors1.size() - 1; i++) {
      if (frustum2.checkInterior(vectors1.get(i)))
        newVectors.add(vectors1.get(i));

      Vector3D norm = normals1.get(i);

      ArrayList<Vector3D> intersections = new ArrayList<Vector3D>();
      int count = 0;
      int j = outsideIndex;
      while(count < normals2.size()) {
        Vector3D intersection = GeometryOperations.intersectPlanes(norm,
            normals2.get(j));
        // check if intersection or its negation is contained in both frustums
        if (frustum1.checkInterior(intersection)
            && frustum2.checkInterior(intersection))
          intersections.add(intersection);
        else {
          intersection.scale(-1);
          if (frustum1.checkInterior(intersection)
              && frustum2.checkInterior(intersection))
            intersections.add(intersection);
        }
        if(intersections.size() == 2) break;
        // should never have more than 2
        j = (j+1)%normals2.size();
        count++;
      }
      if(intersections.size() == 1) newVectors.add(intersections.get(0));
      else if(intersections.size() == 2) {
        newVectors.add(intersections.get(1));
        newVectors.add(intersections.get(0));
      }
    }
    if(newVectors.size() == 0) return null;
    return new Frustum3D(newVectors);
  }

}
