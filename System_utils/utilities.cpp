#include "utilities.h"
#include <stdio.h>
#include <stdarg.h>
#include <iostream>
#include <fstream>
#include <map>
#include "triangulation/triangulation.h"
#include "Volume.h"
#include "utilities.h"
#define PI 3.141592653589793238
#define CONST_VOL 4.71404520791
/* ======================= */
using namespace std;

void pause() {
  pause("PAUSE...");
}
void pause(char* format, ...) {
  va_list args;
  va_start(args, format);
  fflush(stdin); vprintf(format, args); cin.get(); // PAUSE
  va_end(args);
}

void printGradient(double grad[], int size) {
     printf("grad = <");
     for(int i = 0; i < size; i++) {
       printf("%f, ", grad[i]);
     }
     printf(">\n");
}

void printHessian(double *hess[], int size) {
     for(int i = 0; i < size; i++) {
        printf("hess[%d][] = <", i);
        for(int j = 0; j < size; j++) {
           printf("%f, ", hess[i][j]);
        }
        printf(">\n");
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
int LinearEquationsSolver(double *pfMatr[], double* pfVect, double* pfSolution, int nDim)
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

    if( pfMatr[k][k] == 0.0) {
      return 1; // Matrix has a column of all 0s !!!
    }

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


