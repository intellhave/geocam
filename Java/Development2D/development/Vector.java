package development;

import java.util.Arrays;

public class Vector {
  private static final double epsilon = Math.pow(10, -6);


  protected double[] components_;

  // constructors
  public Vector(double[] components) {
    components_ = Arrays.copyOf(components, components.length);
  }
  
  public Vector(Vector v, double w){
    //create new vector augmenting v with specified w value
    components_ = new double[v.getDimension()+1];
    for(int i=0; i<v.getDimension(); i++){
      components_[i] = v.getComponent(i);
    }
    components_[v.getDimension()] = w;
  }
  
  public Vector(double x, double y){
    components_ = new double[] { x, y };
  }

  public Vector(double x, double y, double z) {
    components_ = new double[] { x, y, z };
  }

  public Vector(int dimension) {
    components_ = new double[dimension];
  }

  public Vector(Vector copy) {
    components_ = Arrays.copyOf(copy.components_, copy.components_.length);
  }

  // methods
  @Override
  public String toString() {
    String result = new String("");
    result += "(" + components_[0];
    for (int i = 1; i < components_.length; i++) {
      result += ", " + components_[i];
    }
    result += ")";
    return result.toString();
  }

  public void setComponent(int k, double val) {
    components_[k] = val;
  }

  public double getComponent(int k) {
    return components_[k];
  }

  public double[] getVectorAsArray() {
    return Arrays.copyOf(components_, components_.length);
  }

  public int getDimension() {
    return components_.length;
  }

  // this = this + rhs
  public void add(Vector rhs) {
    // add rhs to this
    for (int i = 0; i < components_.length; i++) {
      components_[i] += rhs.components_[i];
    }
  }

  // this = this - rhs
  public void subtract(Vector rhs) {
    // subtract rhs from this
    for (int i = 0; i < components_.length; i++) {
      components_[i] -= rhs.components_[i];
    }
  }

  // create new vector v1 + v2
  public static Vector add(Vector v1, Vector v2) {
    Vector w = new Vector(v1);
    w.add(v2);
    return w;
  }

  // create new vector v1 - v2
  public static Vector subtract(Vector v1, Vector v2) {
    Vector w = new Vector(v1);
    w.subtract(v2);
    return w;
  }

  // this = this * factor
  public void scale(double factor) {
    for (int i = 0; i < components_.length; i++) {
      components_[i] *= factor;
    }
  }

  // create new vector v * factor
  public static Vector scale(Vector v, double factor) {
    Vector w = new Vector(v);
    w.scale(factor);
    return w;
  }

  public static double dot(Vector a, Vector b) {
    double result = 0;
    for (int i = 0; i < a.getDimension(); i++) {
      result += a.components_[i] * b.components_[i];
    }
    return result;
  }

  public double lengthSquared() {

    double result = 0;
    for (int i = 0; i < components_.length; i++) {
      result += components_[i] * components_[i];
    }
    return result;
  }

  public double length() {
    return Math.sqrt(lengthSquared());
  }

  public static double distance(Vector v1, Vector v2) throws Exception {
    v1.subtract(v2);
    return Math.sqrt(v1.lengthSquared());
  }

  public void normalize() {

    double invlen = 1 / Math.sqrt(lengthSquared());
    scale(invlen);
  }

  public static Vector normalize(Vector v) {
    Vector w = new Vector(v);
    w.normalize();
    return w;
  }

  public boolean isZero() {
    for (int i = 0; i < this.getDimension(); i++) {
      if (this.getComponent(i) != 0)
        return false;
    }
    return true;
  }

  public static Vector cross(Vector a, Vector b) {
    if(a.getDimension() != 3 || b.getDimension() != 3) return null;
    return new Vector(a.components_[1] * b.components_[2] - a.components_[2]
        * b.components_[1], -a.components_[0] * b.components_[2]
        + a.components_[2] * b.components_[0], a.components_[0]
        * b.components_[1] - a.components_[1] * b.components_[0]);
  }

  public static double findAngle2D(Vector u, Vector v) {
    Vector u3d = new Vector(new double[] { u.getComponent(0),
        u.getComponent(1), 0 });
    Vector v3d = new Vector(new double[] { v.getComponent(0),
        v.getComponent(1), 0 });
    Vector c = cross(u3d, v3d);
    if (c.getComponent(2) >= 0) { // will return a positive angle
      double cos_theta = (dot(u3d, v3d))
          / (Math.sqrt(u3d.lengthSquared() * v3d.lengthSquared()));
      if (cos_theta < 0) {
        double minus_cos_theta = (-1) * cos_theta;
        double theta = Math.acos(minus_cos_theta);
        theta = Math.PI - theta;
        return theta;
      } else { // case cos_theta >= 0
        double theta = Math.acos(cos_theta);
        return theta;
      }
    } else {
      double cos_theta = (dot(u3d, v3d))
          / (Math.sqrt(u3d.lengthSquared() * v3d.lengthSquared()));
      if (cos_theta < 0) {
        double minus_cos_theta = (-1) * cos_theta;
        double theta = Math.acos(minus_cos_theta);
        theta = Math.PI - theta;
        return (-1) * theta;
      } else { // case cos_theta >= 0
        double theta = Math.acos(cos_theta);
        return (-1) * theta;
      }
    }
  }
  
  /*
   * Returns the intersection of the line formed by points a and b with the ray
   * from the origin through v. ( ( y1 = (w2/w1)(x-s) + t, y2 = (v2/v1)x )
   */
  public static Vector findIntersection(Vector a, Vector b, Vector v) {
    Vector w = Vector.subtract(b, a);
    double w1 = w.getComponent(0);
    double w2 = w.getComponent(1);
    double s = a.getComponent(0);
    double t = a.getComponent(1);
    double v1 = v.getComponent(0);
    double v2 = v.getComponent(1);

    if ((w1 == 0 && v1 == 0) || (w2 / w1) == (v2 / v1)) { // slopes equal =>
                                                          // parallel
      return null;
    }

    double x, y;
    if (w1 == 0) {
      x = s;
      y = (v2 / v1) * s;
    } else if (v1 == 0) {
      x = 0;
      y = (w2 / w1) * (0 - s) + t;
    } else {
      x = (t - (w2 / w1) * s) / ((v2 / v1) - (w2 / w1));
      y = (v2 / v1) * x;
    }

    Vector intersection = new Vector(x, y);

    if (isContained(a, b, intersection))
      return intersection;
    else
      return null;
  }

  /*
   * returns true if the coordinates of the given vector v are between the
   * coordinates of v1 and v2
   */
  private static boolean isContained(Vector v1, Vector v2, Vector v) {
    double x = v.getComponent(0);
    double y = v.getComponent(1);
    double x1 = v1.getComponent(0);
    double y1 = v1.getComponent(1);
    double x2 = v2.getComponent(0);
    double y2 = v2.getComponent(1);

    return (between(x1, x2, x) && between(y1, y2, y));
  }

  // true if c is between a and b
  private static boolean between(double a, double b, double c) {
    if ((a - epsilon > c && b - epsilon > c)
        || (a + epsilon < c && b + epsilon < c))
      return false;
    return true;
  }

}
