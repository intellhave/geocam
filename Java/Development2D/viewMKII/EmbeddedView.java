package viewMKII;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import markers.ManifoldPosition;
import markersMKII.Marker;
import markersMKII.MarkerAppearance;
import markersMKII.MarkerHandler;
import triangulation.Face;
import triangulation.Triangulation;
import view.ColorScheme;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.DirectionalLight;
import de.jreality.scene.Geometry;
import de.jreality.scene.SceneGraphComponent;
import development.EmbeddedTriangulation;
import development.Vector;

/*********************************************************************************
 * EmbeddedView
 * 
 * Given a triangulated surface embedded in 3-dimensional Euclidean space, this
 * View can present a visualization of the embedding. Specifically, the camera
 * will be positioned so that it either displays a fixed view of the surface
 * sitting in R^3, or else can be configured so that the camera hovers above a
 * designated point on the surface.
 *********************************************************************************/

public class EmbeddedView extends View {

  private HashMap<Marker, SceneGraphComponent> sgcpools;
  //private SceneGraphComponent sgcLight;

  /*********************************************************************************
   * EmbeddedView
   * 
   * Given a development object and a colorscheme, this constructor initializes
   * an EmbeddedView object to display the specified surface.
   *********************************************************************************/
  public EmbeddedView(Development d, MarkerHandler mh, ColorScheme cs) {
    super(d, mh, cs);
    sgcpools = new HashMap<Marker, SceneGraphComponent>();
    updateCamera();

    // create lights
    // TODO: Adding more than 5 lights appears to break jReality.
    int numlights = 4;
    double[][] light_psns = { { 1, 0, 1 }, { -1, 0, 1 }, { 0, 1, 1 },
        { 0, -1, 1 } };

    for (int ii = 0; ii < numlights; ii++) {
      SceneGraphComponent sgcLight = new SceneGraphComponent();
      DirectionalLight light = new DirectionalLight();
      light.setIntensity(1.5);
      sgcLight.setLight(light);
      MatrixBuilder.euclidean().scale(2).translate(light_psns[ii])
          .rotateX(light_psns[ii][0] * Math.PI / 4)
          .rotateY(light_psns[ii][1] * Math.PI / 4).assignTo(sgcLight);
      sgcDevelopment.addChild(sgcLight);
    }
  }

  /*********************************************************************************
   * updateCamera
   * 
   * TODO: Implement this method, so that the camera can either hover over a
   * fixed point or take a more "global view" of the embedded surface.
   *********************************************************************************/
  protected void updateCamera() {
    ManifoldPosition pos = development.getSourceMarker().getPosition();
    Vector embPsn = EmbeddedTriangulation.getCoord3D(pos.getFace(), pos.getPosition());

    Vector forward, left, normal;
    forward = EmbeddedTriangulation.embedVector(pos.getFace(), pos.getDirectionForward());
    left = EmbeddedTriangulation.embedVector(pos.getFace(), pos.getDirectionLeft());
    normal = EmbeddedTriangulation.getEmbeddedNormal(pos.getFace());

    forward.normalize();
    left.normalize();
    normal.normalize();

    double matrix[] = new double[16];

    matrix[0 * 4 + 0] = forward.getComponent(0);
    matrix[0 * 4 + 1] = left.getComponent(0);
    matrix[0 * 4 + 2] = normal.getComponent(0);
    matrix[0 * 4 + 3] = 0.0;

    matrix[1 * 4 + 0] = forward.getComponent(1);
    matrix[1 * 4 + 1] = left.getComponent(1);
    matrix[1 * 4 + 2] = normal.getComponent(1);
    matrix[1 * 4 + 3] = 0.0;

    matrix[2 * 4 + 0] = forward.getComponent(2);
    matrix[2 * 4 + 1] = left.getComponent(2);
    matrix[2 * 4 + 2] = normal.getComponent(2);
    matrix[2 * 4 + 3] = 0.0;

    matrix[3 * 4 + 0] = 0.0;
    matrix[3 * 4 + 1] = 0.0;
    matrix[3 * 4 + 2] = 0.0;
    matrix[3 * 4 + 3] = 1.0;

    normal.scale(2.0);

    MatrixBuilder.euclidean().translate(normal.getVectorAsArray())
        .translate(embPsn.getVectorAsArray()).times(matrix)
        .rotateZ(-Math.PI / 2).assignTo(sgcCamera);
  }

  /*********************************************************************************
   * initializeNewManifold
   * 
   * See the documentation in View.
   *********************************************************************************/
  protected void initializeNewManifold() {
    for (SceneGraphComponent sgc : sgcpools.values()) {
      sgcMarkers.removeChild(sgc);
    }
    sgcpools.clear();

    // use EmbeddedTriangulation to draw the polyhedron (if it exists)
    HashMap<Face, Color> faceColors = new HashMap<Face, Color>();
    // Set<Integer> faceIndexSet = Triangulation.faceTable.keySet();

    HashMap<Integer, Face> faceTable = Triangulation.faceTable;
    Set<Integer> faceIndices = faceTable.keySet();
    for (Integer i : faceIndices) {
      Face f = faceTable.get(i);
      faceColors.put(f, colorScheme.getColor(f));
    }

    Geometry g = EmbeddedTriangulation.get3DGeometry(faceColors);
    sgcDevelopment.setGeometry(g);
    updateCamera();    
  }

  /*********************************************************************************
   * generateManifoldGeometry
   * 
   * See the documentation in View. In this particular view, we don't ever need
   * to change the (computer-graphics related) geometry of the polygons we use
   * to represent the surface, so this method is empty.
   * 
   *********************************************************************************/
  protected void generateManifoldGeometry() {
  }

  /*********************************************************************************
   * generateMarkerGeometry
   * 
   * See the documentation in View. In this particular view, we need to position
   * and orient each marker in R^3, based upon its coordinates on the surface.
   * 
   *********************************************************************************/
  protected void generateMarkerGeometry() {

    HashMap<Marker, Vector[]> objectImages = new HashMap<Marker, Vector[]>();

    // get objects and paths for each face
    HashMap<Integer, Face> faceTable = Triangulation.faceTable;
    Set<Integer> faceIndices = faceTable.keySet();
    for (Integer i : faceIndices) {
      Face f = faceTable.get(i);
      getMarkerPlacementData(f, objectImages);
    }

    for (Marker vo : objectImages.keySet()) {
      SceneGraphComponent sgc = sgcpools.get(vo);

      if (sgc == null) {
        MarkerAppearance oa = vo.getAppearance();
        sgc = oa.prepareNewSceneGraphComponent();
        sgcpools.put(vo, sgc);
        sgcMarkers.addChild(sgc);
      }

      Vector[] tuple = objectImages.get(vo);
      Vector pos = tuple[0];
      Vector forward = Vector.normalize(tuple[1]);
      Vector left = Vector.normalize(tuple[2]);
      Vector normal = Vector.normalize(tuple[3]);

      double[] matrix = new double[16];

      matrix[0 * 4 + 0] = forward.getComponent(0);
      matrix[0 * 4 + 1] = left.getComponent(0);
      matrix[0 * 4 + 2] = normal.getComponent(0);
      matrix[0 * 4 + 3] = 0.0;

      matrix[1 * 4 + 0] = forward.getComponent(1);
      matrix[1 * 4 + 1] = left.getComponent(1);
      matrix[1 * 4 + 2] = normal.getComponent(1);
      matrix[1 * 4 + 3] = 0.0;

      matrix[2 * 4 + 0] = forward.getComponent(2);
      matrix[2 * 4 + 1] = left.getComponent(2);
      matrix[2 * 4 + 2] = normal.getComponent(2);
      matrix[2 * 4 + 3] = 0.0;

      matrix[3 * 4 + 0] = 0.0;
      matrix[3 * 4 + 1] = 0.0;
      matrix[3 * 4 + 2] = 0.0;
      matrix[3 * 4 + 3] = 1.0;

      MatrixBuilder
          .euclidean()
          .translate(pos.getComponent(0), pos.getComponent(1),
              pos.getComponent(2)).times(matrix)
          .scale(vo.getAppearance().getScale()).assignTo(sgc);
      sgc.setVisible(true);
    }

  }

  /*********************************************************************************
   * getMarkerPlacementData
   * 
   * This helper method determines, for each marker, its coordinates and
   * orientation in R^3 based upon its coordinates and orientation on the 2D
   * surface.
   *********************************************************************************/
  private void getMarkerPlacementData(Face f, HashMap<Marker, Vector[]> markerImages) {

    // look for objects
    Collection<Marker> markers = this.markers.getMarkers(f);
    if (markers == null) return;
      
    synchronized (markers) {
      for (Marker m : markers) {
        if (!m.isVisible()) continue;

        Vector[] tuple = new Vector[4];

        ManifoldPosition pos = m.getPosition();
        tuple[0] = EmbeddedTriangulation.getCoord3D(f, pos.getPosition());
        tuple[1] = EmbeddedTriangulation
            .embedVector(f, pos.getDirectionForward());
        tuple[2] = EmbeddedTriangulation.embedVector(f, pos.getDirectionLeft());
        tuple[3] = EmbeddedTriangulation.getEmbeddedNormal(f);

        markerImages.put(m, tuple);
      }
    }
  }

  @Override
  public void removeMarker(Marker m) {
    SceneGraphComponent sgc = sgcpools.get(m);
    sgcMarkers.removeChild(sgc);    
  }
}
