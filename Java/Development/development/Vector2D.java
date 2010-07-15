package development;

import java.util.ArrayList;

public class Vector2D extends Vector {

  public Vector2D(double x, double y) {
    super(x, y);
  }

  public Vector2D(Vector2D vector) {
    super(vector.getComponent(0), vector.getComponent(1));
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
}
