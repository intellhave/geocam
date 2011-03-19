package objects;

import java.awt.Color;
import java.awt.Transparency;

import de.jreality.scene.Appearance;
import de.jreality.shader.CommonAttributes;

/* This class holds appearance information for a VisibleObject
 * 
 * Look at de.jreality.tutorial.app.PointShaderExample for fancier ways to specify point appearance
 */

public class ObjectAppearance {

  double r = 2; //radius
  Color c = Color.BLUE; 
  
  public ObjectAppearance(){ }
  public ObjectAppearance(double radius, Color color){
    r = radius;
    c = color;
  }
  
  public void setRadius(double radius){ r = radius; }
  public void setColor(Color color){ c = color; }
  public boolean isTransparent(){ 
    //possible values for c.getTransparency() are Transparency.BITMASK, Transparency.OPAQUE, Transparency.TRANSLUCENT 
    return (c.getTransparency() == Transparency.TRANSLUCENT); 
  }
  
  public Appearance getJRealityAppearance(){
    //note: color is set in the PointSetFactory in SGCMethods
    Appearance app = new Appearance();
    app.setAttribute(CommonAttributes.VERTEX_DRAW, true);
    app.setAttribute(CommonAttributes.POINT_RADIUS, r);
    app.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, false); //still draws with alpha specified in color
    app.setAttribute(CommonAttributes.PICKABLE, false);
    return app;
  }
  
  public Color getColor(){
    return c;
  }
}
