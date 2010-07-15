package development;

import java.util.Arrays;

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
  
  
  //Mark and Taylor
  public static Vector pointToVector(Point P){
    double[] temp = new double[P.getDimension()];
    for(int i=0; i < P.getDimension(); i++){
      temp[i] = P.getComponent(i);
    }
    Vector result = new Vector(temp);
    return result;
  }
  
  public static Point translatePoint(Point p, Vector c){
    Point result = new Point(new double[c.getDimension()]);
    for(int i=0; i < c.getDimension(); i++){
      result.setComponent(i, p.getComponent(i)+ c.getComponent(i));
    }
    return result;
  }
  
  public static Vector add_better(Vector a, Vector b){
    Vector c = new Vector(new double[a.getDimension()]);
    for(int i=0; i < a.getDimension(); i++){
      c.setComponent(i, a.getComponent(i) + b.getComponent(i));
    }
    return c;
    
  }
  
  public Vector scale_better(double factor){
    //scale this by factor
      Vector result = new Vector(new double[components_.length]);
      for(int i=0; i<components_.length; i++){
        result.setComponent(i, components_[i]*factor);
      }
      return result;
    }
  
  static public Vector cross(Vector a, Vector b) throws Exception{
    
    if((a.getDimension() != 3) || (b.getDimension() != 3)){ 
      throw new Exception("Dimension must be 3");
    }else{
      return new Vector(
          a.components_[1]*b.components_[2]-a.components_[2]*b.components_[1], 
          -a.components_[0]*b.components_[2]+a.components_[2]*b.components_[0], 
          a.components_[0]*b.components_[1]-a.components_[1]*b.components_[0]
      );
    }
  }
  
  
  public static double findAngle2D(Vector u, Vector v) throws Exception{
    Vector u3d = new Vector(new double[] {u.getComponent(0), u.getComponent(1), 0});
    Vector v3d = new Vector(new double[] {v.getComponent(0), v.getComponent(1), 0});
    Vector c = cross(u3d, v3d);
    if(c.getComponent(2) >= 0 ){ //will return a positive angle
      double cos_theta = (dot(u3d,v3d))/(Math.sqrt(u3d.lengthSquared()* v3d.lengthSquared()));
      if(cos_theta < 0){
        double minus_cos_theta = (-1)*cos_theta;
        double theta = Math.acos(minus_cos_theta);
        theta = Math.PI - theta;
        return theta;
      }
      else{ //case cos_theta >= 0
        double theta = Math.acos(cos_theta);
        return theta;
      }
    }
    else{
      double cos_theta = (dot(u3d,v3d))/(Math.sqrt(u3d.lengthSquared()* v3d.lengthSquared()));
      if(cos_theta < 0){
        double minus_cos_theta = (-1)*cos_theta;
        double theta = Math.acos(minus_cos_theta);
        theta = Math.PI - theta;
        return (-1)*theta;
      }
      else{ //case cos_theta >= 0
        double theta = Math.acos(cos_theta);
        return (-1)*theta;
      
    }
  }
  
  
}
  
  
  
  
}
