package solvers;

import util.GeoMath;
import util.Matrix;

public abstract class NewtonsMethod extends Solver{
  // The value used for approximating derivatives.
  protected double delta;
  protected double[] grad;
  protected Matrix hess;
  
  public NewtonsMethod() {
    super();
    delta = 0.00001;
  }
  
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
  
  public void setDelta(double delta) {
    this.delta = delta;
  }
  
  public double[] calcSlopes(double[] x_n) {
    double gradLen;        // The length of the gradient vector
    double[] next;  // The array values that x_n will be incremented by.
    
    grad = gradient(x_n);
    hess = hessian(x_n);
    
    // Calculate the length of the gradient.
    gradLen = GeoMath.magnitude(grad);
    
    // If the gradient length is already less than the stopping condition, then
    // just return now. This is for cases when gradLen = 0, which can cause other
    // problems.
    if(gradLen < 0.00001 && gradLen < stoppingCond) {
         return new double[x_n.length];
    }   
    
    // Set grad[i] = - grad[i] for all i = 1,...,dim
    GeoMath.negate(grad);
    
    next = GeoMath.LinearEquationsSolver(hess, grad);
    if(next == null) {
      return new double[x_n.length];
    }
    return next;
  }
  
}
