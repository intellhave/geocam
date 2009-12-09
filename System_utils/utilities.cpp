#include "utilities.h"
#include <stdio.h>
#include <stdarg.h>
#include <iostream>
#include <fstream>
#include "math.h"
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


