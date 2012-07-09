package view;

import static de.jreality.shader.CommonAttributes.EDGE_DRAW;
import static de.jreality.shader.CommonAttributes.FACE_DRAW;
import static de.jreality.shader.CommonAttributes.LIGHTING_ENABLED;
import static de.jreality.shader.CommonAttributes.TRANSPARENCY_ENABLED;
import static de.jreality.shader.CommonAttributes.TUBES_DRAW;
import static de.jreality.shader.CommonAttributes.VERTEX_DRAW;

import java.awt.Color;

import javax.media.opengl.GLCanvas;

import marker.Marker;
import marker.MarkerHandler;

import de.jreality.jogl.JOGLRenderer;
import de.jreality.jogl.Viewer;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.Appearance;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.SceneGraphPath;
import de.jreality.scene.proxy.scene.Camera;
import de.jreality.shader.CommonAttributes;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultLineShader;
import de.jreality.shader.DefaultPolygonShader;
import de.jreality.shader.ShaderUtility;
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
   * Model Data and Viewing Parameters
   * 
   * These protected variables hold references to the parts of the model views
   * must inspect in order to depict the model and variables which specify how
   * to render the model (for example, how faces should be colored).
   *********************************************************************************/
  protected Development development;
  protected MarkerHandler markers;
  protected FaceAppearanceScheme faceAppearanceScheme;

  /*********************************************************************************
   * Scene Graph Data
   * 
   * These protected variables hold the important components of the scene graph
   * that makes up the view. Namely, these parts are the root of the scene
   * graph, the subgraph consisting of the polygons that will represent the
   * development at a given point, and the subgraph consisting of the "objects"
   * or "markers" that will move around in the scene.
   *********************************************************************************/
  public SceneGraphComponent sgcRoot;
  protected SceneGraphComponent sgcDevelopment;
  protected SceneGraphComponent sgcMarkers;
  protected SceneGraphComponent sgcCamera;
  protected SceneGraphPath camPath;

  /*********************************************************************************
   * JOGL Data
   * 
   * These protected variables hold the objects that JOGL needs to display the
   * scene. This include the "viewer," an object we use to transform the scene
   * graph into an actual image.
   *********************************************************************************/
  protected JOGLRenderer renderer;
  protected GLCanvas canvas;
  protected Viewer viewer;
  protected Appearance defaultAppearance;

  /*********************************************************************************
   * View
   * 
   * This constructor is responsible for setting up the scene graph and
   * appearance settings that will be used by any instance of this class.
   *********************************************************************************/
  public View(Development development, MarkerHandler markers,
      FaceAppearanceScheme fas) {
    this.development = development;
    this.markers = markers;
    this.faceAppearanceScheme = fas;

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
    sgcMarkers = new SceneGraphComponent("Objects");
    sgcDevelopment.addChild(sgcMarkers);
    sgcDevelopment.setAppearance(defaultAppearance);
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
    viewer = new Viewer(camPath, sgcRoot);
    // ToolSystem toolSystem = ToolSystem.toolSystemForViewer(viewer);
    // toolSystem.initializeSceneTools();
    // viewer.getViewingComponent();
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
   * setFaceAppearanceScheme
   * 
   * This method takes as input a ColorScheme object, and uses it to color the
   * faces/edges/vertices that make up the Development. As a side effect, this
   * method forces an update to the development's geometry.
   *********************************************************************************/
  public void setFaceAppearanceScheme(FaceAppearanceScheme scheme) {
    faceAppearanceScheme = scheme;
    updateGeometry(true, false);
  }

  /*********************************************************************************
   * setDrawEdges
   * 
   * This method uses the input boolean value to determine whether the edges in
   * the development should be explicitly drawn.
   *********************************************************************************/
  public void setDrawEdges(boolean value) {
    Appearance app = sgcDevelopment.getAppearance();
    app.setAttribute(CommonAttributes.EDGE_DRAW, value);
  }

  /*********************************************************************************
   * setDrawFaces
   * 
   * This method uses the input boolean value to determine whether the faces in
   * the development should be explicitly drawn.
   *********************************************************************************/
  public void setDrawFaces(boolean value) {
    Appearance app = sgcDevelopment.getAppearance();
    app.setAttribute(CommonAttributes.FACE_DRAW, value);
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
    defaultAppearance.setAttribute(VERTEX_DRAW, false);
    defaultAppearance.setAttribute(EDGE_DRAW, true);
    defaultAppearance.setAttribute(FACE_DRAW, true);
    defaultAppearance.setAttribute(TUBES_DRAW, false);
    defaultAppearance.setAttribute(LIGHTING_ENABLED, true);
    defaultAppearance.setAttribute(TRANSPARENCY_ENABLED, false);
    defaultAppearance.setAttribute(CommonAttributes.BACKGROUND_COLOR,
        Color.gray);
    defaultAppearance.setAttribute(CommonAttributes.DIFFUSE_COLOR, Color.white);

    DefaultGeometryShader dgs;
    dgs = (DefaultGeometryShader) ShaderUtility.createDefaultGeometryShader(
        defaultAppearance, true);
    DefaultLineShader dls = (DefaultLineShader) dgs.getLineShader();
    dls.setDiffuseColor(Color.black);

    DefaultPolygonShader dps;
    dps = (DefaultPolygonShader) dgs.createPolygonShader("default");
    dps.setDiffuseColor(Color.white);

    defaultAppearance.setAttribute("LINE_SHADER.POLYGON_SHADER.SMOOTH_SHADING",
        true);

    sgcDevelopment.setAppearance(defaultAppearance);
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
  public void updateGeometry(boolean dev, boolean obj) {
    if (dev) {
      generateManifoldGeometry();
    }
    if (obj) {
      generateMarkerGeometry();
    }
  }

  public void updateGeometry() {
    updateGeometry(true, true);
  }

  /*********************************************************************************
   * updateScene
   *********************************************************************************/
  public void updateScene() {
    updateCamera();
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
  public abstract void initializeNewManifold();

  protected abstract void updateCamera();

  /*********************************************************************************
   * removeMarker
   * 
   * This method is responsible for removing the input marker from the view.
   * Since a marker may have many scene graph components attached to it, this
   * often requires a nontrivial modification of the view's data structures that
   * is specific to the view in question.
   *********************************************************************************/
  public abstract void removeMarker(Marker m);
}