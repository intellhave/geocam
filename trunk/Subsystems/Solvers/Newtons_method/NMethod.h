/*=========================================================================
 ||Class: NMethod (Generalized Newton's Method)
 ||Author: Alex Henniges (henniges@email.arizona.edu)
 ||
 ||Description: This file defines the NMethod class, which acts as a generalized
 ||       Newton's Method where one provides a functional and optionally a
 ||       gradient and/or hessian of the functional. The purpose of Newton's
 ||       Method is to optimize the given functional. NMethod provides a number
 ||       of options to this end.
 ||
 ||       A NMethod object requires a function from R^n to R, but can
 ||       additionally take a gradient or hessian of that function. Using what
 ||       it has, NMethod will attempt to optimize the function starting at a
 ||       given point by calculating the gradient and hessian values (through
 ||       a provided method or by approximation) and then moving in the
 ||       direction as determined by these values.
 ||
 ||       Performing an optimization can be done through a single function call,
 ||       optimize(), or by calling the function step() multiple times until
 ||       an optimal point is reached, measured by the length of the graident
 ||       vector. Both functions allow the user to specify the type of extremum
 ||       they are searching for, either a minimum or maximum. However, the
 ||       calculation of the direction is always based on the concavity of the
 ||       current point, regardless of the extremum chosen. This can result in
 ||       early termination due to the process moving in the wrong direction.
 ||
 ||Bugs/TODO: 
 *=========================================================================*/

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

// This defines a function that will print out results. The FILE* 
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
   double **hess; // Used to hold the hessian matrix
   
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
   
   // This function approximates a gradient vector if a gradient function was
   // not provided.
   void approxGradient(double vars[], double sol[]);
   // This function approximates a hessian matrix if a hessian function was
   // not provided.
   void approxHessian(double vars[], double *sol[]);
   
   // This function sets the above parameters to "hard-coded" defaults.
   void setDefaults() {
        delta = 0.00001;
        stepRatio = 1.0 / 10.0;
        gradLenCond = 0.000001;
   }
   
   /*** PUBLIC INFO ***/
   public:
     /*=========================================================================
      || Constructor: NewtonsMethod(orig_function func, int size)
      ||
      || Description: Build a NewtonsMethod object by providing only a function.
      ||        This will set the gradient and hessian functions to null and
      ||        they will be replaced by approximations. All parameters are set
      ||        to their defaults.
      ||
      || Parameters:    func - A pointer to a function that takes in an array of
      ||                       doubles and returns a double. This is the
      ||                       function that will be optimized and used in the
      ||                       approximations of the gradient and hessian.
      ||
      ||                size - The dimension of the space. That is, the
      ||                       number of doubles that are read from an array.
      ||                       All further dimensions should match this size. 
      ||                       For example, the hessian matrix should be 
      ||                       (size x size).
      *=======================================================================*/
     NewtonsMethod(orig_function func, int size) {
        // Set f to func, and the others to NULL.
        f = func;
        df = NULL;
        d2f = NULL;
        printFunc = NULL; // printFunc is NULL until set.
        
        nDim = size; // Set the dimension.
        
        setDefaults(); // Set other defaults.
        
        // Create space for a gradient vector and hessian matrix to be used
        // during optimization.
        grad = (double*) malloc(sizeof(double) * nDim);
        hess = (double**) malloc(sizeof(double*) * nDim);
        for(int i = 0 ; i < nDim; i++) {
           hess[i] = (double*) malloc(sizeof(double) * nDim);
        }
     }

     /*=========================================================================
      || Constructor: NewtonsMethod(orig_function func, gradient dfunc, int size)
      ||
      || Description: Build a NewtonsMethod object by providing a function and
      ||        its gradient. The hessian function is set to null and will be
      ||        approximated using the provided function. All parameters are set
      ||        to their default.
      ||
      || Parameters:    func - A pointer to a function that takes in an array of
      ||                       doubles and returns a double. This is the
      ||                       function that will be optimized and used in the
      ||                       approximations of the hessian.
      ||
      ||               dfunc - A pointer to a function that takes in an array of
      ||                       doubles and fills out values for a second array
      ||                       of doubles. This function should represent the
      ||                       gradient of func, taking in a point in the
      ||                       size'th dimension, and calculating the gradient
      ||                       vector at that point.
      ||
      ||                size - The dimension of the space. That is, the
      ||                       number of doubles that are read from an array.
      ||                       All further dimensions should match this size. 
      ||                       For example, the hessian matrix should be 
      ||                       (size x size).
      *=======================================================================*/     
     NewtonsMethod(orig_function func, gradient dfunc, int size) {
        // Set f to func, df to dfunc, and d2f to NULL.
        f = func;
        df = dfunc;
        d2f = NULL;
        printFunc = NULL; // printFunc is NULL until set.
        
        nDim = size; // Set the dimension.
        
        setDefaults(); // Set other defaults.
        
        // Create space for a gradient vector and hessian matrix to be used
        // during optimization.
        grad = (double*) malloc(sizeof(double) * nDim);
        hess = (double**) malloc(sizeof(double*) * nDim);
        for(int i = 0 ; i < nDim; i++) {
           hess[i] = (double*) malloc(sizeof(double) * nDim);
        }
     }

     /*=========================================================================
      || Constructor: NewtonsMethod(orig_function func, hessian d2func, int size)
      ||
      || Description: Build a NewtonsMethod object by providing a function and
      ||        its hessian. The gradient function is set to null and will be
      ||        approximated using the provided function. All parameters are set
      ||        to their default.
      ||
      || Parameters:    func - A pointer to a function that takes in an array of
      ||                       doubles and returns a double. This is the
      ||                       function that will be optimized and used in the
      ||                       approximations of the gradient.
      ||
      ||              d2func - A pointer to a function that takes in an array of
      ||                       doubles and fills out values for a two-d array
      ||                       of doubles. This function should represent the
      ||                       hessian of func, taking in a point in the
      ||                       size'th dimension, and calculating the hessian
      ||                       matrix at that point.
      ||
      ||                size - The dimension of the space. That is, the
      ||                       number of doubles that are read from an array.
      ||                       All further dimensions should match this size. 
      ||                       For example, the hessian matrix should be 
      ||                       (size x size).
      *=======================================================================*/       
     NewtonsMethod(orig_function func, hessian d2func, int size) {
        // Set f to func, df to NULL, and d2f to d2func.
        f = func;
        df = NULL;
        d2f = d2func;
        printFunc = NULL; // printFunc is NULL until set.
        
        nDim = size; // Set the dimension.
        
        setDefaults(); // Set other defaults.
        
        // Create space for a gradient vector and hessian matrix to be used
        // during optimization.
        grad = (double*) malloc(sizeof(double) * nDim);
        hess = (double**) malloc(sizeof(double*) * nDim);
        for(int i = 0 ; i < nDim; i++) {
           hess[i] = (double*) malloc(sizeof(double) * nDim);
        }
     }
     
     /*=========================================================================
      || Constructor: NewtonsMethod(orig_function func, gradient dfunc, 
      ||                                          hessian d2func, int size)
      ||
      || Description: Build a NewtonsMethod object by providing a function, its
      ||              gradient, and its hessian. All parameters are set to their
      ||              default.
      ||
      || Parameters:    func - A pointer to a function that takes in an array of
      ||                       doubles and returns a double. This is the
      ||                       function that will be optimized.
      ||
      ||               dfunc - A pointer to a function that takes in an array of
      ||                       doubles and fills out values for a second array
      ||                       of doubles. This function should represent the
      ||                       gradient of func, taking in a point in the
      ||                       size'th dimension, and calculating the gradient
      ||                       vector at that point.
      ||
      ||              d2func - A pointer to a function that takes in an array of
      ||                       doubles and fills out values for a two-d array
      ||                       of doubles. This function should represent the
      ||                       hessian of func, taking in a point in the
      ||                       size'th dimension, and calculating the hessian
      ||                       matrix at that point.
      ||
      ||                size - The dimension of the space. That is, the
      ||                       number of doubles that are read from an array.
      ||                       All further dimensions should match this size. 
      ||                       For example, the hessian matrix should be 
      ||                       (size x size).
      *=======================================================================*/   
     NewtonsMethod(orig_function func, gradient dfunc, hessian d2func, int size) {
        // Set f to func, df to dfunc, and d2f to d2func.
        f = func;
        df = dfunc;
        d2f = d2func;
        printFunc = NULL; // printFunc is NULL until set.
        
        nDim = size; // Set the dimension.
        
        setDefaults(); // Set other defaults.
        
        // Create space for a gradient vector and hessian matrix to be used
        // during optimization.
        grad = (double*) malloc(sizeof(double) * nDim);
        hess = (double**) malloc(sizeof(double*) * nDim);
        for(int i = 0 ; i < nDim; i++) {
           hess[i] = (double*) malloc(sizeof(double) * nDim);
        }
     }
     
     /*
      * The deconstructor frees the space from the saved gradient vector and
      * hessian matrix.
      */
     ~NewtonsMethod() {
        free(grad);
        for(int i = 0; i < nDim; i++) {
           free(hess[i]);
        }
        free(hess);
     }
     
     /*=========================================================================
      || Function: optimize(double initial[], double soln[])
      ||
      || Description: This function will attempt to find an extremum of the
      ||        function provided by the user in the construction of this
      ||        NewtonsMethod object. The optimization is performed as a series
      ||        of steps of Newton's Method. At each step, the gradient vector
      ||        and hessian matrix at the current point is calculated. From
      ||        this, the direction and length to move from the current point is
      ||        determined. This is repeated until the length of the gradient
      ||        is less than the stopping condition, gradLenCond.
      ||
      || Precondition: Let n be the size of the initial array and be equivalent
      ||               to the size value given to the constructor. soln is also
      ||               required to be of at least size n.
      ||
      || Parameters: initial - The initial point in our n-dimensional space.
      ||                       Depending on the function provided, this may
      ||                       affect the outcome of the results.
      ||
      ||                soln - An array to hold the solution. That is, the point
      ||                       in n-dimensional space that is a local extremum
      ||                       of the provided function.
      *=======================================================================*/
     void optimize(double initial[], double soln[]);
     
     /*=========================================================================
      || Function: optimize(double initial[], double soln[], int extremum)
      ||
      || Description: This function will attempt to find an extremum of the
      ||        function provided by the user in the construction of this
      ||        NewtonsMethod object. The optimization is performed as a series
      ||        of steps of Newton's Method. At each step, the gradient vector
      ||        and hessian matrix at the current point is calculated. From
      ||        this, the direction and length to move from the current point is
      ||        determined. This is repeated until the length of the gradient
      ||        is less than the stopping condition, gradLenCond.
      ||
      || Precondition: Let n be the size of the initial array and be equivalent
      ||               to the size value given to the constructor. soln is also
      ||               required to be of at least size n.
      ||
      || Parameters: initial - The initial point in our n-dimensional space.
      ||                       Depending on the function provided, this may
      ||                       affect the outcome of the results.
      ||
      ||                soln - An array to hold the solution. That is, the point
      ||                       in n-dimensional space that is a local extremum
      ||                       of the provided function.
      ||
      ||            extremum - This indicates the type of extremum, max or min,
      ||                       that the user is looking for. This will create a
      ||                       more intelligent step procedure (explained in the
      ||                       step function).
      *=======================================================================*/
     void optimize(double initial[], double soln[], int extremum);
     
     /*=========================================================================
      || Function: step(double x_n[])
      ||
      || Description: This function performs a single step of Newton's Method.
      ||        The current point is the value given by x_n, and the point that
      ||        results from the step is placed into x_n at the end of the
      ||        function. The step is performed by calculating the gradient
      ||        vector and hessian matrix from the intial point and moving in
      ||        the resulting direction that points to an extremum. The length
      ||        of the resulting gradient vector is returned.
      ||
      || Parameters:     x_n - An array of double indicating the current point
      ||                       in n-dimensional space. The resulting point after
      ||                       the step is completed is written into x_n.
      ||
      || Returns: The length of the gradient vector calculated during the step.
      ||          This can allow the user to continuously call this function
      ||          until the length is less than a certain threshold. 
      *=======================================================================*/
     double step(double x_n[]);
     
     /*=========================================================================
      || Function: step(double x_n[], int extremum)
      ||
      || Description: This function performs a single step of Newton's Method.
      ||        The current point is the value given by x_n, and the point that
      ||        results from the step is placed into x_n at the end of the
      ||        function. The step is performed by calculating the gradient
      ||        vector and hessian matrix from the intial point and moving in
      ||        the resulting direction that points to an extremum. The length
      ||        of the resulting gradient vector is returned.
      ||
      ||        The extremum value indicates whether the user is searching for
      ||        a maximum or minimum. This increases the functionality of
      ||        Newton's Method in several ways: First, the movement from the
      ||        current point is performed in small increments (defined by the
      ||        stepRatio parameter) while the function continues to increase
      ||        or decrease, depening on the choice of extremum. This helps to
      ||        avoid a step of Newton's Method from moving too far away.
      ||
      ||        If the function does not increase or decrease as expected after
      ||        even one unit of movement, the amount of movement continues to
      ||        decrease rapidly until such a point occurs. If that moment does
      ||        not occur, an announcment is made about moving in the wrong
      ||        direction, and the step returns with -1.
      ||
      || Parameters:     x_n - An array of double indicating the current point
      ||                       in n-dimensional space. The resulting point after
      ||                       the step is completed is written into x_n.
      ||
      ||            extremum - This indicates the type of extremum, max or min,
      ||                       that the user is looking for. This will create a
      ||                       more intelligent step procedure.
      ||
      || Returns: The length of the gradient vector calculated during the step.
      ||          This can allow the user to continuously call this function
      ||          until the length is less than a certain threshold. 
      *=======================================================================*/
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
