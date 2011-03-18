package objects;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import triangulation.Face;
import triangulation.Triangulation;

/* This is the 'master object' which holds all VisibleObjects on the Triangulation
 * 
 * Anything which draws the triangulation should use the getObjects method to see which
 * objects are currently on the specified face
 * 
 * Any VisibleObject created is automatically handled by the ManifoldObjectHandler
 */

public class ManifoldObjectHandler{

  //references to the objects in objectList, sorted by Face
  private static int nextIndex = 0;
  private static HashMap<Face,LinkedList<VisibleObject>> sortedObjectList = new HashMap<Face,LinkedList<VisibleObject>>();
  private static HashMap<Face,LinkedList<VisiblePath>> sortedPathList = new HashMap<Face,LinkedList<VisiblePath>>();
  
  private ManifoldObjectHandler(){ }
  
  public static int generateIndex(){
    nextIndex++;
    return nextIndex;
  }
  
  //========= OBJECTS =============
  
  //status
  public static void printObjectInfo(){
    
    //quick way to check for bugs, e.g. objects in duplicate or wrong face lists
    
    System.out.println("\nOBJECT STATS:");
    
    HashMap<Integer,Face> faceTable = Triangulation.faceTable;
    Set<Integer> faceIndices = faceTable.keySet();
    for(Integer i : faceIndices){
      Face f = faceTable.get(i);
      LinkedList<VisibleObject> objectList = sortedObjectList.get(f);
      
      if(objectList == null){
        System.out.println("Face " + f.getIndex() + ": null list");
        continue;
      }
      
      int n = objectList.size();
      System.out.println("Face " + f.getIndex() + ": " + n + " objects:");
      for(VisibleObject o : objectList){
        System.out.println("  Object " + o.getIndex() + " (Face " + o.getFace().getIndex() + ")");
      }
    }
  }

  //add and remove objects
  public static void addObject(VisibleObject o){ 
    
    LinkedList<VisibleObject> objectList = getObjectsCreateIfNull(o.getFace());
    objectList.add(o);
  }
  
  public static void removeObject(VisibleObject o){

    LinkedList<VisibleObject> objectList = sortedObjectList.get(o.getFace());
    if(objectList == null){ return; }
    if(!objectList.remove(o)){
      System.err.println("(ManifoldObjectHandler.removeObject) Error removing object " + o.getIndex() + " from face " + o.getFace().getIndex());
    }
  }
  
  public static void clearObjects(){
    sortedObjectList.clear();
  }
  
  //access to object lists  
  public static LinkedList<VisibleObject> getObjects(Face f){
    return sortedObjectList.get(f);
  }
  
  private static LinkedList<VisibleObject> getObjectsCreateIfNull(Face f){
    LinkedList<VisibleObject> objectList = sortedObjectList.get(f);
    if(objectList == null){
      objectList = new LinkedList<VisibleObject>();
      sortedObjectList.put(f, objectList);
    }
    return objectList;
  }
  
  //this should get called whenever the position of a referenced object changes its face 
  public static void updateObject(VisibleObject o, Face oldFace) {

    if(o == null){ return; }

    //remove the object, which should be in the list for oldFace
    if(oldFace != null){
      LinkedList<VisibleObject> objectList = sortedObjectList.get(oldFace);
      if(objectList != null){ 
        if(!objectList.remove(o)){
          System.err.println("(ManifoldObjectHandler.updateObject) Error transferring object " + o.getIndex() + " from face " + oldFace.getIndex() + " to face " + o.getFace().getIndex());
        }
      }
    }
    
    //re-add the object
    addObject(o);
  }
  
  //========= PATHS =============

  //add and remove paths
  public static void addPath(VisiblePath p){ 
    LinkedList<VisiblePath> pathList = getPathsCreateIfNull(p.getFace());
    pathList.add(p);
  }
  
  public static void removePath(VisiblePath p){
    LinkedList<VisiblePath> pathList = sortedPathList.get(p.getFace());
    if(pathList == null){ return; }
    pathList.remove(p);
  }
  
  public static void clearPaths(){
    sortedPathList.clear();
  }
  
  //access to path lists  
  public static LinkedList<VisiblePath> getPaths(Face f){
    return sortedPathList.get(f);
  }
  
  private static LinkedList<VisiblePath> getPathsCreateIfNull(Face f){
    LinkedList<VisiblePath> pathList = sortedPathList.get(f);
    if(pathList == null){
      pathList = new LinkedList<VisiblePath>();
      sortedPathList.put(f, pathList);
    }
    return pathList;
  }
}
