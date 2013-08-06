package solvers.implemented;

import geoquant.LKCurvature;
import geoquant.Radius;
import solvers.Solver;
import triangulation.Triangulation;
import triangulation.Vertex;

public class conformaldiskflow extends Solver{

  @Override
  public double[] calcSlopes(double[] x) {
    int i = 0;
    for(Vertex v: Triangulation.vertexTable.values()){
      Radius r;
      r=Radius.at(v);
      r.setValue(x[i]);
      i++;
    }
    double[] slopes = new double[x.length];
    i = 0;
    
    double renorm=0;
    
    for(Vertex v: Triangulation.vertexTable.values()){
      if (v.getMultiplicity()==-1){
        LKCurvature K = LKCurvature.at(v);
       renorm = (K.getValue());
        break;
      }
    }
//    
    for(Vertex v: Triangulation.vertexTable.values()){
      LKCurvature K = LKCurvature.at(v);
//        slopes[i] = x[i] *(renorm - K.getValue());
      slopes[i] = x[i] *(- K.getValue());
      if (v.getMultiplicity()==-1){
        slopes[i]= -slopes[i];
//        slopes[i]=0;
      }
      i++;
    }
    return slopes;
  }

}
