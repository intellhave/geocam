package development;

import java.util.ArrayList;
import java.util.Arrays;

public class Face {
  private Vector3D[] vectors_;
  private Vector3D normal_;

  // expects vectors in counter-clockwise order
  public Face(ArrayList<Vector3D> v) {
    vectors_ = (Vector3D[]) v.toArray();
    findNormal();
  }

  public Face(Vector3D... vectors) {
    vectors_ = vectors;
    findNormal();
  }
  
  public int getNumberVertices() {
    return vectors_.length;
  }
  
  public boolean contains(Vector3D v) {
    for(int i =0 ; i < vectors_.length; i++) {
      if(vectors_[i].equals(v)) return true;
    }
    return false;
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

  private void findNormal() {
    Vector3D v1 = Vector3D.subtract(vectors_[1], vectors_[0]);
    Vector3D v2 = Vector3D.subtract(vectors_[2], vectors_[1]);
    normal_ = Vector3D.cross(v1, v2);
  }

  public Vector3D getNormal() {
    return normal_;
  }
}