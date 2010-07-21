package solver;

import java.util.Observable;

public abstract class DESolver extends Observable{

  protected DESystem system;
  
  public DESolver(DESystem systemIn){
    system = systemIn;
  }
  
  public abstract double[] step(double[] x, double stepsize);
  
  public double[] run(double[] initial, double stepsize, int numSteps){
    double[] x = new double[initial.length];
    for(int i = 0; i < initial.length; i++){
      x[i] = initial[i];
    }
    
    for(int i = 0; i < numSteps; i++){
      x = step(x, stepsize);
      setChanged();
      notifyObservers(x);
    }

    return x;
  }
  
  public double[] run(double[] initial, double stepsize, double precision) {
    double[] curr = new double[initial.length];
    double[] next = new double[initial.length];
    for(int i = 0; i < initial.length; i++){
      curr[i] = initial[i];
    }
    
    double currentPrecision = 0;
    do{
      next = step(curr, stepsize);
      currentPrecision = 0;
      for(int i = 0; i < initial.length; i++) {
        currentPrecision += Math.pow(next[i] - curr[i], 2);
      }
      currentPrecision = Math.sqrt(currentPrecision);
      curr = next;
      setChanged();
      notifyObservers(curr);
    } while(currentPrecision > precision);
    
    return curr;
    
  }
}
