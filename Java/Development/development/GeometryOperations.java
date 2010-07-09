package development;

public class GeometryOperations {
  public static Vector3D intersectPlanes(Vector3D norm1, Vector3D norm2) {
    Vector3D result = Vector3D.cross(norm1, norm2);
    result.normalize();
    return result;
  }
}
