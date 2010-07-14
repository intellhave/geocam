package development;

import java.util.ArrayList;

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
  
  public static ArrayList<Point2D> getPoints(ArrayList<Vector2D> vectors) {
    ArrayList<Point2D> points = new ArrayList<Point2D>();
    for(int i = 0; i < vectors.size(); i++) {
      points.add(new Point2D(vectors.get(i).getComponent(0), vectors.get(i).getComponent(1)));
    }
    return points;
  }

}
