#ifndef UTILITIES_H_
#define UTILITIES_H_
#include <map>
#include "Matrix.h"
void pause();
void pause(char* format, ...);

void printGradient(double grad[], int size);

void printHessian(double *hess[], int size);

int LinearEquationsSolver(Matrix<double>& mat, double vect[], double sol[], int nDim);

#endif
