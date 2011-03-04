package view;

import java.awt.Color;

import de.jreality.scene.SceneGraphComponent;
import development.FadingNode;
import development.Node;
import development.Vector;

public class NodeImage {
  protected Color color;
  protected double radius;
  protected double transparency;
  protected Vector position;
  protected boolean isFadingNode;
  protected SceneGraphComponent sgc2d;
  protected SceneGraphComponent sgc3d;
  
  protected Node sourceNode; // reference to originating node
  
  public NodeImage(Node node, Vector pos) {
    this.color = node.getColor();
    this.radius = node.getRadius();
    this.transparency = node.getTransparency();
    this.position = pos;
    sourceNode = node;
    
    isFadingNode = node instanceof FadingNode;
    
    sgc2d = SGCMethods.sgcFromNode(this, 2);
    sgc3d = SGCMethods.sgcFromNode(this, 3);
  }
  
  public boolean isFadingNode() {
    return isFadingNode;
  }
  
  public Color getColor() { return color; }
  public double getRadius() { return radius; }
  public double getTransparency() { return transparency; }
  public Vector getPosition() { return position; }
  public SceneGraphComponent getSGC(int dimension) { 
    if(dimension == 2) return sgc2d;
    return sgc3d;
  } 
  public Node getNode() { return sourceNode; }
  public void setPosition(Vector v) { position = v; }
}
