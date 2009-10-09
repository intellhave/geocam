/*=========================================================================
 ||Class: NewtonsMethod (Generalized Newton's Method)
 ||Author: Alex Henniges (henniges@email.arizona.edu)
 ||
 || The source file for the NewtonsMethod class.
 *=========================================================================*/
#include "NMethod.h"
#include "utilities.h"
#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include <cerrno>

void NewtonsMethod::optimize(double initial[], double soln[]) {
   // Copy the values of initial to soln.
   for(int i = 0; i < nDim; i++) {
     soln[i] = initial[i];
   }
   double len;
   do {
     // Step through NewtonsMethod, updating soln, until len < gradLenCond
     len = step(soln);   
   }
   while(len >= gradLenCond);
}

void NewtonsMethod::optimize(double initial[], double soln[], int extremum) {
   // Same as above, but use the extremum value in the step function.
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
   double gradLen;        // The length of the gradient vector
   double next[nDim];     // The array values that x_n will be incremented by.

   // If a gradient function was not provided, approximate the gradient instead.
   if(df == NULL) {
      approxGradient(x_n, grad);
   } else { // Otherise, get the gradient vector from a user-defined function.
      df(x_n, grad);
   }

   // If a hesiian function was not provided, approximate the hessian instead.
   if( d2f == NULL) {
      approxHessian(x_n, hess);
   } else { // Otherwise, get the hessian matrix from a user-defined function.
      d2f(x_n, hess);
   }
      
   // Calculate the length of the gradient.
   gradLen = getGradientLength(grad);

   // If the gradient length is already less than the stopping condition, then
   // just return now. This is for cases when gradLen = 0, which can cause other
   // problems.
   if(gradLen < gradLenCond) {
        return gradLen;
   }   
      
   // Set grad[i] = - grad[i] for all i = 1,...,nDim
   negateArray(grad);
   // Use the linear equations solver to fill out values for next[].
   if(LinearEquationsSolver(hess, grad, next) == 1) {
     printf("Error with Solver\n");
     return -1;
   }

   // Shift x_n[i] by next[i].
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
   double gradLen;        // The length of the gradient vector
   double next[nDim];     // The array values that x_n will be incremented by.

   // If a gradient function was not provided, approximate the gradient instead.
   if(df == NULL) {
      approxGradient(x_n, grad);
   } else { // Otherise, get the gradient vector from a user-defined function.
      df(x_n, grad);
   }

   // If a hesiian function was not provided, approximate the hessian instead.
   if( d2f == NULL) {
      approxHessian(x_n, hess);
   } else { // Otherwise, get the hessian matrix from a user-defined function.
      d2f(x_n, hess);
   }
   // Calculate the length of the gradient.
   gradLen = getGradientLength(grad);
   
   // If the gradient length is already less than the stopping condition, then
   // just return now. This is for cases when gradLen = 0, which can cause other
   // problems.
   if(gradLen < gradLenCond) {
        f(x_n);
        return gradLen;
   }   
   
   // Set grad[i] = - grad[i] for all i = 1,...,nDim
   negateArray(grad);
   
   // Use the linear equations solver to fill out values for next[].
   if(LinearEquationsSolver(hess, grad, next) == 1) {
     printf("Error with Solver\n");
     return -1;
   }
   // Modify the next[] array by scaling it by stepRatio.
   for(int i = 0; i < nDim; i++) {
      next[i] = stepRatio * next[i];
   }

   double curVal;           // The current function value.
   double nextVal = f(x_n); // The next function value.
      
   // The max number of increments before a point has moved the entire length
   // in the direction of optimization.
   int maxNum = (int) floor( 1 / stepRatio ); 
   
   // If the searched for extremum is a maximum...
   if( extremum == NMETHOD_MAX) {
       for(int j = 0; j < maxNum; j++) {
         curVal = nextVal;   // Set current value to next.
         // Increment the point by one unit of next[].
         for(int i = 0; i < nDim; i++) {
           x_n[i] += next[i];
         }
         nextVal = f(x_n);   // Get the next value of the function.
         // If the nextVal < curVal, we have moved too far and we want to 
         // stop.
         if( nextVal < curVal ) {
           // If this was the first increment, then we need to check smaller.
           if( j == 0 ) {
               int k = 1;
               // While we still haven't increased and we're still decrementing
               // by a non-zero amount...
               while(nextVal < curVal && k <= 15) {
                 // Decrement by a sclaing factor of 1/2, 1/4, 1/8, etc.
                 for(int i = 0; i < nDim; i++) {
                   x_n[i] -= next[i] / (pow(2, k));
                 }
                 k++;
                 nextVal = f(x_n);   // Get the next value of the functional.
               }
               // If we still couldn't find it, announce that it can't be found
               // (i.e. we were headed towards a minimum) and return -1.
               if(nextVal < curVal) {
//DAN                    printf("Wrong Direction!\n");
                    return -1;
               }
               // Else if it was found, we are at the correct point right now, 
               // so return.
               return gradLen;
           }
           // If this wasn't the first increment, then we found the right point,
           // but we went one unit too far.
           for(int i = 0; i < nDim; i++) {
             x_n[i] -= next[i];
           }
           return gradLen;   
         }
       }
   } else { // The search is for a minimum
       for(int j = 0; j < maxNum; j++) {
         curVal = nextVal;   // Set current value to next.
         // Increment the point by one unit of next[].      
         for(int i = 0; i < nDim; i++) {
           x_n[i] += next[i];
         }
         nextVal = f(x_n);   // Get the next value of the function.
         // If the nextVal > curVal, we have moved too far and we want to 
         // stop.
         if( nextVal > curVal ) {
           // If this was the first increment, then we need to check smaller.
           if( j == 0 ) {
               int k = 1;
               // While we still haven't decreased and we're still decrementing
               // by a non-zero amount...
               while(nextVal > curVal && k <= 15) {
                 // Decrement by a sclaing factor of 1/2, 1/4, 1/8, etc.
                 for(int i = 0; i < nDim; i++) {
                   x_n[i] -= next[i] / (pow(2, k));
                 }
                 k++;
                 nextVal = f(x_n);   // Get the next value of the functional.
               }
               // If we still couldn't find it, announce that it can't be found
               // (i.e. we were headed towards a minimum) and return -1.
               if(nextVal > curVal) {
                    printf("Wrong Direction!\n");
                    return -1;
               }
               // Else if it was found, we are at the correct point right now, 
               // so return.
               return gradLen;
           }
           // If this wasn't the first increment, then we found the right point,
           // but we went one unit too far.
           for(int i = 0; i < nDim; i++) {
             x_n[i] -= next[i];
           }
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
     double val = 0;        // A running sum for the numerator
     
     for(int i = 0; i < nDim; i++) {
//        printf("grad: %d\n", i);
        vars[i] = vars[i] + delta; // x_i + delta
        val = f(vars); // f(x_i + delta);
        vars[i] = vars[i] - 2*delta; // x_i - delta
        val = val - f(vars); // f(x_i + delta) - f(x_i - delta)
        sol[i] = val / (2*delta); // soln[i] = [f(x_i + delta) - f(x_i - delta)] 
                                  //              / (2 * delta)
        vars[i] = vars[i] + delta; // Reset x_i.
     }
}

void NewtonsMethod::approxHessian(double vars[], double *sol[]) {
     double val = 0;        // A running sum for the numerator
     double f_x = f(vars);  // Save f(vars)
     double temp;
     //DEBUG: FILE* hessFile = fopen("Data/Input_EHR/hessian.txt", "w");
     for(int i = 0; i < nDim; i++) {
       for(int j = 0; j < nDim; j++) {
        //DEBUG: fprintf(hessFile, "hess: %d, %d = ", i, j);
          if(i > j) { // The matrix is diagonal
            sol[i][j] = sol[j][i];
          } else if( i != j) { // If i != j
              vars[i] = vars[i] + delta; // x_i + delta
              vars[j] = vars[j] + delta; // x_j + delta
              val = f(vars); // f(x_i + delta, x_j + delta)
              //DEBUG: fprintf(hessFile, "%.20f - ", val);
              vars[j] = vars[j] - 2 * delta; // x_j - delta
              temp = f(vars); 
              val = val - temp; // val - f(x_i + delta, x_j - delta)
              //DEBUG: fprintf(hessFile, "%.20f + ", temp);
              vars[i] = vars[i] - 2*delta; // x_i - delta
              temp = f(vars);
              val = val + temp; // val + f(x_i - delta, x_j - delta)
              //DEBUG: fprintf(hessFile, "%.20f - ");
              vars[j] = vars[j] + 2*delta; // x_j + delta
              temp = f(vars);
              //DEBUG: fprintf(hessFile, "%.20f  / (4 * %f^2) = ", temp, delta);
              val = val - temp; // val - f(x_i - delta, x_j + delta)
              vars[i] = vars[i] + delta; // Reset x_i
              vars[j] = vars[j] - delta; // Reset x_j
              
              //DEBUG: fprintf(hessFile, "%.20f / (4*del^2) = ", val);
              
              //    f(x_i + del, x_j + del) - f(x_i + del, x_j - del) 
              //       - f(x_i - del, x_j + del) + f(x_i - del, x_j - del)
              //  ------------------------------------------------------------
              //                        4 * delta ^ 2
              sol[i][j] = val / (4 * delta * delta);
          } else { // If i = j
              vars[i] = vars[i] + 2*delta; // x_i + 2 * delta
              val = f(vars); // f(x_i + delta)
              //DEBUG: fprintf(hessFile, "%.20f + ", val);
              vars[i] = vars[i] - 4*delta; // x_i - 2 * delta
              temp = f(vars);
              //DEBUG: fprintf(hessFile, "%.20f - 2*", temp);
              val = val + temp; // val + f(x_i - delta)
              vars[i] = vars[i] + 2 * delta; // reset x_i
              //DEBUG: fprintf(hessFile, "%.20f / (4 * %f^2) = ", f_x, delta);
              val = val - 2*f_x; // val - 2 * f(vars)
              
              //DEBUG: fprintf(hessFile, "%.20f / (4*del^2) = ", val);
              
              //  f(x_i + 2 * delta) + f(x_i - 2 * delta) - 2 * f(x)
              // --------------------------------------------
              //                 4 * delta ^ 2
              sol[i][j] = val / (4*delta * delta);
          }
          //DEBUG: fprintf(hessFile, "%.20f\n", sol[i][j]);
       }
     }
     //DEBUG: fclose(hessFile);
}


void NewtonsMethod::printInfo(FILE* out) {
/*DEBUG:   fprintf(out, "grad = <");
/*   for(int i = 0; i < nDim; i++) {
/*     fprintf(out, "%f, ", grad[i]);
/*   }
/*   fprintf(out, ">\n");
/*   
/*   fprintf(out, "Hess = \n");
/*   for(int i = 0; i < nDim; i++) {
/*      fprintf(out, "<");
/*      for(int j = 0; j < nDim; j++) {
/*           fprintf(out, "%f, ", hess[i][j]);   
/*      }
/*      fprintf(out, ">\n");
/*   }
/*   fprintf(out, "\n");
/*DEBUG*/

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
    // If the solution is 0, don't bother dividing.
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
