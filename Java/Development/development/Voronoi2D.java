package development;

import java.util.ArrayList;

public class Voronoi2D {

  private ArrayList<Vector> points_;
  private Face[] cells_; //cells_[i] is the voronoi cell of points_[i], intersected with bounding_face_
  private Face bounding_face_;
  
  public Voronoi2D(Face bounding_face, ArrayList<Vector> points) {
    points_ = points;
    bounding_face_ = bounding_face;
    cells_ = new Face[points_.size()];
    computeCells();
  }
  
  public Vector getPointAt(int i){
    return points_.get(i);
  }
  
  public Face getCellAt(int i){
    return cells_[i];
  }
  
  private void computeCells(){
    
    //compute voronoi cells from points_
    
    //start with cells_[0] = entire face
    //now for i = 1 to last
    // 1) set cells_[i] = entire face
    // 2) for j = 0 to i-1, 
    //      a) let x = midpoint(points_[i], points_[j])
    //         let u1 = rotate points_[i]-x CCW by pi/2
    //         let u2 = rotate points_[j]-x CCW by pi/2
    //         let F_i = Frustum2D(left = u1, right = u2)
    //         let F_j = Frustum2D(left = u2, right = u1)
    //      b) intersect cells_[i]-x with F_i, +x
    //         intersect cells_[j]-x with F_j, +x
    
    int n = points_.size();
    
    cells_[0] = new Face(bounding_face_);
    for(int i=1; i<n; i++){
      
      cells_[i] = new Face(bounding_face_);
      
      for(int j=0; j<i; j++){
        Vector pi = new Vector(points_.get(i));
        Vector pj = new Vector(points_.get(j));
        
        Vector x = Vector.scale(Vector.add(pi,pj), 0.5);
        pi.subtract(x);
        pj.subtract(x);
        Vector ui = new Vector(pi.getComponent(1), -pi.getComponent(0));
        Vector uj = new Vector(pj.getComponent(1), -pj.getComponent(0));
        Frustum2D fi = new Frustum2D(ui,uj);
        Frustum2D fj = new Frustum2D(uj,ui);
        
        //cells_[i] = fi.clipFace(cells_[i] - x) + x;  
        //cells_[j] = fj.clipFace(cells_[j] - x) + x;
      }
    }
  }
  
  
}
