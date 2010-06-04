package Flows;

import util.Matrix;
import Geoquant.NEHR;
import Geoquant.Radius;
import Solvers.NewtonsMethod;
import Triangulation.Vertex;
import Triangulation.Triangulation;

public class RadiusFlowNEHR extends NewtonsMethod {
  private Radius[] radii;
  private NEHR nehr;
  private NEHR.Partial[] partials;
  private NEHR.SecondPartial[][] secondPartials;
  
  public RadiusFlowNEHR() {
    super();
    
    int i = 0;
    int j;
    nehr = NEHR.getInstance();
    for(Vertex v : Triangulation.vertexTable.values()) {
      radii[i] = Radius.At(v);
      partials[i] = NEHR.partialAt(v);
      j = 0;
      for(Vertex w : Triangulation.vertexTable.values()) {
        secondPartials[i][j] = NEHR.secondPartialAt(v, w);
      }
    }
  }
  
  private void setRadii(double[] vars) {
    for(int i = 0; i < vars.length; i++) {
      radii[i].setValue(vars[i]);
    }
  }
    
  public double function(double[] vars) {
    setRadii(vars);
    return nehr.getValue();
  }
  
  @Override
  public double[] gradient(double[] vars) {
    setRadii(vars);
    double[] gradient = new double[partials.length];
    for(int i = 0; i < partials.length; i++) {
      gradient[i] = partials[i].getValue();
    }
    return gradient;
  }
  
  @Override
  public Matrix hessian(double[] vars) {
    setRadii(vars);
    Matrix hessian = new Matrix(radii.length, radii.length);
    for(int i = 0; i < secondPartials.length; i++) {
      for(int j = 0; j < secondPartials[i].length; j++) {
        hessian.m[i][j] = secondPartials[i][j].getValue();
      }
    }
    return hessian;
  }

}
