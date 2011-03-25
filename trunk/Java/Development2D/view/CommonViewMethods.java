package view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import objects.ManifoldObjectHandler;
import objects.ManifoldPath;
import objects.VisibleObject;
import objects.VisiblePath;
import de.jreality.scene.SceneGraphComponent;
import development.AffineTransformation;
import development.DevelopmentNode;
import development.Frustum2D;
import development.LineSegment;
import development.Vector;

public class CommonViewMethods {

  protected static SceneGraphComponent generateDevelopmentObjectGeometry(DevelopmentNode devRoot, boolean clipNear, double clipNearRadius){

    //instead of vector, use an affine transformation (to record position + orientation of images)
    HashMap<VisibleObject,ArrayList<Vector>> objectImages = new HashMap<VisibleObject,ArrayList<Vector>>();
    HashMap<VisiblePath,ArrayList<LineSegment>> pathImages = new HashMap<VisiblePath,ArrayList<LineSegment>>();
    
    getDevelopmentObjectImages(devRoot, objectImages, clipNear, clipNearRadius);
    getDevelopmentPathImages(devRoot, pathImages); 
    
    //generate sgc's for the objects
    SceneGraphComponent sgcNewObjects = new SceneGraphComponent("Objects");
    
    Set<VisibleObject> objectList = objectImages.keySet();
    for(VisibleObject o : objectList){
      sgcNewObjects.addChild(SGCMethods.objectSGCFromList(objectImages.get(o), o.getAppearance(), true, 0));
    }
    
    Set<VisiblePath> pathList = pathImages.keySet();
    for(VisiblePath p : pathList){
      ArrayList<LineSegment> images = pathImages.get(p);
      if(images.size() == 0){ continue; }
      sgcNewObjects.addChild(SGCMethods.pathSGCFromList(images, p.getAppearance(), true, 0));
    }

    return sgcNewObjects;
  }

  /*
   * Recursively adds geometry for each face in tree to a DevelopmentGeometrySim3D, 
   * and adds nodes to nodeList (should be empty at start)
   */
  private static void getDevelopmentObjectImages(DevelopmentNode devNode, HashMap<VisibleObject,ArrayList<Vector>> objectImages, boolean clipNear, double clipNearRadius) {
        
    //look for objects
    Collection<VisibleObject> objectList = ManifoldObjectHandler.getObjects(devNode.getFace());
    if(objectList != null){
      
      Frustum2D frustum = devNode.getFrustum();
      AffineTransformation affineTrans = devNode.getAffineTransformation();
      
      for(VisibleObject o : objectList){
        if(!o.isVisible()){ continue; }

        Vector transPos = affineTrans.affineTransPoint(o.getPosition());
        //check if object image should be clipped by frustum
        if(frustum != null){
          if(!frustum.checkInterior(transPos)){ continue; }
        }
        //check if object should be clipped by specified clipNearRadius (ok, sqrt(radius), but who cares)
        if(transPos.lengthSquared() < clipNearRadius){ continue; }
        
        //add to image list
        ArrayList<Vector> imageList = objectImages.get(o);
        if(imageList == null){
          imageList = new ArrayList<Vector>();
          objectImages.put(o,imageList);
        }
        imageList.add(transPos);
      }
    }

    Iterator<DevelopmentNode> itr = devNode.getChildren().iterator();
    while (itr.hasNext()) {
      getDevelopmentObjectImages(itr.next(), objectImages, clipNear, clipNearRadius);
    }
  }
  
  private static void getDevelopmentPathImages(DevelopmentNode devNode, HashMap<VisiblePath,ArrayList<LineSegment>> pathImages) {
        
    //look for paths
    Collection<VisiblePath> pathList = ManifoldObjectHandler.getPaths(devNode.getFace());
    if(pathList != null){
      
      Frustum2D frustum = devNode.getFrustum();
      AffineTransformation affineTrans = devNode.getAffineTransformation();
      
      for(VisiblePath p : pathList){
        if(!p.isVisible()){ continue; }

        //get list of segments of this path contained in the face f
        Collection<ManifoldPath.Segment> segments = p.getPathSegmentsInFace(devNode.getFace());
        if(segments == null){ continue; }
        
        //make image list for this path if one doesn't already exist
        ArrayList<LineSegment> imageList = pathImages.get(p);
        if(imageList == null){
          imageList = new ArrayList<LineSegment>();
          pathImages.put(p, imageList);
        }
        
        //add the clipped path segments to the list
        for(ManifoldPath.Segment s : segments){
          //get clipped and transformed line segment
          LineSegment cs = LineSegment.clipWithFrustum(frustum, new LineSegment(
              affineTrans.affineTransPoint(s.startPos),
              affineTrans.affineTransPoint(s.endPos)
          ));
          //clipWithFrustum returns null if the intersection is empty
          if(cs != null){ imageList.add(cs); }
        }
      }
    }

    Iterator<DevelopmentNode> itr = devNode.getChildren().iterator();
    while (itr.hasNext()) {
      getDevelopmentPathImages(itr.next(), pathImages);
    }
  }
  
}
