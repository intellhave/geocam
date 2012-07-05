package markers;

import markersMKII.MarkerAppearance;


/* This object changes its appearance with time, then removes itself from the manifold
 */

public class ExplodingMarker extends VisibleMarker{
  
  private static final long TIME_TO_LIVE = 500;
  
  private static final double RADIUS_START = 0.05, RADIUS_END = 0.15;
  private static final int R_START = 0, R_END = 255;
  private static final int G_START = 0, G_END = 0;
  private static final int B_START = 255, B_END = 0;
  private static final int A_START = 255, A_END = 0;
  
  private long startTime;

  public ExplodingMarker(ManifoldPosition manifoldPosition){
    super(manifoldPosition, getAppearance(0));
    startTime = System.currentTimeMillis();
  }

  public MarkerAppearance getAppearance(){
    long dt = System.currentTimeMillis() - startTime;
    if(dt > TIME_TO_LIVE){
      removeFromManifold();
      return getAppearance(1.0);
    }
    return getAppearance((double)dt/(double)TIME_TO_LIVE);
  }

  private static MarkerAppearance getAppearance(double percent){
    double radius = (RADIUS_END - RADIUS_START)*percent*percent + RADIUS_START; //percent^2 so it grows faster near the end
    int r = (int)((R_END - R_START)*percent) + R_START;
    int g = (int)((G_END - G_START)*percent) + G_START;
    int b = (int)((B_END - B_START)*percent) + B_START;
    int a = (int)((A_END - A_START)*percent) + A_START;
    return new MarkerAppearance();
  }
}
