package objects;

import java.awt.Color;

import de.jreality.scene.Appearance;
import de.jreality.shader.CommonAttributes;

/* This class holds appearance information for a VisibleObject
 * 
 * Look at de.jreality.tutorial.app.PointShaderExample for fancier ways to specify point appearance
 */

public class ObjectAppearance {

  boolean t = false; //transparency enabled
  double a = 1.0; //alpha value
  double r = 2; //radius
  Color c = Color.BLUE; //color
  
  public ObjectAppearance(){ }
  public ObjectAppearance(double radius, Color color){
    r = radius;
    c = color;
    t = false;
  }
  public ObjectAppearance(double radius, Color color, double alpha){
    r = radius;
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
    app.setAttribute(CommonAttributes.VERTEX_DRAW, true);
    app.setAttribute(CommonAttributes.POINT_RADIUS, r);
    app.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, t);
    if(t){ app.setAttribute(CommonAttributes.TRANSPARENCY, a); }
    app.setAttribute(CommonAttributes.PICKABLE, false);
    return app;
  }
  
  public Color getColor(){
    return c;
  }
}
