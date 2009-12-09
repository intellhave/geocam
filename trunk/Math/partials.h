#ifndef PARTIALS_H
#define PARTIALS_H

void mixedNEHRPartial(int eta, double* gradient);
void radiusPartial(int eta, double* soln, double* hessian);
int LinearEquationsSolving(int nDim, double* pfMatr, double* pfVect, double* pfSolution);
#endif
