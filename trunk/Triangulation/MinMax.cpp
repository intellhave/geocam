#include "MinMax.h"
#include <cmath>
#include "3DTriangulation/3Dtriangulationmath.h"
#include "delaunay.h"
#define PI 	3.141592653589793238 

void printData(FILE* result) 
{
    fprintf(result, "\nRadii\n__________\n");
    for(int i = 1; i <= Triangulation::vertexTable.size(); i++)
    {
        fprintf(result, "Vertex %d: %f\n", i, Triangulation::vertexTable[i].getRadius());
    }
    fprintf(result, "\nEta\n__________\n");
    for(int i = 1; i <= Triangulation::edgeTable.size(); i++)
    {
        fprintf(result, "Edge %d: %f\n", i, Triangulation::edgeTable[i].getEta());
    }
    fprintf(result, "\nF() = %f\n", F());
}



double FE(double deltaEta, int index)
{
   double curEta = Triangulation::edgeTable[index].getEta();
   Triangulation::edgeTable[index].setEta(curEta + deltaEta);
   vector<double> radii;
   vector<double> curvs;
   double initRadii[Triangulation::vertexTable.size()];
   int steps = 500;
   double dt = 0.005;
   Triangulation::getRadii(initRadii);
   yamabeFlow(&radii, &curvs, dt, initRadii, steps, true);
   double result = F();
   Triangulation::edgeTable[index].setEta(curEta);
   Triangulation::setRadii(initRadii);
   return result;
}
double FR(double deltaRadius, int index)
{
   double curRad = Triangulation::vertexTable[index].getRadius();
   Triangulation::vertexTable[index].setRadius(curRad + deltaRadius);
   double result = F();
   Triangulation::vertexTable[index].setRadius(curRad);
//    printf("FR: Radii\n__________\n");
//    for(int i = 1; i <= Triangulation::vertexTable.size(); i++)
//    {
//        printf("Vertex %d: %f\n", i, Triangulation::vertexTable[i].getRadius());
//    }
   return result;   
}
double F()
{
   map<int, Vertex>::iterator vit;
   map<int, Edge>::iterator eit;
   double sumK = 0;
   double sumR = 0;
   for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++)
   {
       double curv = curvature3D(vit->second);
       //sumR += vit->second.getRadius();
       sumK += curv;
   }
   for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end();eit++)
   {
       vector<int> vertices = *(eit->second.getLocalVertices());
       sumR += eit->second.getLength();
   }
   return sumK / sumR;
}
void updateEtas(map<int, double>* deltaFE, double b)
{
     double length = 0;
     map<int, double>::iterator dfit;
     for(dfit = (*deltaFE).begin(); dfit != (*deltaFE).end(); dfit++)
     {
        length += pow(dfit->second, 2);
     }
     length = sqrt(length);
     
     for(dfit = (*deltaFE).begin(); dfit != (*deltaFE).end(); dfit++)
     {
        double curEta = Triangulation::edgeTable[dfit->first].getEta();
        Triangulation::edgeTable[dfit->first].setEta(curEta + dfit->second / length * b);
     }
}
void updateRadii(map<int, double>* deltaFR, double a) 
{
     double length = 0;
     map<int, double>::iterator dfit;
     for(dfit = (*deltaFR).begin(); dfit != (*deltaFR).end(); dfit++)
     {
        length += pow(dfit->second, 2);
     }
     length = sqrt(length);
     
     for(dfit = (*deltaFR).begin(); dfit != (*deltaFR).end(); dfit++)
     {
        double curRad = Triangulation::vertexTable[dfit->first].getRadius();
        Triangulation::vertexTable[dfit->first].setRadius(curRad - dfit->second / length * a);
     }     
}
bool allNegative(map<int, double>* deltaFE)
{
   map<int, double>::iterator dfit;
   for(dfit = (*deltaFE).begin(); dfit != (*deltaFE).end(); dfit++)
   {
      if(dfit->second >= 0) {
         return false;
      }
   }
   return true;
}
bool allPositive(map<int, double>* deltaFR)
{
   map<int, double>::iterator dfit;
   for(dfit = (*deltaFR).begin(); dfit != (*deltaFR).end(); dfit++)
   {
      if(dfit->second <= 0) {
         return false;
      }
   }
   return true;
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
void calcDeltaFR(map<int, double>* deltaFR, double deltaRadius)
{
     double base = F();
     map<int, double>::iterator dfit;
     for(dfit = (*deltaFR).begin(); dfit != (*deltaFR).end(); dfit++)
     {
         double value = (FR(deltaRadius, dfit->first) - base) / deltaRadius;
         dfit->second = value;
     }
}
void MinMax(double deltaRadius, double a, double deltaEta, double b) 
{
   char results[] = "Triangulation Files/ODE Result.txt";
   FILE* result = fopen(results, "w");
   printData(result);
   map<int, double> deltaFE;
   map<int, double> deltaFR;
   map<int, Edge>::iterator eit;
   map<int, Vertex>::iterator vit;
   for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
       deltaFE.insert(pair<int, double>(eit->first, 0));
   }
   for(vit = Triangulation::vertexTable.begin(); vit != Triangulation::vertexTable.end(); vit++) {
       deltaFR.insert(pair<int, double>(vit->first, 0));
   }
   
   calcDeltaFR(&deltaFR, deltaRadius);
   map<int, double>::iterator dfit;
   for(dfit = (deltaFR).begin(); dfit != (deltaFR).end(); dfit++)
   {
      printf("MinMax FR: Index = %d, Value = %f\n", dfit->first, dfit->second);
   }
   while(!allPositive(&deltaFR)) {
      updateRadii(&deltaFR, a);
      printData(result);
      calcDeltaFR(&deltaFR, deltaRadius);
      for(dfit = (deltaFR).begin(); dfit != (deltaFR).end(); dfit++)
      {
         printf("MinMax FR: Index = %d, Value = %f\n", dfit->first, dfit->second);
      }
   }
   
   calcDeltaFE(&deltaFE, deltaEta);
   for(dfit = (deltaFE).begin(); dfit != (deltaFE).end(); dfit++)
   {
      printf("MinMax FE: Index = %d, Value = %f\n", dfit->first, dfit->second);
   }
   while(!allNegative(&deltaFE)) {
      updateEtas(&deltaFE, b);
      printData(result);
      calcDeltaFE(&deltaFE, deltaEta);
      for(dfit = (deltaFE).begin(); dfit != (deltaFE).end(); dfit++)
      {
        printf("MinMax FE: Index = %d, Value = %f\n", dfit->first, dfit->second);
      }
   }    
    printData(result);
    fclose(result);
}
void MinMax(double deltaEta, double b)
{
   char results[] = "Triangulation Files/ODE Result.txt";
   FILE* result = fopen(results, "w");
   map<int, double> deltaFE;
   map<int, Edge>::iterator eit;
   double initRadii[Triangulation::vertexTable.size()];
   int steps = 500;
   double dt = 0.005;
   for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++) {
       deltaFE.insert(pair<int, double>(eit->first, 0));
   }
   printData(result);
   map<int, double>::iterator dfit;
   
   Triangulation::getRadii(initRadii);
   yamabeFlow(dt, initRadii, steps, true);
   calcDeltaFE(&deltaFE, deltaEta);
   for(dfit = (deltaFE).begin(); dfit != (deltaFE).end(); dfit++)
   {
      printf("MinMax FE: Index = %d, Value = %f\n", dfit->first, dfit->second);
   }
   printData(result);
   while(!allNegative(&deltaFE)) {
      updateEtas(&deltaFE, b);
      printData(result);
      Triangulation::getRadii(initRadii);
      yamabeFlow(dt, initRadii, steps, true);
      calcDeltaFE(&deltaFE, deltaEta);
      for(dfit = (deltaFE).begin(); dfit != (deltaFE).end(); dfit++)
      {
        printf("MinMax FE: Index = %d, Value = %f\n", dfit->first, dfit->second);
      }
   }
    Triangulation::getRadii(initRadii);
    //yamabeFlow(dt, initRadii, steps, true);
    printData(result);
    fclose(result);
}
