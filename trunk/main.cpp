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
#include "TriangulationCoordinateSystem.h"
#include <ctime>
#include <iomanip>
#include <cmath>
#include "spherical/sphericalmath.h"
#include "hyperbolic/hyperbolicmath.h"
#define PI 	3.141592653589793238

using namespace std;

void runFlow()
{

char from[] = "Triangulations/testsample.txt";
char to[] = "Triangulations/testsampleresult.txt";
makeTriangulationFile(from, to);
readTriangulationFile(to);

//<<<<<<< .mine
////writeTriangulationFile(to);
//
////char to[] = "Triangulations/torus-9.txt";
//
////char from[] = "Triangulations/manifold.txt";
////char to[] = "Triangulations/manifold converted.txt";
////makeTriangulationFile(from, to);
////char to[] = "Triangulations/testsampleresult.txt";
////readTriangulationFile(to);
//
//
////flip(Triangulation::edgeTable[1]);  
////addNewVertex(Triangulation::faceTable[3], 1);
////removeVertex(Triangulation::vertexTable[3]);
////addLeaf(Triangulation::edgeTable[1], 2);
////addNewVertex(Triangulation::faceTable[5], 1);
////addNewVertex(Triangulation::faceTable[12], 1);
////map<int, Face>::iterator fit;
//
//   srand(time(NULL));
////   
//   
//    int vertexSize = Triangulation::vertexTable.size();
//    double weights[vertexSize];
////    weights[0] = 0.74;
////    weights[1]=  0.54;
////    weights[2] = 0.23;
////    weights[3] = 0.04;
////    weights[4] = 0.33;
////    weights[5] = 0.37;
////    weights[6] = 0.80;
////    weights[7] = 0.89;
////    weights[8] = 0.66;
////    weights[9] = 0.61;
//    double product = 1;
//    for(int i = 1; i <= vertexSize; i++)
//   {
//       //weights[i - 1] = 3.141592653589793238/(rand() % 100 + 1.0);
//       //weights[i - 1] = (i+0.0)/(2*vertexSize);
//      weights[i-1] = 0.6027;
//      //weights[i - 1] = acos(1)/2 + (rand() % 5+1.0)/100.0;
//      //weights[i - 1] =   (rand() % 80 + 1)/100.0;
//
//   }
//                                                   
//   char fileName[] = "Triangulations/ODE Result.txt";
//   time_t start, end;
//   time(&start);
//   vector<double> weightsR;
//   vector<double> curvatures;
//   double dt = 0.03;
//   int Steps = 400;
//   
////   sphericalCalcFlow(&weightsR, &curvatures, dt, weights, Steps, false);
////   printResultsStep(fileName, &weightsR, &curvatures);
//////printResultsNum(fileName, &weightsR, &curvatures);
//
////   calcFlow(&weightsR, &curvatures, dt, weights, Steps, false);
////   printResultsStep(fileName, &weightsR, &curvatures);
//////printResultsNum(fileName, &weightsR, &curvatures);
//
//   hyperbolicCalcFlow(&weightsR, &curvatures, dt, weights, Steps, false);
//   printResultsStep(fileName, &weightsR, &curvatures);
////printResultsNum(fileName, &weightsR, &curvatures);
//
//   time(&end);
//   cout << difftime(end, start) << " real time seconds" << endl;
//   cout << dt*Steps << " simulated seconds" << endl;
//=======
}
void testDelaunay()
{
    //firstTriangle(1.0, 1.0, 1.0);
    //addTriangle(Triangulation::edgeTable[1], 1.0, 1.0);
    char filename[] = "Triangulations/Plane Test.txt";
//    cout << isDelaunay(Triangulation::edgeTable[1]) << endl;
//    flip(Triangulation::edgeTable[1]);
//    writeTriangulationFile(filename);
//    cout << isDelaunay(Triangulation::edgeTable[1]) << endl;
    generateTriangulation(20);
    map<int, Edge>::iterator eit;
    for(eit = Triangulation::edgeTable.begin(); eit != Triangulation::edgeTable.end(); eit++)
    {
         cout << eit->second.getLength() << endl;
    }
    system("PAUSE");
    cout << isWeightedDelaunay(Triangulation::edgeTable[13]) << endl;
    
    
    system("PAUSE");
    for(int i = 1; i < Triangulation::edgeTable.size(); i++)
    {
        cout << "Edge " << i << ": ";
        cout << isDelaunay(Triangulation::edgeTable[i]) << "    ";
        //printLengths(Triangulation::edgeTable[i]);
        cout << endl;
    }
}

void testGeneratePlane()
{
     generateTriangulation(30);
     char filename[] = "Triangulations/Plane Test.txt";
     writeTriangulationFile(filename);
     TriangulationCoordinateSystem tp;
     tp.generatePlane();
     int index;
     int edgeSize = Triangulation::edgeTable.size();
     int vertexSize = Triangulation::vertexTable.size();
     for(int i = 1; i <= edgeSize; i++)
     {
        cout << i << ": " << Triangulation::edgeTable[i].getLength() << "     ";
        if(i % 3 == 0)
        {
             cout << endl;
        }
     }
     cout << "\n";
//     cout << "Pick a line index between 1 and " << edgeSize << ":  ";
//     cin >> index;
//     while(index != 0)
//     {
//        if(!tp.containsLine(index))
//        {
//           cout << "Line is not in system!\n";
//        }
//        if(index <= edgeSize && index >= 1)
//        {
//             Line l = tp.getLine(index);
//             cout << "Line " << index << "\n";
//             cout << "Initial X: " << l.getInitialX() << endl;
//             cout << "Initial Y: " << l.getInitialY() << endl;
//             cout << "Ending X: " << l.getEndingX() << endl;
//             cout << "Ending Y: " << l.getEndingY() << endl;
//             cout << "Length: " << l.getLength() << endl; 
//        }
//        cout << "\nPick a point index between 1 and " << vertexSize << ": ";
//        cin >> index;
//        if(index <= vertexSize && index >= 1)
//        {
//           Point p = tp.getPoint(index);
//           cout << "Point " << index << " (" << p.x << ", " << p.y << ")\n";
//        }
//        cout << "Pick a line index between 1 and " << edgeSize << ":  ";
//        cin >> index;
//     }
     char fileN[] = "Triangulations/ODE Result.txt";
     tp.printToFile(fileN);
     flipAlgorithm();
     char fileN2[] = "Triangulations/ODE Result 2.txt";
     tp.generatePlane();
     tp.printToFile(fileN2);
}
void testMiscMath()
{
     Line l(4,5,1,1);
     Point p = findPoint(l, 3, -acos(3/5.));
     printPoint(p);
     Line l2(l.getEnding(), p);
     Line l1(l.getInitial(), p);
     cout << l2.getLength() << " ";
     cout << l1.getLength();
     cout << endl;
}
int main(int argc, char *argv[])
{
    
    generateTriangulation(100);
    generateWeights();
    TriangulationCoordinateSystem tcs;
    tcs.generatePlane();
    tcs.printToFile("Triangulations/ODE Result.txt");
    system("PAUSE");
    
    weightedFlipAlgorithm();
    tcs.generatePlane();
    tcs.printToFile("Triangulations/ODE Result.txt");
    system("PAUSE");
    return 0;
}
