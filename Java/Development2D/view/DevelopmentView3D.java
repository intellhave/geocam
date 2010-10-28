package view;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import view.Development.DevelopmentNode;
import view.SGCTree.SGCNode;
import de.jreality.geometry.PointSetFactory;
import de.jreality.math.Pn;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.Scene;
import de.jreality.scene.Appearance;
import de.jreality.scene.Camera;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.SceneGraphPath;
import de.jreality.scene.Viewer;
import de.jreality.shader.CommonAttributes;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultPointShader;
import de.jreality.shader.ShaderUtility;
import de.jreality.util.CameraUtility;
import de.jreality.util.SceneGraphUtility;
import development.Vector;

public class DevelopmentView3D extends JRViewer implements Observer {
  private Viewer viewer;
  private SceneGraphPath camera_source;
  private SceneGraphComponent sgc_camera;
  private SceneGraphComponent sgcRoot;
  private SceneGraphComponent srcPnt;
  private Scene scene;
  private Vector cameraForward;

  private int maxDepth;
  private ColorScheme colorScheme;
  private SGCTree sgcTree;
  private Vector sourcePoint;

  public DevelopmentView3D(Development development, ColorScheme scheme) {
    sgcRoot = new SceneGraphComponent();

    maxDepth = development.getDesiredDepth();
    colorScheme = scheme;
    System.out.println("building SGCTree");
    sgcTree = new SGCTree(development, colorScheme, 3);
    System.out.println("done");
    sourcePoint = development.getSourcePoint();
    System.out.println("seting SGC");
    updateSGC(sgcTree.getRoot(), 0);
    System.out.println("done");
    this.setContent(sgcRoot);
    scene = this.getPlugin(Scene.class);
    this.startup();
    sgcRoot.addChild(sgcFromPoint(sourcePoint));
    CameraUtility.encompass(scene.getAvatarPath(), scene.getContentPath(),
        scene.getCameraPath(), 1.75, Pn.EUCLIDEAN);

    // make camera and sgc_camera
    Camera camera = new Camera();
    camera.setNear(.015);
    camera.setFieldOfView(60);
    cameraForward = new Vector(-1, 0);

    sgc_camera = SceneGraphUtility.createFullSceneGraphComponent("camera");
    sgcRoot.addChild(sgc_camera);
    sgc_camera.setCamera(camera);
    updateCamera();

    this.setContent(sgcRoot);
    scene = this.getPlugin(Scene.class);

    viewer = this.getViewer();
    camera_source = SceneGraphUtility.getPathsBetween(viewer.getSceneRoot(),
        sgc_camera).get(0);
    camera_source.push(sgc_camera.getCamera());

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

  private void updateSGC(SGCNode node, int depth) {
    if (depth > maxDepth)
      return;
    if (depth == 0)
      clearSGC();

    sgcRoot.addChild(node.getSGC());
    Iterator<SGCNode> itr = node.getChildren().iterator();
    while (itr.hasNext()) {
      updateSGC(itr.next(), depth + 1);
    }
  }

  @Override
  public void update(Observable development, Object arg) {
    sourcePoint = ((Development) development).getSourcePoint();
    maxDepth = ((Development) development).getDesiredDepth();
    sgcTree.setVisibleDepth(maxDepth);
    sgcRoot.addChild(sgcFromPoint(sourcePoint));
  }

  public void setColorScheme(ColorScheme scheme) {
    colorScheme = scheme;
    sgcTree.setColorScheme(colorScheme);
  }

  private void clearSGC() {
    List<SceneGraphComponent> list = sgcRoot.getChildComponents();
    for (int i = 0; i < list.size(); i++) {
      sgcRoot.removeChild(list.get(i));
    }
  }

  public static SceneGraphComponent sgcFromPoint(Vector point) {

    // create the sgc
    SceneGraphComponent sgc_points = new SceneGraphComponent();

    // create appearance
    Appearance app_points = new Appearance();

    // set some basic attributes
    app_points.setAttribute(CommonAttributes.VERTEX_DRAW, true);
    app_points.setAttribute(CommonAttributes.LIGHTING_ENABLED, true);

    // set point shader
    DefaultGeometryShader dgs = (DefaultGeometryShader) ShaderUtility
        .createDefaultGeometryShader(app_points, true);
    DefaultPointShader dps = (DefaultPointShader) dgs.getPointShader();
    dps.setSpheresDraw(true);
    dps.setPointRadius(0.01);
    dps.setDiffuseColor(Color.BLUE);

    // set appearance
    sgc_points.setAppearance(app_points);

    // set vertlist
    double[][] vertlist = { { point.getComponent(0), point.getComponent(1), 0 } };

    // create geometry with pointsetfactory
    PointSetFactory psf = new PointSetFactory();

    psf.setVertexCount(1);
    psf.setVertexCoordinates(vertlist);
    psf.update();

    // set geometry
    sgc_points.setGeometry(psf.getGeometry());

    // return
    return sgc_points;
  }

}
