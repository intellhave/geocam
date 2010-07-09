package development;

public class Vector3D extends Vector {

  public Vector3D(double x, double y, double z) {
    super(x, y, z);
  }

  static public Vector3D cross(Vector3D a, Vector3D b) {
    return new Vector3D(a.components_[1] * b.components_[2] - a.components_[2]
        * b.components_[1], -a.components_[0] * b.components_[2]
        + a.components_[2] * b.components_[0], a.components_[0]
        * b.components_[1] - a.components_[1] * b.components_[0]);
  }
}
