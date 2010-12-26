package development;

import java.awt.Color;

import triangulation.Face;

public class Node {
  private Color color;
  private Face face;  // face containing this node
  private Vector pos; // coordinates in containing face
  private double radius = 0.03;
  
  public Node(Color color, Face face, Vector pos) {
    this.color = color;
    this.face = face;
    this.pos = pos;
  }
  
  public Color getColor() {
    return color;
  }
  
  public Face getFace() {
    return face;
  }
  
  public Vector getPosition() {
    return pos;
  }
  
  public void setFace(Face face) {
    this.face = face;
  }
  
  public void setColor(Color color) {
    this.color = color;
  }

  public double getRadius() {
    return radius;
  }
  
  public void setRadius(double radius) {
    this.radius = radius;
  }
}
