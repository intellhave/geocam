package development;

import java.awt.Color;
import java.util.List;

import triangulation.Face;
import triangulation.Tetra;

public class Node3D {
  protected Color color;
  protected Tetra tetra;  // face containing this node
  protected Vector pos; // coordinates in containing face
  protected double radius = 0.03;
  protected Vector movement = new Vector(0,0);
  protected double transparency = 0.1;
  
  public Node3D(Color color, Tetra tetra, Vector pos) {
    this.color = color;
    this.tetra = tetra;
    this.pos = new Vector(pos);
  }
  
  public Node3D(Color color, Tetra tetra, Vector pos, double radius) {
    this.color = color;
    this.tetra = tetra;
    this.pos = new Vector(pos);
    this.radius = radius;
  }
  
  public Node3D(Node3D node) {
    tetra = node.getTetra();
    pos = new Vector(node.getPosition());
    radius = node.getRadius();
    movement = new Vector(node.getMovement());
    transparency = node.getTransparency();
    color = node.getColor();
  }
  
  public void move() {
    if(!movement.isZero())
      pos = computeEnd(Vector.add(pos, movement), tetra, null);
  }
  
  public Vector computeEnd(Vector point, Tetra t, Face ignoreFace) {
    Vector l = Development3DComputations.getBarycentricCoords(point, t);
    double l1 = l.getComponent(0);
    double l2 = l.getComponent(1);
    double l3 = l.getComponent(2);
    double l4 = l.getComponent(3);
    
    if (l1 >= 0 && l1 < 1 && l2 >= 0 && l2 < 1 && l3 >= 0 && l3 < 1 && l4 >= 0 && l4 < 1) {
      this.tetra = t;
      return new Vector(point);
    }
    // find which face vector intersects to get next tetra
    // (currently not handling vector through vertex or edge)
    boolean foundFace = false;
    Face face = null;
    List<Face> faces = t.getLocalFaces();
    
    for (int i = 0; i < faces.size(); i++) {
      face = faces.get(i);
      if (ignoreFace != null && face.equals(ignoreFace))
        continue;
      Vector v1 = Coord3D.coordAt(face.getLocalVertices().get(0), t);
      Vector v2 = Coord3D.coordAt(face.getLocalVertices().get(1), t);
      Vector v3 = Coord3D.coordAt(face.getLocalVertices().get(2), t);
      
      EmbeddedFace ef = new EmbeddedFace(v1,v2,v3);
      Vector normal = ef.getNormal();

      Vector diff1 = Vector.subtract(pos, v1);
      Vector diff2 = Vector.subtract(point, v1);
      
      double dot1 = Vector.dot(diff1, normal);
      double dot2 = Vector.dot(diff2, normal);
      
      if (dot1*dot2 < 0) { // should be opposite signs
        foundFace = true;
        break;
      }
    }
    
    if (foundFace) {
      Tetra nextTetra = null;

      List<Tetra> tetras = face.getLocalTetras();
      if (tetras.get(0).equals(t))
        nextTetra = tetras.get(1);
      else
        nextTetra = tetras.get(0);

      // get transformation taking current face to next
      AffineTransformation trans = CoordTrans3D.affineTransAt(t, face);
      Vector newPoint = trans.affineTransPoint(point);
      movement = trans.affineTransVector(movement);

      return computeEnd(newPoint, nextTetra, face);
    } else {
      System.out.println("did not find face\n");
      return null;
    }
  }

  public void setMovement(Vector v) { movement = v; }
  public Vector getMovement() { return movement; }
  public Color getColor() { return color; }
  public Tetra getTetra() { return tetra; }
  public Vector getPosition() { return pos; }
  public void setPosition(Vector pos) { this.pos = pos; }
  public void setTetra(Tetra tetra) { this.tetra = tetra; }
  public void setColor(Color color) { this.color = color; }
  public double getRadius() { return radius; }
  public void setRadius(double radius) { this.radius = radius; }
  public double getTransparency() { return transparency; }
  public void setTransparency(double transparency) { this.transparency = transparency; }
}
