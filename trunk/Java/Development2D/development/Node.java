package development;

import java.awt.Color;
import objects.ManifoldPosition;
import triangulation.Face;


/*************************************************
 *  Node
 *  
 *  @author K. Kiviat
 *  
 *  Overview: A node is an object that lives on the realization
 *      of the triangulation. It contains the following info:
 *        * ManifoldPosition pos, a face-coordinates tuple
 *        * radius that node is rendered (nodes are spheres)
 *        * color to render the node
 *        * transparency to render the node
 *        * movement: direction of movement
 *        * units_per_millisecond to move
 * 
 */

public class Node {
  protected Color color;
  protected ManifoldPosition pos;
  protected double radius = 0.03;
  protected Vector movement = new Vector(0,0);
  protected double transparency = 0.1;
  
  protected double movement_units_per_second = 4;
  protected double units_per_millisecond = movement_units_per_second/1000;

  
  public Node(Color color, ManifoldPosition mp, double units_per_millisecond, double radius) {
    this.color = color;
    this.pos = new ManifoldPosition(mp); 
    this.units_per_millisecond = units_per_millisecond;
    this.radius = radius;
  }
  
  
  public Node(Node node) {
    pos = new ManifoldPosition(node.getManifoldPosition());
    radius = node.getRadius();
    units_per_millisecond = node.getVelocity();
    movement = new Vector(node.getMovement());
    transparency = node.getTransparency();
    color = node.getColor();
  }

  public void setMovement(Vector v) { movement = v; }
  public Vector getMovement() { return movement; }
  public Color getColor() { return color; }
  public ManifoldPosition getManifoldPosition(){ return pos; }
  public void setManifoldPosition(ManifoldPosition newpos){ pos = newpos; }
  public Vector getPosition(){ return pos.getPosition(); }
  public Face getFace(){ return pos.getFace(); }
  public void setColor(Color color) { this.color = color; }
  public double getRadius() { return radius; }
  public void setRadius(double radius) { this.radius = radius; }
  public double getTransparency() { return transparency; }
  public void setTransparency(double transparency) { this.transparency = transparency; }
  public void setVelocity(double v) { units_per_millisecond = v; }
  public double getVelocity() { return units_per_millisecond; }
  
  public void move(double elapsedTime) {
    if(movement.isZero()) return;
    
    // assumes movement is normalized
    Vector v = new Vector(movement);
    v.scale(elapsedTime*units_per_millisecond);
    pos.move(v,movement);
  }
  
}
