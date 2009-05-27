#include <cstdio>
#include <cmath>
#include <cstdlib>

int LinearEquationsSolving(int nDim, double* pfMatr, double* pfVect, double* pfSolution);

int main() {
  double AYE[3][3] = {{ 1, 2, 3 }, { 4, 5, 6 }, { 0, 8, 9 } };
  double BEE[3];
  double SOL[3];
  double AXE[3];
  int N;

  BEE[0] = 1;
  BEE[1] = 2;
  BEE[2] = 3;
  N=3;

  double* matrix = (double*) AYE;

  LinearEquationsSolving(N, matrix, BEE, SOL);

  printf("%12.10f\n", SOL[0]);
  printf("%12.10f\n", SOL[1]);
  printf("%12.10f\n", SOL[2]);

  return 0;
}

/********************************************************************************
 * This procedure solves the system of linear equations Ax = b, where A is an
 * nDim by nDim matrix of floating point values, b is a vector of floating point
 * values with nDim entries, and x is the vector for which we would like to
 * solve.
 *
 * A return value 1 indicates the system is unsolvable.
 * nDim - system dimension
 * pfMatr - the matrix A
 * pfVect - the vector b
 * pfSolution - the vector x we would like to solve for.
 *
 * Notes: pfMatr is changed in the course of the calculation! Afterward, 
 * the matrix will be triangular. Likewise pfVect (which denotes b) will be 
 * changed by the procedure.
 *
 * Developer: Henry Guennadi Levkin
 *
 ********************************************************************************/

int LinearEquationsSolving(int nDim, double* pfMatr, double* pfVect, double* pfSolution){
  double fMaxElem;
  double fAcc;

  int i, j, k, m;

  for(k=0; k<(nDim-1); k++) {
      fMaxElem = fabs( pfMatr[k*nDim + k] );
      m = k;
      for(i=k+1; i<nDim; i++){
	if(fMaxElem < fabs(pfMatr[i*nDim + k]) ) {
	  fMaxElem = pfMatr[i*nDim + k];
	  m = i;
	}
      }
  
      if(m != k){
	for(i=k; i<nDim; i++){
	  fAcc               = pfMatr[k*nDim + i];
	  pfMatr[k*nDim + i] = pfMatr[m*nDim + i];
	  pfMatr[m*nDim + i] = fAcc;
	}

	fAcc = pfVect[k];
	pfVect[k] = pfVect[m];
	pfVect[m] = fAcc;
      }

      if( pfMatr[k*nDim + k] == 0.) return 1;

      for(j=(k+1); j<nDim; j++) {
	fAcc = - pfMatr[j*nDim + k] / pfMatr[k*nDim + k];
	for(i=k; i<nDim; i++)
	  pfMatr[j*nDim + i] = pfMatr[j*nDim + i] + fAcc*pfMatr[k*nDim + i];
	pfVect[j] = pfVect[j] + fAcc*pfVect[k];
      }
  }

  for(k=(nDim-1); k>=0; k--){
    pfSolution[k] = pfVect[k];
    for(i=(k+1); i<nDim; i++)
      pfSolution[k] -= (pfMatr[k*nDim + i]*pfSolution[i]);
       
    pfSolution[k] = pfSolution[k] / pfMatr[k*nDim + k];
  }

  return 0;
}

/******************************************************************
 * http://math.nist.gov/iml++/cg.h.txt
 *
 * Iterative template routine -- CG
 *
 * CG solves the symmetric positive definite linear
 * system Ax=b using the Conjugate Gradient method.
 *
 * CG follows the algorithm described on p. 15 in the 
 * SIAM Templates book.
 *
 * The return value indicates convergence within max_iter (input)
 * iterations (0), or no convergence within max_iter iterations (1).
 *
 * Upon successful return, output arguments have the following values:
 *  
 *        x  --  approximate solution to Ax = b
 * max_iter  --  the number of iterations performed before the
 *               tolerance was reached
 *      tol  --  the residual after the final iteration
 *  
 ******************************************************************/
int ConjugateGradient(int nDim, double* pfMatr, double* pfVect, double* pfSolution)
int CG(const Matrix &A, Vector &x, const Vector &b, const Preconditioner &M, int &max_iter, Real &tol){

  Real resid;
  Vector p, z, q;
  Vector alpha(1), beta(1), rho(1), rho_1(1);

  Real normb = norm(b);
  Vector r = b - A*x;

  if (normb == 0.0) 
    normb = 1;
  
  if ((resid = norm(r) / normb) <= tol) {
    tol = resid;
    max_iter = 0;
    return 0;
  }

  for (int i = 1; i <= max_iter; i++) {
    z = M.solve(r);
    rho(0) = dot(r, z);
    
    if (i == 1)
      p = z;
    else {
      beta(0) = rho(0) / rho_1(0);
      p = z + beta(0) * p;
    }
    
    q = A*p;
    alpha(0) = rho(0) / dot(p, q);
    
    x += alpha(0) * p;
    r -= alpha(0) * q;
    
    if ((resid = norm(r) / normb) <= tol) {
      tol = resid;
      max_iter = i;
      return 0;     
    }

    rho_1(0) = rho(0);
  }
  
  tol = resid;
  return 1;
}
