package view;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import view.SGCTree.SGCNode;
import de.jreality.geometry.IndexedLineSetFactory;
import de.jreality.math.Pn;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.Scene;
import de.jreality.scene.Appearance;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.shader.CommonAttributes;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultPointShader;
import de.jreality.shader.ShaderUtility;
import de.jreality.util.CameraUtility;
import development.Development;
import development.Vector;

public class DevelopmentView2D extends JRViewer implements Observer {
  private SGCTree sgcTree;
  private SceneGraphComponent sgcRoot;
  private SceneGraphComponent sgcDevelopment;
  private Scene scene;
  private ColorScheme colorScheme;
  private int maxDepth;

  private Vector cameraForward = new Vector(2, 0);
  private SceneGraphComponent viewingDirection = new SceneGraphComponent();

  public DevelopmentView2D(Development development, ColorScheme scheme) {
    maxDepth = development.getDesiredDepth();
    colorScheme = scheme;
    sgcTree = new SGCTree(development, colorScheme, 2);
    sgcRoot = new SceneGraphComponent();
    sgcDevelopment = new SceneGraphComponent();
    
    updateSGC(sgcTree.getRoot(), 0);
    sgcRoot.addChild(sgcDevelopment);    
    sgcRoot.addChild(viewingDirection);
    setViewingDirection(cameraForward);
    this.addBasicUI();
 
    this.setContent(sgcRoot);
    scene = this.getPlugin(Scene.class);
    CameraUtility.encompass(scene.getAvatarPath(), scene.getContentPath(),
        scene.getCameraPath(), 1.75, Pn.EUCLIDEAN);
    
    this.startup();
  }

  private void updateSGC(SGCNode node, int depth) {
    if (depth > maxDepth)
      return;
    if (depth == 0)
      clearSGC(sgcDevelopment);

    sgcDevelopment.addChild(node.getSGC());
    Iterator<SGCNode> itr = node.getChildren().iterator();
    while (itr.hasNext()) {
      updateSGC(itr.next(), depth + 1);
    }
  }

  @Override
  public void update(Observable development, Object arg) {
    String whatChanged = (String)arg;
    
    if (whatChanged.equals("surface")) {
      sgcTree = new SGCTree((Development) development, colorScheme, 2);
      sgcRoot.removeChild(sgcDevelopment);
      updateSGC(sgcTree.getRoot(), 0);
      sgcRoot.addChild(sgcDevelopment);
      CameraUtility.encompass(scene.getAvatarPath(), scene.getContentPath(),
          scene.getCameraPath(), 1.75, Pn.EUCLIDEAN);
      
    } else if (whatChanged.equals("depth")) {
      maxDepth = ((Development) development).getDesiredDepth();
      sgcTree.setVisibleDepth(maxDepth);
    }
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
  }

}
