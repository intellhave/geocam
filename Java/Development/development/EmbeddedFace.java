package development;

import java.awt.Color;
import java.util.ArrayList;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.geometry.IndexedLineSetFactory;
import de.jreality.scene.Geometry;

public class EmbeddedFace {
  private ArrayList<Vector> vectors_;
  private Vector normal_;

  // expects vectors in counter-clockwise order
  public EmbeddedFace(ArrayList<Vector> v) {
    vectors_ = v;
    findNormal();
  }

  public EmbeddedFace(Vector... vectors) {
    vectors_ = new ArrayList<Vector>();
    for(int i = 0; i < vectors.length; i++)
      vectors_.add(vectors[i]);
    findNormal();
  }
  
  public EmbeddedFace(EmbeddedFace face) {
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
    return getGeometry(color,0); 
  }
  
  public Geometry getGeometry(Color color, double z) {
    double[][] ifsf_verts = new double[getNumberVertices()][3];
    int[][] ifsf_faces = new int[1][getNumberVertices()];

    for (int i = 0; i < getNumberVertices(); i++) {
      if(getVectorAt(i).getDimension() == 3)
        ifsf_verts[i] = getVectorAt(i).getVectorAsArray();
      else {
        ifsf_verts[i] = new double[]{getVectorAt(i).getComponent(0), getVectorAt(i).getComponent(1),z};
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

  public Geometry getGeometry3D(double zheight) {
    
    int n = getNumberVertices();
    double[][] ilsf_verts = new double[2*n][3];
    int[][] ilsf_edges = new int[3*n][2];

    for (int i=0; i<n; i++){
      ilsf_verts[i] = new double[]{ getVectorAt(i).getComponent(0), getVectorAt(i).getComponent(1), -zheight };
      ilsf_verts[i+n] = new double[]{ getVectorAt(i).getComponent(0), getVectorAt(i).getComponent(1), zheight };
    }
    
    for(int i=0; i<n; i++){
      int j = (i+1)%n;
      ilsf_edges[i] = new int[]{ i, j };
      ilsf_edges[i+n] = new int[]{ i+n, j+n };
      ilsf_edges[i+n+n] = new int[]{ i, i+n };
    }

    IndexedLineSetFactory ilsf = new IndexedLineSetFactory();
    ilsf.setVertexCount(ilsf_verts.length);
    ilsf.setVertexCoordinates(ilsf_verts);
    ilsf.setEdgeCount(ilsf_edges.length);
    ilsf.setEdgeIndices(ilsf_edges);
    ilsf.update();
    return ilsf.getGeometry();
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

  public boolean sharesEdgeWith(EmbeddedFace face) {
    int count = 0;
    for(int i = 0; i < face.getNumberVertices(); i++) {
      if(vectors_.contains(face.getVectorAt(i)))
        count++;
    }

    return count > 1;
  }
  
  public String toString() {
    String result = "";
    for(int i = 0; i < this.getNumberVertices(); i++) {
      result += this.getVectorAt(i) + "\n";
    }
    return result;
  }
}