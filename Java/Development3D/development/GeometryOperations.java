package development;

public class GeometryOperations {
  public static Vector intersectPlanes(Vector norm, Vector vector) {
    Vector result = Vector.cross(norm, vector);
    return result;
  }
}
