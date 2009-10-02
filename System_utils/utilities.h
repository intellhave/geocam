#ifndef UTILITIES_H_
#define UTILITIES_H_
#include <map>
void pause();
void pause(char* format, ...);

void printVolumes();

void printGradient(double grad[], int size);

void printHessian(double *hess[], int size);
#endif
