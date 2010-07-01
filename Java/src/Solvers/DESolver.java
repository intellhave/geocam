package Solvers;

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
}
