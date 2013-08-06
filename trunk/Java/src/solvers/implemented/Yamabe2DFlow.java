package solvers.implemented;

import geoquant.Curvature2D;
import geoquant.Geometry;
import geoquant.Radius;
import solvers.Solver;

public class Yamabe2DFlow extends Solver{

  @Override
  public double[] calcSlopes(double[] x) {
    int i = 0;
    for(Radius r : Geometry.getRadii()){
      r.setValue(x[i]);
      i++;
    }
    double[] slopes = new double[x.length];
    i = 0;
    double avg = Curvature2D.sum().getValue() / x.length;
    for(Curvature2D K : Geometry.getCurvature2D()){
      slopes[i] = x[i] *(avg - K.getValue());
      i++;
    }
    return slopes;
  }

}
