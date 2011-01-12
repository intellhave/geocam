package development;

import java.awt.Color;

import triangulation.Tetra;

public class Node3D {
  protected Color color;
  protected Tetra tetra;  // face containing this node
  protected Vector pos; // coordinates in containing face
  protected double radius = 0.03;
  protected Vector movement = new Vector(0,0);
  protected double transparency = 0.01;
  
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
