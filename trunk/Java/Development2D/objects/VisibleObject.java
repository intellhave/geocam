package objects;

import triangulation.Face;

/* abstract class representing a visible object on the Triangulation
 * 
 * This is intended to be extended to particular types of objects
 * which are then used in a class that extends ObjectDynamics
 * 
 * Any VisibleObject that gets created is automatically handled by ManifoldObjectHandler
 */

public abstract class VisibleObject extends ManifoldPosition{
  
  private int index;
  ObjectAppearance app;
  
  public VisibleObject(ManifoldPosition manifoldPosition, ObjectAppearance appearance){ 
    super(manifoldPosition);
    
    index = ManifoldObjectHandler.generateIndex();
    app = appearance;
    
    ManifoldObjectHandler.addObject(this);
  }
  
  public int getIndex(){ return index; }
  
  public ObjectAppearance getAppearance(){ return app; }
  public void setAppearance(ObjectAppearance appearance){ app = appearance; }
  
  protected void reportFaceChange(Face oldFace){
    //make sure this object appears in the correct list in the Handler
    ManifoldObjectHandler.updateObject(this,oldFace);
  }
  
}
