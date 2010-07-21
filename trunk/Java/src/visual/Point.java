package visual;


public class Point {
  public final double x,y;

  public Point(double x, double y) {
    this.x = x;
    this.y = y;
  }
  
  //returns a value in [0,2pi)
  public double angle() {
    return Math.atan2(x,y);
  }
  
  // returns a new point that is this point rotated CCW by angle
  public Point rotate(double angle) {
    return new Point(Math.cos(angle) * x - Math.sin(angle) * y,
                      Math.sin(angle) * x + Math.cos(angle) * y);
  }
  
  public double distancePoint(Point b) {
    
    /*         ____________________________             
            _ |           2              2    
      d =    \|(a_x - b_x)  + (a_y - b_y)
    */
   
    return Math.sqrt((x -b.x)*(x - b.x)+(y -b.y)*(y -b.y));
 }
 
 
  
  public Point add(Point p) {
    return new Point(x + p.x ,y + p.y);
  }
  
  public Point subtract(Point p) {
    return new Point(x - p.x ,y - p.y);

  }
  
  public Point copy() {
    return new Point(x, y);
  }
  
  public String toString() {
    return "(" + x + "," + y + ")";
  }
  
  public boolean equals(Object obj) {
    return (obj instanceof Point && ((Point) obj).x == this.x && ((Point) obj).y == this.y);
  }
  
  public int hashCode() {
    Double tmpX = new Double(x);
    Double tmpY = new Double(y);
    return tmpX.hashCode() ^ tmpY.hashCode();
  }
}
