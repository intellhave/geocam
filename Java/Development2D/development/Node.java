package development;

import java.awt.Color;
import java.util.List;

import triangulation.Edge;
import triangulation.Face;

public class Node {
  private Color color;
  private Face face;  // face containing this node
  private Vector pos; // coordinates in containing face
  private double radius = 0.03;
  private Vector movement = new Vector(0,0);
  
  public Node(Color color, Face face, Vector pos) {
    this.color = color;
    this.face = face;
    this.pos = pos;
  }
  
  public void setMovement(Vector v) { movement = v;  }
  public Color getColor() { return color; }
  public Face getFace() { return face; }
  public Vector getPosition() { return pos; }
  public void setFace(Face face) { this.face = face; }
  public void setColor(Color color) { this.color = color; }
  public double getRadius() { return radius; }
  public void setRadius(double radius) { this.radius = radius; }
  
  public void move() {
    pos = computeEnd(Vector.add(pos, movement), face, null);
  }
  
  /*
   * Traces geodesic in direction of point, applying the appropriate affine
   * transformation whenever it crosses an edge. When a face is found containing
   * the transformed point, these become the new source face and point.
   * ignoreEdge is the edge just crossed, so don't want to cross it again.
   */
  private Vector computeEnd(Vector point, Face face, Edge ignoreEdge) {

    // see if current face contains point
    Vector l = DevelopmentComputations.getBarycentricCoords(point, face);
    double l1 = l.getComponent(0);
    double l2 = l.getComponent(1);
    double l3 = l.getComponent(2);

    if (l1 >= 0 && l1 < 1 && l2 >= 0 && l2 < 1 && l3 >= 0 && l3 < 1) {
      this.face = face;
      return new Vector(point);
    }

    // find which edge vector intersects to get next face
    // (currently not handling vector through vertex)
    boolean foundEdge = false;
    Edge edge = null;
    List<Edge> edges = face.getLocalEdges();

    for (int i = 0; i < edges.size(); i++) {
      edge = edges.get(i);
      if (ignoreEdge != null && edge.equals(ignoreEdge))
        continue;
      Vector v1 = Coord2D.coordAt(edge.getLocalVertices().get(0), face);
      Vector v2 = Coord2D.coordAt(edge.getLocalVertices().get(1), face);

      Vector edgeDiff = Vector.subtract(v1, v2);
      Vector sourceDiff = Vector.subtract(pos, v2);
      Vector pointDiff = Vector.subtract(point, v2);
      Vector intersection = Vector.findIntersection(sourceDiff, pointDiff,
          edgeDiff);
      if (intersection != null) {
        foundEdge = true;
        break;
      }
    }
    if (foundEdge) {
      Face nextFace = null;

      List<Face> faces = edge.getLocalFaces();
      if (faces.get(0).equals(face))
        nextFace = faces.get(1);
      else
        nextFace = faces.get(0);

      // get transformation taking current face to next
      AffineTransformation trans = CoordTrans2D.affineTransAt(face, edge);
      Vector newPoint = trans.affineTransPoint(point);
      movement = trans.affineTransVector(movement);

      return computeEnd(newPoint, nextFace, edge);
    } else {
      System.out.println("did not find edge\n");
      return null;
    }
  }
}
