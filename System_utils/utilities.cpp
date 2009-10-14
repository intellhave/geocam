#include "utilities.h"
#include <stdio.h>
#include <stdarg.h>
#include <iostream>
#include <fstream>
#include <map>
#include "triangulation/triangulation.h"
#include "Volume.h"
/* ==== Include Files ==== */
#include <cstdlib>
#include <iostream>
#include <list>
#include <cmath>
#include "triangulation.h"
#include "3DInputOutput.h"
#include <ctime>
#include "triangulation.h"
#include <map>
#include <cmath>
#include <cerrno>
#include "Pipelined_Newtons_Method.h"
#include "NMethod.h"

#include "eta.h"
#include "radius.h"
#include "curvature3D.h"
#include "totalcurvature.h"
#include "totalvolume.h"
#include "edge_curvature.h"
#include "total_volume_partial.h"
#include "volume_length_tetra_partial.h"
#include "ehr_partial.h"
#include "ehr_second_partial.h"

#include "utilities.h"

#include <map>
#include <vector>
#include "NMethod.h"
#include "simplex.h"
#include "radius.h"
#include "triangulation.h"
#include "Volume.h"
#include "eta.h"
#include "utilities.h"
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

