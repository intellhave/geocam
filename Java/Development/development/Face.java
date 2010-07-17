package development;

import java.awt.Color;
import java.util.ArrayList;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.scene.Geometry;

public class Face {
  private ArrayList<Vector> vectors_;
  private Vector normal_;

  // expects vectors in counter-clockwise order
  public Face(ArrayList<Vector> v) {
    vectors_ = v;
    findNormal();
  }

  public Face(Vector... vectors) {
    vectors_ = new ArrayList<Vector>();
    for(int i = 0; i < vectors.length; i++)
      vectors_.add(vectors[i]);
    findNormal();
  }
  
  public Face(Face face) {
    vectors_ = new ArrayList<Vector>();
    ArrayList<Vector> oldVectors = face.getVectors();
    for(int i = 0; i < oldVectors.size(); i++) {
      vectors_.add(new Vector(oldVectors.get(i)));
    }
    findNormal();
  }

  private void findNormal() {
    Vector v1 = Vector.subtract(vectors_.get(1), vectors_.get(0));
    Vector v2 = Vector.subtract(vectors_.get(2), vectors_.get(1));
    normal_ = Vector.cross(v1, v2);
  }

  public int getNumberVertices() {
    return vectors_.size();
  }
  
  public ArrayList<Vector> getVectors() {
    ArrayList<Vector> vectors = new ArrayList<Vector>();
    for(int i = 0; i < vectors_.size(); i++) {
      vectors.add(vectors_.get(i));
    }
    return vectors;
  }
  
  public ArrayList<Vector> getVectorsAsArray() {
    return vectors_;
  }

  public Vector getVectorAt(int index) {
    return vectors_.get(index);
  }

  public Vector getNormal() {
    return normal_;
  }
  
  public Geometry getGeometry(Color color) {
    double[][] ifsf_verts = new double[getNumberVertices()][3];
    int[][] ifsf_faces = new int[1][getNumberVertices()];

    for (int i = 0; i < getNumberVertices(); i++) {
      if(getVectorAt(i).getDimension() == 3)
        ifsf_verts[i] = getVectorAt(i).getVectorAsArray();
      else {
        ifsf_verts[i] = new double[]{getVectorAt(i).getComponent(0), getVectorAt(i).getComponent(1), 0};
      }
      ifsf_faces[0][i] = i;
    }
    
    Color[] colors = new Color[ifsf_faces.length];
    for(int i = 0; i < ifsf_faces.length; i++) {
      colors[i] = color;
    }

    IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();
    ifsf.setVertexCount(ifsf_verts.length);
    ifsf.setVertexCoordinates(ifsf_verts);
    ifsf.setFaceCount(ifsf_faces.length);
    ifsf.setFaceIndices(ifsf_faces);
    ifsf.setGenerateEdgesFromFaces(true);
    ifsf.setFaceColors(colors);
    ifsf.update();
    return ifsf.getGeometry();
  }
  
  
  public void addVertex(Vector v) {
    vectors_.add(v);
  }
  
  public int indexOf(Vector v) {
    return vectors_.indexOf(v);
  }
  
  public boolean hasVertex(Vector v) {
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