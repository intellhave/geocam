package development;

import java.awt.Color;
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
  
  public int getNumberVertices() {
    return vectors_.size();
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

  private void findNormal() {
    Vector3D v1 = Vector3D.subtract(vectors_.get(1), vectors_.get(0));
    Vector3D v2 = Vector3D.subtract(vectors_.get(2), vectors_.get(1));
    normal_ = Vector3D.cross(v1, v2);
  }

  public Vector3D getNormal() {
    return normal_;
  }
  
  public Geometry getGeometry() {
    double[][] ifsf_verts = new double[getNumberVertices() + 1][3];
    int[][] ifsf_faces = new int[getNumberVertices()][3];
    ifsf_verts[0] = new double[] { 0, 0, 0 };

    for (int i = 1; i < getNumberVertices() + 1; i++) {
      ifsf_verts[i] = getVectorAt(i - 1).getVectorAsArray();
      if (i - 1 == getNumberVertices() - 1)
        ifsf_faces[i - 1] = new int[] { 0, i, 1 };
      else
        ifsf_faces[i - 1] = new int[] { 0, i, i + 1 };
    }

    IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();
    ifsf.setVertexCount(ifsf_verts.length);
    ifsf.setVertexCoordinates(ifsf_verts);
    ifsf.setFaceCount(ifsf_faces.length);
    ifsf.setFaceIndices(ifsf_faces);
    ifsf.setGenerateEdgesFromFaces(true);
//    Color[] colors = new Color[ifsf_faces.length];
//    for (int i = 0; i < ifsf_faces.length; i++)
//      colors[i] = color;
//    ifsf.setFaceColors(colors);
    ifsf.update();
    return ifsf.getGeometry();
  }

  public boolean sharesEdgeWith(Face face) {
    int count = 0;
    ArrayList<Vector3D> vectors = face.getVectors();
    for(int i = 0; i < face.getNumberVertices(); i++) {
      if(vectors_.contains(face.getVectorAt(i)))
        count++;
    }

    return count > 1;
  }
}