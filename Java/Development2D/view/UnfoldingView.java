package view;

import java.util.HashMap;
import java.util.List;

import de.jreality.geometry.IndexedFaceSetFactory;
import de.jreality.math.Matrix;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.Appearance;
import de.jreality.scene.Camera;
import de.jreality.scene.DirectionalLight;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.data.Attribute;
import development.EmbeddedTriangulation;
import development.ManifoldPosition;
import development.TextureCoords;
import development.Vector;
import triangulation.Face;
import triangulation.FaceGrouping;
import triangulation.Triangulation;
import view.TextureLibrary.TextureDescriptor;
import marker.Marker;

public class UnfoldingView extends View {

  private Marker source;
  private SceneGraphComponent sourceSGC;

  public UnfoldingView(Marker source, FaceAppearanceScheme fas) {
    super(null, null, fas);
    this.source = source;
   sourceSGC = source.getAppearance().makeSceneGraphComponent();
   sgcMarkers.addChild(sourceSGC);
   double[][] light_psns = { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 },
       { -1, 0, 0 }, { 0, -1, 0 }, { 0, 0, -1 } };

   Vector origin = new Vector(0, 0, 0);
   for (double[] psn : light_psns) {
     SceneGraphComponent sgcLight = new SceneGraphComponent();
     DirectionalLight light = new DirectionalLight();
     light.setIntensity(1.0);
     sgcLight.setLight(light);

     MatrixBuilder m = View.lookAt(new Vector(psn), origin);
     m.assignTo(sgcLight);

     sgcDevelopment.addChild(sgcLight);
   }
    
  }

  public void initializeNewManifold() {
    Face f = source.getPosition().getFace();
    SceneGraphComponent sgc = makeSGC(f);
    sgcDevelopment.addChild(sgc);
    FaceGrouping fg = Triangulation.groupTable.get(f);
    List<Face> connectedFaces = fg.getFaces();
    System.out.println("Number of faces in faceGrouping is: "+connectedFaces.size());
    for(Face ff: connectedFaces){
      if(ff!=f)
        sgc.addChild(makeSGC(ff));
    }
  }
  
  protected SceneGraphComponent makeSGC(Face f){
    double[][] verts = EmbeddedTriangulation.getFaceGeometry(f);
    double[][] texCoords = TextureCoords.getCoordsAsArray(f);
    int[][] ifsf_faces = new int[][] { { 0, 1, 2 } };

    IndexedFaceSetFactory ifsf = new IndexedFaceSetFactory();
    ifsf.setVertexCount(verts.length);
    ifsf.setVertexCoordinates(verts);
    ifsf.setFaceCount(ifsf_faces.length);
    ifsf.setFaceIndices(ifsf_faces);
    ifsf.setGenerateEdgesFromFaces(true);
    ifsf.setGenerateFaceNormals(true);
    ifsf.setVertexAttribute(Attribute.TEXTURE_COORDINATES, texCoords);
    ifsf.update();
    Appearance app;

    TextureDescriptor td = faceAppearanceScheme.getTextureDescriptor(f);
    app = TextureLibrary.getAppearance(td);

    SceneGraphComponent sgc = new SceneGraphComponent();
    sgc.setGeometry(ifsf.getGeometry());
    sgc.setAppearance(app);
    sgc.setVisible(true);
    return sgc;
    
  }

  protected void generateMarkerGeometry() {
    Face f = source.getPosition().getFace();
    ManifoldPosition sourcePos = source.getPosition();
    Vector[] tuple = new Vector[4];
    tuple[0]= EmbeddedTriangulation.getCoord3D(f, sourcePos.getPosition());
    tuple[1] = EmbeddedTriangulation.embedVector(f,  sourcePos.getDirectionForward());
    tuple[2] = EmbeddedTriangulation.embedVector(f, sourcePos.getDirectionLeft());
    tuple[3]= EmbeddedTriangulation.getEmbeddedNormal(f);    
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
        .scale(source.getAppearance().getScale()).assignTo(sourceSGC);
   sourceSGC.setVisible(true);

  }

  protected void generateManifoldGeometry() {
  generateMarkerGeometry();

  }

  protected void updateCamera() {
    ManifoldPosition pos = source.getPosition();
    Vector embedPos = EmbeddedTriangulation.getCoord3D(pos.getFace(), pos.getPosition());
   
    Vector forward, left, normal;
    forward = EmbeddedTriangulation.embedVector(pos.getFace(),
        pos.getDirectionForward());
    left = EmbeddedTriangulation.embedVector(pos.getFace(),
        pos.getDirectionLeft());
    normal = EmbeddedTriangulation.getEmbeddedNormal(pos.getFace());

    forward.normalize();
    left.normalize();
    normal.normalize();

    Matrix rot = MatrixBuilder.euclidean()
        .rotate(-Math.PI / 8, left.getVectorAsArray()).getMatrix();
    Vector adjustedNormal = new Vector(rot.multiplyVector(normal
        .getVectorAsArray()));
    Vector adjustedForward = new Vector(rot.multiplyVector(forward
        .getVectorAsArray()));
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

    MatrixBuilder.euclidean().translate(adjustedNormal.getVectorAsArray())
        .translate(embedPos.getVectorAsArray()).times(matrix)
        .rotateZ(-Math.PI / 2).assignTo(sgcCamera);
    
  }

  public void removeMarker(Marker m) {
  }

}
