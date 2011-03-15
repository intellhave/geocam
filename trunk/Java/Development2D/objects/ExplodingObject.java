package objects;

import java.awt.Color;

/* This is the most basic type of VisibleObject
 * It doesn't do anything but sit in one place
 * This is useful for drawing, for example, the source point on a development
 */

public class ExplodingObject extends VisibleObject{
  
  private static final long TIME_TO_LIVE = 500;
  
  private static final double RADIUS_START = 0.05, RADIUS_END = 0.15;
  private static final int R_START = 0, R_END = 255;
  private static final int G_START = 0, G_END = 0;
  private static final int B_START = 255, B_END = 0;
  private static final double A_START = 1.0, A_END = 0.0;
  
  private long startTime;

  public ExplodingObject(ManifoldPosition manifoldPosition){
    super(manifoldPosition, getAppearance(0));
    startTime = System.currentTimeMillis();
  }

  public ObjectAppearance getAppearance(){
    long dt = System.currentTimeMillis() - startTime;
    if(dt > TIME_TO_LIVE){
      removeFromManifold();
      return getAppearance(1.0);
    }
    return getAppearance((double)dt/(double)TIME_TO_LIVE);
  }

  private static ObjectAppearance getAppearance(double percent){
    double radius = (RADIUS_END - RADIUS_START)*percent*percent + RADIUS_START; //percent^2 so it grows faster near the end
    int r = (int)((R_END - R_START)*percent) + R_START;
    int g = (int)((G_END - G_START)*percent) + G_START;
    int b = (int)((B_END - B_START)*percent) + B_START;
    double a = (A_END - A_START)*percent + A_START;
    return new ObjectAppearance(radius,new Color(r,g,b), a);
  }
}
