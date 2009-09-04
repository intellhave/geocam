#ifndef PIPELINED_NEWTONS_METHOD_H
#define PIPELINED_NEWTONS_METHOD_H

int LinearEquationsSolving(int nDim, double* pfMatr, 
			double* pfVect, double* pfSolution);

void Newtons_Method( double stoppingcondition, bool printdata);

#endif /* PIPELINED_NEWTONS_METHOD_H */
