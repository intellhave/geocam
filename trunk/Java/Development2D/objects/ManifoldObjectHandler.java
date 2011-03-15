package objects;

import java.util.HashMap;
import java.util.LinkedList;

import triangulation.Face;

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
  
  private ManifoldObjectHandler(){ }
  
  public static int generateIndex(){
    nextIndex++;
    return nextIndex;
  }

  public static void addObject(VisibleObject o){ 
    Face f = o.getFace();
    LinkedList<VisibleObject> objectList = sortedObjectList.get(f);
    if(objectList == null){
      objectList = new LinkedList<VisibleObject>();
      sortedObjectList.put(f, objectList);
    }
    objectList.add(o);
  }
  
  public static void removeObject(VisibleObject o){
    Face f = o.getFace();
    LinkedList<VisibleObject> objectList = sortedObjectList.get(f);
    if(objectList == null){ return; }
    objectList.remove(o);
  }
  
  public static void clearObjects(){
    sortedObjectList.clear();
  }
  
  public static LinkedList<VisibleObject> getObjects(Face f){
    return sortedObjectList.get(f);
  }
  
  //this should get called whenever the position of a referenced object changes its face 
  public static void updateObject(VisibleObject o, Face oldFace) {
    
    if(o == null){ return; }

    //remove the object, which should be in the list for oldFace
    if(oldFace != null){
      LinkedList<VisibleObject> objectList = sortedObjectList.get(oldFace);
      if(objectList == null){ return; }
      objectList.remove(o);
    }
    
    //re-add the object
    addObject(o);
  }
}
