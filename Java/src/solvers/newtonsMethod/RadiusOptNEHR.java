package solvers.newtonsMethod;

import geoquant.Radius;
import geoquant.VEHR;
import triangulation.Triangulation;
import triangulation.Vertex;
import util.Matrix;

public class RadiusOptNEHR extends NewtonsMethod {
  private Radius[] radii;
  private VEHR nehr;
  private VEHR.Partial[] partials;
  private VEHR.SecondPartial[][] secondPartials;
  
  public RadiusOptNEHR() {
    super();
    
    int i = 0;
    int j;
    int length = Triangulation.vertexTable.size();
    nehr = VEHR.getInstance();
    radii = new Radius[length];
    partials = new VEHR.Partial[length];
    secondPartials = new VEHR.SecondPartial[length][length];
    
    for(Vertex v : Triangulation.vertexTable.values()) {
      radii[i] = Radius.At(v);
      partials[i] = VEHR.partialAt(v);
      j = 0;
      for(Vertex w : Triangulation.vertexTable.values()) {
        secondPartials[i][j] = VEHR.secondPartialAt(v, w);
        j++;
      }
      i++;
    }
  }
  
  public void setLogRadii(double[] vars) {
    for(int i = 0; i < vars.length; i++) {
      radii[i].setValue(Math.exp(vars[i]));
    }
  }
  
  public double[] getLogRadii() {
    double[] log_radii = new double[radii.length];
    for(int i = 0; i < log_radii.length; i++) {
      log_radii[i] = Math.log(radii[i].getValue());
    }
    return log_radii;
  }
      
  public double function(double[] vars) {
    setLogRadii(vars);
    return nehr.getValue();
  }
  
  @Override
  public double[] gradient(double[] vars) {
    setLogRadii(vars);
    double[] gradient = new double[partials.length];
    for(int i = 0; i < partials.length; i++) {
      gradient[i] = partials[i].getValue();
    }
    return gradient;
  }
  
  @Override
  public Matrix hessian(double[] vars) {
    setLogRadii(vars);
    Matrix hessian = new Matrix(radii.length, radii.length);
    for(int i = 0; i < secondPartials.length; i++) {
      for(int j = 0; j < secondPartials[i].length; j++) {
        hessian.m[i][j] = secondPartials[i][j].getValue();
      }
    }
    return hessian;
  }

}
