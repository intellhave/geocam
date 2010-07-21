package visual;

import java.util.LinkedList;
import java.util.List;

import util.GeoMath;


public class Circle {
  final Point center;
  final double radius;
  
  public Circle(Point center, double radius) {
    this.center = center;
    this.radius = radius;
  }
  
  public List<Point> circleIntersection(Point center1, double r1, Point center2, double r2) {
    List<Point> solutions = new LinkedList<Point>();
    if(center1.distancePoint(center2) > (r1 + r2)) {
        // No solutions
        return solutions;
    }
    if(center1.x == center2.x && center1.y == center2.y) {
       if(r1 == r2) {
             // Same circle, infinite options, return same point.
             solutions.add(center1);
             return solutions;
       }
       else {
       // No solutions
          return solutions;
       }
    }
    if(center1.distancePoint(center2) < Math.abs(r1 - r2)) {
       return solutions;
    }
    // (x-x1)^2 + (y-y1)^2 = r1^2
    // (x-x2)^2 + (y-y2)^2 = r2^2
    
    /* x^2 - 2*x1*x + x1^2 + y^2 - 2*y1*y + y1^2 = r1^2
       -
       x^2 - 2*x2*x + x2^2 + y^2 - 2*y2*y + y2^2 = r2^2
       ------------------------------------------------
       (2*x2 - 2*x1)x +(2*y2 - 2*y2)y = r1^2 - x1^2 - y1^2 - (r2^2 - x2^2 - y2^2)
       
       xComp*x + yComp*y = rComp
    */
    double xComp = 2 * center2.x - 2 * center1.x;
    double yComp = 2 * center2.y - 2 * center1.y;
    double r1Comp = r1*r1 - center1.x * center1.x - center1.y * center1.y;
    double r2Comp = r2*r2 - center2.x * center2.x - center2.y * center2.y;
    double rComp = r1Comp - r2Comp;
    
    if(yComp == 0) {
       /* x = rComp / xComp
                         _________________
          y = y1  +/-  \|r1^2 - (x - x1)^2  
       */    
       double ySol1 = Math.sqrt(r1*r1 - Math.pow((rComp/xComp - center1.x), 2)) + center1.y;
       double ySol2 = (-1)* Math.sqrt(r1*r1 - Math.pow((rComp/xComp - center1.x),2)) + center1.y;
       Point sol1 = new Point (rComp/xComp, ySol1);
       Point sol2 = new Point (rComp/xComp, ySol2);
       solutions.add(sol1);
       solutions.add(sol2);
       return solutions;
    }
    if(xComp == 0) {
        /* y = rComp / yComp
                          _________________
           x = x1  +/-  \|r1^2 - (y - y1)^2  
        */
       double xSol1 = Math.sqrt(r1*r1 - Math.pow((rComp/yComp - center1.y), 2)) + center1.x;
       double xSol2 = (-1)* Math.sqrt(r1*r1 - Math.pow((rComp/yComp - center1.y),2)) + center1.x;
       
       Point sol1 = new Point(xSol1, rComp/yComp);
       Point sol2 = new Point (xSol2, rComp/yComp);
       solutions.add(sol1);
       solutions.add(sol2);
       return solutions;
    }
    
    /*
        y = (rComp - xComp*x) / yComp
        
     => x^2 - 2*x1*x + x1^2 + ((rComp - xComp*x) / yComp - y1)^2 - r1^2 = 0
        
     => (1 + xComp/yComp)^2 * x^2 - 2*[x1 - (rComp/yComp - y1)*xComp/yComp] * x
           + x1^2 + (rComp/yComp - y1)^2 - r1^2 = 0
    */
    double b = (-2)*center1.x - 2*(rComp/yComp - center1.y)*(xComp/yComp);

    double a = 1 + Math.pow((xComp/yComp), 2);

    double c = center1.x*center1.x + Math.pow((rComp/yComp - center1.y), 2) - r1*r1;

    // Find x solutions using quadratic formula.
    List<Double> quadSol = GeoMath.quadratic(a, b, c);
    for(int i = 0; i < quadSol.size(); i++) {
        
        double xSol = quadSol.get(i);
        double ySol = rComp/yComp - xComp/yComp*xSol;

        Point sol = new Point (xSol, ySol);
        solutions.add(sol);
    }
    return solutions;
}
  
  public List<Point> circleIntersection(Circle circle1, Circle circle2) {
      Point center1 = circle1.center;
      Point center2 = circle2.center;
      double r1 = circle1.radius;
      double r2 = circle2.radius;              
      return circleIntersection(center1, r1, center2, r2);
  }
  
  public boolean equals(Object obj) {
    return (obj instanceof Circle && ((Circle) obj).center.equals(center) && ((Circle) obj).radius == radius);
  }
  
  public int hashCode() {
    Double tmpDouble = new Double(radius);
    return center.hashCode() ^ tmpDouble.hashCode();
  }  
}
