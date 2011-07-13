package solvers.implemented;

import solvers.Solver;
import geoquant.Curvature2D;
import geoquant.Geometry;
import geoquant.Radius;

public class RicciFlow extends Solver{
  public double[] calcSlopes(double[] x)
  {
    int i = 0;
    for(Radius r : Geometry.getRadii()){
      r.setValue(x[i]);
      i++;
    }
    
    double[] slopes = new double[x.length];
    i = 0;
    double totalK=0;
    for(Curvature2D K : Geometry.getCurvature2D()){
      totalK += K.getValue();
      i++;
    }
    double aveK = totalK/i;
//    System.out.println("aveK ="+aveK);
    i=0;
    for(Curvature2D K : Geometry.getCurvature2D()){
      slopes[i] = (aveK - K.getValue()) * x[i];
      i++;
    }
    
    return slopes;
  }
}
