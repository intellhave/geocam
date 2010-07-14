package development;

import java.util.ArrayList;

public class Vector2D extends Vector {

  public Vector2D(double x, double y) {
    super(x, y);
  }

  public Vector2D(Vector2D vector) {
    super(vector.getComponent(0), vector.getComponent(1));
  }

  public Vector2D(Point2D point) {
    super(point.getComponent(0), point.getComponent(1));
  }

  public static double dot(Vector2D a, Vector2D b) {
    return a.getComponent(0) * b.getComponent(0) + a.getComponent(1)
        * b.getComponent(1);
  }

  public static Vector2D subtract(Vector2D v1, Vector2D v2) {
    return new Vector2D(v1.getComponent(0) - v2.getComponent(0), v1
        .getComponent(1)
        - v2.getComponent(1));
  }

  public static ArrayList<Vector2D> getVectors(ArrayList<Point2D> points) {
    ArrayList<Vector2D> vectors = new ArrayList<Vector2D>();
    for(int i =0 ; i < points.size(); i++) {
      vectors.add(new Vector2D(points.get(i).getX(), points.get(i).getY()));
    }
    return null;
  }
}