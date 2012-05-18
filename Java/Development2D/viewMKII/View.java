package viewMKII;

import java.awt.Color;

import view.ColorScheme;
import view.SGCMethods;
import de.jreality.jogl.Viewer;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.Appearance;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.SceneGraphPath;
import de.jreality.scene.proxy.scene.Camera;
import de.jreality.shader.CommonAttributes;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultPolygonShader;
import de.jreality.shader.ShaderUtility;
import de.jreality.toolsystem.ToolSystem;
import development.Development;

/*********************************************************************************
 * View
 * 
 * This class encapsulates the data and methods needed to set up a basic
 * visualization of a triangulated surface with markers. This entails setting up
 * a scene graph, adding a camera to the graph, and giving the camera a position
 * in space. Classes that extend View need to specify methods that define how
 * the geometry of the surface and markers should be presented in that
 * particular visualization.
 *********************************************************************************/

public abstract class View {

  /*********************************************************************************
   * Scene Graph Data
   * 
   * These protected variables hold the important components of the scene graph
   * that makes up the view. Namely, these parts are the root of the scene
   * graph, the subgraph consisting of the polygons that will represent the
   * development at a given point, and the subgraph consisting of the "objects"
   * or "markers" that will move around in the scene.
   *********************************************************************************/
  protected SceneGraphComponent sgcRoot;
  protected SceneGraphComponent sgcDevelopment;
  protected SceneGraphComponent sgcObjects;
  protected SceneGraphComponent sgcCamera;
  protected SceneGraphPath camPath;

  /*********************************************************************************
   * JOGL Data
   * 
   * These protected variables hold the objects that JOGL needs to display the
   * scene. This include the "viewer," an object we use to transform the scene
   * graph into an actual image. It also includes some data about how certain
   * basic geometric primitives (lines/vertices/etc) should be displayed.
   *********************************************************************************/
  protected Viewer viewer;
  protected Appearance defaultAppearance;
  protected ColorScheme colorScheme;
  protected Development development;

  /*********************************************************************************
   * View
   * 
   * This constructor is responsible for setting up the scene graph and
   * appearance settings that will be used by any instance of this class.
   *********************************************************************************/
  public View(Development development, ColorScheme colorScheme) {
    this.development = development;
    this.colorScheme = colorScheme;

    initSceneGraph();
    initAppearances();
    initViewer();
  }

  /*********************************************************************************
   * initSceneGraph
   * 
   * This method is responsible for initializing the scene graph components that
   * will hold the basic parts of any scene, namely the surface, the markers,
   * and the camera.
   *********************************************************************************/
  public void initSceneGraph() {
    sgcRoot = new SceneGraphComponent("Root");
    sgcDevelopment = new SceneGraphComponent("Development");
    sgcObjects = new SceneGraphComponent("Objects");
    sgcDevelopment.addChild(sgcObjects);
    sgcDevelopment.setAppearance(SGCMethods.getDevelopmentAppearance());
    sgcRoot.addChild(sgcDevelopment);

    // Initialize the camera.
    Camera cam = new Camera();
    cam.setFar(200.0);
    cam.setNear(0.015);
    cam.setFieldOfView(90);
    sgcCamera = new SceneGraphComponent("Camera");
    sgcRoot.addChild(sgcCamera);
    sgcCamera.setCamera(cam);
    camPath = new SceneGraphPath(sgcRoot, sgcCamera);
    camPath.push(cam);
    MatrixBuilder.euclidean().translate(0, 0, 5).assignTo(sgcCamera); // FIX
                                                                      // THIS
  }

  /*********************************************************************************
   * initViewers
   * 
   * TODO : Documentation
   *********************************************************************************/
  public void initViewer() {
    viewer = new Viewer();
    viewer.setSceneRoot(sgcRoot);
    viewer.setCameraPath(camPath);
    ToolSystem toolSystem = ToolSystem.toolSystemForViewer(viewer);
    toolSystem.initializeSceneTools();
  }

  /*********************************************************************************
   * getViewer()
   * 
   * TODO : Documentation
   *********************************************************************************/
  public Viewer getViewer() {
    return viewer;
  }

  /*********************************************************************************
   * setColorScheme
   * 
   * This method takes as input a ColorScheme object, and uses it to color the
   * faces/edges/vertices that make up the Development. As a side effect, this
   * method forces an update to the development's geometry.
   *********************************************************************************/
  public void setColorScheme(ColorScheme scheme) {
    colorScheme = scheme;
    updateGeometry(true, false);
  }

  /*********************************************************************************
   * setDrawEdges
   * 
   * This method uses the input boolean value to determine whether the edges in
   * the development should be explicitly drawn.
   *********************************************************************************/
  public void setDrawEdges(boolean value) {
    sgcDevelopment.getAppearance().setAttribute(CommonAttributes.EDGE_DRAW,
        value);
  }

  /*********************************************************************************
   * setDrawFaces
   * 
   * This method uses the input boolean value to determine whether the faces in
   * the development should be explicitly drawn.
   *********************************************************************************/
  public void setDrawFaces(boolean value) {
    sgcDevelopment.getAppearance().setAttribute(CommonAttributes.FACE_DRAW,
        value);
  }

  /*********************************************************************************
   * initAppearances
   * 
   * This method is responsible for initializing the default appearance settings
   * for the viewer. The protected variable defaultAppearance stores
   * instructions for JOGL that determine how vertices, edges, and faces should
   * be depicted.
   * 
   * For example, if edges should be depicted by colored tubes of a certain
   * diameter and vertices should be depicted with spheres of a certain
   * diameter, that should be specified here.
   *********************************************************************************/
  protected void initAppearances() {
    defaultAppearance = new Appearance();
    defaultAppearance.setAttribute(CommonAttributes.VERTEX_DRAW, false);
    defaultAppearance.setAttribute(CommonAttributes.TUBES_DRAW, false);
    defaultAppearance
        .setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, false);
    defaultAppearance.setAttribute(CommonAttributes.BACKGROUND_COLOR,
        new Color(0f, .1f, .1f));
    defaultAppearance.setAttribute(CommonAttributes.DIFFUSE_COLOR, new Color(
        1f, 0f, 0f));

    DefaultGeometryShader dgs = (DefaultGeometryShader) ShaderUtility
        .createDefaultGeometryShader(defaultAppearance, true);
    DefaultPolygonShader dps = (DefaultPolygonShader) dgs
        .createPolygonShader("default");
    //dps.setAmbientColor(Color.white);
    //dps.setDiffuseColor(Color.white);
    //dps.setAmbientCoefficient(0.2);
    //dps.setDiffuseCoefficient(0.5);

    sgcRoot.setAppearance(defaultAppearance);
  }

  /*********************************************************************************
   * updateGeometry
   * 
   * This method is responsible for updating the geometry in the view. When a
   * player moves or re-orients his marker on the surface, it will usually be
   * necessary to recompute the development and markers are visible to the
   * player in the view.
   * 
   * The "dev" boolean indicates whether the geometry of the development needs
   * to be recomputed. The "obj" boolean indicates whether the geometry of the
   * markers needs to be recalculated. Recalculating the geometry of the
   * development is more expensive than recalculating the placement of the
   * markers, so these booleans need to be used judiciously.
   *********************************************************************************/
  protected void updateGeometry(boolean dev, boolean obj) {
    if (dev) {
      generateManifoldGeometry();
    }
    if (obj) {
      generateMarkerGeometry();
    }
  }

  protected void updateGeometry() {
    updateGeometry(true, true);
  }

  /*********************************************************************************
   * updateScene
   *********************************************************************************/
  public void updateScene() {
    viewer.render();
  }

  /*********************************************************************************
   * generateManifoldGeometry
   * 
   * Classes which extend View will need to supply this method, which explains
   * how to display the manifold in this particular viewpoint. When this method
   * is called, it should update the "sgcDevelopment" part of the scene graph to
   * reflect the part of the manifold that is being viewed.
   *********************************************************************************/
  protected abstract void generateManifoldGeometry();

  /*********************************************************************************
   * generateObjectGeometry
   * 
   * Classes which extend View will need to supply this method, which explains
   * how to display the markers on the manifold in this particular viewpoint.
   * When this method is called, it should update the "sgcObjects" part of the
   * scene graph, to reflect how the markers appear in this particular view of
   * the manifold.
   *********************************************************************************/
  protected abstract void generateMarkerGeometry();

  /*********************************************************************************
   * initializeNewManifold
   * 
   * Classes which extend View will need to supply this method, which explains
   * how to reinitialize the view when the old manifold is replaced with a new
   * one.
   *********************************************************************************/
  protected abstract void initializeNewManifold();

}
