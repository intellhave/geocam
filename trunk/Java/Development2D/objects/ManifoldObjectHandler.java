package objects;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import triangulation.Face;

/* This is the 'master object' which holds all VisibleObjects on the Triangulation
 * 
 * Anything which draws the triangulation should use the getObjects method to see which
 * objects are currently on the specified face
 * 
 * Any VisibleObject created is automatically handled by the ManifoldObjectHandler
 */

public class ManifoldObjectHandler{

  private static int nextIndex = 0;

  //might be useful to have references to all objects/paths as well, for DevelopmentViewEmbedded (for example)
  //private static HashSet<VisibleObject> allVisibleObjects = new HashSet<VisibleObject>();
  //private static HashSet<VisiblePath> allVisiblePaths = new HashSet<VisiblePath>();
  
  //references to visible objects and paths, sorted by face (path references appear in any face the path intersects)
  private static HashMap<Face,HashSet<VisibleObject>> sortedObjectList = new HashMap<Face,HashSet<VisibleObject>>();
  private static HashMap<Face,HashSet<VisiblePath>> sortedPathList = new HashMap<Face,HashSet<VisiblePath>>();
  
  
  private ManifoldObjectHandler(){ }
  
  public static int generateIndex(){
    nextIndex++;
    return nextIndex;
  }
  
  //========= OBJECTS =============
  
  //status
  /*public static void printObjectInfo(){
    
    //quick way to check for bugs, e.g. objects in duplicate or wrong face lists
    
    System.out.println("\nOBJECT STATS:");
    
    HashMap<Integer,Face> faceTable = Triangulation.faceTable;
    Set<Integer> faceIndices = faceTable.keySet();
    for(Integer i : faceIndices){
      Face f = faceTable.get(i);
      Collection<VisibleObject> objectList = sortedObjectList.get(f);
      
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
  }*/

  //add and remove objects
  public static void addObject(VisibleObject o){ 
    
    Collection<VisibleObject> objectList = getObjectsCreateIfNull(o.getFace());
    objectList.add(o);
  }
  
  public static void removeObject(VisibleObject o){

    Collection<VisibleObject> objectList = sortedObjectList.get(o.getFace());
    if(objectList == null){ return; }
    if(!objectList.remove(o)){
      System.err.println("(ManifoldObjectHandler.removeObject) Error removing object " + o.getIndex() + " from face " + o.getFace().getIndex());
    }
  }
  
  public static void clearObjects(){
    sortedObjectList.clear();
  }
  
  //access to object lists  
  public static Collection<VisibleObject> getObjects(Face f){
    return sortedObjectList.get(f);
  }
  
  private static Collection<VisibleObject> getObjectsCreateIfNull(Face f){
    HashSet<VisibleObject> objectList = sortedObjectList.get(f);
    if(objectList == null){
      objectList = new HashSet<VisibleObject>();
      sortedObjectList.put(f, objectList);
    }
    return objectList;
  }
  
  //this should get called whenever the position of a referenced object changes its face 
  public static void updateObject(VisibleObject o, Face oldFace) {

    if(o == null){ return; }

    //remove the object, which should be in the list for oldFace
    if(oldFace != null){
      Collection<VisibleObject> objectList = sortedObjectList.get(oldFace);
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

  //status
  /*public static void printPathInfo(){
    
    //quick way to check for bugs, e.g. paths in wrong face lists
    
    System.out.println("\nPATH STATS:");
    
    //HashSet<VisiblePath> allVisiblePaths = new HashSet<VisiblePath>();
    
    HashMap<Integer,Face> faceTable = Triangulation.faceTable;
    Set<Integer> faceIndices = faceTable.keySet();
    for(Integer i : faceIndices){
      Face f = faceTable.get(i);
      Collection<VisiblePath> pathList = sortedPathList.get(f);
      
      if(pathList == null){
        System.out.println("Face " + f.getIndex() + ": null list");
        continue;
      }
      
      int n = pathList.size();
      System.out.println("Face " + f.getIndex() + ": " + n + " paths:");
      for(VisiblePath p : pathList){
        System.out.println("  Path " + p.getIndex());
        //allVisiblePaths.add(p);
      }
    }
  }*/

  //add and remove paths
  public static void addPathToFace(Face face, VisiblePath p){
    Collection<VisiblePath> pathList = getPathsCreateIfNull(face);
    pathList.add(p);
  }
  
  public static void removePathFromFace(Face face, VisiblePath p){
    Collection<VisiblePath> pathList = sortedPathList.get(face);
    if(pathList == null){ return; }
    pathList.remove(p);
  }
  
  public static void clearPaths(){
    sortedPathList.clear();
  }
  
  //access to path lists  
  public static Collection<VisiblePath> getPaths(Face f){
    return sortedPathList.get(f);
  }
  
  private static Collection<VisiblePath> getPathsCreateIfNull(Face f){
    HashSet<VisiblePath> pathList = sortedPathList.get(f);
    if(pathList == null){
      pathList = new HashSet<VisiblePath>();
      sortedPathList.put(f, pathList);
    }
    return pathList;
  }
}
