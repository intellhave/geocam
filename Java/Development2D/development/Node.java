package development;

import java.awt.Color;
import java.util.List;

import triangulation.Edge;
import triangulation.Face;

/*************************************************
 *  Node
 *  
 *  @author K. Kiviat
 *  
 *  Overview: A node is an object that lives on the realization
 *      of the triangulation. It contains the following info:
 *        * Face the node is contained in
 *        * position pos within face (specified by coordinates 
 *          determined in Coord2D)
 *        * radius that node is rendered (nodes are spheres)
 *        * color to render the node
 *        * transparency to render the node
 *        * movement: direction and how far it moves in one movement
 *            step size
 *            
 *   Possible upgrades:
 *        * change movement to a velocity
 * 
 */

public class Node {
  protected Color color;
  protected Face face;  // face containing this node
  protected Vector pos; // coordinates in containing face
  protected double radius = 0.03;
  protected Vector movement = new Vector(0,0);
  protected double transparency = 0.1;
  
  protected double movement_units_per_second = 4;
  protected double units_per_millisecond = movement_units_per_second/1000;

  
  public Node(Color color, Face face, Vector pos) {
    this.color = color;
    this.face = face;
    this.pos = new Vector(pos);
  }
  
  public Node(Color color, Face face, Vector pos, double radius) {
    this.color = color;
    this.face = face;
    this.pos = new Vector(pos);
    this.radius = radius;
  }
  
  public Node(Node node) {
    face = node.getFace();
    pos = new Vector(node.getPosition());
    radius = node.getRadius();
    movement = new Vector(node.getMovement());
    transparency = node.getTransparency();
    color = node.getColor();
  }

  public void setMovement(Vector v) { movement = v; }
  public Vector getMovement() { return movement; }
  public Color getColor() { return color; }
  public Face getFace() { return face; }
  public Vector getPosition() { return pos; }
  public void setPosition(Vector pos) { this.pos = pos; }
  public void setFace(Face face) { this.face = face; }
  public void setColor(Color color) { this.color = color; }
  public double getRadius() { return radius; }
  public void setRadius(double radius) { this.radius = radius; }
  public double getTransparency() { return transparency; }
  public void setTransparency(double transparency) { this.transparency = transparency; }
  
  public void move(double elapsedTime) {
    if(movement.isZero()) return;
    
    // assumes movement is normalized
    Vector v = new Vector(movement);
    v.scale(elapsedTime*units_per_millisecond);
    pos = computeEnd(Vector.add(pos, v), face, null);
  }
  
  /*
   * Traces geodesic in direction of point, applying the appropriate affine
   * transformation whenever it crosses an edge. When a face is found containing
   * the transformed point, these become the new source face and point.
   * ignoreEdge is the edge just crossed, so don't want to cross it again.
   */
  protected Vector computeEnd(Vector point, Face face, Edge ignoreEdge) {

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
