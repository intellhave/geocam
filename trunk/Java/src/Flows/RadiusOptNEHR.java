package Flows;

import util.Matrix;
import Geoquant.NEHR;
import Geoquant.Radius;
import Solvers.NewtonsMethod;
import Triangulation.Vertex;
import Triangulation.Triangulation;

public class RadiusOptNEHR extends NewtonsMethod {
  private Radius[] radii;
  private NEHR nehr;
  private NEHR.Partial[] partials;
  private NEHR.SecondPartial[][] secondPartials;
  
  public RadiusOptNEHR() {
    super();
    
    int i = 0;
    int j;
    int length = Triangulation.vertexTable.size();
    nehr = NEHR.getInstance();
    radii = new Radius[length];
    partials = new NEHR.Partial[length];
    secondPartials = new NEHR.SecondPartial[length][length];
    
    for(Vertex v : Triangulation.vertexTable.values()) {
      radii[i] = Radius.At(v);
      partials[i] = NEHR.partialAt(v);
      j = 0;
      for(Vertex w : Triangulation.vertexTable.values()) {
        secondPartials[i][j] = NEHR.secondPartialAt(v, w);
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
