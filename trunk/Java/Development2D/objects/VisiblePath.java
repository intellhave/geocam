package objects;

import development.Vector;
import triangulation.Face;

/* class for specifying a visible path on the Triangulation
 * 
 * Any VisiblePath that gets created is automatically handled by ManifoldObjectHandler
 */

public abstract class VisiblePath{
  
  private int index;
  
  Face face;
  private Vector pathStart;
  private Vector pathEnd;
  
  private boolean isVisible;
  PathAppearance app;
  
  public VisiblePath(ManifoldPosition pathStart, ManifoldPosition pathEnd, PathAppearance pathAppearance){
    
    index = ManifoldObjectHandler.generateIndex();
    
    pathStart = new ManifoldPosition(pathStart);
    pathEnd = new ManifoldPosition(pathEnd);
    
    app = pathAppearance;
    
    ManifoldObjectHandler.addPath(this);
  }
  
  public int getIndex(){ return index; }
  public void setVisible(boolean visibility){ isVisible = visibility; }
  public boolean isVisible(){ return isVisible; }
  
  public Face getFace(){ return face; }
  public Vector getPathStartPosition(){ return pathStart; }
  public Vector getPathEndPosition(){ return pathEnd; }
  
  public PathAppearance getAppearance(){ return app; }
  public void setAppearance(PathAppearance appearance){ app = appearance; }

  public void removeFromManifold(){
    ManifoldObjectHandler.removePath(this);
  }
}
