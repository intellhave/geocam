package view;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import de.jreality.geometry.IndexedLineSetFactory;
import de.jreality.math.MatrixBuilder;
import de.jreality.math.Pn;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.Scene;
import de.jreality.scene.Appearance;
import de.jreality.scene.DirectionalLight;
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
  private SceneGraphComponent objects = new SceneGraphComponent();
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
    sgcDevelopment.addChild(objects);
    sgcDevelopment.setAppearance(SGCMethods.getDevelopmentAppearance());
    
    // create light
    SceneGraphComponent sgcLight = new SceneGraphComponent();
    DirectionalLight light = new DirectionalLight();
    light.setIntensity(1.10);
    sgcLight.setLight(light);
    MatrixBuilder.euclidean().rotate(2.0, new double[]{0,1,0}).assignTo(sgcLight);
    sgcRoot.addChild(sgcLight);

    updateGeometry();
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

  @Override
  public void update(Observable development, Object arg) {
    String whatChanged = (String) arg;
    
    if (whatChanged.equals("surface") || whatChanged.equals("source")) {
      sgcTree = new SGCTree((Development) development, colorScheme, 2);
      updateGeometry();
      CameraUtility.encompass(scene.getAvatarPath(), scene.getContentPath(),
          scene.getCameraPath(), 1.75, Pn.EUCLIDEAN);
      
    } else if (whatChanged.equals("depth")) {
      maxDepth = ((Development) development).getDesiredDepth();
      sgcTree.setVisibleDepth(maxDepth);
      updateGeometry();
    }
  }

  private void updateGeometry() {
    sgcDevelopment.removeChild(objects);
    sgcDevelopment.setGeometry(sgcTree.getGeometry());
    objects = sgcTree.getObjects();
    sgcDevelopment.addChild(objects);
  }

  public void setColorScheme(ColorScheme scheme) {
    colorScheme = scheme;
    sgcTree.setColorScheme(colorScheme);
    updateGeometry();
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

    ilsf.setVertexCoordinates(new double[][] { { 0, 0, 1 },
        { vector.getComponent(0), vector.getComponent(1), 1 } });
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
