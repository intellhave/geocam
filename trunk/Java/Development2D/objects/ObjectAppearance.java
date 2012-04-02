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
  
  public static enum ModelType { ANT, APPLE, COOKIE, ROCKET, SATTELITE, CUBE };
  
  private double scale = 1.0;
  private double default_scale = 1.0;
  
  private Color c = Color.BLUE; 
  private ModelType model = ModelType.ANT; 
    
  public ObjectAppearance( ModelType mt, double scale ){
    this.setModelType( mt );
    this.setScale( scale );

    this.model = mt;
    switch (mt) {
    case CUBE:
      this.setDefaultScale(0.2);
      break;
    case APPLE:
      this.setDefaultScale(0.1);
      break;

    case SATTELITE:
    case ROCKET:
      this.setDefaultScale(1.0);
      break;
    case ANT:
    case COOKIE:    
    default:
      this.setDefaultScale(1.0);
      break;
    }
  }
  
  public ObjectAppearance( ModelType mt ){ this( mt, 1.0 ); }
  public ObjectAppearance(){ this( ModelType.ANT, 1.0 ); }
    
  public void setModelType(ModelType mt) {
    this.model = mt;
  }
    
  // Default Scale methods should be used to scale models so
  // that they have a reasonable size for most models.
  // The setScale methods exist so users can dynamically resize models
  // however they want, without losing the default scale.
  public void setDefaultScale( double scale ) { this.default_scale = scale; }
  public double getDefaultScale() { return default_scale; }
  public void setScale(double scale){ this.scale = scale; }
  public double getScale(){ return this.scale; }
  
  public void setColor(Color color){ c = color; }
  public boolean isTransparent(){ 
    //possible values for c.getTransparency() are Transparency.BITMASK,
    // Transparency.OPAQUE, Transparency.TRANSLUCENT 
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
      ff = new File("Data/blender/cookie.3ds");
      break;
    case ROCKET:
      ff = new File("Data/blender/rocket.3ds");
      break;
    case SATTELITE:
      ff = new File("Data/blender/sattelite.3ds");
      break;
    case CUBE:
    default:
      ff = new File("Data/blender/cube.3ds");
      break;
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
