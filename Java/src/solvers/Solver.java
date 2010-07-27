package solvers;

import java.util.Observable;

public abstract class Solver extends Observable{
  // The value used for advancing along the gradient, indicates the amount
  // to move in that direction.
  protected double stepsize; 
  // This value is used only as a safety to end a step early if the length
  // of the gradient is very nearly 0.0.
  protected double stoppingCond;
  
  public Solver() {
    setDefaults();
  }
  
  public void setStepsize(double step) {
    this.stepsize = step;
  }
  
  public void setStoppingCondition(double cond) {
    this.stoppingCond = cond;
  }
  
  public double getStoppingCondition() {
    return stoppingCond;
  }
  
  public void setDefaults() {
    stepsize = 1;
    stoppingCond = 0.0001;
  }
  
  public abstract double[] calcSlopes(double[] x);

  public double[] step(double[] x) {
    double[] slopes = calcSlopes(x);
    double[] x_n = new double[x.length];
    for(int i = 0; i < x.length; i++){
      x_n[i] = x[i] + slopes[i] * stepsize;
    }
    return x_n;
  }
  
  public double[] run(double[] initial) {
    double[] curr = new double[initial.length];
    double[] next = new double[initial.length];
    for(int i = 0; i < initial.length; i++){
      curr[i] = initial[i];
    }
    
    double currentPrecision = 0;
    do{
      next = step(curr);
      currentPrecision = 0;
      for(int i = 0; i < initial.length; i++) {
        currentPrecision += Math.pow(next[i] - curr[i], 2);
      }
      currentPrecision = Math.sqrt(currentPrecision);
      curr = next;
      setChanged();
      notifyObservers(curr);
    } while(currentPrecision > stoppingCond);
    
    return curr; 
  }
  
  public double[] run(double[] initial, int numSteps) {
    double[] curr = new double[initial.length];
    double[] next = new double[initial.length];
    for(int i = 0; i < initial.length; i++){
      curr[i] = initial[i];
    }
   
    for(int j = 0; j < numSteps; j++) {
      next = step(curr);
      curr = next;
      setChanged();
      notifyObservers(curr);
    }
    
    return curr; 
  }
}
