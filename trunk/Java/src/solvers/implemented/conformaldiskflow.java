package solvers.implemented;

import solvers.Solver;
import geoquant.LKCurvature;
import geoquant.Geometry;
import geoquant.Radius;

public class conformaldiskflow extends Solver{

  @Override
  public double[] calcSlopes(double[] x) {
    int i = 0;
    for(Radius r : Geometry.getRadii()){
      r.setValue(x[i]);
      i++;
    }
    double[] slopes = new double[x.length];
    i = 0;
    for(LKCurvature K : Geometry.getLKCurvature()){
      slopes[i] = x[i] *(-K.getValue());
      i++;
    }
    return slopes;
  }

}
