package solver;

import geoquant.*;
import triangulation.Edge;
import triangulation.Triangulation;
import triangulation.Vertex;
import util.Matrix;

public class EtaOptNEHR extends NewtonsMethod {
  private Eta[] etas;
  private VEHR nehr;
  private VEHR.Partial[] nehr_eta_partials;
  private VEHR.SecondPartial[][] nehr_eta_eta_partials;
  private VEHR.SecondPartial[][] nehr_rad_eta_partials;
  private Radius.Partial[][] radius_partials;
  private RadiusOptNEHR minRadii;
  
  public EtaOptNEHR() {
    super();
    
    int vertSize = Triangulation.vertexTable.size();
    int edgeSize = Triangulation.edgeTable.size();
    
    nehr = VEHR.getInstance();
    etas = new Eta[edgeSize];
    nehr_eta_partials = new VEHR.Partial[edgeSize];
    nehr_eta_eta_partials = new VEHR.SecondPartial[edgeSize][edgeSize];
    nehr_rad_eta_partials = new VEHR.SecondPartial[vertSize][edgeSize];
    radius_partials = new Radius.Partial[vertSize][edgeSize];
    
    minRadii = new RadiusOptNEHR();
    
    int i = 0;
    int j;
    for(Edge e : Triangulation.edgeTable.values()) {
      etas[i] = Eta.At(e);
      nehr_eta_partials[i] = VEHR.partialAt(e);
      j = 0;
      for(Vertex v : Triangulation.vertexTable.values()) {
        radius_partials[j][i] = Radius.At(v).partialAt(e);
        nehr_rad_eta_partials[j][i] = VEHR.secondPartialAt(v, e);
        j++;
      }
      j = 0;
      for(Edge f : Triangulation.edgeTable.values()) {
        nehr_eta_eta_partials[i][j] = VEHR.secondPartialAt(e, f);
        j++;
      }
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
  public double function(double[] vars) {
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
      return -1;
    }
    return nehr.getValue();
  }
  
  @Override
  public double[] gradient(double[] vars) {
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
    double[] gradient = new double[nehr_eta_partials.length];
    for(int i = 0; i < gradient.length; i++) {
      gradient[i] = nehr_eta_partials[i].getValue();
    }
    return gradient;
  }
  
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
    int e_length = etas.length;
    int v_length = radius_partials.length;
    Matrix hessian = new Matrix(e_length, e_length);
    
    double sum;
    for(int i = 0; i < e_length; i++) {
      for(int j = 0; j < e_length; j++) {
        sum = 0;
        for(int k = 0; k < v_length; k++) {
          sum += nehr_rad_eta_partials[k][i].getValue() * radius_partials[k][j].getValue();
        }
        hessian.m[i][j] = sum + nehr_eta_eta_partials[i][j].getValue();
      }
    }
    return hessian;
  }
}
