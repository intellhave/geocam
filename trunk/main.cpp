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
        //Triangulation::vertexTable[i].setRadius(1.0);
        Triangulation::vertexTable[i].setRadius((0.4 + i/5.0));
    } 
    for(int i = 1; i <= Triangulation::edgeTable.size(); i++)
    {
        //Triangulation::edgeTable[i].setEta(1.0);
        Triangulation::edgeTable[i].setEta(0.3 + i/7.0);
    }
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
//    double deltaEta = 0.05;
//    double deltaRadius = 0.05;
//    double a = 0.1;
//    double b = 0.1;
//     
//   
//    //MinMax(deltaRadius, a, deltaEta, b);
//    MinMax(deltaEta, b);

    vector<double> radii;
    vector<double> curvs;
    double initRadii[Triangulation::vertexTable.size()];
    Triangulation::getRadii(initRadii);
    double dt = 0.020;
    int numSteps = 400;
    yamabeFlow(&radii, &curvs, dt, initRadii, numSteps, true);
    printResultsStep("Triangulation Files/ODE Result.txt", &radii, &curvs);
    system("PAUSE");
}
