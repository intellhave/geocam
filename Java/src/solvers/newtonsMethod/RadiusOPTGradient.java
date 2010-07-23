package solvers.newtonsMethod;

import geoquant.Geometry;
import geoquant.Radius;
import inputOutput.TriangulationIO;
import triangulation.Triangulation;
import util.Matrix;

public class RadiusOPTGradient extends RadiusOptNEHR {
  @Override
  public Matrix hessian(double[] vars) {
    setLogRadii(vars);
    Matrix hessian = new Matrix(vars.length, vars.length);
    for(int i = 0; i < vars.length; i++) {
      for(int j = 0; j < vars.length; j++) {
        if(i == j) {
          hessian.m[i][j] = 1;
        } else {
          hessian.m[i][j] = 0;
        }
      }
    }
    return hessian;
  }
  
  public static void main(String[] args) {
//    TriangulationIO.readTriangulation("Data/Triangulations/CommonManifolds/pentachoron_regular.xml");
//    Radius.At(Triangulation.vertexTable.get(1)).setValue(1.5);
//
//    RadiusOPTGradient flow = new RadiusOPTGradient();
//    flow.setLogRadii(flow.optimize(flow.getLogRadii()));
//    
//    for(Radius r : Geometry.getRadii()) {
//      System.out.println(r);
//    }
    
    TriangulationIO.readTriangulation("Data/Triangulations/CommonManifolds/pentachoron_regular.xml");
    Radius.At(Triangulation.vertexTable.get(1)).setValue(1.5);
    
    RadiusOptNEHR flow2 = new RadiusOptNEHR();
    flow2.setLogRadii(flow2.optimize(flow2.getLogRadii()));
    for(Radius r : Geometry.getRadii()) {
      System.out.println(r);
    }
  }
}
