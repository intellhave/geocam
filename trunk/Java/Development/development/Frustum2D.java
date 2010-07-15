package development;


public class Frustum2D {
  
  private static final double epsilon = Math.pow(10, -6);
  private Vector2D left, right;
  private Vector2D leftNormal, rightNormal;
  
  public Frustum2D(Vector2D l, Vector2D r){
    left = l;
    right = r;
    findNormals();
  }
  
  public Frustum2D(double[] l, double[] r) {
    left = new Vector2D(l[0], l[1]);
    right = new Vector2D(r[0], r[1]);
    findNormals();
  }
  
  public Frustum2D(Frustum2D f) {
    left = new Vector2D(f.getLeft());
    right = new Vector2D(f.getRight());
    findNormals();
  }
  
  private void findNormals(){
    leftNormal = new Vector2D(left.getComponent(1),-left.getComponent(0));
    rightNormal = new Vector2D(-right.getComponent(1), right.getComponent(0));
  }
  
  public Vector2D getLeft() {
    return left;
  }
  
  public Vector2D getRight() {
    return right;
  }
  
  public void normalizeVectors() {
    left.normalize();
    right.normalize();
  }
  
  public boolean checkInterior(Vector2D vector) {

    if(Vector2D.dot(leftNormal, vector) < -epsilon) return false;
    if(Vector2D.dot(rightNormal, vector) < -epsilon) return false;
    return true;
  }
  
  public static Frustum2D intersect(Frustum2D frustum1, Frustum2D frustum2) {
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
  
  