package Visualization;

public class Circle {
  Point center;
  double radius;
  
  public Circle(Point center, double radius) {
    this.center = center;
    this.radius = radius;
  }
  
  public boolean equals(Object obj) {
    return (obj instanceof Circle && ((Circle) obj).center.equals(center) && ((Circle) obj).radius == radius);
  }
  
  public int hashCode() {
    Double tmpDouble = new Double(radius);
    return center.hashCode() ^ tmpDouble.hashCode();
  }
}
