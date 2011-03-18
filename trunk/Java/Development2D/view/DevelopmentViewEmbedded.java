package view;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import objects.ManifoldObjectHandler;
import objects.VisibleObject;

import triangulation.Face;
import triangulation.Triangulation;

import de.jreality.math.Pn;
import de.jreality.plugin.basic.Scene;
import de.jreality.scene.Appearance;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.shader.CommonAttributes;
import de.jreality.tools.RotateTool;
import de.jreality.util.CameraUtility;
import development.Development;
import development.EmbeddedTriangulation;
import development.Vector;

public class DevelopmentViewEmbedded extends DevelopmentView {
  
  Appearance defaultAppearance;

  public DevelopmentViewEmbedded(Development development, ColorScheme colorScheme) {
    super(development, colorScheme, false);

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
    
    HashMap<VisibleObject,ArrayList<Vector>> objectImages = new HashMap<VisibleObject,ArrayList<Vector>>();
    
    //get objects for each face
    HashMap<Integer,Face> faceTable = Triangulation.faceTable;
    Set<Integer> faceIndices = faceTable.keySet();
    for(Integer i : faceIndices){
      Face f = faceTable.get(i);
      generateObjectGeometry(f, objectImages);
    }
    
    //generate sgc's for each object
    SceneGraphComponent sgcNewObjects = new SceneGraphComponent("Objects");
    
    Set<VisibleObject> objectList = objectImages.keySet();
    for(VisibleObject o : objectList){
      if(!o.isVisible()){ continue; }
      sgcNewObjects.addChild(SGCMethods.sgcFrom3DList(objectImages.get(o), o.getAppearance()));
    }
    
    sgcDevelopment.removeChild(sgcObjects);
    sgcObjects = sgcNewObjects;
    sgcDevelopment.addChild(sgcObjects);
  }
  
  private void generateObjectGeometry(Face f, HashMap<VisibleObject,ArrayList<Vector>> objectImages){

    //look for objects
    LinkedList<VisibleObject> objectList = ManifoldObjectHandler.getObjects(f);
    if(objectList == null){ return; }

    for(VisibleObject o : objectList){

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
