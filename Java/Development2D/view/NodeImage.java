package view;

import java.awt.Color;

import de.jreality.scene.SceneGraphComponent;
import development.FadingNode;
import development.Node;
import development.Vector;

public class NodeImage {
  private Color color;
  private double radius;
  private double transparency;
  private Vector position;
  private boolean isFadingNode;
  private SceneGraphComponent sgc2d;
  private SceneGraphComponent sgc3d;
  
  public NodeImage(Node node, Vector pos) {
    this.color = node.getColor();
    this.radius = node.getRadius();
    this.transparency = node.getTransparency();
    this.position = pos;
    
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
}
