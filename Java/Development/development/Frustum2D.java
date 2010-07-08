package development;

import de.jreality.plugin.JRViewer;
import de.jreality.plugin.basic.Scene;
import de.jreality.scene.SceneGraphComponent;

public class Frustum2D {
  
  private Vector left, right;
  private Vector leftNormal, rightNormal;
  
  public Frustum2D(Vector l, Vector r){
    left = l;
    right = r;
    findNormals();
  }
  
  public Frustum2D(Frustum2D f) {
    left = new Vector(f.getLeft());
    right = new Vector(f.getRight());
  }
  
  private void findNormals(){
    leftNormal = new Vector(left.getComponent(1),-left.getComponent(0));
    rightNormal = new Vector(-right.getComponent(1), right.getComponent(0));
  }
  
  public Vector getLeft() {
    return left;
  }
  
  public Vector getRight() {
    return right;
  }
  
  public boolean checkInterior(Vector vector) throws Exception {
    if(Vector.dot(leftNormal, vector) < 0) return false;
    if(Vector.dot(rightNormal, vector) < 0) return false;
    return true;
  }
  
  public boolean checkInterior(Point point) throws Exception {
    if(Vector.dot(leftNormal, point) < 0) return false;
    if(Vector.dot(rightNormal, point) < 0) return false;
    return true;
  }
  
  public static Frustum2D intersect(Frustum2D frustum1, Frustum2D frustum2) throws Exception {
    if(frustum1.checkInterior(frustum2.getLeft())) {
      if(frustum1.checkInterior(frustum2.getRight()))
        return frustum2;
      else
        return new Frustum2D(frustum2.getLeft(), frustum1.getRight());
    }
    
    if(frustum2.checkInterior(frustum1.getLeft())) {
      if(frustum2.checkInterior(frustum1.getRight()))
        return frustum1;
      else
        return new Frustum2D(frustum1.getLeft(), frustum2.getRight());
    }
    
    return null; // no intersection
  }
}
  
  