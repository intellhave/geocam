package view;

import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import view.SGCTree.SGCNode;
import de.jreality.math.Pn;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.Scene;
import de.jreality.scene.Camera;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.SceneGraphPath;
import de.jreality.scene.Viewer;
import de.jreality.util.CameraUtility;
import de.jreality.util.SceneGraphUtility;
import development.Development;
import development.Vector;

public class DevelopmentView3D extends JRViewer implements Observer {
  private Viewer viewer;
  private SceneGraphPath camera_source;
  private SceneGraphComponent sgc_camera;
  private SceneGraphComponent sgcRoot;
  private SceneGraphComponent sgcDevelopment;
  private Scene scene;
  private Vector cameraForward = new Vector(-1, 0);;

  private int maxDepth;
  private ColorScheme colorScheme;
  private SGCTree sgcTree;

  public DevelopmentView3D(Development development, ColorScheme scheme) {
    sgcRoot = new SceneGraphComponent();
    sgcDevelopment = new SceneGraphComponent();
    
    // make camera and sgc_camera
    Camera camera = new Camera();
    camera.setNear(.015);
    camera.setFieldOfView(60);
   
    sgc_camera = SceneGraphUtility.createFullSceneGraphComponent("camera");
    sgcRoot.addChild(sgc_camera);
    sgc_camera.setCamera(camera);
    updateCamera();

    maxDepth = development.getDesiredDepth();
    colorScheme = scheme;
    
    // System.out.println("building SGCTree");
    sgcTree = new SGCTree(development, colorScheme, 3);
    // System.out.println("done");
    
    // System.out.println("seting SGC");
    updateSGCDevelopment(sgcTree.getRoot(), 0);
    sgcRoot.addChild(sgcDevelopment);
    // System.out.println("done");
    
    this.setContent(sgcRoot);
    scene = this.getPlugin(Scene.class);
    this.startup();
    CameraUtility.encompass(scene.getAvatarPath(), scene.getContentPath(),
        scene.getCameraPath(), 1.75, Pn.EUCLIDEAN);

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

  private void updateSGCDevelopment(SGCNode node, int depth) {
    if (depth > maxDepth)
      return;
    if (depth == 0)
      clearSGC(sgcDevelopment);

    sgcDevelopment.addChild(node.getSGC());
    Iterator<SGCNode> itr = node.getChildren().iterator();
    while (itr.hasNext()) {
      updateSGCDevelopment(itr.next(), depth + 1);
    }
  }

  @Override
  public void update(Observable development, Object arg) {
    String whatChanged = (String) arg;
    if (whatChanged.equals("depth")) {
      maxDepth = ((Development) development).getDesiredDepth();
      sgcTree.setVisibleDepth(maxDepth);
    } else if(whatChanged.equals("surface")) {
      sgcTree = new SGCTree((Development)development, colorScheme, 3);
      sgcRoot.removeChild(sgcDevelopment);
      updateSGCDevelopment(sgcTree.getRoot(), 0);
      sgcRoot.addChild(sgcDevelopment);
    }
    updateCamera();
  }

  public void setColorScheme(ColorScheme scheme) {
    colorScheme = scheme;
    sgcTree.setColorScheme(colorScheme);
  }

  private void clearSGC(SceneGraphComponent sgc) {
    List<SceneGraphComponent> list = sgc.getChildComponents();
    while(list.size() > 0) {
      sgc.removeChild(list.get(0));
    }
  }

}
