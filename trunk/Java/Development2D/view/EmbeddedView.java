package view;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import marker.Marker;
import marker.MarkerAppearance;
import marker.MarkerHandler;
import triangulation.Face;
import triangulation.Triangulation;
import view.TextureLibrary.TextureDescriptor;
import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.math.Matrix;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.Appearance;
import de.jreality.scene.Camera;
import de.jreality.scene.DirectionalLight;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.data.Attribute;
import development.Development;
import development.EmbeddedTriangulation;
import development.ManifoldPosition;
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
   * Given a development object and a color scheme, this constructor initializes
   * an EmbeddedView object to display the specified surface.
   *********************************************************************************/
  public EmbeddedView(Development d, MarkerHandler mh, FaceAppearanceScheme fas) {
    super(d, mh, fas);
    sgcpools = new HashMap<Marker, SceneGraphComponent>();
    updateCamera();

    // create lights
    // TODO: Adding more than 5 lights appears to break jReality.
    int numlights = 4;
    double[][] light_psns = 
      { { 1, 0, 1 }, { -1, 0, 1 }, { 0, 1, 1 }, { 0, -1, 1 } };

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
    
    Camera cam = sgcCamera.getCamera();
    //cam.setFocalLength(0.01);
    //cam.setFieldOfView(160);
    System.out.println("Focal Length:" + cam.getFocalLength());
    System.out.println("Field of View:" + cam.getFieldOfView());
    
  }

  /*********************************************************************************
   * updateCamera
   * 
   * TODO: Implement this method, so that the camera can either hover over a
   * fixed point or take a more "global view" of the embedded surface.
   *********************************************************************************/
  protected void updateCamera() {
    ManifoldPosition pos = development.getSource();
    Vector embPsn = EmbeddedTriangulation.getCoord3D(pos.getFace(), pos.getPosition());

    Vector forward, left, normal;
    forward = EmbeddedTriangulation.embedVector(pos.getFace(), pos.getDirectionForward());
    left = EmbeddedTriangulation.embedVector(pos.getFace(), pos.getDirectionLeft());
    normal = EmbeddedTriangulation.getEmbeddedNormal(pos.getFace());

    forward.normalize();
    left.normalize();
    normal.normalize();

    Matrix rot = MatrixBuilder.euclidean().rotate( -Math.PI/8, left.getVectorAsArray()).getMatrix();
    Vector adjustedNormal = new Vector(rot.multiplyVector(normal.getVectorAsArray()));
    Vector adjustedForward = new Vector(rot.multiplyVector(forward.getVectorAsArray()));
    Vector adjustedLeft = left;
    double matrix[] = new double[16];

    matrix[0 * 4 + 0] = adjustedForward.getComponent(0);
    matrix[0 * 4 + 1] = adjustedLeft.getComponent(0);
    matrix[0 * 4 + 2] = adjustedNormal.getComponent(0);
    matrix[0 * 4 + 3] = 0.0;

    matrix[1 * 4 + 0] = adjustedForward.getComponent(1);
    matrix[1 * 4 + 1] = adjustedLeft.getComponent(1);
    matrix[1 * 4 + 2] = adjustedNormal.getComponent(1);
    matrix[1 * 4 + 3] = 0.0;

    matrix[2 * 4 + 0] = adjustedForward.getComponent(2);
    matrix[2 * 4 + 1] = adjustedLeft.getComponent(2);
    matrix[2 * 4 + 2] = adjustedNormal.getComponent(2);
    matrix[2 * 4 + 3] = 0.0;

    matrix[3 * 4 + 0] = 0.0;
    matrix[3 * 4 + 1] = 0.0;
    matrix[3 * 4 + 2] = 0.0;
    matrix[3 * 4 + 3] = 1.0;

    adjustedNormal.scale(3.0);

    MatrixBuilder.euclidean()
        .translate(adjustedNormal.getVectorAsArray())
        .translate(embPsn.getVectorAsArray())
        .times(matrix)
        .rotateZ(-Math.PI / 2)
        .assignTo(sgcCamera);
  }

  /*********************************************************************************
   * initializeNewManifold
   * 
   * See the documentation in View.
   *********************************************************************************/
  public void initializeNewManifold() {
    for (SceneGraphComponent sgc : sgcpools.values()) {
      sgcMarkers.removeChild(sgc);
    }
    sgcpools.clear();

    int[][] ifsf_faces = new int[][]{ {0,1,2} };     
    
    for( Face f : Triangulation.faceTable.values() ){
      double[][] verts = EmbeddedTriangulation.getFaceGeometry(f);
      
      IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();      
      ifsf.setVertexCount(verts.length);
      ifsf.setVertexCoordinates(verts);      
      ifsf.setFaceCount(ifsf_faces.length);
      ifsf.setFaceIndices(ifsf_faces);      
      ifsf.setGenerateEdgesFromFaces(true);
      ifsf.setGenerateFaceNormals(true);
      ifsf.setVertexAttribute(Attribute.TEXTURE_COORDINATES, verts);
      ifsf.update();

      TextureDescriptor td = faceAppearanceScheme.getTextureDescriptor(f);
      Appearance app = TextureLibrary.getAppearance(td);
      
      SceneGraphComponent sgc = new SceneGraphComponent();
      sgc.setGeometry(ifsf.getGeometry());
      sgc.setAppearance(app);
      sgcDevelopment.addChild(sgc);
    }
           
    updateCamera();    
  }

  /*********************************************************************************
   * generateManifoldGeometry
   * 
   * See the documentation in View. In this particular view, we don't ever need
   * to change the (computer-graphics related) geometry of the polygons we use
   * to represent the surface, so this method is empty.
   *********************************************************************************/
  protected void generateManifoldGeometry() {
  }

  /*********************************************************************************
   * generateMarkerGeometry
   * 
   * See the documentation in View. In this particular view, we need to position
   * and orient each marker in R^3, based upon its coordinates on the surface.
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
