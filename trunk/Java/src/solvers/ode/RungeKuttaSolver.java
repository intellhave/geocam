package solvers.ode;

public class RungeKuttaSolver extends DESolver{
  public RungeKuttaSolver(DESystem system){
    super(system);
  }
  
  public double[] step(double[] x1, double stepsize){
    double[] x2 = new double[x1.length];

    double[][] samples = new double[4][x1.length];
    double[] weight = {1, 1.0/2, 1.0/2, 1};
    double[] slopes;
    
    for(int i = 0; i < x1.length; i++) {
      x2[i] = x1[i];
    }
    
    for(int i = 0; i < 4; i++) {
      slopes = system.calcSlopes(x2);
      for(int j = 0; j < x1.length; j++){
        samples[i][j] = slopes[j] * stepsize;
        x2[i] = x1[i] + samples[i][j] * weight[i];
      }
    }
    
    double avg;
    for(int i = 0; i < x1.length; i++) {
      avg = samples[0][i] + 2 * samples[1][i] + 2 * samples[2][i] + samples[3][i];
      avg = avg / 6;
      x2[i] = x1[i] + avg;
    }
    return x2;
  }
}
