package view;

import java.awt.Color;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import markers.ManifoldMarkerHandler;
import markers.VisibleMarker;
import markersMKII.MarkerAppearance;

import triangulation.Face;
import triangulation.Triangulation;
import de.jreality.math.MatrixBuilder;
import de.jreality.math.Pn;
import de.jreality.plugin.basic.Scene;
import de.jreality.scene.Appearance;
import de.jreality.scene.PointLight;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.shader.CommonAttributes;
import de.jreality.tools.RotateTool;
import de.jreality.util.CameraUtility;
import development.Development;
import development.EmbeddedTriangulation;
import development.Vector;

public class DevelopmentViewEmbedded extends DevelopmentView {
  
  Appearance defaultAppearance;
  
  private HashMap<VisibleMarker, SceneGraphComponent> sgcpools;
  
  public DevelopmentViewEmbedded(Development development, ColorScheme colorScheme) {
    super(development, colorScheme, false);

    sgcpools = new HashMap<VisibleMarker, SceneGraphComponent>();
    
    this.startup();
    
    defaultAppearance = new Appearance();
    defaultAppearance.setAttribute(CommonAttributes.VERTEX_DRAW, false);
    defaultAppearance.setAttribute(CommonAttributes.TUBES_DRAW, false);
    defaultAppearance.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, false);
    //defaultAppearance.setAttribute(CommonAttributes.TRANSPARENCY, 0.05d);
    defaultAppearance.setAttribute(CommonAttributes.PICKABLE, true);
    defaultAppearance.setAttribute(CommonAttributes.AMBIENT_COLOR, Color.WHITE);

    this.addBasicUI(); //scene graph inspector causes deadlock (?)

    this.setContent(sgcRoot);
    scene = this.getPlugin(Scene.class);
    updateCamera();

    // create lights
    // TODO: Adding more than 5 lights appears to break jReality.
    int numlights = 4;
    double[][] light_psns = {{ 2, -2, 0},
                             {-2,  2, 0},
                             { 2,  2, 0},
                             {-2, -2, 0}};
        
    for( int ii = 0; ii < numlights; ii++ ){
      SceneGraphComponent sgcLight = new SceneGraphComponent();
      PointLight light = new PointLight();
      light.setIntensity(4.0);
      sgcLight.setLight(light);
      MatrixBuilder.euclidean().translate(light_psns[ii]).assignTo(sgcLight);
      sgcRoot.addChild(sgcLight);           
    }
        
    sgcRoot.addTool(new RotateTool());
    sgcDevelopment.setAppearance(defaultAppearance);

    this.startup();
  }

  private void updateCamera(){
    CameraUtility.encompass(scene.getAvatarPath(), scene.getContentPath(),
        scene.getCameraPath(), 1.75, Pn.EUCLIDEAN);
  }
  
  protected void initializeNewManifold(){
    for (SceneGraphComponent sgc : sgcpools.values()) {
      sgcObjects.removeChild(sgc);        
    }
    sgcpools.clear();    
        
    //use EmbeddedTriangulation to draw the polyhedron (if it exists)

    HashMap<Face,Color> faceColors = new HashMap<Face,Color>();
    //Set<Integer> faceIndexSet = Triangulation.faceTable.keySet();
    
    HashMap<Integer,Face> faceTable = Triangulation.faceTable;
    Set<Integer> faceIndices = faceTable.keySet();
    for(Integer i : faceIndices){
      Face f = faceTable.get(i);
      faceColors.put(f,colorScheme.getColor(f));
    }
    
    sgcDevelopment.setGeometry(EmbeddedTriangulation.get3DGeometry(faceColors));
    
    updateCamera();
    updateGeometry(true,true);
  }
  
  protected void generateManifoldGeometry(){ 
    //this should only be called when the source point moves
    //no need to remake the manifold geometry
  }

  protected void generateObjectGeometry() {

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
      
      matrix[0*4+0] = forward.getComponent(0); matrix[0*4+1] = left.getComponent(0); matrix[0*4+2] = normal.getComponent(0); matrix[0*4+3] = 0.0;
      matrix[1*4+0] = forward.getComponent(1); matrix[1*4+1] = left.getComponent(1); matrix[1*4+2] = normal.getComponent(1); matrix[1*4+3] = 0.0;
      matrix[2*4+0] = forward.getComponent(2); matrix[2*4+1] = left.getComponent(2); matrix[2*4+2] = normal.getComponent(2); matrix[2*4+3] = 0.0;
      matrix[3*4+0] = 0.0;                     matrix[3*4+1] = 0.0;                  matrix[3*4+2] = 0.0;                    matrix[3*4+3] = 1.0;
                               

      MatrixBuilder.euclidean()
                   .translate(pos.getComponent(0), pos.getComponent(1), pos.getComponent(2))
                   .times(matrix)
                   .scale(vo.getAppearance().getScale())
                   .assignTo(sgc);
      sgc.setVisible(true);
    }

  }
  
  private void getObjectEmbeddedPositionsAndOrientations(Face f, HashMap<VisibleMarker,Vector[]> objectImages){

    //look for objects
    Collection<VisibleMarker> objectList = ManifoldMarkerHandler.getObjects(f);
    if(objectList == null){ return; }
        
    synchronized(objectList) {
      for(VisibleMarker o : objectList){
        if(!o.isVisible()){ continue; }
        
        Vector[] tuple = new Vector[4];
        
        tuple[0] = EmbeddedTriangulation.getCoord3D(f,o.getPosition());
        tuple[1] = EmbeddedTriangulation.embedVector(f, o.getDirectionForward());
        tuple[2] = EmbeddedTriangulation.embedVector(f, o.getDirectionLeft());        
        tuple[3] = EmbeddedTriangulation.getEmbeddedNormal(f);
        
        objectImages.put( o, tuple );
      }
    }
  }  
}
