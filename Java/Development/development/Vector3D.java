package development;

public class Vector3D extends Vector {

  public Vector3D(double x, double y, double z) {
    super(x, y, z);
  }

  public Vector3D(Vector3D original) {
    super(original.getComponent(0), original.getComponent(1), original.getComponent(2));
  }

  static public Vector3D cross(Vector3D a, Vector3D b) {
    return new Vector3D(a.components_[1] * b.components_[2] - a.components_[2]
        * b.components_[1], -a.components_[0] * b.components_[2]
        + a.components_[2] * b.components_[0], a.components_[0]
        * b.components_[1] - a.components_[1] * b.components_[0]);
  }

  public static double dot(Vector3D a, Vector3D b) {
    return a.getComponent(0) * b.getComponent(0) + a.getComponent(1)
        * b.getComponent(1) + a.getComponent(2) * b.getComponent(2);
  }

  public static Vector3D subtract(Vector3D a, Vector3D b) {
    return new Vector3D(a.getComponent(0) - b.getComponent(0), a
        .getComponent(1)
        - b.getComponent(1), a.getComponent(2) - b.getComponent(2));
  }
}
