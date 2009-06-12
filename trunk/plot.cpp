#include <cmath>
#include "3DTriangulation/3Dtriangulationmath.h"
#include "delaunay.h"
#include "Geometry/geoquants.h"
#define PI 	3.141592653589793238 

#include "Triangulation/MinMax.h"



double plot()
{
   char results[] = "Triangulation Files/ODE Result.txt";
   FILE* result = fopen(results, "w");
   int i, j;
   int index1 = 1;
   int index2 = 6;
   Eta* eta1 = Eta::At(Triangulation::edgeTable[index1]);
   Eta* eta2 = Eta::At(Triangulation::edgeTable[index2]);
   double curEta1 = eta1->getValue();
   double curEta2 = eta2->getValue();
   for(i=-10; i<11; ++i) {
      for(j=-10; j<11; ++j) {
         eta1->setValue(curEta1-(double)i*0.02);
         eta2->setValue(curEta2-(double)j*0.02);
         fprintf(result, "%12.10f", FE(0.00,1));
         printf("j= %d\n" , j);
      }
                   
      fprintf(result, "\n");        
      printf("i= %d\n" , i);
   }
                   
   fclose(result);  

}

