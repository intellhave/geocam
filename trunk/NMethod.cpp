#include "NMethod.h"
#include "utilities.h"
#include <stdlib.h>
#include <stdio.h>
#include <math.h>

void NewtonsMethod::optimize(double initial[], double soln[]) {
   for(int i = 0; i < nDim; i++) {
     soln[i] = initial[i];
   }
   double len;
   do {
     len = step(soln);   
   }
   while(len >= gradLenCond);
}

void NewtonsMethod::optimize(double initial[], double soln[], int extremum) {
   for(int i = 0; i < nDim; i++) {
     soln[i] = initial[i];
   }
   double len;
   do {
     len = step(soln, extremum);  
   }
   while(len >= gradLenCond);
}

double NewtonsMethod::step(double x_n[]) {
   double gradLen;
   double next[nDim];

   if(df == NULL) {
      approxGradient(x_n, grad);
   } else {
      df(x_n, grad);
   }

   if( d2f == NULL) {
      approxHessian(x_n, hess);
   } else {
      d2f(x_n, hess);
   }

   negateArray(grad);
   LinearEquationsSolver(hess, grad, next);
   gradLen = getGradientLength(grad);
      
   if(gradLen < gradLenCond) {
        return gradLen;
   }

   for(int i = 0; i < nDim; i++) {
      x_n[i] += next[i];
   }
   return gradLen;   
}

void printArray(double arr[], int size) {
     for(int i = 0; i < size; i++) {
             printf("arr[%d] = %f\n", i, arr[i]);
     }
}

double NewtonsMethod::step(double x_n[], int extremum) {
   double gradLen;
   double next[nDim];
   if(df == NULL) {
      approxGradient(x_n, grad);
   } else {
      df(x_n, grad);
   }

   if( d2f == NULL) {
      approxHessian(x_n, hess);
   } else {
      d2f(x_n, hess);
   }
   negateArray(grad);
   if(LinearEquationsSolver(hess, grad, next) == 1) {
     pause("Error with Solver!\n");
   }
   
   for(int i = 0; i < nDim; i++) {
      printf("next[%d] = %f\n", i, next[i]);
      next[i] = stepRatio * next[i];
   }
   gradLen = getGradientLength(grad);
      
   if(gradLen < gradLenCond) {
        return gradLen;
   }

   double curVal; 
   double nextVal = f(x_n);

   int maxNum = (int) floor( 1 / stepRatio );
   printf("MaxNum = %d\n", maxNum);
  // printArray(x_n, nDim);
   if( extremum == NMETHOD_MAX) {
       for(int j = 0; j < maxNum; j++) {
         curVal = nextVal;      
         for(int i = 0; i < nDim; i++) {
           x_n[i] += next[i];
         }
         nextVal = f(x_n);
         if( nextVal < curVal ) {
           if( j == 0 ) {
               int k = 1;
               while(nextVal < curVal && k <= 15) {
                 //pause("k = %d...", k); // PAUSE
                 for(int i = 0; i < nDim; i++) {
                   x_n[i] -= next[i] / (pow(2, k));
                 }
                 k++;
                 nextVal = f(x_n);
               }
               if(k > 15) {
                    pause("Wrong Direction! Press enter to continue...");
                    return -1;
               }
               return gradLen;
           }
           for(int i = 0; i < nDim; i++) {
             x_n[i] -= next[i];
           }
           return gradLen;   
         }
       }
   } else { // NMETHOD_MIN
       for(int j = 0; j < maxNum; j++) {
         curVal = nextVal;      
         for(int i = 0; i < nDim; i++) {
           x_n[i] += next[i];
         }
        // printArray(x_n, nDim);
         nextVal = f(x_n);
         if( nextVal > curVal ) {
           if( j == 0 ) {
               int k = 1;
               while(nextVal > curVal && k <= 15) {
                 for(int i = 0; i < nDim; i++) {
                   x_n[i] -= next[i] / (pow(2, k));
                 }
                 k++;
                 nextVal = f(x_n);
               }
               printf("k = %d\n", k);
               if(k > 15) {
                    pause("Wrong Direction! Press enter to continue...");
                    return -1;
               }
               return gradLen;
           }
           for(int i = 0; i < nDim; i++) {
             x_n[i] -= next[i];
           }
          // printArray(x_n, nDim);
           printf("j = %d\n", j);
           return gradLen;  
         }
       }   
   }
   
   return gradLen;   
}

void NewtonsMethod::negateArray(double arr[]) {
   for(int i = 0; i < nDim; i++) {
     arr[i] = -arr[i];
   }
}

void NewtonsMethod::approxGradient(double vars[], double sol[]) {
     double val = 0;
     
     for(int i = 0; i < nDim; i++) {
        printf("Approximating gradient of %d\n", i);
        //printf("grad[%d] : ", i);
        vars[i] = vars[i] + delta;
        val = f(vars);
        //printf("(%.10f - ", val);
        vars[i] = vars[i] - 2*delta;
        val = val - f(vars);
        //printf("%.10f) / (2 * %.10f) = ", f(vars), delX);
        sol[i] = val / (2*delta);
        //printf("%.10f\n", sol[i]);
        vars[i] = vars[i] + delta;
     }
}

void NewtonsMethod::approxHessian(double vars[], double *sol[]) {
     double val = 0;
     double f_x = f(vars);
     
     for(int i = 0; i < nDim; i++) {
       for(int j = 0; j < nDim; j++) {
          printf("Approximating hessian of %d, %d\n", i, j);
          if(i > j) {
            sol[i][j] = sol[j][i];
          } else if( i != j) {
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

   fprintf(out, "\nHessian:\n");
   for(int i = 0; i < nDim; i++) {
      for(int k = 0; k < nDim; k++) {
         fprintf(out, "% 3.5f", hess[i][k]);
      }
      fprintf(out, "\n");
   }
   fprintf(out, "-------------------\n");
   
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
    if(fabs(pfMatr[k][k]) < 0.000000001) {
       pfSolution[k] = 0;
    } else {
      pfSolution[k] = pfSolution[k] / pfMatr[k][k];
    }
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
