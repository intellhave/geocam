package solvers.implemented;

import geoquant.Geometry;
import geoquant.LCSC;
import geoquant.Length;
import geoquant.Radius;
import geoquant.VCSC;
import geoquant.Volume;
import inputOutput.TriangulationIO;
import triangulation.Triangulation;
import triangulation.Vertex;
import util.Matrix;

public class RadiusVEHRGradient extends RadiusVEHRNewton {
  private Radius[] radii;
  private VCSC[] vcsc;
  private Volume.Sum totalV;
  
  public RadiusVEHRGradient() {
    super();
    int length = Triangulation.vertexTable.size();
    radii = new Radius[length];
    vcsc = new VCSC[length];
    int i = 0;
    for(Vertex v : Triangulation.vertexTable.values()) {
      radii[i] = Radius.at(v);
      vcsc[i] = VCSC.at(v);
      i++;
    }
    totalV = Volume.sum();
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
      slopes[i] = -vcsc[i].getValue() / Math.pow(totalV.getValue(), -1/3.0);
    }
    return slopes;
  }
}
