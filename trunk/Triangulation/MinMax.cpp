#include "MinMax.h"
#include <cmath>
#include "3DTriangulation/3Dtriangulationmath.h"
#include "delaunay.h"
#include <cerrno>

#include "flow/approximator.h"
#include "flow/rungaApprox.h"
#include "flow/eulerApprox.h"
#include "flow/sysdiffeq.h"

#include "Geometry/Geometry.h"
#define PI 	3.141592653589793238 

void printData(FILE* result) 
{
     double L;
     L=0;
        
    map<int, Vertex>::iterator vvit;
    map<int, Edge>::iterator eit;
    map<int, Tetra>::iterator tit;
    fprintf(result, "\n************************************\n");
    
           fprintf(result, "\n\nRadii - Curvature\n__________\n");
    for(vvit = Triangulation::vertexTable.begin(); vvit != Triangulation::vertexTable.end(); vvit++)
    {
       double curv = Geometry::curvature(vvit->second);
       fprintf(result, "Vertex %3d:\t%.10f\t%.10f\n", vvit->first, Geometry::radius(vvit->second), curv/(Geometry::radius(vvit->second)));
          }
          fprintf(result, "\n\nVolumes:\n");
          int i;
          for(i=1; i<=Triangulation::tetraTable.size(); ++i)
          {
                   fprintf(result, "Tetrahedron %d:\t%.10f\n", i, Geometry::volume(Triangulation::tetraTable[i]));
          }
    fprintf(result, "\n\nEta - Length\n__________\n");
      for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++)
      {
        fprintf(result, "Edge %3d:\t%.10f\t%.10f\n", eit->first, Geometry::eta(eit->second),
                       Geometry::length(eit->second) );
                       L=L+Geometry::length(eit->second);
      }
//    fprintf(result, "\nsum of lengths = %f\n", L);
    fprintf(result, "\n\nF() = %f\n", F());
}



void MinMax(double deltaEta, double b, double a)
{
   Approximator* app = new EulerApprox( (sysdiffeq) Yamabe);                
   char results[] = "Triangulation Files/ODE Result.txt";
   FILE* result = fopen(results, "w");
   map<int, double> deltaFE;
   map<int, Edge>::iterator eit;
   map<int, Tetra>::iterator tit;
   double initRadii[Triangulation::vertexTable.size()];

   double dt = 0.030;
   double accuracy = 0.000000001;
   double precision = 0.000000001;


   for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
       deltaFE.insert(pair<int, double>(eit->first, 0));
   }
   map<int, double>::iterator dfit;
   
   app->run(precision, accuracy, dt);
   printf("F = %.10f\n", F());
   calcDeltaFE(&deltaFE, deltaEta);
   double length = 0;
   for(dfit = (deltaFE).begin(); dfit != (deltaFE).end(); dfit++)
   {
//      printf("MinMax FE: Index = %3d, Value = %.10f\n", dfit->first, dfit->second);
      length += pow(dfit->second, 2);
   }
     length = sqrt(length);
     printf("\nGradient Length: %.10f\n", length);
   printf("\n");
   updateEtas(&deltaFE, b, a);
   printData(result);
   
while(true) {
 //  while(!allNegative(&deltaFE)) { 
      for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++)
                {
                printf("Edge %3d: %.10f\n", eit->first, Geometry::eta(eit->second));
                } 
      double totalvolume=0;
      for(tit = Triangulation::tetraTable.begin(); tit != Triangulation::tetraTable.end(); tit++)
      {
              printf("Tetrahedron %d: %.10f\n", tit->first, Geometry::volume(tit->second));
              totalvolume += Geometry::volume(tit->second) ;
              }
              printf("Total Volume = %.10f\n", totalvolume);
                          
      //updateEtas(&deltaFE, b);
      //printData(result);
      //updateEtas(&deltaFE, b);
      Geometry::getRadii(initRadii);
      printf("\n*** *** *** *** *** *** *** *** *** *** ***\n");
      app->run(precision, accuracy, dt);
      printf("\nF = %.10f\n", F());
      printData(result);
      
          
      map<int, Vertex>::iterator vvvit;
      for(vvvit = Triangulation::vertexTable.begin(); vvvit != Triangulation::vertexTable.end(); vvvit++)
   {
       double curv = Geometry::curvature(vvvit->second);
       printf("vertex %3d: %f\t%.10f\n", vvvit->first, Geometry::radius(vvvit->second), curv/(Geometry::radius(vvvit->second)));
           }
      
      
      
      calcDeltaFE(&deltaFE, deltaEta);
      updateEtas(&deltaFE, b, a);

      printf("\n");
   }
}

double FE(double deltaEta, int index)
{
   Approximator* app = new EulerApprox( (sysdiffeq) Yamabe); 
   double curEta = Geometry::eta(Triangulation::edgeTable[index]);
   Geometry::setEta(Triangulation::edgeTable[index], curEta + deltaEta);
   vector<double> radii;
   vector<double> curvs;
   double initRadii[Triangulation::vertexTable.size()];

   double dt = 0.030;
   double accuracy = 0.000000001;
   double precision = 0.000000001;
   
   Geometry::getRadii(initRadii);
   app->run(precision, accuracy, dt);
   double result = F();
   Geometry::setEta(Triangulation::edgeTable[index], curEta);
   Geometry::setRadii(initRadii);
   return result;
}

double F()
{
   map<int, Vertex>::iterator vit;
   map<int, Tetra>::iterator tit;
   double sumK = 0;
   double sumV = 0;
   for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++)
   {
       double curv = Geometry::curvature(vit->second);
       //sumR += vit->second.getRadius();
       sumK += curv;
   }
   for(tit = Triangulation::tetraTable.begin(); tit != Triangulation::tetraTable.end(); tit++)
   {
          
       sumV += Geometry::volume(tit->second);
// sumV calculates the total volume of the manifold
   }
   return sumK/(pow(sumV, (1.0/3.0)));
}

void updateEtas(map<int, double>* deltaFE, double b, double a)
{
     double length = 0;
     map<int, double>::iterator dfit;
     for(dfit = (*deltaFE).begin(); dfit != (*deltaFE).end(); dfit++)
     {
        length += pow(dfit->second, 2);
     }
     length = sqrt(length);
     printf("\nGradient Length: %.10f\n", length);
     
     for(dfit = (*deltaFE).begin(); dfit != (*deltaFE).end(); dfit++)
     {
         double curEta = Geometry::eta(Triangulation::edgeTable[dfit->first]);

         //Triangulation::edgeTable[dfit->first].setEta(curEta + b*(dfit->second) );
         Geometry::setEta(Triangulation::edgeTable[dfit->first], curEta + dfit->second / length * a);

     }
}

void calcDeltaFE(map<int, double>* deltaFE, double deltaEta)
{
     double base = F();
          map<int, double>::iterator dfit;
     for(dfit = (*deltaFE).begin(); dfit != (*deltaFE).end(); dfit++)
     {   
         dfit->second = (FE(deltaEta, dfit->first) - base) / deltaEta;
     }
}
