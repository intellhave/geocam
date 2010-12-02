package view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import de.jreality.geometry.IndexedFaceSetFactory;
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
import de.jreality.tools.RotateTool;
import de.jreality.util.CameraUtility;
import de.jreality.util.SceneGraphUtility;
import development.Development;
import development.StopWatch;
import development.Vector;
import development.Development.DevelopmentNode;

public class DevelopmentView3D extends JRViewer implements Observer {
  private final double simulated3DHeight = 0.08;
  
  private Development development;

  private Viewer viewer;
  private SceneGraphPath camera_source;
  private SceneGraphComponent sgc_camera;
  private SceneGraphComponent sgcRoot;
  private SceneGraphComponent sgcDevelopment;
  private SceneGraphComponent sgcObjects; // objects on faces, including source
                                          // point
  private Scene scene;
  private Vector cameraForward = new Vector(-1, 0);;

  private ColorScheme colorScheme;

  public DevelopmentView3D(Development dev, ColorScheme scheme) {
    development = dev;
    sgcRoot = new SceneGraphComponent();
    sgcDevelopment = new SceneGraphComponent();
    sgcObjects = new SceneGraphComponent();
    sgcRoot.addChild(sgcObjects);
    
    sgcDevelopment.setAppearance(SGCMethods.getDevelopmentAppearance());

    // make camera and sgc_camera
    Camera camera = new Camera();
    camera.setNear(.015);
    camera.setFieldOfView(60);

    sgc_camera = SceneGraphUtility.createFullSceneGraphComponent("camera");
    sgcRoot.addChild(sgc_camera);
    sgc_camera.setCamera(camera);
    updateCamera();

    colorScheme = scheme;

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
    light.setIntensity(0.5);
    light.setColor(Color.white);
    sgcLight.setLight(light);
    MatrixBuilder.euclidean().rotate(2.0, new double[] { 0, 1, 0 })
        .assignTo(sgcLight);
    sgcRoot.addChild(sgcLight);
    
    sgcRoot.addTool(new RotateTool());


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

  @Override
  public void update(Observable dev, Object arg) {
    development = (Development)dev;
    StopWatch sTotal = new StopWatch();
    sTotal.start();

    String whatChanged = (String) arg;
    if (whatChanged.equals("surface") || whatChanged.equals("source")) {
      updateGeometry();
    }
    
    updateCamera();
    System.out.println("total time to update 3d: " + sTotal.getElapsedTime());
    System.out.println();
  }

  private void updateGeometry() {
    sgcRoot.removeChild(sgcObjects);
    sgcObjects = new SceneGraphComponent();
    sgcDevelopment.setGeometry(getGeometry());
    sgcRoot.addChild(sgcObjects);
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
    int[][] ifsf_edges = geometry.getEdges();

    ifsf.setVertexCount(ifsf_verts.length);
    ifsf.setVertexCoordinates(ifsf_verts);
    ifsf.setEdgeCount(ifsf_edges.length);
    ifsf.setEdgeIndices(ifsf_edges);
    ifsf.setFaceCount(ifsf_faces.length);
    ifsf.setFaceIndices(ifsf_faces);
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

      if (node.getEmbeddedFace().contains(transSourcePoint2d)) {
        System.out.println("adding object");
        sgcObjects.addChild(SGCMethods.sgcFromPoint(transSourcePoint));
      }
    }

    double[][] face = node.getEmbeddedFace().getVectorsAsArray();
    geometry.addFace(face);
    
    // (adding two faces at a time)
    colors.add(colorScheme.getColor(node));
    colors.add(colorScheme.getColor(node));
    
    Iterator<DevelopmentNode> itr = node.getChildren().iterator();
    while (itr.hasNext()) {
      computeDevelopment(itr.next(), colors, geometry);
    }
  }

  public void setColorScheme(ColorScheme scheme) {
    colorScheme = scheme;
    updateGeometry();
  }
  
  // class designed to make it easy to use an IndexedFaceSetFactory
  public class DevelopmentGeometry {

    private ArrayList<double[]> geometry_verts = new ArrayList<double[]>();
    private ArrayList<int[]> geometry_faces = new ArrayList<int[]>();
    private ArrayList<int[]> geometry_edges = new ArrayList<int[]>();


    public void addFace(double[][] faceverts) {
      
      int n = faceverts.length;
      double[][] ifsf_verts = new double[2*n][3];
      int[][] ifsf_edges = new int[3*n][2];
      int[][] ifsf_faces = new int[2][n];
      
      for (int i=0; i<n; i++){
        // for some reason, switching '-' sign makes light work
        // but colors are flipped either way
        ifsf_verts[i] = new double[]{ faceverts[i][0], faceverts[i][1], simulated3DHeight };
        ifsf_verts[i+n] = new double[]{ faceverts[i][0], faceverts[i][1], -simulated3DHeight };
      }
      
      for(int i=0; i<n; i++){
        int j = (i+1)%n;
        ifsf_edges[i] = new int[]{ i+geometry_verts.size(), j+geometry_verts.size()};
        ifsf_edges[i+n] = new int[]{ i+n+geometry_verts.size(), j+n+geometry_verts.size() };
        ifsf_edges[i+n+n] = new int[]{ i+geometry_verts.size(), i+n+geometry_verts.size() };
      }
      
      for(int i=0; i<n; i++){
        ifsf_faces[0][i] = geometry_verts.size() + i;
        ifsf_faces[1][i] = n+(n-1)-i + geometry_verts.size();
      }
      
      geometry_faces.add(ifsf_faces[0]);
      geometry_faces.add(ifsf_faces[1]);
      
      for(int i = 0; i < 2*n; i++) {
        geometry_verts.add(ifsf_verts[i]);
        geometry_edges.add(ifsf_edges[i]);
      }
      for(int i = 2*n; i < 3*n; i++) {
        geometry_edges.add(ifsf_edges[i]);
      }
      
//      Color[] colors = new Color[ifsf_faces.length];
//      for(int i = 0; i < ifsf_faces.length; i++) {
//        colors[i] = colorScheme.getColor(node);
//      }
      
    }

    public double[][] getVerts() {
      return (double[][]) geometry_verts.toArray(new double[0][0]);
    }

    public int[][] getFaces() {
      return (int[][]) geometry_faces.toArray(new int[0][0]);
    }
    
    public int[][] getEdges() {
      return (int[][]) geometry_edges.toArray(new int[0][0]);
    }
  };

}
