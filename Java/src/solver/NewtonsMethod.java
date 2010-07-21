package solver;

import java.util.Observable;

import util.GeoMath;
import util.Matrix;

public abstract class NewtonsMethod extends Observable{
  // The value used for approximating derivatives.
  protected double delta;
  // The value used for advancing along the gradient, indicates the amount
  // to move in that direction.
  protected double stepRatio; 
  // This value is used only as a safety to end a step early if the length
  // of the gradient is very nearly 0.0.
  protected double gradLenCond;
  protected double[] grad;
  protected Matrix hess;
  
  
  public abstract double function(double[] vars);
  
  public double[] gradient(double[] vars) {
    double val = 0;        // A running sum for the numerator
    double[] sol = new double[vars.length];
    for(int i = 0; i < vars.length; i++) {
       vars[i] = vars[i] + delta; // x_i + delta
       val = function(vars); // f(x_i + delta);
       vars[i] = vars[i] - 2*delta; // x_i - delta
       val = val - function(vars); // f(x_i + delta) - f(x_i - delta)
       sol[i] = val / (2*delta); // soln[i] = [f(x_i + delta) - f(x_i - delta)] 
                                 //              / (2 * delta)
       vars[i] = vars[i] + delta; // Reset x_i.
    }
    return sol;
  }

  public Matrix hessian(double[] vars) {
    double val = 0;        // A running sum for the numerator
    double f_x = function(vars);  // Save f(vars)
    double temp;
    Matrix sol = new Matrix(vars.length,vars.length);
    
    for(int i = 0; i < vars.length; i++) {
      for(int j = 0; j < vars.length; j++) {
         if(i > j) { // The matrix is diagonal
           sol.m[i][j] = sol.m[j][i];
         } else if( i != j) { // If i != j
             vars[i] = vars[i] + delta; // x_i + delta
             vars[j] = vars[j] + delta; // x_j + delta
             val = function(vars); // f(x_i + delta, x_j + delta)
             vars[j] = vars[j] - 2 * delta; // x_j - delta
             temp = function(vars); 
             val = val - temp; // val - f(x_i + delta, x_j - delta)
             vars[i] = vars[i] - 2*delta; // x_i - delta
             temp = function(vars);
             val = val + temp; // val + f(x_i - delta, x_j - delta)
             vars[j] = vars[j] + 2*delta; // x_j + delta
             temp = function(vars);
             val = val - temp; // val - f(x_i - delta, x_j + delta)
             vars[i] = vars[i] + delta; // Reset x_i
             vars[j] = vars[j] - delta; // Reset x_j
             
             
             //    f(x_i + del, x_j + del) - f(x_i + del, x_j - del) 
             //       - f(x_i - del, x_j + del) + f(x_i - del, x_j - del)
             //  ------------------------------------------------------------
             //                        4 * delta ^ 2
             sol.m[i][j] = val / (4 * delta * delta);
         } else { // If i = j
             vars[i] = vars[i] + 2*delta; // x_i + 2 * delta
             val = function(vars); // f(x_i + delta)
             vars[i] = vars[i] - 4*delta; // x_i - 2 * delta
             temp = function(vars);
             val = val + temp; // val + f(x_i - delta)
             vars[i] = vars[i] + 2 * delta; // reset x_i
             val = val - 2*f_x; // val - 2 * f(vars)
             
             
             //  f(x_i + 2 * delta) + f(x_i - 2 * delta) - 2 * f(x)
             // --------------------------------------------
             //                 4 * delta ^ 2
             sol.m[i][j] = val / (4*delta * delta);
         }
      }
    }
    return sol;
  }

  public void setDefaults() {
    delta = 0.00001;
    stepRatio = 1.0 / 10.0;
    gradLenCond = 0.00001;
  }
  
  public NewtonsMethod() {
    setDefaults();
  }
  
  
  public void setDelta(double delta) {
    this.delta = delta;
  }
  
  public void setStepRatio(double step) {
    this.stepRatio = step;
  }
  
  public void setStoppingCondition(double cond) {
    this.gradLenCond = cond;
  }
  
  public double getStoppingCondition() {
    return gradLenCond;
  }
        
  protected double[] buildNext(double[] x_n) {
    double gradLen;        // The length of the gradient vector
    double[] next;  // The array values that x_n will be incremented by.
    
    grad = gradient(x_n);
    hess = hessian(x_n);
    
    // Calculate the length of the gradient.
    gradLen = GeoMath.magnitude(grad);
    
    // If the gradient length is already less than the stopping condition, then
    // just return now. This is for cases when gradLen = 0, which can cause other
    // problems.
    if(gradLen < gradLenCond) {
         return null;
    }   
    
    // Set grad[i] = - grad[i] for all i = 1,...,dim
    GeoMath.negate(grad);
    
    next = GeoMath.LinearEquationsSolver(hess, grad);
    if(next == null) {
      return null;
    }
    // Modify the next[] array by scaling it by stepRatio.
    for(int i = 0; i < next.length; i++) {
       next[i] = stepRatio * next[i];
    }
    return next;
  }
  
  public double stepMin(double[] x_n) throws WrongDirectionException {
    double gradLen;        // The length of the gradient vector
    double[] next;  // The array values that x_n will be incremented by.

    next = buildNext(x_n);
    
    gradLen = GeoMath.magnitude(grad);
    
    if(gradLen < gradLenCond) {
      return gradLen;
    }   
    if(next == null) {
      return -1;
    }

    double curVal;           // The current function value.
    double nextVal = function(x_n); // The next function value.
       
    // The max number of increments before a point has moved the entire length
    // in the direction of optimization.
    int maxNum = (int) Math.floor( 1 / stepRatio ); 
    
    // The search is for a minimum
    for(int j = 0; j < maxNum; j++) {
      curVal = nextVal;   // Set current value to next.
      // Increment the point by one unit of next[].      
      for(int i = 0; i < next.length; i++) {
        x_n[i] += next[i];
      }
      nextVal = function(x_n);   // Get the next value of the function.
      // If the nextVal > curVal, we have moved too far and we want to 
      // stop.
      if( nextVal > curVal ) {
        // If this was the first increment, then we need to check smaller.
        if( j == 0 ) {
          int k = 1;
          // While we still haven't decreased and we're still decrementing
          // by a non-zero amount...
          while(nextVal > curVal && k <= 15) {
            // Decrement by a scaling factor of 1/2, 1/4, 1/8, etc.
            for(int i = 0; i < next.length; i++) {
              x_n[i] -= next[i] / (Math.pow(2, k));
            }
            k++;
            nextVal = function(x_n);   // Get the next value of the functional.
          }
          // If we still couldn't find it, announce that it can't be found
          // (i.e. we were headed towards a maximum) and return -1.
          if(nextVal > curVal) {
            System.err.println("Wrong Direction! : " + this.getClass());
            throw new WrongDirectionException();
          }
          // Else if it was found, we are at the correct point right now, 
          // so return.
          return gradLen;
        }
        // If this wasn't the first increment, then we found the right point,
        // but we went one unit too far.
        for(int i = 0; i < next.length; i++) {
          x_n[i] -= next[i];
        }
        return gradLen;  
      }   
    }
    
    return gradLen;  
  }
  
  public double stepMax(double[] x_n) throws WrongDirectionException {
    double gradLen;        // The length of the gradient vector
    double[] next;  // The array values that x_n will be incremented by.

    next = buildNext(x_n);
    
    gradLen = GeoMath.magnitude(grad);
    
    if(gradLen < gradLenCond) {
      return gradLen;
    }   
    if(next == null) {
      return -1;
    }
    
    double curVal;           // The current function value.
    double nextVal = function(x_n); // The next function value.
       
    // The max number of increments before a point has moved the entire length
    // in the direction of optimization.
    int maxNum = (int) Math.floor( 1 / stepRatio ); 
    
    // The search is for a maximum
    for(int j = 0; j < maxNum; j++) {
      curVal = nextVal;   // Set current value to next.
      // Increment the point by one unit of next[].      
      for(int i = 0; i < next.length; i++) {
        x_n[i] += next[i];
      }
      nextVal = function(x_n);   // Get the next value of the function.
      // If the nextVal < curVal, we have moved too far and we want to 
      // stop.
      if( nextVal < curVal ) {
        // If this was the first increment, then we need to check smaller.
        if( j == 0 ) {
          int k = 1;
          // While we still haven't increased and we're still decrementing
          // by a non-zero amount...
          while(nextVal < curVal && k <= 15) {
            // Decrement by a scaling factor of 1/2, 1/4, 1/8, etc.
            for(int i = 0; i < next.length; i++) {
              x_n[i] -= next[i] / (Math.pow(2, k));
            }
            k++;
            nextVal = function(x_n);   // Get the next value of the functional.
          }
          // If we still couldn't find it, announce that it can't be found
          // (i.e. we were headed towards a minimum) and return -1.
          if(nextVal < curVal) {
            System.err.println("Wrong Direction! : " + this.getClass());
            throw new WrongDirectionException();
          }
          // Else if it was found, we are at the correct point right now, 
          // so return.
          return gradLen;
        }
        // If this wasn't the first increment, then we found the right point,
        // but we went one unit too far.
        for(int i = 0; i < next.length; i++) {
          x_n[i] -= next[i];
        }
        return gradLen;  
      }   
    }
    
    return gradLen;  
  }
  
  public double step(double[] x_n) {
    double gradLen;        // The length of the gradient vector
    double[] next;  // The array values that x_n will be incremented by.

    next = buildNext(x_n);
    
    gradLen = GeoMath.magnitude(grad);
    
    if(gradLen < gradLenCond) {
      return gradLen;
    }
    if(next == null) {
      return -1;
    }
    
    for(int i = 0; i < next.length; i++) {
      x_n[i] += next[i];
    }
    return gradLen;
  }
  
  public double[] minimize(double[] initial) throws WrongDirectionException {
    double[] soln = new double[initial.length];
    // Copy the values of initial to soln.
    for(int i = 0; i < initial.length; i++) {
      soln[i] = initial[i];
    }
    double len;
    do {
      // Step through NewtonsMethod, updating soln, until len < gradLenCond
      len = stepMin(soln);
      setChanged();
      notifyObservers(soln);
    }
    while(len >= gradLenCond);
    return soln;
  }
  
  public double[] maximize(double[] initial) throws WrongDirectionException {
    double[] soln = new double[initial.length];
    // Copy the values of initial to soln.
    for(int i = 0; i < initial.length; i++) {
      soln[i] = initial[i];
    }
    double len;
    do {
      // Step through NewtonsMethod, updating soln, until len < gradLenCond
      len = stepMax(soln);  
      setChanged();
      notifyObservers(soln);
    }
    while(len >= gradLenCond);
    return soln;
  }
  
  public double[] optimize(double[] initial) {
    double[] soln = new double[initial.length];
    // Copy the values of initial to soln.
    for(int i = 0; i < initial.length; i++) {
      soln[i] = initial[i];
    }
    double len;
    do {
      // Step through NewtonsMethod, updating soln, until len < gradLenCond
      len = step(soln);
      setChanged();
      notifyObservers(soln);
    }
    while(len >= gradLenCond);
    return soln;
  }
}
