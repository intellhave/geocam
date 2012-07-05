package markers;

import markersMKII.MarkerAppearance;
import triangulation.Face;

/* Represents a visible object on the Triangulation
 * 
 * This can easily be extended to particular types of objects
 * which can then be used in a class that extends ObjectDynamics
 * 
 * Any VisibleObject that gets created is automatically handled by ManifoldObjectHandler
 */

public class VisibleMarker extends ManifoldPosition{
  
  private int index;
  private boolean isVisible;
  MarkerAppearance app;
  
  public VisibleMarker(ManifoldPosition manifoldPosition, MarkerAppearance appearance){ 
    super(manifoldPosition);
    
    index = ManifoldMarkerHandler.generateIndex();
    isVisible = true;
    app = appearance;
    
    ManifoldMarkerHandler.addObject(this);
  }
  
  public int getIndex(){ return index; }
  public void setVisible(boolean visibility){ isVisible = visibility; }
  public boolean isVisible(){ return isVisible; }
  
  public MarkerAppearance getAppearance(){ return app; }
  public void setAppearance(MarkerAppearance appearance){ app = appearance; }
  
  protected void reportFaceChange(Face oldFace){
    //make sure this object appears in the correct list in the Handler
    ManifoldMarkerHandler.updateObject(this,oldFace);
  }
  
  public void removeFromManifold(){
    ManifoldMarkerHandler.removeObject(this);
  }
}
