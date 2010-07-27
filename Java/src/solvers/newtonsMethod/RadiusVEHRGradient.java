package solvers.newtonsMethod;

import geoquant.Geometry;
import geoquant.Radius;
import inputOutput.TriangulationIO;
import triangulation.Triangulation;
import util.Matrix;

public class RadiusVEHRGradient extends RadiusVEHRNewton {
  private int dir = 1;
  @Override
  public Matrix hessian(double[] vars) {
    setLogRadii(vars);
    Matrix hessian = new Matrix(vars.length, vars.length);
    for(int i = 0; i < vars.length; i++) {
      for(int j = 0; j < vars.length; j++) {
        if(i == j) {
          hessian.m[i][j] = dir*1;
        } else {
          hessian.m[i][j] = 0;
        }
      }
    }
    return hessian;
  }
  
  public double stepMin(double[] x_n) throws WrongDirectionException {
    dir = 1;
    return super.stepMin(x_n);
  }
  
  public double stepMax(double[] x_n) throws WrongDirectionException {
    dir = -1;
    return super.stepMax(x_n);
  }
  
  public double step(double[] x_n) {
    dir = -1;
    return super.step(x_n);
  }
}
