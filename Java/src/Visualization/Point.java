package Visualization;

public class Point {
  public double x,y;

  public Point(double x, double y) {
    this.x = x;
    this.y = y;
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
