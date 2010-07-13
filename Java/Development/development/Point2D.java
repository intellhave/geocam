package development;

public class Point2D extends Point {
  public Point2D(double x, double y) {
    super(x, y);
  }
  
  public double getX() {
    return getComponent(0);
  }
  
  public double getY() {
    return getComponent(1);
  }

}
