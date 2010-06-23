package Solvers;

import Geoquant.Curvature2D;
import Geoquant.Geometry;
import Geoquant.Radius;

public class RicciFlow implements DESystem{
  public double[] calcSlopes(double[] x)
  {
    int i = 0;
    for(Radius r : Geometry.getRadii()){
      r.setValue(x[i]);
      i++;
    }
    
    double[] slope = new double[x.length];
    int j = 0;
    for(Curvature2D K : Geometry.getCurvature2D()){
      slope[j] = -K.getValue() * x[i];
      j++;
    }
    
    return slope;
  }
}
