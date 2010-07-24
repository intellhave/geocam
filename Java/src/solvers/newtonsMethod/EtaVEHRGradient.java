package solvers.newtonsMethod;

import util.Matrix;

public class EtaVEHRGradient extends EtaOptNEHR {
  private int dir = 1;
  @Override
  public Matrix hessian(double[] vars) {
    setEtas(vars);
    try {
      minRadii.setLogRadii(minRadii.minimize(minRadii.getLogRadii()));
    } catch (Exception e) {
      double radii[] = minRadii.getLogRadii();
      System.err.print("\nRadii=");
      for(int i =0; i < radii.length; i++) {
        System.err.print(Math.exp(radii[i]) + ", ");
      }
      System.err.println();
      System.err.print("\nEta=");
      for(int i =0; i < vars.length; i++) {
        System.err.print(vars[i] + ", ");
      }
      System.err.println();
    }
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
