package view;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import view.SGCTree.SGCNode;
import de.jreality.geometry.IndexedLineSetFactory;
import de.jreality.geometry.PointSetFactory;
import de.jreality.plugin.JRViewer;
import de.jreality.scene.Appearance;
import de.jreality.scene.Camera;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.SceneGraphPath;
import de.jreality.scene.Viewer;
import de.jreality.shader.CommonAttributes;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultPointShader;
import de.jreality.shader.ShaderUtility;
import de.jreality.util.SceneGraphUtility;
import development.Vector;

public class DevelopmentView2D extends JRViewer implements Observer {
  private SGCTree sgcTree;
  private SceneGraphComponent sgcRoot;
  private ColorScheme colorScheme;
  private int maxDepth;

  private Vector cameraForward = new Vector(1, 0);
  private SceneGraphComponent viewingDirection = new SceneGraphComponent();
  private SceneGraphComponent sgcCamera;
  private Viewer viewer;
  private SceneGraphPath cameraFree;

  public DevelopmentView2D(Development development, ColorScheme scheme) {
    maxDepth = development.getDesiredDepth();
    colorScheme = scheme;
    sgcTree = new SGCTree(development, colorScheme, 2);
    sgcRoot = new SceneGraphComponent();
    updateSGC(sgcTree.getRoot(), 0);
    setViewingDirection(cameraForward);
    sgcRoot.addChild(viewingDirection);
    this.setContent(sgcRoot);
    this.startup();

    // make camera and sgc_camera
    Camera camera = new Camera();
    camera.setNear(.015);
    camera.setFieldOfView(60);

    sgcCamera = SceneGraphUtility.createFullSceneGraphComponent("camera");
    sgcRoot.addChild(sgcCamera);
    sgcCamera.setCamera(camera);
    updateCamera();

    viewer = this.getViewer();
    cameraFree = viewer.getCameraPath();
    viewer.setCameraPath(cameraFree);
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

  private void updateCamera() {

    de.jreality.math.Matrix M = new de.jreality.math.Matrix(
        cameraForward.getComponent(1), 0, cameraForward.getComponent(0), 0,
        -cameraForward.getComponent(0), 0, cameraForward.getComponent(1), 0, 0,
        1, 0, 0, 0, 0, 0, 1);
    M.assignTo(sgcCamera);
  }

  @Override
  public void update(Observable development, Object arg) {
    String whatChanged = (String)arg;
    if (whatChanged.equals("surface")) {
      sgcTree = new SGCTree((Development) development, colorScheme, 2);
      updateSGC(sgcTree.getRoot(), 0);
    } else if (whatChanged.equals("depth")) {
      maxDepth = ((Development) development).getDesiredDepth();
      sgcTree.setVisibleDepth(maxDepth);
    }
  }

  public void setColorScheme(ColorScheme scheme) {
    colorScheme = scheme;
    sgcTree.setColorScheme(colorScheme);
  }

  private void clearSGC() {
    List<SceneGraphComponent> list = sgcRoot.getChildComponents();
    while(list.size() > 0) {
      sgcRoot.removeChild(list.get(0));
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

  public void setViewingDirection(Vector vector) {
    Appearance app_points = new Appearance();
    app_points.setAttribute(CommonAttributes.TUBE_RADIUS, 0.005);
    // set point shader
    DefaultGeometryShader dgs = (DefaultGeometryShader) ShaderUtility
        .createDefaultGeometryShader(app_points, true);
    DefaultPointShader dps = (DefaultPointShader) dgs.getPointShader();
    dps.setSpheresDraw(true);
    dps.setPointRadius(0.01);
    dps.setDiffuseColor(Color.BLUE);
    viewingDirection.setAppearance(app_points);

    IndexedLineSetFactory ilsf = new IndexedLineSetFactory();
    ilsf.setVertexCount(2);
    ilsf.setEdgeCount(1);

    ilsf.setVertexCoordinates(new double[][] { { 0, 0, 0 },
        { vector.getComponent(0), vector.getComponent(1), 0 } });
    // ilsf.setVertexColors(new Color[] { Color.blue, Color.black });
    ilsf.setEdgeIndices(new int[][] { { 0, 1 } });
    ilsf.update();
    viewingDirection.setGeometry(ilsf.getGeometry());
  }

  public void rotate(double angle) {
    double cos = Math.cos(-angle);
    double sin = Math.sin(-angle);
    double x = cameraForward.getComponent(0);
    double y = cameraForward.getComponent(1);

    double x_new = cos * x - sin * y;
    double y_new = sin * x + cos * y;
    cameraForward = new Vector(x_new, y_new);
    sgcRoot.removeChild(viewingDirection);
    setViewingDirection(cameraForward);
    sgcRoot.addChild(viewingDirection);
    updateCamera();
  }

}
