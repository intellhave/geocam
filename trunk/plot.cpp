#include <cmath>
#include "3DTriangulation/3Dtriangulationmath.h"
#include "delaunay.h"
#define PI 	3.141592653589793238 

#include "Triangulation/MinMax.h"



double plot()
{
       char results[] = "Triangulation Files/ODE Result.txt";
   FILE* result = fopen(results, "w");
          int i, j;
          int index = 1;
          double curEta = Triangulation::edgeTable[index].getEta();
          for(i=0; i<10; ++i) {
                   for(j=0; j<10; ++j) {
                            Triangulation::edgeTable[1].setEta(curEta+(double)i*0.1-0.5);
                            Triangulation::edgeTable[17].setEta(curEta+(double)j*0.1-0.5);
                            fprintf(result, "%12f", FE(0.00,1));
                            printf("i= %d , j= %d\n", i, j);
                            }
                   fprintf(result, "\n");
                   }
                 fclose(result);  

}

