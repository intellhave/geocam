package marker;

import java.awt.Color;
import java.awt.Transparency;
import java.io.File;
import java.util.EnumMap;

import de.jreality.geometry.Primitives;
import de.jreality.math.MatrixBuilder;
import de.jreality.reader.Readers;
import de.jreality.scene.Appearance;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.Transformation;
import de.jreality.shader.CommonAttributes;

/*********************************************************************************
 * MarkerAppearance
 * 
 * This class is responsible for loading and organizing the geometric (mesh)
 * data needed to represent markers in the simulation. In normal use, a marker
 * will be modeled in blender, then exported as a 3DS file. Since one marker
 * model might be used in several places, this class maintains a static mapping
 * of model types to scene graphs obtained by reading in 3DS files. This way, we
 * can simply copy the "template scene graph" rather than reading that data from
 * disk every time we need a new scene graph component for a marker.
 * 
 * In addition, a marker appearance object allows us to keep track of appearance
 * data specific to a particular model. For example, one can specify a
 * particular size (scaling), so that a given scene may have many ants of
 * different sizes.
 *********************************************************************************/

public class MarkerAppearance {

  /*********************************************************************************
   * This enumeration provides a succinct description of which marker model this
   * appearance represents.
   *********************************************************************************/
  public static enum ModelType {
    ANT, APPLE, COOKIE, ROCKET, SATTELITE, CUBE, SPHERE
  };

  /*********************************************************************************
   * This EnumMap and static code are responsible for maintaining a mapping from
   * ModelTypes to template scene graph components.
   *********************************************************************************/
  private static EnumMap<ModelType, SceneGraphComponent> templateSGCs;

  static {
    templateSGCs = new EnumMap<ModelType, SceneGraphComponent>(ModelType.class);
    File ff;
    SceneGraphComponent sgc;

    ff = new File("Data/blender/ant.3ds");
    sgc = loadTemplateSGC(ff);
    templateSGCs.put(ModelType.ANT, sgc);

    ff = new File("Data/blender/apple.3ds");
    sgc = loadTemplateSGC(ff);
    templateSGCs.put(ModelType.APPLE, sgc);

    ff = new File("Data/blender/cookie.3ds");
    sgc = loadTemplateSGC(ff);
    templateSGCs.put(ModelType.COOKIE, sgc);

    ff = new File("Data/blender/rocket.3ds");
    sgc = loadTemplateSGC(ff);
    templateSGCs.put(ModelType.ROCKET, sgc);

    ff = new File("Data/blender/sattelite.3ds");
    sgc = loadTemplateSGC(ff);
    templateSGCs.put(ModelType.SATTELITE, sgc);

    ff = new File("Data/blender/cube.3ds");
    sgc = loadTemplateSGC(ff);
    templateSGCs.put(ModelType.CUBE, sgc);

    sgc = Primitives.sphere(1.0, new double[] { 0.0, 0.0, 0.0 });
    templateSGCs.put(ModelType.SPHERE, sgc);

    Appearance app = new Appearance();
    app.setAttribute(CommonAttributes.VERTEX_DRAW, false);
    app.setAttribute(CommonAttributes.EDGE_DRAW, false);
    app.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, false);
    app.setAttribute(CommonAttributes.PICKABLE, false);

    for (SceneGraphComponent val : templateSGCs.values()) {
      val.setAppearance(app);
    }
  }

  /*********************************************************************************
   * loadTemplateSGC
   * 
   * This method reads a scene graph component from the input file object. Note:
   * Since reading from disk is expensive, this method is only supposed to be
   * used to initialize the "templateSGCs" map. When a marker model is needed,
   * we make a copy of one of the templates in this map.
   *********************************************************************************/
  private static SceneGraphComponent loadTemplateSGC(File ff) {
    SceneGraphComponent sgc = null;
    try {
      sgc = Readers.read(ff);
    } catch (Exception ee) {
      System.err.println("Error: Unable to read model file " + ff);
      ee.printStackTrace();
      System.exit(1);
    }
    return sgc;
  }

  /*********************************************************************************
   * copySceneGraph
   * 
   * This method makes a copy of the input scene graph based at root. Naturally,
   * this involves recursively copying all scene graph components below root.
   * Since we only need geometry and appearance attributes, these are what we
   * copy. Future versions may require us to copy other data.
   *********************************************************************************/
  private static SceneGraphComponent copySceneGraph(SceneGraphComponent root) {
    SceneGraphComponent sgc = new SceneGraphComponent();

    sgc.setAppearance(root.getAppearance());
    sgc.setGeometry(root.getGeometry());

    int numChildren = root.getChildComponentCount();
    for (int ii = 0; ii < numChildren; ii++) {
      SceneGraphComponent child = root.getChildComponent(ii);
      sgc.addChild(copySceneGraph(child));
    }

    return sgc;
  }

  /*********************************************************************************
   * Private Data
   * 
   * These parameters allow us to set further appearance attributes for specific
   * models.
   *********************************************************************************/
  private double scale = 1.0;
  private double default_scale = 1.0;
  private Color color = Color.BLUE;
  private ModelType model = ModelType.ANT;

  /*********************************************************************************
   * Constructors
   * 
   * Several different constructors are provided below. By default, a marker
   * will have the ant appearance with scaling factor 1.0.
   *********************************************************************************/
  public MarkerAppearance(ModelType mt, double scale) {
    this.setModelType(mt);
    this.setScale(scale);

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

  public MarkerAppearance(ModelType mt) {
    this(mt, 1.0);
  }

  public MarkerAppearance() {
    this(ModelType.ANT, 1.0);
  }

  public void setModelType(ModelType mt) {
    this.model = mt;
  }

  /*********************************************************************************
   * setDefaultScale / getDefaultScale
   * 
   * These methods should be used to scale models so that they have a particular
   * size relative to the other models in the scene. The setScale methods exist
   * so users can dynamically resize models however they want, without losing
   * the "default scale" data, which is chosen to make the model look reasonable
   * in most scenes.
   * 
   *********************************************************************************/
  public void setDefaultScale(double scale) {
    this.default_scale = scale;
  }

  public double getDefaultScale() {
    return default_scale;
  }

  public void setScale(double scale) {
    this.scale = scale;
  }

  public double getScale() {
    return this.scale;
  }

  public void setColor(Color color) {
    this.color = color;
  }

  public Color getColor() {
    return color;
  }

  public boolean isTransparent() {
    return (color.getTransparency() == Transparency.TRANSLUCENT);
  }

  public Appearance getJRealityAppearance() {
    Appearance app = new Appearance();
    app.setAttribute(CommonAttributes.VERTEX_DRAW, false);
    app.setAttribute(CommonAttributes.EDGE_DRAW, false);
    app.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, false);
    app.setAttribute(CommonAttributes.PICKABLE, false);
    return app;
  }

  /*********************************************************************************
   * prepareNewSceneGraphComponent
   * 
   * This method is the primary method called by users of MarkerAppearance.
   * Given the appearance data provided to a given instance of MarkerAppearance,
   * that instance can use this method to create a new scene graph component
   * (not referenced in any other scene graph) representing that data. Thus,
   * users of MarkerAppearance are freed from having to load model meshes, deal
   * with their textures, etc.
   *********************************************************************************/
  public SceneGraphComponent prepareNewSceneGraphComponent() {
    SceneGraphComponent sgc = copySceneGraph(templateSGCs.get(this.model));

    Appearance app = new Appearance();
    app.setAttribute(CommonAttributes.VERTEX_DRAW, false);
    app.setAttribute(CommonAttributes.EDGE_DRAW, false);
    app.setAttribute(CommonAttributes.FACE_DRAW, true);
    app.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, false);
    app.setAttribute(CommonAttributes.PICKABLE, false);    
    app.setAttribute(CommonAttributes.LIGHTING_ENABLED, true);
    app.setAttribute(CommonAttributes.SMOOTH_SHADING, true);
    
    sgc.setAppearance(app);
    double[] mat = MatrixBuilder.euclidean().scale(this.scale).getMatrix()
        .getArray();
    Transformation t = new Transformation(mat);
    sgc.setTransformation(t);
    sgc.setVisible(true);

    return sgc;
  }

}
