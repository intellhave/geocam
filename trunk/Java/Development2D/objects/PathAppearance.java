package objects;

import java.awt.Color;

import de.jreality.scene.Appearance;
import de.jreality.shader.CommonAttributes;

/* This class holds appearance information for a VisiblePath
 */

public class PathAppearance {

  boolean t = false; //transparency enabled
  double a = 1.0; //alpha value
  double r = 2; //tube size
  Color c = Color.BLUE; //color
  
  public PathAppearance(){ }
  public PathAppearance(double tubeSize, Color color){
    r = tubeSize;
    c = color;
    t = false;
  }
  public PathAppearance(double tubeSize, Color color, double alpha){
    r = tubeSize;
    c = color;
    t = true;
    a = alpha;
  }
  
  public void setOpaque(){ t = false; }
  public void setTransparent(double alpha){ t = true; a = alpha; }
  public void setRadius(double radius){ r = radius; }
  public void setColor(Color color){ c = color; }
  
  public Appearance getJRealityAppearance(){
    Appearance app = new Appearance();
    app.setAttribute(CommonAttributes.VERTEX_DRAW, false);
    app.setAttribute(CommonAttributes.EDGE_DRAW, true);
    app.setAttribute(CommonAttributes.TUBES_DRAW, true);
    app.setAttribute(CommonAttributes.TUBE_RADIUS, r);
    app.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, t);
    if(t){ app.setAttribute(CommonAttributes.TRANSPARENCY, a); }
    app.setAttribute(CommonAttributes.PICKABLE, false);
    return app;
  }
  
  public Color getColor(){
    return c;
  }
}
