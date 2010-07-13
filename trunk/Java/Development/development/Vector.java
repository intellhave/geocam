package development;

import java.util.Arrays;

import development.Point;

public class Vector {
  
  protected double[] components_;
  
  //constructors
  public Vector(double[] components){
    components_ = Arrays.copyOf(components,components.length);
  }
  
  public Vector(double x, double y){
    components_ = new double[] { x, y };
  }
  
  public Vector(int dimension){
    components_ = new double[dimension];
  }
  
  public Vector(double x, double y, double z){
    components_ = new double[] { x, y, z };
  }
  
  public Vector(Vector copy){
    components_ = Arrays.copyOf(copy.components_,copy.components_.length);
  }
    
  //methods
  @Override 
  public String toString() {
      String result = new String("");
      result += "(" + components_[0];
      for(int i=1; i<components_.length; i++){
        result += ", " + components_[i];
      }
      result += ")";
      return result.toString();
  }
  
  public void setComponent(int k, double val){
    components_[k] = val;
  }

  public double getComponent(int k){
    return components_[k];
  }
  
  public int getDimension(){
    return components_.length;
  }

  public void add(Vector rhs) throws Exception{
  //add rhs to this
    if(components_.length != rhs.components_.length){
      throw new Exception("Dimension mismatch");
    }else{
      for(int i=0; i<components_.length; i++){
        components_[i] += rhs.components_[i];
      }
    }
  }
  
  public void subtract(Vector rhs) throws Exception{
  //subtract rhs from this
    if(components_.length != rhs.components_.length){
      throw new Exception("Dimension mismatch");
    }else{
      for(int i=0; i<components_.length; i++){
        components_[i] -= rhs.components_[i];
      }
    }
  }
  
  public void scale(double factor){
  //scale this by factor
    for(int i=0; i<components_.length; i++){
      components_[i] *= factor;
    }
  }
  
  public double[] getVectorAsArray() {
    return Arrays.copyOf(components_, components_.length);
  }
  
  static public double dot(Vector a, Vector b) throws Exception{

    if(a.getDimension() != b.getDimension()){
      throw new Exception("Dimension mismatch");
    }else{
      double result = 0;
      for(int i=0; i<a.getDimension(); i++){
        result += a.components_[i]*b.components_[i];
      }
      return result;
    }
  }
  
  static public double dot(Vector v, Point p) throws Exception{
    if(v.getDimension() != p.getDimension()){
      throw new Exception("Dimension mismatch");
    }else{
      double result = 0;
      for(int i=0; i<v.getDimension(); i++){
        result += v.getComponent(i)*p.getComponent(i);
      }
      return result;
    }
  }
  
  static public double distance(Vector v1, Vector v2) throws Exception {
    v1.subtract(v2);
    return Math.sqrt(v1.lengthSquared());
  }
  
  public static Vector add(Vector v1, Vector v2) {
    Vector sum = new Vector(v1.getDimension());
    for(int i = 0; i < v1.getDimension(); i++) {
      sum.setComponent(i, v1.getComponent(i)+v2.getComponent(i));
    }
    return sum;
  }
  
  public static Vector subtract(Vector v1, Vector v2) {
    Vector sum = new Vector(v1.getDimension());
    for(int i = 0; i < v1.getDimension(); i++) {
      sum.setComponent(i, v1.getComponent(i)-v2.getComponent(i));
    }
    return sum;
  }

  public double lengthSquared(){
    
    double result = 0;
    for(int i=0; i<components_.length; i++){
      result += components_[i]*components_[i];
    }
    return result;
  }
  
  public double length() {
    return Math.sqrt(lengthSquared());
  }
  
  public void normalize(){
    
    double len = Math.sqrt(lengthSquared());
    for(int i=0; i<components_.length; i++){
      components_[i] /= len;
    }
  }
  
  public boolean isZero() {
    for(int i = 0; i < this.getDimension(); i++) {
      if(this.getComponent(i) != 0) return false;
    }
    return true;
  }
}
