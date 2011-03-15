package view;

import development.AffineTransformation;
import development.Node;
import development.Vector;

public class SourceNodeImage extends NodeImage {
  private AffineTransformation at;
  private boolean rotated = false;
  
  public SourceNodeImage(Node node, AffineTransformation affineTrans) {
    super(node, node.getPosition());
    at = affineTrans;
    // create the triangle
    double r = node.getRadius();
    double x = node.getPosition().getComponent(0);
    double y = node.getPosition().getComponent(1);
    Vector va = new Vector(x, y+r);
    Vector vb = new Vector(x+4*r, y);
    Vector vc = new Vector(x, y-r);
    
    Vector v1 = affineTrans.affineTransPoint(va);
    Vector v2 = affineTrans.affineTransPoint(vb);
    Vector v3 = affineTrans.affineTransPoint(vc);
    
    this.setPosition(affineTrans.affineTransPoint(node.getPosition()));
    
    sgc2d = SGCMethods.sgcFromVertices(1.0,v1,v2,v3);
  }
  
  
  /*
   * at = A*R*T, where T is the translation applied, R the rotation, and 
   * A some affine trans. We want to cancel out the rotation, by multiplying
   * at by A*R*T*T^-1*R^-1*T
   */
  public void rotate(AffineTransformation R1, Vector translation) {
    
    if(rotated) return;
    
    AffineTransformation T = new AffineTransformation(translation);
    AffineTransformation T1 = new AffineTransformation(Vector.scale(translation, -1));
    
    AffineTransformation rhs = T;
    rhs.leftMultiply(R1);

    rhs.leftMultiply(T1); // rhs = T^-1 * R^-1 * T
    
    rhs.leftMultiply(at);
    at = rhs;
    
    setSGC();
    rotated = true;
  }
  
  private void setSGC() {
    Vector basePosition = this.getNode().getPosition();
    double x = basePosition.getComponent(0);
    double y = basePosition.getComponent(1);
    double radius = this.getNode().getRadius();
    
    Vector va = new Vector(x, y+radius);
    Vector vb = new Vector(x+4*radius, y);
    Vector vc = new Vector(x, y-radius);
    
    Vector v1 = at.affineTransPoint(va);
    Vector v2 = at.affineTransPoint(vb);
    Vector v3 = at.affineTransPoint(vc);
    
    this.setPosition(at.affineTransPoint(basePosition));
    
    sgc2d = SGCMethods.sgcFromVertices(1.0,v1,v2,v3);
  }
  
}
