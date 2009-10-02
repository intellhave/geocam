#include "utilities.h"
#include <stdio.h>
#include <stdarg.h>
#include <iostream>
#include <fstream>
#include <map>
#include "triangulation.h"
#include "Volume.h"

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

void printVolumes() {
     map<int, Tetra>::iterator tit;
     for(tit = Triangulation::tetraTable.begin(); tit != Triangulation::tetraTable.end();
             tit++) {
         printf("Volume at tetra %d: %f\n", tit->first, Volume::valueAt(tit->second));
     }

      // for(int i = 0; i < Triangulation::e
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

