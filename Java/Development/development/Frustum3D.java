package development;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import util.Matrix;
import triangulation.Edge;
import triangulation.Face;
import triangulation.Tetra;
import triangulation.Vertex;

public class Frustum3D {

  private static final double epsilon = Math.pow(10, -6);
  ArrayList<Vector> rays = new ArrayList<Vector>();
  ArrayList<Vector> normals = new ArrayList<Vector>();

  // normals(i) is normal to plane formed by vectors(i) and vectors(i+1)

  // vectors should be listed in counter-clockwise order
  public Frustum3D(Vector... input) {
    for (int i = 0; i < input.length; i++) {
      rays.add(input[i]);
    }
    findNormals();
  }

  public Frustum3D(ArrayList<Vector> input) {
    rays = input;
    findNormals();
  }

  private void findNormals() {
    for (int i = 0; i < rays.size() - 1; i++) {
      Vector v1 = rays.get(i);
      Vector v2 = rays.get(i + 1);
      Vector norm = Vector.cross(v1, v2);
      norm.normalize();
      normals.add(norm);
    }
    Vector v1 = rays.get(rays.size() - 1);
    Vector v2 = rays.get(0);
    Vector norm = Vector.cross(v1, v2);
    norm.normalize();
    normals.add(norm);
  }

  public int getNumberRays() {
    return rays.size();
  }

  public ArrayList<Vector> getVectors() {
    return rays;
  }

  public ArrayList<Vector> getNormals() {
    return normals;
  }

  // checkInterior assumes normals are normalized
  public boolean checkInterior(Vector vector) {
    for (int i = 0; i < normals.size(); i++) {
      if (Vector.dot(normals.get(i), vector) < -epsilon)
        return false;
    }
    return true;
  }

  public boolean contains(Frustum3D frustum) {
    ArrayList<Vector> vectors = frustum.getVectors();
    for (int i = 0; i < vectors.size(); i++) {
      if (!this.checkInterior(vectors.get(i)))
        return false;
    }
    return true;
  }

  public Vector getVectorAt(int index) {
    return rays.get(index);
  }

  public static Frustum3D intersect(Frustum3D frustum1, Frustum3D frustum2) {

    if (frustum1.contains(frustum2))
      return frustum2;
    if (frustum2.contains(frustum1))
      return frustum1;

    ArrayList<Vector> newVectors = new ArrayList<Vector>();
    ArrayList<Vector> normals1 = frustum1.getNormals();
    ArrayList<Vector> normals2 = frustum2.getNormals();
    ArrayList<Vector> vectors1 = frustum1.getVectors();
    ArrayList<Vector> vectors2 = frustum2.getVectors();

    // walk around vectors in frustum1
    for (int i = 0; i < vectors1.size(); i++) {

      // check whether current vector in frustum 1 is contained in frustum 2
      if (frustum2.checkInterior(vectors1.get(i)))
        newVectors.add(vectors1.get(i));

      Vector norm = normals1.get(i);
      ArrayList<Vector> intersections = new ArrayList<Vector>();

      int lastIntersectionIndex = 0;
      // will be index of last sector from
      // frustum2 that intersects the
      // current sector of frustum1

      for (int j = 0; j < normals2.size(); j++) {
        boolean intersects;
        Vector intersection = GeometryOperations.intersectPlanes(norm, normals2
            .get(j));

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
              Vector difference = Vector.subtract(vectors1.get((i + 1)
                  % vectors1.size()), vectors1.get(i % vectors1.size()));
              double dot = Vector.dot(difference, normals2.get(j));
              if (dot < 0) {
                intersections.add(intersection);
                lastIntersectionIndex = j;
              } else if (dot > 0)
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

  private static boolean checkInIntersection(Vector vector, Frustum3D f1,
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

  private static boolean closeTogether(Vector v1, Vector v2) {
    Vector difference = Vector.subtract(v1, v2);
    double cos = (v1.lengthSquared() + v2.lengthSquared() - difference
        .lengthSquared())
        / (2 * v1.length() * v2.length());
    return Math.abs(cos) > 1 - epsilon;
  }

  /*
   * intersects vector v with plane formed by ray[index] and ray[index+1]
   */
  private boolean sectorContainsVector(int index, Vector v) {
    double dot = Vector.dot(normals.get(index), v);
    return (Math.abs(dot) < epsilon && this.checkInterior(v));
  }

  /*
   * finds intersection of face f with line between a and b
   */
  public Vector findIntersectionWithFace(EmbeddedFace f, Vector v) {
    Vector intersection = findIntersection(f.getVectorAt(0), f.getVectorAt(1),
        f.getVectorAt(2), new Vector(0, 0, 0), v);
    Frustum3D frustum = new Frustum3D(f.getVectors());
    if (intersection == null || frustum.checkInterior(intersection))
      return intersection;
    return null;
  }

  public Vector findSectorIntersection(Vector a, Vector b, int index) {
    Vector v1 = this.getVectorAt(index);
    Vector v2 = this.getVectorAt((index + 1) % this.getNumberRays());
    Vector v3 = new Vector(0, 0, 0);
    Vector intersection = findIntersection(v1, v2, v3, a, b);
//    System.out.println();
//    System.out.println("found intersection between sector at "
//        + this.getVectorAt(index) + " and "
//        + this.getVectorAt((index + 1) % this.getNumberRays())
//        + " with line through " + a + " and " + b);
//    System.out.println("it is: " + intersection);
//    if (intersection != null) {
//      System.out.println("contained in sector? "
//          + sectorContainsVector(index, intersection));
//      System.out.println("contained in edge? "
//          + isContained(a, b, intersection) + "\n");
//    }
    if (intersection == null
        || (isContained(a, b, intersection) && sectorContainsVector(index,
            intersection)))
      return intersection;
    return null;
  }

  /*
   * finds intersection of plane containing endpoints of v1, v2 and v3 with line
   * through endpoints of a and b
   */
  private static Vector findIntersection(Vector v1, Vector v2, Vector v3,
      Vector a, Vector b) {

    double x1 = v1.getComponent(0);
    double y1 = v1.getComponent(1);
    double z1 = v1.getComponent(2);

    double x2 = v2.getComponent(0);
    double y2 = v2.getComponent(1);
    double z2 = v2.getComponent(2);

    double x3 = v3.getComponent(0);
    double y3 = v3.getComponent(1);
    double z3 = v3.getComponent(2);

    double x4 = a.getComponent(0);
    double y4 = a.getComponent(1);
    double z4 = a.getComponent(2);

    double x5 = b.getComponent(0);
    double y5 = b.getComponent(1);
    double z5 = b.getComponent(2);

    double[][] matrix1 = new double[][] { { 1, 1, 1, 1 }, { x1, x2, x3, x4 },
        { y1, y2, y3, y4 }, { z1, z2, z3, z4 } };
    double[][] matrix2 = new double[][] { { 1, 1, 1, 0 },
        { x1, x2, x3, x5 - x4 }, { y1, y2, y3, y5 - y4 },
        { z1, z2, z3, z5 - z4 } };
    Matrix m1 = new Matrix(matrix1);
    Matrix m2 = new Matrix(matrix2);
    if (m2.determinant() == 0)
      return null;
    double t = -m1.determinant() / m2.determinant();

    double x = x4 + (x5 - x4) * t;
    double y = y4 + (y5 - y4) * t;
    double z = z4 + (z5 - z4) * t;

    Vector intersection = new Vector(x, y, z);
    return intersection;
  }

  /*
   * returns intersection of line through a and b with line through origin and v
   */
  private Vector findIntersection(Vector a, Vector b, Vector v) {
    Vector w = Vector.subtract(b, a);
    double w1 = w.getComponent(0);
    double w2 = w.getComponent(1);
    double w3 = w.getComponent(2);

    double a1 = a.getComponent(0);
    double a2 = a.getComponent(1);
    double a3 = a.getComponent(2);

    double v1 = v.getComponent(0);
    double v2 = v.getComponent(1);
    double v3 = v.getComponent(2);

    // check for parallel
    if (areScalarMultiples(w, v))
      return null;

    double s, t;

    if (v1 == 0 && v2 == 0) { // v is vertical
      // w1 and w2 can't both be 0 since already checked for parallel
      if (w1 != 0)
        t = -a1 / w1;
      else
        t = -a2 / w2;

      if (Math.abs(w2 * t + a2) > epsilon || Math.abs(w1 * t + a1) > epsilon)
        // not zero => no intersection
        return null;
      double z = w3 * t + a3;
      return new Vector(0, 0, z);
    } else if (v1 != 0) {

      t = ((a1 * v2 / v1) - a2) / (w2 - (v2 * w1 / v1));
      s = (w1 / v1) * t + (a1 / v1);
    } else { // v2 != 0

      t = ((a2 * v1 / v2) - a1) / (w1 - (v1 * w2 / v2));
      s = (w2 / v2) * t + (a2 / v2);
    }

    double x = v1 * s;
    double y = v2 * s;
    double z1 = w3 * t + a3;
    double z2 = v3 * s;

    if (Math.abs(z1 - z2) > epsilon)
      return null;

    Vector intersection = new Vector(x, y, z1);

    if (isContained(a, b, intersection))
      return intersection;
    else
      return null;
  }

  private boolean areScalarMultiples(Vector a, Vector b) {
    double ratio = 0;
    if (a.isZero())
      return b.isZero();
    for (int i = 0; i < 3; i++) {
      if (a.getComponent(i) != 0)
        ratio = b.getComponent(i) / a.getComponent(i);
    }
    for (int i = 0; i < 3; i++) {
      if (Math.abs((b.getComponent(i) / a.getComponent(i)) - ratio) > epsilon)
        return false;
    }
    return true;
  }

  /*
   * returns true if the coordinates of the given vector v are between the
   * coordinates of v1 and v2
   */
  private boolean isContained(Vector v1, Vector v2, Vector v) {
    double x = v.getComponent(0);
    double y = v.getComponent(1);
    double z = v.getComponent(2);
    double x1 = v1.getComponent(0);
    double y1 = v1.getComponent(1);
    double z1 = v1.getComponent(2);
    double x2 = v2.getComponent(0);
    double y2 = v2.getComponent(1);
    double z2 = v2.getComponent(2);

    return (between(x1, x2, x) && between(y1, y2, y) && between(z1, z2, z));
  }

  // true if c is between a and b
  private static boolean between(double a, double b, double c) {
    if ((a - epsilon > c && b - epsilon > c)
        || (a + epsilon < c && b + epsilon < c))
      return false;
    return true;
  }

  public ArrayList<Vector> clipFace(Tetra tetra) {

    HashMap<Vertex, Vector> vertexCoords = new HashMap<Vertex, Vector>();
    List<Vertex> vertices = tetra.getLocalVertices();
//    System.out.println("vertices: " + vertices.get(0).equals(vertices.get(1)));
    vertexCoords.put(vertices.get(0), Coord3D.coordAt(vertices.get(0), tetra));
    vertexCoords.put(vertices.get(1), Coord3D.coordAt(vertices.get(1), tetra));
    vertexCoords.put(vertices.get(2), Coord3D.coordAt(vertices.get(2), tetra));
    vertexCoords.put(vertices.get(3), Coord3D.coordAt(vertices.get(3), tetra));

    HashMap<Edge, Vector[]> edgeCoords = new HashMap<Edge, Vector[]>();
    List<Edge> edges = tetra.getLocalEdges();
    for (int i = 0; i < edges.size(); i++) {
      List<Vertex> ends = edges.get(i).getLocalVertices();
      Vector[] points = new Vector[2];
      points[0] = vertexCoords.get(ends.get(0));
      points[1] = vertexCoords.get(ends.get(1));
      edgeCoords.put(edges.get(i), points);
    }

    HashMap<Face, EmbeddedFace> faces = new HashMap<Face, EmbeddedFace>();
    List<Face> faceList = tetra.getLocalFaces();
    for (Face face : faceList) {
      List<Vertex> vs = face.getLocalVertices();
      ArrayList<Vector> vectors = new ArrayList<Vector>();
      for (int i = 0; i < vs.size(); i++) {
        vectors.add(vertexCoords.get(vs.get(i)));
      }
      EmbeddedFace f = new EmbeddedFace(vectors);
      faces.put(face, f);
    }

    ArrayList<Vector> hullPoints = new ArrayList<Vector>();

    ArrayList<Vertex> omitVerts = new ArrayList<Vertex>();
    ArrayList<Edge> omitEdgesRay = new ArrayList<Edge>();
    ArrayList<Face> omitFacesRay = new ArrayList<Face>();

    ArrayList<ArrayList<Vertex>> omitVertsSector = new ArrayList<ArrayList<Vertex>>();
    ArrayList<ArrayList<Edge>> omitEdgesSector = new ArrayList<ArrayList<Edge>>();
    for (int i = 0; i < this.getNumberRays(); i++) {
      omitVertsSector.add(new ArrayList<Vertex>());
      omitEdgesSector.add(new ArrayList<Edge>());
    }

    // rays
    for (int rayIndex = 0; rayIndex < this.getNumberRays(); rayIndex++) {
//      System.out.println();
//      System.out.println("checking ray index " + rayIndex + ": "
//          + rays.get(rayIndex));
      Vector ray = rays.get(rayIndex);
      // ray's local sectors are at rayIndex and rayIndex-1
      int prevIndex = (rayIndex - 1 + getNumberRays()) % getNumberRays();

      omitEdgesRay.clear();
      omitFacesRay.clear();

      for (Vertex vertex : tetra.getLocalVertices()) {
        // System.out.println("*checking vertex: " + vertexCoords.get(vertex));
        // System.out.print("adding? ");
        if (closeTogether(vertexCoords.get(vertex), ray)) {
          // System.out.println("yes");
          hullPoints.add(vertexCoords.get(vertex));

          omitEdgesRay.addAll(vertex.getLocalEdges());
          omitFacesRay.addAll(vertex.getLocalFaces());
          omitVerts.add(vertex);

          omitEdgesSector.get(rayIndex).addAll(vertex.getLocalEdges());
          omitEdgesSector.get(prevIndex).addAll(vertex.getLocalEdges());

          omitVertsSector.get(rayIndex).add(vertex);
          omitVertsSector.get(prevIndex).add(vertex);
        } else
          ;
        // System.out.println("no");
      }

      for (Edge edge : tetra.getLocalEdges()) {
//        System.out.println("checking edge :" + edgeCoords.get(edge)[0] + " to "
//            + edgeCoords.get(edge)[1]);
//        System.out.print("adding? ");
        if (!omitEdgesRay.contains(edge)) {
          Vector intersection = findIntersection(edgeCoords.get(edge)[0],
              edgeCoords.get(edge)[1], ray);
          if (intersection != null
              && isContained(edgeCoords.get(edge)[0], edgeCoords.get(edge)[1],
                  intersection) && this.checkInterior(intersection)) {
//            System.out.println(intersection);
            hullPoints.add(intersection);
            omitFacesRay.addAll(edge.getLocalFaces());

            omitEdgesSector.get(rayIndex).add(edge);
            omitEdgesSector.get(prevIndex).add(edge);
          } else;
//            System.out.println("no");
        } else;
//          System.out.println("omitted");
      }

      for (Face face : tetra.getLocalFaces()) {
        System.out.println("checking face: \n" + faces.get(face));
        System.out.print("adding? ");
        if (!omitFacesRay.contains(face)) {
          Vector intersection = findIntersectionWithFace(faces.get(face), ray);
          if (intersection != null && this.checkInterior(intersection)) {
            System.out.println(intersection);
            hullPoints.add(intersection);
          } else
            System.out.println("no");
        } else
          System.out.println("omitted");
      }
    } // end rays

    // sectors
    System.out.println();
    for (int sectorIndex = 0; sectorIndex < this.getNumberRays(); sectorIndex++) {
      // System.out.println("\nchecking sector index " + sectorIndex);
      // System.out.println("with vertices:");
      for (Vertex vertex : tetra.getLocalVertices()) {
        // System.out.println("*checking vertex: " + vertexCoords.get(vertex));
        // System.out.print("adding? ");
        if (!omitVerts.contains(vertex)
            && sectorContainsVector(sectorIndex, vertexCoords.get(vertex))) {
          // System.out.println("yes");
          hullPoints.add(vertexCoords.get(vertex));
          omitEdgesSector.get(sectorIndex).addAll(vertex.getLocalEdges());
          omitVerts.add(vertex);
        } else
          ;
        // System.out.println("no");
      }

      // System.out.println("\nwith edges: ");
      for (Edge edge : tetra.getLocalEdges()) {
//         System.out.println("checking edge: " + edgeCoords.get(edge)[0] +
//         " to "
//         + edgeCoords.get(edge)[1]);
//         System.out.print("adding? ");
        if (!omitEdgesSector.get(sectorIndex).contains(edge)) {
          Vector intersection = findSectorIntersection(edgeCoords.get(edge)[0],
              edgeCoords.get(edge)[1], sectorIndex);
          if (intersection != null) {
            hullPoints.add(intersection);
//             System.out.println(intersection);
          } else
            ;
//           System.out.println("no");
        } else
          ;
//         System.out.println("omitted");
      }
    } // end sectors

    for (Vertex vertex : tetra.getLocalVertices()) {
      if (!omitVerts.contains(vertex)
          && this.checkInterior(vertexCoords.get(vertex)))
        hullPoints.add(vertexCoords.get(vertex));
    }

    return hullPoints;
  }
}
