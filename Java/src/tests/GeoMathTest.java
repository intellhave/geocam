package tests;

import util.GeoMath;
import util.Matrix;

public class GeoMathTest {
  public static void main(String[] args) {
    testSingularitySolver();
  }
 
  public static void testLinearEquationsSolver() {
    Matrix A = new Matrix(3, 3);
    
    A.m[0][0] = 1;
    A.m[0][1] = 2;
    A.m[0][2] = 3;
    
    A.m[1][0] = 4;
    A.m[1][1] = 5;
    A.m[1][2] = 6;
    
    A.m[2][0] = 7;
    A.m[2][1] = 8;
    A.m[2][2] = 9;
    
    double[] b = {1, 1, 1};
    double[] x = GeoMath.LinearEquationsSolver(A, b);
    for(int i = 0; i < x.length; i++) {
      System.out.printf("x[%d] = %f\n", i, x[i]);
    }
  }
  
  public static void testSingularitySolver() {
    Matrix A = new Matrix(4, 4);
    
    A.m[0][0] = 1;
    A.m[0][1] = 2;
    A.m[0][2] = 3;
    A.m[0][3] = 4;
    
    A.m[1][0] = 0;
    A.m[1][1] = 0;
    A.m[1][2] = 1;
    A.m[1][3] = 2;

    A.m[2][0] = 0;
    A.m[2][1] = 0;
    A.m[2][2] = 1;
    A.m[2][3] = 2;
    
    A.m[3][0] = 0;
    A.m[3][1] = 0;
    A.m[3][2] = 0;
    A.m[3][3] = 0;
    
    double[] b = {1, 1, 1, 0};
    double[] x = GeoMath.LinearEquationsSolver(A, b);
    for(int i = 0; i < x.length; i++) {
      System.out.printf("x[%d] = %f\n", i, x[i]);
    }
  }
}
