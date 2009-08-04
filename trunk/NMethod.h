#ifndef NMETHOD_H_
#define NMETHOD_H_

#include <stdlib.h>
#include <stdio.h>

#define NMETHOD_MIN 1
#define NMETHOD_MAX 2
class NewtonsMethod;

typedef double (*orig_function)(double vars[]);
typedef void (*gradient)(double vars[], double sol[]);
typedef void (*hessian)(double vars[], double *sol[]);
typedef void (*printer)(FILE* out);

class NewtonsMethod {
   orig_function f;
   gradient df;
   hessian d2f;
   printer printFunc;
   int nDim;
   double *grad;
   double **hess;
   
   
   int LinearEquationsSolver(double *matr[], double vect[], double sol[]);
   
   void negateArray(double arr[]);

   void approxGradient(double vars[], double sol[]);
   void approxHessian(double vars[], double *sol[]);
   
   double getGradientLength(double gradient[]);
   
   public:
          
     NewtonsMethod(orig_function func, int size) {
        f = func;
        df = NULL;
        d2f = NULL;
        printFunc = NULL;
        
        nDim = size;
        
        grad = (double*) malloc(sizeof(double) * nDim);
        hess = (double**) malloc(sizeof(double*) * nDim);
        for(int i = 0 ; i < nDim; i++) {
           hess[i] = (double*) malloc(sizeof(double) * nDim);
        }
     }
     
     NewtonsMethod(orig_function func, gradient dfunc, int size) {
        f = func;
        df = dfunc;
        d2f = NULL;
        printFunc = NULL;
        
        nDim = size;
        
        grad = (double*) malloc(sizeof(double) * nDim);
        hess = (double**) malloc(sizeof(double*) * nDim);
        for(int i = 0 ; i < nDim; i++) {
           hess[i] = (double*) malloc(sizeof(double) * nDim);
        }
     }
     
     NewtonsMethod(orig_function func, gradient dfunc, hessian d2func, int size) {
        f = func;
        df = dfunc;
        d2f = d2func;
        printFunc = NULL;
        
        nDim = size;
        
        grad = (double*) malloc(sizeof(double) * nDim);
        hess = (double**) malloc(sizeof(double*) * nDim);
        for(int i = 0 ; i < nDim; i++) {
           hess[i] = (double*) malloc(sizeof(double) * nDim);
        }
     }
     
     ~NewtonsMethod() {
        free(grad);
        for(int i = 0; i < nDim; i++) {
           free(hess[i]);
        }
        free(hess);
     }
     
     void optimize(double initial[], double soln[]);
     
     double step(double x_n[]);
     double step(double x_n[], int extremum);
     
     void printInfo(FILE* out);
     
     void setPrintFunc(printer pf) {
          printFunc = pf;
     }
     
};

#endif
