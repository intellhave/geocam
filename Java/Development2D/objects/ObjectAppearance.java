package objects;

import java.awt.Color;
import java.awt.Transparency;
import java.io.File;

import de.jreality.math.MatrixBuilder;
import de.jreality.reader.Readers;
import de.jreality.scene.Appearance;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.Transformation;
import de.jreality.shader.CommonAttributes;

/* This class holds appearance information for a VisibleObject
 * 
 * Look at de.jreality.tutorial.app.PointShaderExample for fancier ways to specify point appearance
 */



public class ObjectAppearance {
  
  public static enum ModelType { ANT, APPLE, COOKIE };
  
  private double scale = 1.0;
  private Color c = Color.BLUE; 
  private ModelType model = ModelType.ANT; 
  
  public ObjectAppearance(){ }
  public ObjectAppearance(double scale, Color color){
    this.scale = scale;
    c = color;
  }
  
  public static ObjectAppearance makeModel(ModelType mt) {
    ObjectAppearance oa = new ObjectAppearance();
    oa.model = mt;
    switch (mt) {
    case ANT:
      oa.scale = 1.0;
      break;
    case APPLE:
      oa.scale = 0.005;
      break;
    case COOKIE:
      oa.scale = 0.05;
      break;
    }
    return oa;
  }

  public void setRadius(double scale){ this.scale = scale; }
  public void setColor(Color color){ c = color; }
  public boolean isTransparent(){ 
    //possible values for c.getTransparency() are Transparency.BITMASK, Transparency.OPAQUE, Transparency.TRANSLUCENT 
    return (c.getTransparency() == Transparency.TRANSLUCENT); 
  }
  
  public Appearance getJRealityAppearance(){
    //note: color is set in the PointSetFactory in SGCMethods
    Appearance app = new Appearance();
    
    app.setAttribute(CommonAttributes.VERTEX_DRAW, false);
    app.setAttribute(CommonAttributes.EDGE_DRAW, false);
    //app.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, false); //still draws with alpha specified in color
    app.setAttribute(CommonAttributes.PICKABLE, false);
    return app;
  }
  
  public Color getColor(){
    return c;
  }
  
  public SceneGraphComponent prepareNewSceneGraphComponent() {

    File ff;
    switch (this.model) {
    case ANT:
      ff = new File("Data/blender/ant.3ds");
      break;
    case APPLE:
      ff = new File("Data/blender/apple.3ds");
      break;
    case COOKIE:
    default:
      ff = new File("Data/blender/cookie.3ds");
    }

    SceneGraphComponent sgc = null;
    try{
      sgc = Readers.read( ff );
    } catch (Exception ee){
      ee.printStackTrace();
      System.exit(1);
    }
    Appearance ap = new Appearance();
    ap.setAttribute(CommonAttributes.VERTEX_DRAW, false);
    ap.setAttribute(CommonAttributes.EDGE_DRAW, false);
    ap.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, false);
    sgc.setAppearance(ap);
    
    sgc.setTransformation(new Transformation(MatrixBuilder
          .euclidean().scale( this.scale )
          .getMatrix()
          .getArray()));
    
    return sgc;
  }
}
