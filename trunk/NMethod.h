/**************************************************************
Class: Generalized Newton's Method
Author: Alex Henniges
Version: August 31, 2009
***************************************************************
This file defines the NMethod class, which acts as a generalized
Newton's Method where one provides a functional and optionally a
gradient and/or hessian of the functional. The purpose of Newton's
Method is to optimize the given functional.
**************************************************************/

#ifndef NMETHOD_H_
#define NMETHOD_H_

/*** HEADERS ***/
#include <stdlib.h>
#include <stdio.h>

/*** DEFINITIONS ***/
#define NMETHOD_MIN 1 // Define the flag for finding a minimum
#define NMETHOD_MAX 2 // Define the flag for finding a maximum
class NewtonsMethod;

// Description of what a functional should look like.
// Math - F : R^n -> R
// C++  - F(arr[n]) : double
typedef double (*orig_function)(double vars[]);

// A gradient takes in an array of doubles that represents the
// base point for the gradient. The second array of doubles is where
// the gradient vector will be placed.
typedef void (*gradient)(double vars[], double sol[]);

// A hessian takes in an array of doubles that represents the
// base point for the hessian. The n x n matrix of doubles is where
// the hessian matrix will be placed.
typedef void (*hessian)(double vars[], double *sol[]);

// This defined a function that will print out results. The FILE* 
// parameter is the file stream that the data will be printed to.
// It may be useful to learn about the fopen( ) function. Also,
// stdout is a pre-defined FILE* that indicates printing to the console.
typedef void (*printer)(FILE* out);


/*** CLASS DEFINITION ***/
class NewtonsMethod {
   /*** PRIVATE INFO ***/
   orig_function f; // instance variable for functional
   gradient df; // instance variable for gradient
   hessian d2f; // instance variable for hessian
   printer printFunc; // instance variable for printing function
   int nDim; // The dimension of our space ( ex. R^n)
   double *grad; // Used to hold the gradient vector
   double **hess; // Used to hold the hessian vector
   
   // The following are values that are "soft-coded" into NMethod to create
   // consistency in the calculations. They have default values but can all
   // be set by the user.
   
   // The value used for approximating derivatives.
   double delta;
   // The value used for advancing along the gradient, indicates the amount
   // to move in that direction.
   double stepRatio; 
   // This value is used only as a safety to end a step early if the length
   // of the gradient is very nearly 0.0.
   double gradLenCond;
   
   // This function solves a system of linear equations given the hessian
   // and gradient to determine the direction and magnitude of the next step.
   int LinearEquationsSolver(double *matr[], double vect[], double sol[]);
   // This function simply changes the sign of every entry of the gradient vector.
   void negateArray(double arr[]);
   // This function returns the length of a given gradient vector.
   double getGradientLength(double gradient[]);
   
   // This function sets the above parameters to "hard-coded" defaults.
   void setDefaults() {
        delta = 0.00001;
        stepRatio = 1.0 / 10.0;
        gradLenCond = 0.0000000001;
   }
   
   /*** PUBLIC INFO ***/
   public:
          
     NewtonsMethod(orig_function func, int size) {
        f = func;
        df = NULL;
        d2f = NULL;
        printFunc = NULL;
        
        nDim = size;
        
        setDefaults();
        
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
        
        setDefaults();
        
        grad = (double*) malloc(sizeof(double) * nDim);
        hess = (double**) malloc(sizeof(double*) * nDim);
        for(int i = 0 ; i < nDim; i++) {
           hess[i] = (double*) malloc(sizeof(double) * nDim);
        }
     }
     
     NewtonsMethod(orig_function func, hessian d2func, int size) {
        f = func;
        df = NULL;
        d2f = d2func;
        printFunc = NULL;
        
        nDim = size;
        
        setDefaults();
        
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
        
        setDefaults();
        
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
     
     
     void approxGradient(double vars[], double sol[]);
     void approxHessian(double vars[], double *sol[]);
     
     void optimize(double initial[], double soln[]);
     void optimize(double initial[], double soln[], int extremum);
     
     double step(double x_n[]);
     double step(double x_n[], int extremum);
     
     void printInfo(FILE* out);
     
     void setPrintFunc(printer pf) {
          printFunc = pf;
     }
     
     double getDelta() {
            return delta;
     }
     double getStepRatio() {
            return stepRatio;
     }
     double getStoppingGradientLength() {
            return gradLenCond;
     }
     
     void setDelta(double del) {
          delta = del;
     }
     void setStepRatio(double ratio) {
          stepRatio = ratio;
     }
     void setStoppingGradientLength(double cond) {
          gradLenCond = cond;
     }
     
};

#endif
