#include <cmath>
#include "3DTriangulation/3Dtriangulationmath.h"
#include "delaunay.h"
#define PI 	3.141592653589793238 

#include "Triangulation/MinMax.h"



double plotting()
{
       char results[] = "Triangulation Files/ODE Result.txt";
   FILE* result = fopen(results, "w");
          int i, j;
          int index = 1;
          double curEta = Triangulation::edgeTable[index].getEta();
          for(i=0; i<10; ++i) {
                   for(j=0; j<10; ++j) {
                            Triangulation::edgeTable[1].setEta(curEta+(double)i*0.01-0.25);
                            Triangulation::edgeTable[6].setEta(curEta+(double)j*0.01-0.25);
                            fprintf(result, "%12f", FE(0.00,1));
                            }
                   fprintf(result, "\n");
                   }
    fclose(result);
}

