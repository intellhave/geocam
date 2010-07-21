package tests;

import solver.NewtonsMethod;
import solver.WrongDirectionException;
import util.Matrix;

public class NewtonsMethodTest {
  public static void main(String[] args) {
    testNMethod();
  }
  
  public static void testNMethod() {
    class Ellipse extends NewtonsMethod {

      public double function(double[] vars) {
        double val = 1 - Math.pow(vars[0], 2) / 4 - Math.pow(vars[1], 2) / 9;
        return Math.sqrt(val);
      }
      
      @Override
      public double[] gradient(double[] vars) {
        double[] sol = new double[vars.length];
        double val = 1 - Math.pow(vars[0], 2) / 4 - Math.pow(vars[1], 2) / 9;
        sol[0] = (-vars[0]/4)/Math.sqrt(val);
        sol[1] = (-vars[1]/9)/Math.sqrt(val);
        return sol;
      }
      
      @Override
      public Matrix hessian(double[] vars) {
        Matrix sol = new Matrix(vars.length, vars.length);
        double val = 1 - Math.pow(vars[0], 2) / 4 - Math.pow(vars[1], 2) / 9;
        sol.m[0][0] = ((-1.0/4)*val - (vars[0]/16)) / Math.pow(val, 3.0/2);
        sol.m[1][1] = ((-1.0/9)*val - (vars[1]/81)) / Math.pow(val, 3.0/2);
        sol.m[0][1] = (-vars[0]/4)*(-vars[1]/9)*(-1) / Math.pow(val, 3.0/2);
        sol.m[1][0] = sol.m[0][1];
        return sol;
      }
    }
    NewtonsMethod nm = new Ellipse();
    double x_n[] = {1, 1};
    int i = 1;
    for(int j = 0; j < x_n.length; j++) {
      System.out.printf("x_n_%d[%d] = %f\n", i, j, x_n[j]);
    }
    // Continue with the procedure until the length of the gradient is
    // less than 0.000001.
    try {
      while(nm.stepMax(x_n) > 0.000001) {
        System.out.printf("\n***** Step %d *****\n", i++);
        for(int j = 0; j < x_n.length; j++) {
          System.out.printf("x_n_%d[%d] = %f\n", i, j, x_n[j]);
        }
      }
    } catch (WrongDirectionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}
