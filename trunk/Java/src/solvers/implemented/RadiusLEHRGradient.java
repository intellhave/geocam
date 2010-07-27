package solvers.implemented;

import solvers.Solver;
import triangulation.Triangulation;
import triangulation.Vertex;
import util.Matrix;
import geoquant.Curvature3D;
import geoquant.Geometry;
import geoquant.LCSC;
import geoquant.LEHR;
import geoquant.Length;
import geoquant.Radius;

public class RadiusLEHRGradient extends Solver{
  private Radius[] radii;
  private LCSC[] lcsc;
  private Length.Sum totalL;
  
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

  public double[] calcSlopes(double[] x) {
    setLogRadii(x);
    double[] slopes = new double[x.length];
    for(int i = 0; i < x.length; i++) {
      slopes[i] = -lcsc[i].getValue() / totalL.getValue();
    }
    return slopes;
  }
}
