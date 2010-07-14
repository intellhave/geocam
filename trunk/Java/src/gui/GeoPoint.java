package gui;

import java.awt.Point;

public class GeoPoint extends Point {
  String message;
  public GeoPoint(int x, int y, String s) {
    super(x, y);
    message = s;
  }
  
//  @Override
//  public boolean equals(Object other) {
//    if(other instanceof GeoPoint) {
//      GeoPoint p = (GeoPoint) other;
//      double distance = Math.pow(this.getX() - p.getX(), 2) 
//                        + Math.pow(this.getY() - p.getY(), 2);
//      return distance < 6.25;
//    }
//    return false;
//  }
//  
//  @Override
//  public int hashCode() {
//    return 0;
//  }
  
  public double distance(int x, int y) {
    return Math.sqrt(Math.pow(this.getX() - x, 2) + Math.pow(this.getY() - y, 2));
  }
  
  public String getMessage() {
    return message;
  }
  
}
