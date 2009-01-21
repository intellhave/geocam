/**************************************************************
Class: Main
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 10, 2008
**************************************************************/

#include "3DTriangulation/3DInputOutput.h"
#include <iostream>
#include <fstream>
#include <sstream>
#include <iomanip>
#include <cmath>
#include "triangulation/smallMorphs.h"
#include "3DTriangulation/3DTriangulationMorph.h"
#include <ctime>
#include "triangulation/triangulationPlane.h"
#include "3DTriangulation/3Dtriangulationmath.h"
#include "triangulation/smallmorphs.h"
#include "triangulation/MinMax.h"
#define PI 	3.141592653589793238

using namespace std;
int main(int argc, char *argv[])
{
    char filename[] = "Triangulation Files/3D Manifolds/Lutz Format/pentachron.txt";
    char modified[] = "Triangulation Files/manifold converted.txt";
    
    make3DTriangulationFile(filename, modified);
    read3DTriangulationFile(modified);
    
    for(int i = 1; i <= Triangulation::vertexTable.size(); i++)
    {
        Triangulation::vertexTable[i].setRadius(0.8+0.01*i*i);
        //Triangulation::vertexTable[i].setRadius((0.4 + i/5.0));
    } 
//    for(int i = 1; i <= Triangulation::edgeTable.size(); i++)
//    {
//        //Triangulation::edgeTable[i].setEta(1.0);
//        Triangulation::edgeTable[i].setEta(0.84 + i*.03);
//        
//    }

    
    Triangulation::edgeTable[1].setEta(1.00);
    Triangulation::edgeTable[2].setEta(0.95);
    Triangulation::edgeTable[3].setEta(1.00);
    Triangulation::edgeTable[4].setEta(1.00);
    Triangulation::edgeTable[5].setEta(1.00);
    Triangulation::edgeTable[6].setEta(1.00);
    Triangulation::edgeTable[7].setEta(1.00);
    Triangulation::edgeTable[8].setEta(1.00);
    Triangulation::edgeTable[9].setEta(1.00);
    Triangulation::edgeTable[10].setEta(1.00);

    
//    Vertex v;
    time_t start,end;
//    double dummy;
//    time (&start);
//    for(int i = 0; i < 2000; i++) 
//    {
//       v = Triangulation::vertexTable[i % 16 + 1];
//       dummy = curvature3D(v);
//    }
//    time (&end);
//    double diff1 = difftime(end,start);
//    time (&start);
//    for(int i = 0; i < 2000; i++) 
//    {
//       v = Triangulation::vertexTable[i % 16 + 1];
//       dummy = edgeCurvature(v);
//    }
//    time (&end);
//    double diff2 = difftime(end,start);
//    printf("diff1 = %f, diff2 = %f\n", diff1, diff2);
//    for(int i = 1; i <= Triangulation::vertexTable.size(); i++)
//    {
//        Vertex v = Triangulation::vertexTable[i];
//        printf("Index = %d, Curv1 = %f, Curv2 = %f\n",
//                       i, curvature3D(v), edgeCurvature(v));
//    } 
    
//    for(int j = 1; j < 100; j++) {
//       for(int i = 1; i <= Triangulation::edgeTable.size(); i++)
//       {
//          Triangulation::edgeTable[i].setEta(j*j);
//       }
//       printf("Eta = %d, F = %f\n", j*j, F());
//    }

    double deltaEta = 0.0001;
    double deltaRadius = 0.0001;
    double a = 0.001;
    double b = 0.001;
   
    //MinMax(deltaRadius, a, deltaEta, b);
    MinMax(deltaEta, b);




//    vector<double> radii;
//    vector<double> curvs;
//    double initRadii[Triangulation::vertexTable.size()];
//    Triangulation::getRadii(initRadii);
//    printf("%f, %f, %f, %f, %f\n", initRadii[0], initRadii[1], initRadii[2], initRadii[3], initRadii[4]);
//    double dt = 0.020;
//    //int stepSize = 2;
//    double accuracy = 0.0000001;
//    double precision = 0.0000001;
//    time (&start);
//    yamabeFlow(&radii, &curvs, dt, initRadii, accuracy, precision, true);
//    time (&end);
//    printf("%f\n", difftime(end,start));
//    printResultsStep("Triangulation Files/ODE Result.txt", &radii, &curvs);




    system("PAUSE");
}
