package solvers.implemented;

import geoquant.Eta;
import geoquant.VEHR;
import solvers.Solver;
import triangulation.Edge;
import triangulation.Triangulation;
import util.Matrix;

public class EtaVEHRGradient extends Solver {
  private Eta[] etas;
  private VEHR.Partial[] vehr_eta_partials;
  protected RadiusVEHRNewton minRadii;
  
  public EtaVEHRGradient() {
    super();
    
    int vertSize = Triangulation.vertexTable.size();
    int edgeSize = Triangulation.edgeTable.size();
    etas = new Eta[edgeSize];
    vehr_eta_partials = new VEHR.Partial[edgeSize];
    
    minRadii = new RadiusVEHRNewton();
    
    int i = 0;
    for(Edge e : Triangulation.edgeTable.values()) {
      etas[i] = Eta.at(e);
      vehr_eta_partials[i] = VEHR.partialAt(e);
      i++;
    }
  }
  
  public void setEtas(double[] vars) {
    for(int i = 0; i < vars.length; i++) {
      etas[i].setValue(vars[i]);
    }
  }
  
  public double[] getEtas() {
    double[] eta_vals = new double[etas.length];
    for(int i = 0; i< etas.length; i++) {
      eta_vals[i] = etas[i].getValue();
    }
    return eta_vals;
  }

  @Override
  public double[] calcSlopes(double[] x) {
    setEtas(x);
    try {
      minRadii.setLogRadii(minRadii.run(minRadii.getLogRadii()));
    } catch (Exception e) {
      double radii[] = minRadii.getLogRadii();
      System.err.print("\nRadii=");
      for(int i =0; i < radii.length; i++) {
        System.err.print(Math.exp(radii[i]) + ", ");
      }
      System.err.println();
      System.err.print("\nEta=");
      for(int i =0; i < x.length; i++) {
        System.err.print(x[i] + ", ");
      }
      System.err.println();
    }
    double[] gradient = new double[vehr_eta_partials.length];
    for(int i = 0; i < gradient.length; i++) {
      gradient[i] = -vehr_eta_partials[i].getValue();
    }
    return gradient;
  }
  
}
