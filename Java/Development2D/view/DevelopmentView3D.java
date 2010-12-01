package view;

import java.awt.Color;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import view.SGCTree3D.SGCNode;
import de.jreality.geometry.GeometryMergeFactory;
import de.jreality.math.MatrixBuilder;
import de.jreality.math.Pn;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.Scene;
import de.jreality.scene.Camera;
import de.jreality.scene.DirectionalLight;
import de.jreality.scene.Geometry;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.SceneGraphPath;
import de.jreality.scene.Viewer;
import de.jreality.util.CameraUtility;
import de.jreality.util.SceneGraphUtility;
import development.Development;
import development.StopWatch;
import development.Vector;

public class DevelopmentView3D extends JRViewer implements Observer {
  private Viewer viewer;
  private SceneGraphPath camera_source;
  private SceneGraphComponent sgc_camera;
  private SceneGraphComponent sgcRoot;
  private SceneGraphComponent sgcDevelopment;
  private SceneGraphComponent sgcObjects; // objects on faces, including source
                                          // point
  private Scene scene;
  private Vector cameraForward = new Vector(-1, 0);;

  private int maxDepth;
  private ColorScheme colorScheme;
  private SGCTree3D sgcTree;

  public DevelopmentView3D(Development development, ColorScheme scheme) {
    sgcRoot = new SceneGraphComponent();
    sgcDevelopment = new SceneGraphComponent();
    sgcObjects = new SceneGraphComponent();
    sgcDevelopment.addChild(sgcObjects);

    // make camera and sgc_camera
    Camera camera = new Camera();
    camera.setNear(.015);
    camera.setFieldOfView(60);

    sgc_camera = SceneGraphUtility.createFullSceneGraphComponent("camera");
    sgcRoot.addChild(sgc_camera);
    sgc_camera.setCamera(camera);
    updateCamera();

    maxDepth = development.getDepth();
    colorScheme = scheme;

    sgcTree = new SGCTree3D(development, colorScheme);

    updateGeometry();
    sgcRoot.addChild(sgcDevelopment);

    this.addBasicUI();

    this.setContent(sgcRoot);
    scene = this.getPlugin(Scene.class);
    this.startup();
    CameraUtility.encompass(scene.getAvatarPath(), scene.getContentPath(),
        scene.getCameraPath(), 1.75, Pn.EUCLIDEAN);

    viewer = this.getViewer();
    camera_source = SceneGraphUtility.getPathsBetween(viewer.getSceneRoot(),
        sgc_camera).get(0);
    camera_source.push(sgc_camera.getCamera());

    // create light
    SceneGraphComponent sgcLight = new SceneGraphComponent();
    DirectionalLight light = new DirectionalLight();
    light.setIntensity(1.50);
    light.setColor(Color.white);
    sgcLight.setLight(light);
    MatrixBuilder.euclidean().rotate(2.0, new double[] { 0, 1, 0 })
        .assignTo(sgcLight);
    sgcRoot.addChild(sgcLight);

    viewer.setCameraPath(camera_source);
    viewer.render();
  }

  private void updateCamera() {
    de.jreality.math.Matrix M = new de.jreality.math.Matrix(
        cameraForward.getComponent(1), 0, cameraForward.getComponent(0), 0,
        -cameraForward.getComponent(0), 0, cameraForward.getComponent(1), 0, 0,
        1, 0, 0, 0, 0, 0, 1);
    M.assignTo(sgc_camera);
  }

  public void rotate(double angle) {
    double cos = Math.cos(angle);
    double sin = Math.sin(angle);
    double x = cameraForward.getComponent(0);
    double y = cameraForward.getComponent(1);

    double x_new = cos * x - sin * y;
    double y_new = sin * x + cos * y;
    cameraForward = new Vector(x_new, y_new);
    updateCamera();
  }

  private void updateSGC(SGCNode node, int depth, SceneGraphComponent sgc) {
    if (depth > maxDepth)
      return;

    sgc.addChild(node.getSGC());
    Iterator<SGCNode> itr = node.getChildren().iterator();
    while (itr.hasNext()) {
      updateSGC(itr.next(), depth + 1, sgc);
    }
  }

  @Override
  public void update(Observable development, Object arg) {
    StopWatch s = new StopWatch();
    StopWatch sTotal = new StopWatch();
    sTotal.start();

    String whatChanged = (String) arg;
    if (whatChanged.equals("depth")) {
      // depth slide no longer exists
      // maxDepth = ((Development) development).getDesiredDepth();
      // sgcTree.setVisibleDepth(maxDepth);
      //
    } else if (whatChanged.equals("surface") || whatChanged.equals("source")) {
      s.start();
      sgcTree = new SGCTree3D((Development) development, colorScheme);
      s.stop();
      System.out.println("time to build SGCTree: " + s.getElapsedTime());

      updateGeometry();

    }
    updateCamera();
    System.out.println("total time to update 3d: " + sTotal.getElapsedTime());
    System.out.println();
  }

  private void updateGeometry() {
    GeometryMergeFactory mergeFact = new GeometryMergeFactory();
    SceneGraphComponent sgc = new SceneGraphComponent();

    StopWatch s = new StopWatch();
    s.start();
    updateSGC(sgcTree.getRoot(), 0, sgc);
    s.stop();
    System.out.println("time to update SGC: " + s.getElapsedTime());

    s.start();
    Geometry g = mergeFact.mergeGeometrySets(sgc);
    s.stop();
    System.out.println("time to merge geometry: " + s.getElapsedTime());

    sgcDevelopment.setGeometry(g);

    sgcDevelopment.setAppearance(SGCMethods.getDevelopmentAppearance());
    sgcDevelopment.removeChild(sgcObjects);
    sgcObjects = sgcTree.getObjects();
    sgcDevelopment.addChild(sgcObjects);
  }

  public void setColorScheme(ColorScheme scheme) {
    colorScheme = scheme;
    sgcTree.setColorScheme(colorScheme);
    updateGeometry();
  }

}
