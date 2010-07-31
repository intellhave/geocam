package gui;

import java.awt.geom.Line2D;


public class GeoLine extends Line2D.Double{
  String message;
  public GeoLine(double x1, double y1, double x2, double y2, String msg) {
    super(x1, y1, x2, y2);
    message = msg;
  }

  public String getMessage() {
    return message;
  }
}
