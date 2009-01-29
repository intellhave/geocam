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
   int index1 = 1;
   int index2 = 2;
   double curEta1 = Triangulation::edgeTable[index1].getEta();
   double curEta2 = Triangulation::edgeTable[index2].getEta();
   for(i=-10; i<11; ++i) {
      for(j=-10; j<11; ++j) {
         Triangulation::edgeTable[index1].setEta(curEta1-(double)i*0.01);
         Triangulation::edgeTable[index2].setEta(curEta2-(double)j*0.01);
         fprintf(result, "%12f", FE(0.00,1));
         printf("j= %d\n" , j);
      }
                   
      fprintf(result, "\n");        
      printf("i= %d\n" , i);
   }
                   
   fclose(result);  

}

