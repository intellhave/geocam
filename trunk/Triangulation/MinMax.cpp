#include "MinMax.h"
#include <cmath>
#include "3DTriangulation/3Dtriangulationmath.h"
#include "delaunay.h"
#include <cerrno>
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
       double curv = vvit->second.getCurvature();
       fprintf(result, "Vertex %3d:\t%.10f\t%.10f\n", vvit->first, vvit->second.getRadius(), curv/(vvit->second.getRadius()));
          }
          fprintf(result, "\n\nVolumes:\n");
          int i;
          for(i=1; i<=Triangulation::tetraTable.size(); ++i)
          {
                   fprintf(result, "Tetrahedron %d:\t%.10f\n", i, CayleyvolumeSq(Triangulation::tetraTable[i]));
          }
    fprintf(result, "\n\nEta - Length\n__________\n");
      for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++)
      {
        fprintf(result, "Edge %3d:\t%.10f\t%.10f\n", eit->first, eit->second.getEta(),
                       eit->second.getLength() );
                       L=L+eit->second.getLength();
            }
//    fprintf(result, "\nsum of lengths = %f\n", L);
    fprintf(result, "\n\nF() = %f\n", F());
}



void MinMax(double deltaEta, double b, double a)
{
                  
   char results[] = "Triangulation Files/ODE Result.txt";
   FILE* result = fopen(results, "w");
   map<int, double> deltaFE;
   map<int, Edge>::iterator eit;
   map<int, Tetra>::iterator tit;
   //map<int, Edge>::iterator eeit;
   //for(eeit = Triangulation::edgeTable.begin(); eeit != Triangulation::edgeTable.end(); eeit++)
   //             {
   //             printf("Edge %d: %.10f\n", eeit->first, eeit->second.getEta());
   //             }
   double initRadii[Triangulation::vertexTable.size()];

   double dt = 0.020;
   double accuracy = 0.000000001;
   double precision = 0.000000001;


   for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
       deltaFE.insert(pair<int, double>(eit->first, 0));
   }
   map<int, double>::iterator dfit;
   
   Triangulation::getRadii(initRadii);
   yamabeFlow(dt, initRadii, accuracy, precision, true);
   printf("F = %.10f\n", F());
   calcDeltaFE(&deltaFE, deltaEta);
   double length = 0;
   for(dfit = (deltaFE).begin(); dfit != (deltaFE).end(); dfit++)
   {
      printf("MinMax FE: Index = %3d, Value = %.10f\n", dfit->first, dfit->second);
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
                printf("Edge %3d: %.10f\n", eit->first, eit->second.getEta());
                } 
      double totalvolume=0;
      for(tit = Triangulation::tetraTable.begin(); tit != Triangulation::tetraTable.end(); tit++)
      {
              printf("Tetrahedron %d: %.10f\n", tit->first, CayleyvolumeSq(tit->second));
              totalvolume += CayleyvolumeSq(tit->second) ;
              }
              printf("Total Volume = %.10f\n", totalvolume);
                          
      //updateEtas(&deltaFE, b);
      //printData(result);
      //updateEtas(&deltaFE, b);
      Triangulation::getRadii(initRadii);
      printf("\n*** *** *** *** *** *** *** *** *** *** ***\n");
      yamabeFlow(dt, initRadii, accuracy, precision, true);
      printf("\nF = %.10f\n", F());
      printData(result);
      
          
      map<int, Vertex>::iterator vvvit;
      for(vvvit = Triangulation::vertexTable.begin(); vvvit != Triangulation::vertexTable.end(); vvvit++)
   {
       double curv = vvvit->second.getCurvature();
       printf("vertex %3d: %f\t%.10f\n", vvvit->first, vvvit->second.getRadius(), curv/(vvvit->second.getRadius()));
           }
      
      
      
      calcDeltaFE(&deltaFE, deltaEta);
      updateEtas(&deltaFE, b, a);
      for(dfit = (deltaFE).begin(); dfit != (deltaFE).end(); dfit++)
     {
      printf("MinMax FE: Index = %3d, Value = %.10f\n", dfit->first, dfit->second);
     }
//     system("PAUSE");
      printf("\n");
   }
//    Triangulation::getRadii(initRadii);
    //yamabeFlow(dt, initRadii, steps, true);
//    printData(result);
//    fclose(result);
}





 
double FE(double deltaEta, int index)
{
   double curEta = Triangulation::edgeTable[index].getEta();
   Triangulation::edgeTable[index].setEta(curEta + deltaEta);
   vector<double> radii;
   vector<double> curvs;
   double initRadii[Triangulation::vertexTable.size()];

   double dt = 0.020;
   double accuracy = 0.000000001;
   double precision = 0.000000001;
   
   Triangulation::getRadii(initRadii);
   yamabeFlow(dt, initRadii, accuracy, precision, true);
   double result = F();
   Triangulation::edgeTable[index].setEta(curEta);
   Triangulation::setRadii(initRadii);
   return result;
}

//double FR(double deltaRadius, int index)
//{
//   double curRad = Triangulation::vertexTable[index].getRadius();
//   Triangulation::vertexTable[index].setRadius(curRad + deltaRadius);
//   double result = F();
//   Triangulation::vertexTable[index].setRadius(curRad);
//    printf("FR: Radii\n__________\n");
//    for(int i = 1; i <= Triangulation::vertexTable.size(); i++)
//    {
//        printf("Vertex %d: %f\n", i, Triangulation::vertexTable[i].getRadius());
//    }
//   return result;   
//}


double F()
{
   map<int, Vertex>::iterator vit;
   map<int, Tetra>::iterator tit;
   double sumK = 0;
   double sumV = 0;
   for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++)
   {
       double curv = vit->second.getCurvature();
       //sumR += vit->second.getRadius();
       sumK += curv;
   }
//   double parab=0;
   for(tit = Triangulation::tetraTable.begin(); tit != Triangulation::tetraTable.end(); tit++)
   {
//       parab -= pow((eit->second.getEta() - 1),2);
          
       sumV += sqrt(CayleyvolumeSq(tit->second));
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
         double curEta = Triangulation::edgeTable[dfit->first].getEta();

         Triangulation::edgeTable[dfit->first].setEta(curEta + b*(dfit->second) );
         //Triangulation::edgeTable[dfit->first].setEta(curEta + dfit->second / length * a);

     }
}
//void updateRadii(map<int, double>* deltaFR, double a) 
//{
//     double length = 0;
//     map<int, double>::iterator dfit;
//     for(dfit = (*deltaFR).begin(); dfit != (*deltaFR).end(); dfit++)
//     {
//        length += pow(dfit->second, 2);
//     }
//     length = sqrt(length);
//     
//     for(dfit = (*deltaFR).begin(); dfit != (*deltaFR).end(); dfit++)
//     {
//        double curRad = Triangulation::vertexTable[dfit->first].getRadius();
//        Triangulation::vertexTable[dfit->first].setRadius(curRad - dfit->second / length * a);
//     }     
//}


//bool allNegative(map<int, double>* deltaFE)
//{
//   map<int, double>::iterator dfit;
//   for(dfit = (*deltaFE).begin(); dfit != (*deltaFE).end(); dfit++)
//   {
//      if(dfit->second >= 0) {
//         return false;
//      }
//   }
//   return true;
//}


//bool allPositive(map<int, double>* deltaFR)
//{
//   map<int, double>::iterator dfit;
//   for(dfit = (*deltaFR).begin(); dfit != (*deltaFR).end(); dfit++)
//   {
//      if(dfit->second <= 0) {
//         return false;
//      }
//   }
//   return true;
//}


void calcDeltaFE(map<int, double>* deltaFE, double deltaEta)
{
     double base = F();
//     printf("base= %f\n", base);
          map<int, double>::iterator dfit;
     for(dfit = (*deltaFE).begin(); dfit != (*deltaFE).end(); dfit++)
     {   
         dfit->second = (FE(deltaEta, dfit->first) - base) / deltaEta;
//         printf("dfit: %f\n", dfit->second);
     }
}


//void calcDeltaFR(map<int, double>* deltaFR, double deltaRadius)
//{
//     double base = F();
//     map<int, double>::iterator dfit;
//     for(dfit = (*deltaFR).begin(); dfit != (*deltaFR).end(); dfit++)
//     {
//         double value = (FR(deltaRadius, dfit->first) - base) / deltaRadius;
//         dfit->second = value;
//     }
//}


//void MinMax(double deltaRadius, double a, double deltaEta, double b) 
//{
//   char results[] = "Triangulation Files/ODE Result.txt";
//   FILE* result = fopen(results, "w");
//   map<int, double> deltaFE;
//   map<int, double> deltaFR;
//   map<int, Edge>::iterator eit;
//   map<int, Vertex>::iterator vit;
//
//   for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
//       deltaFE.insert(pair<int, double>(eit->first, 0));
//   }
//   for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
//       deltaFR.insert(pair<int, double>(vit->first, 0));
//   }
//   
//   calcDeltaFR(&deltaFR, deltaRadius);
//   map<int, double>::iterator dfit;
//   for(dfit = (deltaFR).begin(); dfit != (deltaFR).end(); dfit++)
//   {
//      printf("MinMax FR: Index = %d, Value = %f\n", dfit->first, dfit->second);
//   }
//   printf("\n");
//   while(!allPositive(&deltaFR)) {
//      updateRadii(&deltaFR, a);
//      printData(result);
//      calcDeltaFR(&deltaFR, deltaRadius);
//      for(dfit = (deltaFR).begin(); dfit != (deltaFR).end(); dfit++)
//      {
//         printf("MinMax FR: Index = %d, Value = %f\n", dfit->first, dfit->second);
//      }
//      printf("\n");
//   }
//   
//   calcDeltaFE(&deltaFE, deltaEta);
//   for(dfit = (deltaFE).begin(); dfit != (deltaFE).end(); dfit++)
//   {
//      printf("MinMax FE: Index = %d, Value = %f\n", dfit->first, dfit->second);
//   }
//   printf("\n");
//   while(!allNegative(&deltaFE)) {
//      updateEtas(&deltaFE, b);
//      printData(result);
//      calcDeltaFE(&deltaFE, deltaEta);
//      for(dfit = (deltaFE).begin(); dfit != (deltaFE).end(); dfit++)
//      {
//        printf("MinMax FE: Index = %d, Value = %f\n", dfit->first, dfit->second);
//      }
//      printf("\n");
//   }    
//    printData(result);
//    fclose(result);
//}
