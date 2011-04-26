package development;

import java.util.List;

import triangulation.Edge;
import triangulation.Face;
import triangulation.Tetra;
import triangulation.Vertex;
import util.Matrix;


/* 
 * Helpful methods for Development and related classes
 */
public class DevelopmentComputations {
  
  public static Vector getBarycentricCoords(Vector point, Face face) {
    // barycentric coordinates
    // point in interior if l1,l2,l3 all in (0,1)
    // point on edge if l1,l2,l3 in [0,1] with at least one 0
    // otherwise outside
    List<Vertex> vertices = face.getLocalVertices();
    Vector v1 = Coord2D.coordAt(vertices.get(0), face);
    Vector v2 = Coord2D.coordAt(vertices.get(1), face);
    Vector v3 = Coord2D.coordAt(vertices.get(2), face);

    double x1 = v1.getComponent(0);
    double y1 = v1.getComponent(1);
    double x2 = v2.getComponent(0);
    double y2 = v2.getComponent(1);
    double x3 = v3.getComponent(0);
    double y3 = v3.getComponent(1);
    double x = point.getComponent(0);
    double y = point.getComponent(1);

    Matrix T = new Matrix(new double[][] { { (x1 - x3), (x2 - x3) },
        { (y1 - y3), (y2 - y3) } });
    double det = T.determinant();

    double l1 = ((y2 - y3) * (x - x3) + (x3 - x2) * (y - y3)) / det;
    double l2 = ((y3 - y2) * (x - x3) + (x1 - x3) * (y - y3)) / det;
    double l3 = 1 - l1 - l2;

    return new Vector(l1, l2, l3);
  }
  
  // TODO barycentric coords 3D not tested
  public static Vector getBarycentricCoords3D(Vector point, Tetra tetra) {
    // barycentric coordinates
    // point in interior if l1,l2,l3,l4 all in (0,1)
    // point on edge if l1,l2,l3,l4 in [0,1] with at least one 0
    // otherwise outside
    List<Vertex> vertices = tetra.getLocalVertices();
    Vector v1 = Coord3D.coordAt(vertices.get(0), tetra);
    Vector v2 = Coord3D.coordAt(vertices.get(1), tetra);
    Vector v3 = Coord3D.coordAt(vertices.get(2), tetra);
    Vector v4 = Coord3D.coordAt(vertices.get(3), tetra);

    double x1 = v1.getComponent(0);
    double y1 = v1.getComponent(1);
    double z1 = v1.getComponent(2);
    
    double x2 = v2.getComponent(0);
    double y2 = v2.getComponent(1);
    double z2 = v2.getComponent(2);
    
    double x3 = v3.getComponent(0);
    double y3 = v3.getComponent(1);
    double z3 = v3.getComponent(2);
    
    double x4 = v4.getComponent(0);
    double y4 = v4.getComponent(1);
    double z4 = v4.getComponent(2);
    
    double x = point.getComponent(0);
    double y = point.getComponent(1);
    double z = point.getComponent(2);

    Matrix T = new Matrix(new double[][] { 
        { (x1 - x4), (x2 - x4), (x3 - x4) },
        { (y1 - y4), (y2 - y4), (y3 - y4) },
        { (z1 - z4), (z2 - z4), (z3 - z4) }
    });
    
    Vector p = new Vector(x-x4, y-y4, z-z4);
    Matrix Ti = null;
    try {
      Ti = T.inverse();
    } catch (Exception e) {
      e.printStackTrace();
    }
    Vector l = Ti.transformVector(p);

    return l;
  }
  
  /*
   * Returns the edge local to both vertices (null if no such edge exists)
   */
  public static Edge findSharedEdge(Vertex v0, Vertex v1) {
    List<Edge> list0 = v0.getLocalEdges();
    List<Edge> list1 = v1.getLocalEdges();

    Edge edge = null;
    for (int j = 0; j < list0.size(); j++) {
      if (list1.contains(list0.get(j))) {
        edge = list0.get(j);
        break;
      }
    }
    return edge;
  }
  
  /*
   * Returns the vertex local to both edges (null if no such edge exists)
   */
  public static Vertex findSharedVertex(Edge e0, Edge e1) {
    List<Vertex> list0 = e0.getLocalVertices();
    List<Vertex> list1 = e1.getLocalVertices();
    
    //will always be at most 4 comparisons
    for(Vertex v : list0){
      for(Vertex w : list1){
         if(v == w){ return v; }
      }
    }
    return null;
  }

  public static Frustum2D getNewFrustum(Frustum2D frustum, Vector vect0,
      Vector vect1, Vector vect3) {
    // build frustum through edge end-points

    // check which is left and which is right
    Vector left = vect0;
    Vector right = vect1;

    Vector l = Vector.subtract(left, vect3);
    Vector r = Vector.subtract(right, vect3);
    Vector l3d = new Vector(l.getComponent(0), l.getComponent(1), 0);
    Vector r3d = new Vector(r.getComponent(0), r.getComponent(1), 0);
    Vector cross = Vector.cross(r3d, l3d);
    if (cross.getComponent(2) < 0) {// made the wrong choice if z-component is
                                    // negative
      left = vect1;
      right = vect0;
    }

    return Frustum2D.intersect(new Frustum2D(left, right), frustum);
  }

  public static Face getNewFace(Face face, Edge edge) {
    // each edge is adjacent to 2 faces; take the one that is not the
    // current face
    Face newFace;
    List<Face> faces = edge.getLocalFaces();
    if (faces.get(0) == face) {
      if (faces.size() > 1)
        newFace = faces.get(1);
      else newFace = null;
    }
    else
      newFace = faces.get(0);
    return newFace;
  }
  
  public static Tetra getNewTetra(Tetra tetra, Face face) {
    // each edge is adjacent to 2 faces; take the one that is not the
    // current face
    Tetra newTetra;
    List<Tetra> tetras = face.getLocalTetras();
    if (tetras.get(0) == tetra) {
      if (tetras.size() > 1)
        newTetra = tetras.get(1);
      else newTetra = null;
    }
    else
      newTetra = tetras.get(0);
    return newTetra;
  }
}
