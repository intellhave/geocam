package development;

import java.util.Arrays;

public class Point {

  private double[] components_;
  
  public Point(double[] components){
    components_ = Arrays.copyOf(components,components.length);
  }
  
  public Point(Point copy){
    components_ = Arrays.copyOf(copy.components_,copy.components_.length);
  }
  
  public Point(double x, double y) {
    components_ = new double[]{x, y};
  }

  public Point(double x, double y, double z) {
    components_ = new double[]{x, y, z};
  }

  //methods
  @Override 
  public String toString() {
      String result = new String("");
      result += "(" + components_[0];
      for(int i=1; i<components_.length; i++){
        result += "," + components_[i];
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
}
