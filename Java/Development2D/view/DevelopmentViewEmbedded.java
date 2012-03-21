package view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import objects.ManifoldObjectHandler;
import objects.ManifoldPath;
import objects.ObjectAppearance;
import objects.VisibleObject;
import objects.VisiblePath;

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
import development.LineSegment;
import development.Vector;

public class DevelopmentViewEmbedded extends DevelopmentView {
  
  Appearance defaultAppearance;
  
  private HashMap<VisibleObject, LinkedList<SceneGraphComponent>> sgcpools;
  
  public DevelopmentViewEmbedded(Development development, ColorScheme colorScheme) {
    super(development, colorScheme, false);

    sgcpools = new HashMap<VisibleObject, LinkedList<SceneGraphComponent>>();
    
    this.startup();
    
    defaultAppearance = new Appearance();
    defaultAppearance.setAttribute(CommonAttributes.VERTEX_DRAW, false);
    defaultAppearance.setAttribute(CommonAttributes.TUBES_DRAW, false);
    defaultAppearance.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, false);
    //defaultAppearance.setAttribute(CommonAttributes.TRANSPARENCY, 0.05d);
    defaultAppearance.setAttribute(CommonAttributes.PICKABLE, true);
    defaultAppearance.setAttribute(CommonAttributes.AMBIENT_COLOR, Color.WHITE);

    //this.addBasicUI(); //scene graph inspector causes deadlock (?)

    this.setContent(sgcRoot);
    scene = this.getPlugin(Scene.class);
    updateCamera();

    // create light
    SceneGraphComponent sgcLight = new SceneGraphComponent();
    PointLight light = new PointLight();
    light.setIntensity(1.10);
    sgcLight.setLight(light);
    MatrixBuilder.euclidean().rotate(2.0, new double[] { 0, 1, 0 })
        .assignTo(sgcLight);
    sgcRoot.addChild(sgcLight);
    
  
    
    sgcRoot.addTool(new RotateTool());
    sgcDevelopment.setAppearance(defaultAppearance);

    this.startup();
  }

  private void updateCamera(){
    CameraUtility.encompass(scene.getAvatarPath(), scene.getContentPath(),
        scene.getCameraPath(), 1.75, Pn.EUCLIDEAN);
  }
  
  protected void initializeNewManifold(){
    
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
  }
  
  protected void generateManifoldGeometry(){ 
    //this should only be called when the source point moves
    //no need to remake the manifold geometry
  }

  protected void generateObjectGeometry(){
    
    //instead of vector, use an affine transformation (to record position + orientation of images)
    HashMap<VisibleObject,ArrayList<Vector>> objectImages = new HashMap<VisibleObject,ArrayList<Vector>>();
    HashMap<VisiblePath,ArrayList<LineSegment>> pathImages = new HashMap<VisiblePath,ArrayList<LineSegment>>();
    
    //get objects and paths for each face
    HashMap<Integer,Face> faceTable = Triangulation.faceTable;
    Set<Integer> faceIndices = faceTable.keySet();
    for(Integer i : faceIndices){
      Face f = faceTable.get(i);
      getObjectAmbientPositions(f, objectImages);
      getPathAmbientPositions(f, pathImages);
    }
    
    for( VisibleObject vo : objectImages.keySet() ){
      LinkedList<SceneGraphComponent> pool = sgcpools.get( vo );
      
      if( pool == null ){
        pool = new LinkedList<SceneGraphComponent>();
        sgcpools.put( vo, pool );      
      }
      
      ArrayList<Vector> images = objectImages.get( vo );
      if( images == null ) continue;
     
      if( images.size() > pool.size() ){
        int sgcCount = images.size() - pool.size();
        for( int jj = 0; jj < sgcCount; jj++ ){
           ObjectAppearance oa = vo.getAppearance();
           SceneGraphComponent sgc = oa.prepareNewSceneGraphComponent();
           pool.add( sgc );
           sgcObjects.addChild( sgc );
        }        
      }
      
      int counter = 0;
      for( SceneGraphComponent sgc : pool ){
        if( counter >= images.size()  ){
          sgc.setVisible( false );
        } else {
          Vector v = images.get( counter );
          MatrixBuilder.euclidean().translate( v.getComponent(0), v.getComponent(1), v.getComponent(2) ).assignTo( sgc );
          sgc.setVisible( true );                    
        }
        counter++;
      }
    }
    
    
//    //generate sgc's for each object and path
//    SceneGraphComponent sgcNewObjects = new SceneGraphComponent("Objects");
//    
//    Set<VisibleObject> objectList = objectImages.keySet();
//    for(VisibleObject o : objectList){
//      sgcNewObjects.addChild(SGCMethods.objectSGCFromList(objectImages.get(o), o.getAppearance(), false, 0));
//    }
//    
//    Set<VisiblePath> pathList = pathImages.keySet();
//    for(VisiblePath p : pathList){
//      sgcNewObjects.addChild(SGCMethods.pathSGCFromList(pathImages.get(p), p.getAppearance(), false, 0));
//    }
//    
//    sgcDevelopment.removeChild(sgcObjects);
//    sgcObjects = sgcNewObjects;
//    sgcDevelopment.addChild(sgcObjects);
  }
  
  private void getObjectAmbientPositions(Face f, HashMap<VisibleObject,ArrayList<Vector>> objectImages){

    //look for objects
    Collection<VisibleObject> objectList = ManifoldObjectHandler.getObjects(f);
    if(objectList == null){ return; }

    synchronized(objectList) {
      for(VisibleObject o : objectList){
        if(!o.isVisible()){ continue; }
        
        //get position in ambient coordinates
        Vector pos3D = EmbeddedTriangulation.getCoord3D(f,o.getPosition());
        
        //add to image list
        ArrayList<Vector> imageList = objectImages.get(o);
        if(imageList == null){
          imageList = new ArrayList<Vector>();
          objectImages.put(o,imageList);
        }
        imageList.add(pos3D);
      }
    }
  }
  
  private void getPathAmbientPositions(Face f, HashMap<VisiblePath,ArrayList<LineSegment>> pathImages){

    //look for paths
    Collection<VisiblePath> pathList = ManifoldObjectHandler.getPaths(f);
    if(pathList == null){ return; }

    for(VisiblePath p : pathList){
      if(!p.isVisible()){ continue; }
      
      //get list of segments of this path contained in the face f
      Collection<ManifoldPath.Segment> segments = p.getPathSegmentsInFace(f);
      if(segments == null){ continue; }
      
      //make image list for this path if one doesn't already exist
      ArrayList<LineSegment> imageList = pathImages.get(p);
      if(imageList == null){
        imageList = new ArrayList<LineSegment>();
        pathImages.put(p, imageList);
      }
      
      //add the transformed path segments to the list
      for(ManifoldPath.Segment s : segments){
        //transform each segment that appears in this face
        imageList.add(new LineSegment(
            EmbeddedTriangulation.getCoord3D(f, s.startPos),
            EmbeddedTriangulation.getCoord3D(f, s.endPos)
        ));
      }
    }
  }
  
}
