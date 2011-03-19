package objects;

import triangulation.Face;

/* class for specifying a visible path on the Triangulation
 * 
 * Any VisiblePath that gets created is automatically handled by ManifoldObjectHandler
 */

public class VisiblePath extends ManifoldPath{
  
  private int index;
  
  private boolean isVisible;
  PathAppearance app;
  
  public VisiblePath(PathAppearance appearance){
    index = ManifoldObjectHandler.generateIndex();
    isVisible = true;
    app = appearance;
  }
  
  public VisiblePath(ManifoldPath path, PathAppearance appearance){
    super(path);
    index = ManifoldObjectHandler.generateIndex();
    isVisible = true;
    app = appearance;
  }
  
  public int getIndex(){ return index; }
  public void setVisible(boolean visibility){ isVisible = visibility; }
  public boolean isVisible(){ return isVisible; }
  
  public PathAppearance getAppearance(){ return app; }
  public void setAppearance(PathAppearance appearance){ app = appearance; }

  protected void reportFaceChange(Face f, boolean newIntersection){
    //make sure this object appears in, and only in, the lists for the faces the path intersects
    if(newIntersection == true){ ManifoldObjectHandler.addPathToFace(f, this); }
    if(newIntersection == false){ ManifoldObjectHandler.removePathFromFace(f, this); }
  }

  public void removeFromManifold(){
    //ManifoldPath.clear() calls reportFaceChange(f,false) for all faces the path intersects
    //note that this gets rid of the path data, in addition to removing from manifold
    clear(); 
  }
}
