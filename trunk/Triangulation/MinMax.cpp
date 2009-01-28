#include "MinMax.h"
#include <cmath>
#include "3DTriangulation/3Dtriangulationmath.h"
#include "delaunay.h"
#define PI 	3.141592653589793238 

void printData(FILE* result) 
{
     double L;
     L=0;
    fprintf(result, "\nRadii\n__________\n");
    for(int i = 1; i <= Triangulation::vertexTable.size(); i++)
    {
        fprintf(result, "Vertex %d: %f\n", i, Triangulation::vertexTable[i].getRadius());
            }
          
    fprintf(result, "\nCurvatures\n__________\n");
    
    map<int, Vertex>::iterator vvit;
      for(vvit = Triangulation::vertexTable.begin(); vvit != Triangulation::vertexTable.end(); vvit++)
   {
       double curv = vvit->second.getCurvature();
       fprintf(result, "curvature at vertex %d: %f\n", vvit->first, curv/(Triangulation::vertexTable[vvit->first].getRadius()));
          }
    fprintf(result, "\nEta - Length\n__________\n");
    for(int i = 1; i <= Triangulation::edgeTable.size(); i++)
    {
        fprintf(result, "Edge %d: %f - %f\n", i, Triangulation::edgeTable[i].getEta(),
                       Triangulation::edgeTable[i].getLength() );
                       L=L+Triangulation::edgeTable[i].getLength();
            }
    fprintf(result, "\nsum of lengths = %f\n", L);
    fprintf(result, "\nF() = %f\n", F());
}



void MinMax(double deltaEta, double b)
{
   char results[] = "Triangulation Files/ODE Result.txt";
   FILE* result = fopen(results, "w");
   map<int, double> deltaFE;
   map<int, Edge>::iterator eit;
   double initRadii[Triangulation::vertexTable.size()];
   double dt = 0.020;
   double accuracy = 0.00001;
   double precision = 0.00001;

   for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
       deltaFE.insert(pair<int, double>(eit->first, 0));
   }
   map<int, double>::iterator dfit;
   
   Triangulation::getRadii(initRadii);
   yamabeFlow(dt, initRadii, accuracy, precision, true);
   printf("F = %f\n", F());
   calcDeltaFE(&deltaFE, deltaEta);

   for(dfit = (deltaFE).begin(); dfit != (deltaFE).end(); dfit++)
   {
      printf("MinMax FE: Index = %d, Value = %f\n", dfit->first, dfit->second);
   }

   printf("\n");
   printData(result);
   
while(true) {
 //  while(!allNegative(&deltaFE)) { 
      for(int i = 1; i <= Triangulation::edgeTable.size(); i++)
                {
                printf("Edge %d: %f\n", i, Triangulation::edgeTable[i].getEta());
                }               
      //updateEtas(&deltaFE, b);
      //printData(result);
      //updateEtas(&deltaFE, b);
      Triangulation::getRadii(initRadii);
      yamabeFlow(dt, initRadii, accuracy, precision, true);
      printf("F = %f\n", F());
      printData(result);
      
      for(int i = 1; i <= Triangulation::vertexTable.size(); i++)
    {
       printf("radius of vertex %d: %f\n", i, Triangulation::vertexTable[i].getRadius());
    }
      
      map<int, Vertex>::iterator vvvit;
      for(vvvit = Triangulation::vertexTable.begin(); vvvit != Triangulation::vertexTable.end(); vvvit++)
   {
       double curv = vvvit->second.getCurvature();
       printf("curvature at vertex %d: %f\n", vvvit->first, curv/(Triangulation::vertexTable[vvvit->first].getRadius()));
    }
      
      
      
      calcDeltaFE(&deltaFE, deltaEta);
      updateEtas(&deltaFE, b);
      for(dfit = (deltaFE).begin(); dfit != (deltaFE).end(); dfit++)
     {
      printf("MinMax FE: Index = %d, Value = %f\n", dfit->first, dfit->second);
     }

      printf("\n");
   }
    Triangulation::getRadii(initRadii);
    //yamabeFlow(dt, initRadii, steps, true);
    printData(result);
    fclose(result);
}





 
double FE(double deltaEta, int index)
{
   double curEta = Triangulation::edgeTable[index].getEta();
   Triangulation::edgeTable[index].setEta(curEta + deltaEta);
   vector<double> radii;
   vector<double> curvs;
   double initRadii[Triangulation::vertexTable.size()];
   double dt = 0.020;
   double accuracy = 0.00001;
   double precision = 0.00001;
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
   map<int, Edge>::iterator eit;
   double sumK = 0;
   double sumR = 0;
   for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++)
   {
       double curv = vit->second.getCurvature();
       //sumR += vit->second.getRadius();
       sumK += curv;
   }
   //double parab=0;
   for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end();eit++)
   {
    //   parab -= pow((eit->second.getEta() - 1),2);
          
       sumR += eit->second.getLength();
   }
   return sumK/sumR;
}


void updateEtas(map<int, double>* deltaFE, double b)
{
//     double length = 0;
     map<int, double>::iterator dfit;
//     for(dfit = (*deltaFE).begin(); dfit != (*deltaFE).end(); dfit++)
//     {
//        length += pow(dfit->second, 2);
//     }
//     length = sqrt(length);
     
     for(dfit = (*deltaFE).begin(); dfit != (*deltaFE).end(); dfit++)
     {
         double curEta = Triangulation::edgeTable[dfit->first].getEta();
         Triangulation::edgeTable[dfit->first].setEta(curEta + b*(dfit->second) );
         //Triangulation::edgeTable[dfit->first].setEta(curEta + dfit->second / length * b);
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
     //printf("base= %f\n", base);
          map<int, double>::iterator dfit;
     for(dfit = (*deltaFE).begin(); dfit != (*deltaFE).end(); dfit++)
     {   
         dfit->second = (FE(deltaEta, dfit->first) - base) / deltaEta;
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
