package development;

import java.util.ArrayList;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.scene.Geometry;

public class Face {
  private ArrayList<Vector3D> vectors_;
  private Vector3D normal_;

  // expects vectors in counter-clockwise order
  public Face(ArrayList<Vector3D> v) {
    vectors_ = v;
    findNormal();
  }

  public Face(Vector3D... vectors) {
    vectors_ = new ArrayList<Vector3D>();
    for(int i = 0; i < vectors.length; i++)
      vectors_.add(vectors[i]);
    findNormal();
  }
  
  public Face(Face face) {
    vectors_ = new ArrayList<Vector3D>();
    ArrayList<Vector3D> oldVectors = face.getVectors();
    for(int i = 0; i < oldVectors.size(); i++) {
      vectors_.add(new Vector3D(oldVectors.get(i)));
    }
    findNormal();
  }

  private void findNormal() {
    Vector3D v1 = Vector3D.subtract(vectors_.get(1), vectors_.get(0));
    Vector3D v2 = Vector3D.subtract(vectors_.get(2), vectors_.get(1));
    normal_ = Vector3D.cross(v1, v2);
  }

  public int getNumberVertices() {
    return vectors_.size();
  }
  
  public ArrayList<Vector3D> getVectors() {
    ArrayList<Vector3D> vectors = new ArrayList<Vector3D>();
    for(int i = 0; i < vectors_.size(); i++) {
      vectors.add(vectors_.get(i));
    }
    return vectors;
  }
  
  public ArrayList<Vector3D> getVectorsAsArray() {
    return vectors_;
  }

  public Vector3D getVectorAt(int index) {
    return vectors_.get(index);
  }

  public Vector3D getNormal() {
    return normal_;
  }
  
  public Geometry getGeometry() {
    double[][] ifsf_verts = new double[getNumberVertices()][3];
    int[][] ifsf_faces = new int[1][getNumberVertices()];

    for (int i = 0; i < getNumberVertices(); i++) {
      ifsf_verts[i] = getVectorAt(i).getVectorAsArray();
      ifsf_faces[0][i] = i;
    }

    IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();
    ifsf.setVertexCount(ifsf_verts.length);
    ifsf.setVertexCoordinates(ifsf_verts);
    ifsf.setFaceCount(ifsf_faces.length);
    ifsf.setFaceIndices(ifsf_faces);
    ifsf.setGenerateEdgesFromFaces(true);
    ifsf.update();
    return ifsf.getGeometry();
  }
  
  
  public void addVertex(Vector3D v) {
    vectors_.add(v);
  }
  
  public int indexOf(Vector3D v) {
    return vectors_.indexOf(v);
  }
  
  public boolean contains(Vector3D v) {
    for(int i =0 ; i < vectors_.size(); i++) {
      if(vectors_.get(i).equals(v)) return true;
    }
    return false;
  }

  public boolean sharesEdgeWith(Face face) {
    int count = 0;
    for(int i = 0; i < face.getNumberVertices(); i++) {
      if(vectors_.contains(face.getVectorAt(i)))
        count++;
    }

    return count > 1;
  }
}