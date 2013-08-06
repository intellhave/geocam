package util;

import java.util.ArrayList;

public class Vector {
  //private static final double epsilon = Math.pow(10, -6);

  protected double[] components;

  // constructors
  //----------------------------------------
  public Vector(double[] components) {
    this.components = components.clone(); 
  }
  
  public Vector(Vector v, double w){
    //create new vector augmenting v with specified w value
    int n = v.getDimension();
    components = new double[n+1];
    for(int i=0; i<n; i++){
      components[i] = v.getComponent(i);
    }
    components[n] = w;
  }
 
  public Vector(double x){
    components = new double[] { x };
  }
  
  public Vector(double x, double y){
    components = new double[] { x, y };
  }

  public Vector(double x, double y, double z) {
    components = new double[] { x, y, z };
  }

  public Vector(int dimension) {
    components = new double[dimension];
  }

  public Vector(Vector copy) {
    setEqualTo(copy);
  }
  
  //overridden methods
  //----------------------------------------
  @Override
  public String toString() {
    String result = new String("");
    result += "(" + components[0];
    for (int i=1; i<components.length; i++){ result += ", " + components[i]; }
    result += ")";
    return result;
  }
  
  @Override
  public boolean equals(Object object) {
    if(object == this) return true;
    if(!(object instanceof Vector)) return false;
    
    Vector vector = (Vector)object;
    if(vector.getDimension() != this.getDimension()) return false;
    for(int i = 1; i < vector.getDimension(); i++) {
      if(this.getComponent(i) != vector.getComponent(i)) return false;
    }
    return true;
  }

  //basic getters and setters
  //----------------------------------------
  public void setEqualTo(Vector v){ components = v.getVectorAsArray(); }
  public void setComponent(int k, double val){ components[k] = val; }
  public double getComponent(int k){ return components[k]; }
    public double[] getVectorAsArray(){ return components.clone(); }
  public int getDimension(){ return components.length; }

  //methods
  //----------------------------------------

  // this = this + rhs
  public void add(Vector rhs) {
    for (int i = 0; i < components.length; i++) {
      components[i] += rhs.components[i];
    }
  }

  // this = this - rhs
  public void subtract(Vector rhs) {
    for (int i = 0; i < components.length; i++) {
      components[i] -= rhs.components[i];
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
    for (int i = 0; i < components.length; i++) {
      components[i] *= factor;
    }
  }

  // create new vector v * factor
  public static Vector scale(Vector v, double factor) {
    Vector w = new Vector(v);
    w.scale(factor);
    return w;
  }

  // return a.b
  public static double dot(Vector a, Vector b) {
    double result = 0;
    for (int i = 0; i < a.getDimension(); i++) {
      result += a.components[i] * b.components[i];
    }
    return result;
  }

  // return |this|^2
  public double lengthSquared() {

    double result = 0;
    for (int i = 0; i < components.length; i++) {
      result += components[i] * components[i];
    }
    return result;
  }

  // return |this|
  public double length() {
    return Math.sqrt(lengthSquared());
  }

  // return |v1-v2|^2
  public static double distanceSquared(Vector v1, Vector v2){
    return Vector.subtract(v1,v2).lengthSquared();
  }
  
  //return |v1-v2|
  public static double distance(Vector v1, Vector v2){
    return Math.sqrt(Vector.distanceSquared(v1,v2));
  }
  
  //determine if |v-w|^2 < epsilon for any w in a list of Vectors 
  public static boolean closeToAnyOf(ArrayList<Vector> vectors, Vector v, double epsilon) {
    
    for(Vector w : vectors){ 
      if( Vector.distanceSquared(v,w) < epsilon ){ return true; }
    }
    return false;
  }

  // this = this/|this|
  public void normalize() {

    double invlen = 1 / Math.sqrt(lengthSquared());
    scale(invlen);
  }

  // create new vector v/|v|
  public static Vector normalize(Vector v) {
    Vector w = new Vector(v);
    w.normalize();
    return w;
  }

  // check if this == (0,...,0)
  public boolean isZero() {
    for (int i = 0; i < this.getDimension(); i++) {
      if (this.getComponent(i) != 0)
        return false;
    }
    return true;
  }

  // return a x b
  public static Vector cross(Vector a, Vector b) {
    if(a.getDimension() == 3 && b.getDimension() == 3)
      return new Vector(a.components[1] * b.components[2] - a.components[2]
        * b.components[1], -a.components[0] * b.components[2]
        + a.components[2] * b.components[0], a.components[0]
        * b.components[1] - a.components[1] * b.components[0]);
    else if (a.getDimension() == 2 && b.getDimension() == 2)
      return new Vector(a.components[0] * b.components[1] - a.components[1] * b.components[0]);
    else return null;
  }
}
