package solvers.newtonsMethod;

import triangulation.Triangulation;
import triangulation.Vertex;
import util.Matrix;
import geoquant.Curvature3D;
import geoquant.Geometry;
import geoquant.LCSC;
import geoquant.LEHR;
import geoquant.Length;
import geoquant.Radius;

public class RadiusLEHRGradient extends NewtonsMethod{
  private Radius[] radii;
  private LEHR lehr;
  private LCSC[] lcsc;
  private Length.Sum totalL;
  private int dir = 1;
  
  public RadiusLEHRGradient() {
    super();
    int length = Triangulation.vertexTable.size();
    radii = new Radius[length];
    lcsc = new LCSC[length];
    int i = 0;
    for(Vertex v : Triangulation.vertexTable.values()) {
      radii[i] = Radius.at(v);
      lcsc[i] = LCSC.at(v);
      i++;
    }
    lehr = LEHR.getInstance();
    totalL = Length.sum();
  }
  
  public double[] getLogRadii() {
    double[] log_radii = new double[radii.length];
    for(int i = 0; i < log_radii.length; i++) {
      log_radii[i] = Math.log(radii[i].getValue());
    }
    return log_radii;
  }
  
  public void setLogRadii(double[] vars) {
    for(int i = 0; i < vars.length; i++) {
      radii[i].setValue(Math.exp(vars[i]));
    }
  }

  @Override
  public double function(double[] vars) {
    setLogRadii(vars);
    return lehr.getValue();
  }
  
  @Override
  public double[] gradient(double[] vars) {
    setLogRadii(vars);
    double[] grad = new double[vars.length];
    for(int i = 0; i < radii.length; i++) {
      grad[i] = lcsc[i].getValue() / totalL.getValue();
    }
    return grad;
  }
  
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
