package Solvers;

public class EulerSolver extends DESolver {

  public EulerSolver(DESystem system){
    super(system);
  }
  
  public double[] step(double[] x1, double stepsize){
    double[] slopes = system.calcSlopes(x1);
    double[] x2 = new double[x1.length];
    for(int i = 0; i < x1.length; i++){
      x2[i] = x1[i] + slopes[i] * stepsize;
    }
    return x2;
  }
}
