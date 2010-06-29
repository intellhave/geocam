package Flows;

import util.Matrix;
import Geoquant.*;
import Solvers.NewtonsMethod;
import Solvers.WrongDirectionException;
import Triangulation.Edge;
import Triangulation.Triangulation;
import Triangulation.Vertex;

public class EtaOptNEHR extends NewtonsMethod {
  private Eta[] etas;
  private NEHR nehr;
  private NEHR.Partial[] nehr_eta_partials;
  private NEHR.SecondPartial[][] nehr_eta_eta_partials;
  private NEHR.SecondPartial[][] nehr_rad_eta_partials;
  private Radius.Partial[][] radius_partials;
  private RadiusOptNEHR minRadii;
  
  public EtaOptNEHR() {
    super();
    
    int vertSize = Triangulation.vertexTable.size();
    int edgeSize = Triangulation.edgeTable.size();
    
    nehr = NEHR.getInstance();
    etas = new Eta[edgeSize];
    nehr_eta_partials = new NEHR.Partial[edgeSize];
    nehr_eta_eta_partials = new NEHR.SecondPartial[edgeSize][edgeSize];
    nehr_rad_eta_partials = new NEHR.SecondPartial[vertSize][edgeSize];
    radius_partials = new Radius.Partial[vertSize][edgeSize];
    
    minRadii = new RadiusOptNEHR();
    
    int i = 0;
    int j;
    for(Edge e : Triangulation.edgeTable.values()) {
      etas[i] = Eta.At(e);
      nehr_eta_partials[i] = NEHR.partialAt(e);
      j = 0;
      for(Vertex v : Triangulation.vertexTable.values()) {
        radius_partials[j][i] = Radius.At(v).partialAt(e);
        nehr_rad_eta_partials[j][i] = NEHR.secondPartialAt(v, e);
        j++;
      }
      j = 0;
      for(Edge f : Triangulation.edgeTable.values()) {
        nehr_eta_eta_partials[i][j] = NEHR.secondPartialAt(e, f);
        j++;
      }
      i++;
    }
  }
  
  private void setEtas(double[] vars) {
    for(int i = 0; i < vars.length; i++) {
      etas[i].setValue(vars[i]);
    }
  }
  
  private double[] getLogRadii() {
    double[] values = new double[Triangulation.vertexTable.size()];
    int i = 0;
    for(Vertex v : Triangulation.vertexTable.values()) {
      values[i] = Math.log(Radius.valueAt(v));
      i++;
    }
    return values;
  }
  
  private void setLogRadii(double[] vars) {
    int i = 0;
    for(Vertex v : Triangulation.vertexTable.values()) {
      Radius.At(v).setValue(Math.exp(vars[i]));
      i++;
    }
  }
  
  @Override
  public double function(double[] vars) {
    setEtas(vars);
    try {
     setLogRadii(minRadii.minimize(getLogRadii()));
      
    } catch (Exception e) {
      double radii[] = getLogRadii();
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
      setLogRadii(minRadii.minimize(getLogRadii()));
    } catch (Exception e) {
      double radii[] = getLogRadii();
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
      setLogRadii(minRadii.minimize(getLogRadii()));
    } catch (Exception e) {
      double radii[] = getLogRadii();
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
