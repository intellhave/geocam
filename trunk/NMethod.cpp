#include "NMethod.h"
#include <stdlib.h>
#include <stdio.h>
#include <math.h>

void NewtonsMethod::maximize(double initial[], double soln[]) {
   double gradLen;
      
   double next[nDim];
   
   for(int i = 0; i < nDim; i++) {
     soln[i] = initial[i];
   }
   int j = 1;
   while(true) {
      
      printf("\n***** Step %d *****\n", j++);
      
      for(int i = 0; i < nDim; i++) {
        printf("soln[%d] = %.10f\n", i, soln[i]);
      }
      if(df == NULL) {
        approxGradient(soln, grad);
      } else {
        df(soln, grad);
      }
      for(int i = 0; i < nDim; i++) {
        printf("grad[%d] = %.10f\n", i, grad[i]);
      }

      if( d2f == NULL) {
        approxHessian(soln, hess);
      } else {
        d2f(soln, hess);
      }
      for(int i = 0; i < nDim; i++) {
        for(int k = 0; k < nDim; k++) {
          printf("hess[%d][%d] = %.10f\n", i, k, hess[i][k]);
        }
      }

      negateArray(grad);
      LinearEquationsSolver(hess, grad, next);
      gradLen = getGradientLength(grad);
      
      if(gradLen < 0.00000001) {
        return;
      }
      printf("gradlen = %.10f\n", gradLen);
      for(int i = 0; i < nDim; i++) {
        printf("next[%d] = %.10f\n", i, next[i]);
      }

      for(int i = 0; i < nDim; i++) {
              soln[i] += next[i];
      }      
      system("PAUSE");
   }
}

double NewtonsMethod::step(double x_n[]) {
   double gradLen;
   double next[nDim];

//   for(int i = 0; i < nDim; i++) {
//      printf("soln[%d] = %.10f\n", i, soln[i]);
//   }
   if(df == NULL) {
      approxGradient(x_n, grad);
   } else {
      df(x_n, grad);
   }
//   for(int i = 0; i < nDim; i++) {
//      printf("grad[%d] = %.10f\n", i, grad[i]);
//   }

   if( d2f == NULL) {
      approxHessian(x_n, hess);
   } else {
      d2f(x_n, hess);
   }
//   for(int i = 0; i < nDim; i++) {
//      for(int k = 0; k < nDim; k++) {
//         printf("hess[%d][%d] = %.10f\n", i, k, hess[i][k]);
//      }
//   }

   negateArray(grad);
   LinearEquationsSolver(hess, grad, next);
   gradLen = getGradientLength(grad);
      
   if(gradLen < 0.0000000001) {
        return gradLen;
   }
//   printf("gradlen = %.10f\n", gradLen);
//   for(int i = 0; i < nDim; i++) {
//      printf("next[%d] = %.10f\n", i, next[i]);
//   }

   for(int i = 0; i < nDim; i++) {
      x_n[i] += next[i];
   }
   return gradLen;   
}

void NewtonsMethod::negateArray(double arr[]) {
   for(int i = 0; i < nDim; i++) {
     arr[i] = -arr[i];
   }
}

void NewtonsMethod::approxGradient(double vars[], double sol[]) {
     double delX = 0.00001;
     double val = 0;
     
     for(int i = 0; i < nDim; i++) {
        //printf("grad[%d] : ", i);
        vars[i] = vars[i] + delX;
        val = f(vars);
        //printf("(%.10f - ", val);
        vars[i] = vars[i] - 2*delX;
        val = val - f(vars);
        //printf("%.10f) / (2 * %.10f) = ", f(vars), delX);
        sol[i] = val / (2*delX);
        //printf("%.10f\n", sol[i]);
        vars[i] = vars[i] + delX;
     }
}

void NewtonsMethod::approxHessian(double vars[], double *sol[]) {
     double delta = 0.00001;
     double val = 0;
     double f_x = f(vars);
     
     for(int i = 0; i < nDim; i++) {
       for(int j = 0; j < nDim; j++) {
          if( i != j) {
              vars[i] = vars[i] + delta;
              vars[j] = vars[j] + delta;
              val = f(vars);
              vars[j] = vars[j] - 2 * delta;
              val = val - f(vars);
              vars[i] = vars[i] - 2*delta;
              val = val + f(vars);
              vars[j] = vars[j] + 2*delta;
              val = val - f(vars);
              vars[i] = vars[i] + delta;
              vars[j] = vars[j] - delta;
              
              sol[i][j] = val / (4 * delta * delta);
          } else {
              //printf("Hess[%d][%d] : ", i, j);
              vars[i] = vars[i] + delta;
              val = f(vars);
              //printf("(%.10f + ", val); 
              vars[i] = vars[i] - 2*delta;
              val = val + f(vars);
              //printf("%.10f - 2 * ", f(vars));
              vars[i] = vars[i] + delta;
              val = val - 2*f_x;
              //printf("%.10f) / %.10f = ", f_x, delta * delta);
              
              sol[i][j] = val / (delta * delta);
              //printf("%.10f\n", sol[i][j]);
          }
       }
     }
}


void NewtonsMethod::printInfo(FILE* out) {
   for(int i = 0; i < nDim; i++) {
      fprintf(out, "grad[%d] = %.10f\n", i, grad[i]);
   }     

   for(int i = 0; i < nDim; i++) {
      for(int k = 0; k < nDim; k++) {
         printf("hess[%d][%d] = %.10f\n", i, k, hess[i][k]);
      }
   }
   
   if(printFunc != NULL) {
     printFunc(out);
   }
} 

//==============================================================================
// return 1 if system not solving
// nDim - system dimension
// pfMatr - matrix with coefficients
// pfVect - vector with free members
// pfSolution - vector with system solution
// pfMatr becames trianglular after function call
// pfVect changes after function call
//
// Developer: Henry Guennadi Levkin
//
//==============================================================================
int NewtonsMethod::LinearEquationsSolver(double *pfMatr[], double* pfVect, double* pfSolution)
{
  double maxElem; // Used to determine row with max element.
  double temp; // Just a temp variable for switching 

  int i, j, k, m; // indexing variables


  // For each column in matrix...
  for(k=0; k<(nDim-1); k++) 
  // Here k will act as both the starting row and the 
  // current column for the sub-matrix.
  {
    // Find the row with the max element in column k 
    // (ignoring rows above k already completed).
    // NOTE: This is to lessen potential rounding errors.
    maxElem = fabs( pfMatr[k][k] ); // First element
    m = k; // m is the index of the currently found max row
    for(i=k+1; i<nDim; i++) // Iterate through every row below k.
    {
      if(maxElem < fabs(pfMatr[i][k]) )
      { // If this row has the new max element... set values.
        maxElem = fabs(pfMatr[i][k]);
        m = i;
      }
    }
    
    // Permute the kth row with the row with the max element (if necessary).
    if(m != k)
    {
      for(i=k; i<nDim; i++)
      {
        temp               = pfMatr[k][i];
        pfMatr[k][i] = pfMatr[m][i];
        pfMatr[m][i] = temp;
      }
      // Also permute the vector.
      temp = pfVect[k];
      pfVect[k] = pfVect[m];
      pfVect[m] = temp;
    }

    if( pfMatr[k][k] == 0.) return 1; // Matrix has a column of all 0s !!!

    // Triangulate matrix by turning every value in column k below row k 
    // into a 0 using row operations.
    for(j=(k+1); j<nDim; j++) // j is each row below row k.
    {
      // The multiplicative value in the row operation.
      temp = - pfMatr[j][k] / pfMatr[k][k];
       // For each entry in row j to the right of column k
      for(i=k; i<nDim; i++)
      { // perform row operation
        pfMatr[j][i] += temp*pfMatr[k][i];
      }
      // Also perform operation on the vector
      pfVect[j] = pfVect[j] + temp*pfVect[k];
    }
  }

  // For each row beginning with the bottom
  for(k=(nDim-1); k>=0; k--)
  { // Calculate the solution for variable k.
    pfSolution[k] = pfVect[k];
    // Adjust solution using variables already known.
    for(i=(k+1); i<nDim; i++)
    {
      pfSolution[k] -= (pfMatr[k][i]*pfSolution[i]);
    }
    pfSolution[k] = pfSolution[k] / pfMatr[k][k];
  }

  return 0;
}

double NewtonsMethod::getGradientLength(double gradient[]) {
   double sum = 0;
   for(int i = 0; i < nDim; i++) {
      sum += gradient[i] * gradient[i];
   }
   return sqrt(sum);
}
