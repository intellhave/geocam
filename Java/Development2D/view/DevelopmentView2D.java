package view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.geometry.IndexedLineSetFactory;
import de.jreality.math.MatrixBuilder;
import de.jreality.math.Pn;
import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.Scene;
import de.jreality.scene.Appearance;
import de.jreality.scene.DirectionalLight;
import de.jreality.scene.Geometry;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.shader.CommonAttributes;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultPointShader;
import de.jreality.shader.ShaderUtility;
import de.jreality.util.CameraUtility;
import development.Development;
import development.Development.DevelopmentNode;
import development.Vector;

public class DevelopmentView2D extends JRViewer implements Observer {
  private SceneGraphComponent sgcRoot;
  private SceneGraphComponent sgcDevelopment;
  private SceneGraphComponent objects = new SceneGraphComponent();
  private Scene scene;
  private ColorScheme colorScheme;
  private Development development;

  private Vector cameraForward = new Vector(2, 0);
  private SceneGraphComponent viewingDirection = new SceneGraphComponent();

  public DevelopmentView2D(Development dev, ColorScheme scheme) {
    development = dev;
    colorScheme = scheme;
    sgcRoot = new SceneGraphComponent();
    sgcDevelopment = new SceneGraphComponent();
    sgcDevelopment.addChild(objects);
    sgcDevelopment.setAppearance(SGCMethods.getDevelopmentAppearance());

    // create light
    SceneGraphComponent sgcLight = new SceneGraphComponent();
    DirectionalLight light = new DirectionalLight();
    light.setIntensity(1.10);
    sgcLight.setLight(light);
    MatrixBuilder.euclidean().rotate(2.0, new double[] { 0, 1, 0 })
        .assignTo(sgcLight);
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
  public void update(Observable dev, Object arg) {
    String whatChanged = (String) arg;
    development = (Development) dev;

      updateGeometry();
      CameraUtility.encompass(scene.getAvatarPath(), scene.getContentPath(),
          scene.getCameraPath(), 1.75, Pn.EUCLIDEAN);

  }

  private void updateGeometry() {
    sgcDevelopment.removeChild(objects);
    objects = new SceneGraphComponent();
    sgcDevelopment.setGeometry(getGeometry());
    sgcDevelopment.addChild(objects);
  }

  public Geometry getGeometry() {
    DevelopmentGeometry geometry = new DevelopmentGeometry();
    ArrayList<Color> colors = new ArrayList<Color>();
    computeDevelopment(development.getRoot(), colors, geometry);
    IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();

    Color[] colorList = new Color[colors.size()];
    for (int i = 0; i < colors.size(); i++) {
      colorList[i] = colors.get(i);
    }

    double[][] ifsf_verts = geometry.getVerts();
    int[][] ifsf_faces = geometry.getFaces();

    ifsf.setVertexCount(ifsf_verts.length);
    ifsf.setVertexCoordinates(ifsf_verts);
    ifsf.setFaceCount(ifsf_faces.length);
    ifsf.setFaceIndices(ifsf_faces);
    ifsf.setGenerateEdgesFromFaces(true);
    ifsf.setFaceColors(colorList);
    ifsf.update();
    return ifsf.getGeometry();
  }

  /*
   *  Adds appropriate source point objects to objects SGC
   */
  private void computeDevelopment(DevelopmentNode node,
      ArrayList<Color> colors, DevelopmentGeometry geometry) {
    if (node.faceIsSource()) {
      Vector sourcePoint = development.getSourcePoint();
      Vector newSource = new Vector(sourcePoint.getComponent(0),
          sourcePoint.getComponent(1), 1);
      Vector transSourcePoint = node.getAffineTransformation().transformVector(
          newSource);
      Vector transSourcePoint2d = new Vector(transSourcePoint.getComponent(0),
          transSourcePoint.getComponent(1));

      if (node.getEmbeddedFace().contains(transSourcePoint2d) || node.isRoot()) {
        // containment alg doesn't work for root
        objects.addChild(SGCMethods.sgcFromPoint(transSourcePoint));
      }
    }

    double[][] face = node.getEmbeddedFace().getVectorsAsArray();
    geometry.addFace(face);
    colors.add(colorScheme.getColor(node));
    
    Iterator<DevelopmentNode> itr = node.getChildren().iterator();
    while (itr.hasNext()) {
      computeDevelopment(itr.next(), colors, geometry);
    }
  }

  // class designed to make it easy to use an IndexedFaceSetFactory
  public class DevelopmentGeometry {

    private ArrayList<double[]> geometry_verts = new ArrayList<double[]>();
    private ArrayList<int[]> geometry_faces = new ArrayList<int[]>();

    public void addFace(double[][] faceverts) {

      int nverts = faceverts.length;
      int vi = geometry_verts.size();

      int[] newface = new int[nverts];
      for (int k = 0; k < nverts; k++) {
        double[] newvert = new double[3];
        newvert[0] = faceverts[k][0];
        newvert[1] = faceverts[k][1];
        newvert[2] = 1.0;
        geometry_verts.add(newvert);
        newface[k] = vi++;
      }
      geometry_faces.add(newface);
    }

    public double[][] getVerts() {
      return (double[][]) geometry_verts.toArray(new double[0][0]);
    }

    public int[][] getFaces() {
      return (int[][]) geometry_faces.toArray(new int[0][0]);
    }
  };

  public void setColorScheme(ColorScheme scheme) {
    colorScheme = scheme;
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
