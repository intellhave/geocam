/**************************************************************
Class: Main
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 10, 2008
**************************************************************/
#include <cstdlib>
#include <iostream>
#include <vector>
#include "simplex/simplex.h"
#include <map>
#include <fstream>
#include <string>
#include "simplex/vertex.h"
#include <sstream>
#include <ctime>
#include "triangulation.h"
#include "triangulationmath.h"
#include "triangulationmorph.h"
#include "triangulationInputOutput.h"
#include "miscmath.h"
#include "triangulationPlane.h"
#include "delaunay.h"
#include <ctime>
#include <iomanip>
#include <cmath>
#include "spherical/sphericalmath.h"
#include "hyperbolic/hyperbolicmath.h"
#define PI 	3.141592653589793238

using namespace std;

void runFlow()
{
//char from[] = "Triangulations/manifold.txt";
//char to[] = "Triangulations/manifold converted.txt";
//makeTriangulationFile(from, to);
char to[] = "Triangulations/Tetrahedron.txt";
readTriangulationFile(to);

//flip(Triangulation::edgeTable[1]);  
//addNewVertex(Triangulation::faceTable[1], 1);
//removeVertex(Triangulation::vertexTable[3]);
//addLeaf(Triangulation::edgeTable[1], 2);
//addNewVertex(Triangulation::faceTable[1], 1);
//map<int, Face>::iterator fit;
//
//for(int i = 1; i < 2; i++)
//{
//fit = Triangulation::faceTable.begin();
//cout << fit->first << "\n";
//addHandle(fit->second, 1);
//}
//addCrossCap(Triangulation::faceTable[1], 1);
//writeTriangulationFile(to);
   srand(time(NULL));
//   
   
    int vertexSize = Triangulation::vertexTable.size();
    double weights[] = {0.1, 0.1, 0.1, 0.2};
    double product = 1;
    for(int i = 1; i <= vertexSize; i++)
   {
       //weights[i - 1] = acos(-1/3.0)/2 - (rand() %10 + 0.0)/10.0;
      //weights[i - 1] = 2;
      //weights[i - 1] = (rand()%10 + 1)/10.0;
   }
//   while(Triangulation::vertexTable[1].getLocalEdges()->size() > 3)
//   {
//       Vertex v = Triangulation::vertexTable[1];
//       cout << "Edge: " << (*(v.getLocalEdges()))[0] << "\n";
//       int remove = 0;
//      // if((*(v.getLocalEdges()))[0] == 1)
////       {
////        remove = 1 ;
////       }
//       flip(Triangulation::edgeTable[(*(v.getLocalEdges()))[remove]]);                                                    
//   }
   //flip(Triangulation::edgeTable[1]); 
 // writeTriangulationFile(to);                                                    
   char fileName[] = "Triangulations/ODE Result.txt";
   time_t start, end;
   time(&start);
   vector<double> weightsR;
   vector<double> curvatures;
  sphericalCalcFlow(&weightsR, &curvatures, 0.05, weights,2000, true);
  printResultsStep(fileName, &weightsR, &curvatures);
  //printResultsNum(fileName, &weightsR, &curvatures);
   time(&end);
   cout << difftime(end, start) << " seconds" << endl;

}


int main(int argc, char *argv[])
{
    //runFlow();
  //  Line l(1, 1, 2, 3);
//    cout << "Initial X: " << l.getInitialX() << endl;
//    cout << "Initial Y: " << l.getInitialY() << endl;
//    cout << "Ending X: " << l.getEndingX() << endl;
//    cout << "Ending Y: " << l.getEndingY() << endl;
//    cout << "Slope: " << l.getSlope() << endl;
//    cout << "Intercept: " << l.getIntercept() << endl;
//    cout << "Is Vertical? " << l.isVertical() << endl;
//    cout << "(3,5) is on the line? " << l.isOnLine(3,5) << endl;
//    cout << "(0,0) is below? " << l.isBelow(0,0) << endl;
//    cout << "(2, -2) is below? " << l.isBelow(2,-2) << endl;
//Point p1(1, 2);
//Point p2(4, 6);
//vector<Point> solutions = circleIntersection(p1, 3, p2, 4);
//for(int i = 0; i < solutions.size(); i++)
//{
//  Point sol = solutions[i];
//  cout << "Solution " << i << ": (" << sol.x << ", " << sol.y << ")\n";
//}
//vector<double> quadSolutions = quadratic(1, -7, 12);
//for(int i = 0; i < quadSolutions.size(); i++)
//{
//  double sol = quadSolutions[i];
//  cout << "Solution " << i << ": " << sol << "\n";
//}

    srand(time(NULL));
    generateTriangulation(25);
    system("PAUSE");
    flipAlgorithm();
    system("PAUSE");
    return 0;
}





