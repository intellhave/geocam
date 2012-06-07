package viewMKII;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import markers.MarkerAppearance;
import markers.VisibleMarker;
import markersMKII.MarkerHandler;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.DirectionalLight;
import de.jreality.scene.SceneGraphComponent;
import development.Development;
import development.DevelopmentNode;
import development.Vector;
import view.ColorScheme;
import view.CommonViewMethods;
import view.SGCMethods.DevelopmentGeometrySim3D;

/*********************************************************************************
 * FirstPersonView (Previously DevelopmentViewSim3D)
 * 
 * This view visualizes a triangulated 2 dimensional surface using the notion of
 * an exponential map. Starting from the face that contains a designated
 * "source point," this view unfolds pieces of the surface into a plane. Then,
 * it "thickens" the plane by a small amount, so that one can visualize what it
 * would be like to move around in this space as a two dimensional creature.
 *********************************************************************************/

public class FirstPersonView extends View {
  private double height = 25.0 / 100.0;
  private boolean showAvatar = true;
  private Vector cameraForward = new Vector(-1, 0);
  
  private HashMap<VisibleMarker, LinkedList<SceneGraphComponent>> sgcpools;

  /*********************************************************************************
   * ExponentialView
   * 
   * This method initializes a new ExponentialView to use a particular
   * development (for calculating the visualization) and color scheme (for
   * coloring the polygons that make up the visualization).
   *********************************************************************************/
  public FirstPersonView(Development dev, MarkerHandler mh, ColorScheme cs) {
    super(dev, mh, cs);
    this.sgcpools = new HashMap<VisibleMarker, LinkedList<SceneGraphComponent>>();

    SceneGraphComponent sgcLight = new SceneGraphComponent();
    DirectionalLight light = new DirectionalLight();
    light.setIntensity(1.5);
    light.setColor(Color.white);
    sgcLight.setLight(light);
    MatrixBuilder.euclidean().rotate(2.65, new double[] { 0, 1, 0 })
        .assignTo(sgcLight);

    sgcCamera.addChild(sgcLight);

    // By default, everything is upside down. This rotation corrects the
    // problem.
    MatrixBuilder.euclidean().rotate(Math.PI, new double[] { 1, 0, 0 })
        .assignTo(sgcDevelopment);
  }

  /*********************************************************************************
   * updateCamera
   * 
   * This method is responsible for positioning the camera such that the
   * development can be viewed from the perspective of a small creature on the
   * manifold.
   *********************************************************************************/
  protected void updateCamera() {
    de.jreality.math.Matrix M = new de.jreality.math.Matrix(
        cameraForward.getComponent(1), 0, cameraForward.getComponent(0), 0,
        -cameraForward.getComponent(0), 0, cameraForward.getComponent(1), 0, 
        0, 1, 0, 0, 
        0, 0, 0, 1);
    M.assignTo(sgcCamera);
  }

  /*********************************************************************************
   * generateManifoldGeometry
   * 
   * This method constructs the polygons that will make up the development, and
   * places them in the plane. The polygons are constructed via a recursive
   * procedure outlined in one of the 2010 REU papers.
   *********************************************************************************/
  protected void generateManifoldGeometry() {
    DevelopmentGeometrySim3D geometry = new DevelopmentGeometrySim3D();
    ArrayList<Color> colors = new ArrayList<Color>();
    generateManifoldGeometry(development.getRoot(), colors, geometry);
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

    sgcDevelopment.setGeometry(ifsf.getGeometry());

    updateCamera();
  }

  private void generateManifoldGeometry(DevelopmentNode devNode,
      ArrayList<Color> colors, DevelopmentGeometrySim3D geometry) {
    double[][] face = devNode.getClippedFace().getVectorsAsArray();
    geometry.addFace(face, height);

    // (adding two faces at a time)
    colors.add(colorScheme.getColor(devNode));
    colors.add(colorScheme.getColor(devNode));

    Iterator<DevelopmentNode> itr = devNode.getChildren().iterator();
    while (itr.hasNext()) {
      generateManifoldGeometry(itr.next(), colors, geometry);
    }
  }

  /*********************************************************************************
   * generateMarkerGeometry
   * 
   * This method is responsible for placing representations of the markers in
   * the visualization. Due to the nature of this particular view, a single
   * marker may appear in multiple places. This is why we need the sgcpools data
   * structure --- it keeps track of the multiple scene graph components needed
   * to depict each marker in the scene.
   *********************************************************************************/
  protected void generateMarkerGeometry() {
    HashMap<VisibleMarker, ArrayList<Vector[]>> objectImages = new HashMap<VisibleMarker, ArrayList<Vector[]>>();
    CommonViewMethods.getDevelopmentMarkerImagesAndOrientations(
        development.getRoot(), objectImages);

    for (VisibleMarker vo : objectImages.keySet()) {
      LinkedList<SceneGraphComponent> pool = sgcpools.get(vo);

      if (pool == null) {
        pool = new LinkedList<SceneGraphComponent>();
        sgcpools.put(vo, pool);
      }

      ArrayList<Vector[]> images = objectImages.get(vo);
      if (images == null)
        continue;

      if (images.size() > pool.size()) {
        int sgcCount = images.size() - pool.size();
        for (int jj = 0; jj < 2 * sgcCount; jj++) {
          MarkerAppearance oa = vo.getAppearance();
          SceneGraphComponent sgc = oa.prepareNewSceneGraphComponent();
          pool.add(sgc);
          sgcObjects.addChild(sgc);
        }
      }

      int counter = 0;
      for (SceneGraphComponent sgc : pool) {
        if (counter >= images.size()) {
          sgc.setVisible(false);
        } else {
          Vector[] triple = images.get(counter);
          Vector position = triple[0];
          Vector forward = triple[1];
          forward.normalize();
          // Vector left = triple[2];
          // left.normalize();

          double[] matrix = new double[16];
          matrix[0 * 4 + 0] = forward.getComponent(0);
          matrix[0 * 4 + 1] = -forward.getComponent(1);
          matrix[0 * 4 + 2] = 0.0;
          matrix[0 * 4 + 3] = 0.0;
          matrix[1 * 4 + 0] = forward.getComponent(1);
          matrix[1 * 4 + 1] = forward.getComponent(0);
          matrix[1 * 4 + 2] = 0.0;
          matrix[1 * 4 + 3] = 0.0;
          matrix[2 * 4 + 0] = 0.0;
          matrix[2 * 4 + 1] = 0.0;
          matrix[2 * 4 + 2] = 1.0;
          matrix[2 * 4 + 3] = 0.0;
          matrix[3 * 4 + 0] = 0.0;
          matrix[3 * 4 + 1] = 0.0;
          matrix[3 * 4 + 2] = 0.0;
          matrix[3 * 4 + 3] = 1.0;

          MatrixBuilder
              .euclidean()
              .translate(position.getComponent(0), position.getComponent(1),
                  height).times(matrix)
              .rotate(Math.PI, new double[] { 1, 0, 0 })
              .scale(vo.getAppearance().getScale()).assignTo(sgc);

          // This is a hack to find the SGC that displays the avatar.
          // In the future, we should have a dedicated SGC pointer for the
          // avatar.
          double epsilon = 0.05;
          if (position.lengthSquared() < epsilon) {
            sgc.setVisible(this.showAvatar);
          } else {
            sgc.setVisible(true);
          }
        }
        counter++;
      }
    }
  }

  /*********************************************************************************
   * initializeNewManifold
   * 
   * This method is responsible for initializing (or reinitializing) the scene
   * graph in the event that we wish to display a different manifold.
   *********************************************************************************/
  protected void initializeNewManifold() {
    for (LinkedList<SceneGraphComponent> pool : sgcpools.values()) {
      while (!pool.isEmpty()) {
        SceneGraphComponent sgc = pool.remove();
        sgcObjects.removeChild(sgc);
      }
    }
    sgcpools.clear();
    updateCamera();
    updateGeometry(true, true);

  }

  public void setDrawAvatar(boolean showAvatar) {
    this.showAvatar = showAvatar;
  }

}
