package development;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import util.Vector;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.scene.Geometry;

/*
 * EmbeddedFace
 * 
 * Author: K. Kiviat
 * 
 * Overview: A list of points in the plane in an order
 * 
 * Possible upgrade:
 *    * do we need normal?
 * 
 */


public class EmbeddedFace {
  private ArrayList<Vector> vertices;
  private ArrayList<Vector> textureCoordinates;
  private Vector normal_;

  // expects vectors in counter-clockwise order
  
  public EmbeddedFace(ArrayList<Vector> points) {
    initialize(points);
  }

  public EmbeddedFace(Vector... vectors) {
    vertices = new ArrayList<Vector>();
    for(int i = 0; i < vectors.length; i++)
      vertices.add(vectors[i]);
    initialize(vertices);
  }
  
  //TODO: Make this method return the correct texCoords? (Is this even possible???)
  private void initialize(ArrayList<Vector> points){
    vertices = points;
    textureCoordinates = new ArrayList<Vector>();
    for( Vector v : vertices ){
      textureCoordinates.add( new Vector(v) );
    }
    findNormal();
  }
  
  public EmbeddedFace(EmbeddedFace face) {
    vertices = new ArrayList<Vector>();
    ArrayList<Vector> oldVectors = face.getVectors();
    for(int i = 0; i < oldVectors.size(); i++) {
      vertices.add(new Vector(oldVectors.get(i)));
    }
    findNormal();
    
    textureCoordinates = new ArrayList<Vector>();
    for( Vector v : face.textureCoordinates ){
      this.textureCoordinates.add( new Vector(v) );
    }
  }

  public EmbeddedFace(ArrayList<Vector> points, ArrayList<Vector> texCoords){
    vertices = new ArrayList<Vector>();
    textureCoordinates = new ArrayList<Vector>();
    
    for(Vector v : points){
      vertices.add(new Vector(v));
    }
    
    for(Vector tc : texCoords){
      textureCoordinates.add(new Vector(tc));
    }
    findNormal();
  }
  
  private void findNormal() {
    Vector v1 = Vector.subtract(vertices.get(1), vertices.get(0));
    Vector v2 = Vector.subtract(vertices.get(2), vertices.get(0));
    normal_ = Vector.cross(v1, v2);
    if (normal_ == null) {
      normal_ = new Vector(0.0, 0.0, 1.0);
    }
  }

  public int getNumberVertices() {
    return vertices.size();
  }
  
  public ArrayList<Vector> getVectors() {
    ArrayList<Vector> vectors = new ArrayList<Vector>();
    for(int i = 0; i < vertices.size(); i++) {
      vectors.add(vertices.get(i));
    }
    return vectors;
  }
  
  // TODO: Does creating copies of the texture coordinates cause problems???
  public ArrayList<Vector> getTexCoords(){
    ArrayList<Vector> coords = new ArrayList<Vector>();
    for(Vector v : textureCoordinates){
      coords.add(new Vector(v));
    }
    return coords;
  }
  
  public double[][] getVectorsAsArray() {
    double[][] array = new double[vertices.size()][];
    for(int i = 0; i < vertices.size(); i++) {
      array[i] = vertices.get(i).getVectorAsArray();
    }
    return array;
  }

  public Vector getVectorAt(int index) {
    return vertices.get(index);
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

  public Geometry getGeometry3D(Color color, double zheight) {
    
    int n = getNumberVertices();
    double[][] ifsf_verts = new double[2*n][3];
    int[][] ifsf_edges = new int[3*n][2];
    int[][] ifsf_faces = new int[2][n];
    
    for (int i=0; i<n; i++){
      ifsf_verts[i] = new double[]{ getVectorAt(i).getComponent(0), getVectorAt(i).getComponent(1), -zheight };
      ifsf_verts[i+n] = new double[]{ getVectorAt(i).getComponent(0), getVectorAt(i).getComponent(1), zheight };
    }
    
    for(int i=0; i<n; i++){
      int j = (i+1)%n;
      ifsf_edges[i] = new int[]{ i, j };
      ifsf_edges[i+n] = new int[]{ i+n, j+n };
      ifsf_edges[i+n+n] = new int[]{ i, i+n };
    }
    
    for(int i=0; i<n; i++){
      ifsf_faces[0][i] = i;
      ifsf_faces[1][i] = n+(n-1)-i;
    }
    
    Color[] colors = new Color[ifsf_faces.length];
    for(int i = 0; i < ifsf_faces.length; i++) {
      colors[i] = color;
    }
    
    IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();
    ifsf.setVertexCount(ifsf_verts.length);
    ifsf.setVertexCoordinates(ifsf_verts);
    ifsf.setEdgeCount(ifsf_edges.length);
    ifsf.setEdgeIndices(ifsf_edges);
    ifsf.setFaceCount(ifsf_faces.length);
    ifsf.setFaceIndices(ifsf_faces);
    ifsf.setFaceColors(colors);
    ifsf.update();
    return ifsf.getGeometry();
  }
  
  
  public void addVertex(Vector v) {
    vertices.add(v);
  }
  
  public int indexOf(Vector v) {
    return vertices.indexOf(v);
  }
  
  public boolean hasVertex(Vector v) {
    for(int i =0 ; i < vertices.size(); i++) {
      if(vertices.get(i).equals(v)) return true;
    }
    return false;
  }

  public boolean sharesEdgeWith(EmbeddedFace face) {
    int count = 0;
    for(int i = 0; i < face.getNumberVertices(); i++) {
      if(vertices.contains(face.getVectorAt(i)))
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

  public boolean contains(Vector point) {
    List<Vector> vertices = this.getVectors();
    for (int i = 0; i < vertices.size(); i++) {
      Vector v1 = vertices.get(i);
      Vector v2 = vertices.get((i + 1) % vertices.size());
      Frustum2D frustum = new Frustum2D(v1, v2);
      if (frustum.checkInterior(point))
        return true;
    }
    return false;
  }

  public double[][] getTexCoordsAsArray() {
    int n = textureCoordinates.size();
    int m = textureCoordinates.get(0).getDimension();
    double[][] array = new double[n][m];
    
    for(int ii = 0; ii < n; ii++){
      for(int jj = 0; jj < m; jj++){
        array[ii][jj] = textureCoordinates.get(ii).getComponent(jj);
      }
    }
    return array;
  }
}