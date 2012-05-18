package viewMKII;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import markers.ManifoldMarkerHandler;
import markers.MarkerAppearance;
import markers.VisibleMarker;

import triangulation.Face;
import triangulation.Triangulation;
import view.ColorScheme;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.DirectionalLight;
import de.jreality.scene.SceneGraphComponent;
import development.Development;
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

  private HashMap<VisibleMarker, SceneGraphComponent> sgcpools;
  private SceneGraphComponent sgcLight;
  
  /*********************************************************************************
   * EmbeddedView
   * 
   * Given a development object and a colorscheme, this constructor initializes
   * an EmbeddedView object to display the specified surface.
   *********************************************************************************/
  public EmbeddedView(Development development, ColorScheme colorScheme) {
    super(development, colorScheme);
    sgcpools = new HashMap<VisibleMarker, SceneGraphComponent>();
    
    sgcLight = new SceneGraphComponent();
    DirectionalLight light = new DirectionalLight();
    light.setIntensity(4.0);
    sgcLight.setLight(light);
    sgcCamera.addChild(sgcLight);    
    
    updateCamera();
    
    // create lights
    // TODO: Adding more than 5 lights appears to break jReality.
//    int numlights = 4;
//    double[][] light_psns = { { 1, 0, 1 }, { -1, 0, 1 }, { 0, 1, 1 },
//        { 0, -1, 1 } };
//
//    for (int ii = 0; ii < numlights; ii++) {
//      SceneGraphComponent sgcLight = new SceneGraphComponent();
//      DirectionalLight light = new DirectionalLight();
//      light.setIntensity(4.0);
//      sgcLight.setLight(light);
//      MatrixBuilder
//        .euclidean()
//        .scale(2)
//        .translate(light_psns[ii])        
//        .rotateX( light_psns[ii][0] * Math.PI/4 )
//        .rotateY( light_psns[ii][1] * Math.PI/4 )
//        .assignTo(sgcLight);
//      sgcRoot.addChild(sgcLight);
//    }
  }

  /*********************************************************************************
   * updateCamera
   * 
   * TODO: Implement this method, so that the camera can either hover over a
   * fixed point or take a more "global view" of the embedded surface.
   * 
   *********************************************************************************/
  private void updateCamera() {
    VisibleMarker source = development.getSourceObject();
    Vector embPsn = EmbeddedTriangulation.getCoord3D(source.getFace(), source.getPosition());
    
    Vector forward = EmbeddedTriangulation.embedVector(source.getFace(), source.getDirectionForward());
    Vector left = EmbeddedTriangulation.embedVector(source.getFace(), source.getDirectionLeft());
    Vector normal = EmbeddedTriangulation.getEmbeddedNormal(source.getFace());
    
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
        
    MatrixBuilder
      .euclidean()      
      .translate(normal.getVectorAsArray())
      .translate(embPsn.getVectorAsArray())
      .times(matrix)      
      .assignTo(sgcCamera);    
  }

  /*********************************************************************************
   * initializeNewManifold
   * 
   * See the documentation in View.
   *********************************************************************************/
  protected void initializeNewManifold() {
    for (SceneGraphComponent sgc : sgcpools.values()) {
      sgcObjects.removeChild(sgc);
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

    sgcDevelopment.setGeometry(EmbeddedTriangulation.get3DGeometry(faceColors));

    updateCamera();
    // updateGeometry(true,true);
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
   * generateObjectGeometry
   * 
   * See the documentation in View. In this particular view, we need to position
   * and orient each marker in R^3, based upon its coordinates on the surface.
   * 
   *********************************************************************************/
  protected void generateMarkerGeometry() {

    HashMap<VisibleMarker, Vector[]> objectImages = new HashMap<VisibleMarker, Vector[]>();

    // get objects and paths for each face
    HashMap<Integer, Face> faceTable = Triangulation.faceTable;
    Set<Integer> faceIndices = faceTable.keySet();
    for (Integer i : faceIndices) {
      Face f = faceTable.get(i);
      getObjectEmbeddedPositionsAndOrientations(f, objectImages);
    }

    for (VisibleMarker vo : objectImages.keySet()) {
      SceneGraphComponent sgc = sgcpools.get(vo);

      if (sgc == null) {
        MarkerAppearance oa = vo.getAppearance();
        sgc = oa.prepareNewSceneGraphComponent();
        sgcpools.put(vo, sgc);
        sgcObjects.addChild(sgc);
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
          .translate(pos.getComponent(0), pos.getComponent(1), pos.getComponent(2))
          .times(matrix)
          .scale(vo.getAppearance().getScale()).assignTo(sgc);
      sgc.setVisible(true);
    }

  }

  /*********************************************************************************
   * getObjectEmbeddedPositionAndOrientations
   * 
   * This helper method determines, for each marker, its coordinates and
   * orientation in R^3 based upon its coordinates and orientation on the 2D
   * surface.
   *********************************************************************************/
  private void getObjectEmbeddedPositionsAndOrientations(Face f,
    HashMap<VisibleMarker, Vector[]> objectImages) {

    // look for objects
    Collection<VisibleMarker> objectList = ManifoldMarkerHandler.getObjects(f);
    if (objectList == null) {
      return;
    }

    synchronized (objectList) {
      for (VisibleMarker o : objectList) {
        if (!o.isVisible()) {
          continue;
        }

        Vector[] tuple = new Vector[4];

        tuple[0] = EmbeddedTriangulation.getCoord3D(f, o.getPosition());
        tuple[1] = EmbeddedTriangulation.embedVector(f, o.getDirectionForward());
        tuple[2] = EmbeddedTriangulation.embedVector(f, o.getDirectionLeft());
        tuple[3] = EmbeddedTriangulation.getEmbeddedNormal(f);

        objectImages.put(o, tuple);
      }
    }
  }
}
