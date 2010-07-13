package development;
import java.util.ArrayList;
import java.util.Arrays;


public class ConvexHull3D {
  private static final double epsilon = Math.pow(10, -6);
  private ArrayList<Face> faces = new ArrayList<Face>();

  public ConvexHull3D(ArrayList<Vector3D> unsortedVectors) {
    findHull(unsortedVectors);
  }

  private void findHull(ArrayList<Vector3D> unsorted) {
    int i = 4;
    while (coplanar(unsorted.get(0), unsorted.get(1), unsorted.get(2), unsorted
        .get(i))) {
      i++;
    }
    Face f1 = new Face(unsorted.get(0), unsorted.get(1), unsorted.get(2));
    double dot = Vector3D.dot(f1.getNormal(), Vector3D.subtract(
        unsorted.get(i), unsorted.get(0)));
    if (dot > 0) { // change order
      f1 = new Face(unsorted.get(0), unsorted.get(2), unsorted.get(1));
    }
    // complete initial tetra
    Face f2 = new Face(f1.getVectorAt(1), f1.getVectorAt(0), unsorted.get(i));
    Face f3 = new Face(f1.getVectorAt(2), f1.getVectorAt(1), unsorted.get(i));
    Face f4 = new Face(f1.getVectorAt(2), f1.getVectorAt(0), unsorted.get(i));

    unsorted.remove(0);
    unsorted.remove(1);
    unsorted.remove(2);
    unsorted.remove(i);

    f1.addAdjacency(f2, 0);
    f1.addAdjacency(f3, 1);
    f1.addAdjacency(f4, 2);

    f2.addAdjacency(f1, 0);
    f2.addAdjacency(f3, 2);
    f2.addAdjacency(f4, 1);

    f3.addAdjacency(f1, 0);
    f3.addAdjacency(f4, 1);
    f3.addAdjacency(f2, 2);

    f4.addAdjacency(f1, 0);
    f4.addAdjacency(f3, 1);
    f4.addAdjacency(f2, 2);

    faces.add(f1);
    faces.add(f2);
    faces.add(f3);
    faces.add(f4);

    addPoint(unsorted.get(0));
  }

  private void addPoint(Vector3D v) {
    for (int i = 0; i < faces.size(); i++) {
      double dot = Vector3D.dot(faces.get(i).getNormal(), Vector3D.subtract(v, faces
          .get(i).getVectorAt(0)));
      if (Math.abs(dot) < epsilon) { // call it planar
        ArrayList<Vector3D> vectors = faces.get(i).getVectors();
        vectors.add(v);
      }
    }
  }

  private boolean coplanar(Vector3D v1, Vector3D v2, Vector3D v3, Vector3D v4) {
    Vector3D cross1 = Vector3D.cross(v1, v2);
    Vector3D cross2 = Vector3D.cross(v3, v4);
    Vector3D result = Vector3D.cross(cross1, cross2);
    return result.isZero();
  }

  private class Face {
    private Vector3D[] vectors_;
    private Vector3D normal_;
    private Face[] adjacencies_;

    // expects vectors in counter-clockwise order
    public Face(ArrayList<Vector3D> v) {
      vectors_ = (Vector3D[]) v.toArray();
      findNormal();
    }

    public Face(Vector3D... vectors) {
      vectors_ = vectors;
      findNormal();
    }
    
    public ArrayList<Vector3D> getVectors() {
      ArrayList<Vector3D> vectors = new ArrayList<Vector3D>();
      for(int i = 0; i < vectors_.length; i++) {
        vectors.add(vectors_[i]);
      }
      return vectors;
    }
    
    public Vector3D[] getVectorsAsArray() {
      return Arrays.copyOf(vectors_, vectors_.length);
    }

    public Vector3D getVectorAt(int index) {
      return vectors_[index];
    }

    public void addAdjacency(Face face, int i) {
      adjacencies_[i] = face;
    }

    private void findNormal() {
      Vector3D v1 = Vector3D.subtract(vectors_[1], vectors_[0]);
      Vector3D v2 = Vector3D.subtract(vectors_[2], vectors_[1]);
      normal_ = Vector3D.cross(v1, v2);
    }

    public Vector3D getNormal() {
      return normal_;
    }
  }
}
