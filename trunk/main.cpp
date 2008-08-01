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

//char from[] = "Triangulations/manifold.txt";
//char to[] = "Triangulations/manifold converted.txt";
//makeTriangulationFile(from, to);
char to[] = "Triangulations/icosahedron.txt";
readTriangulationFile(to);


//flip(Triangulation::edgeTable[1]);  
//addNewVertex(Triangulation::faceTable[3], 1);
//removeVertex(Triangulation::vertexTable[3]);
//addLeaf(Triangulation::edgeTable[1], 2);
//addNewVertex(Triangulation::faceTable[5], 1);
//addNewVertex(Triangulation::faceTable[12], 1);
//map<int, Face>::iterator fit;

   srand(time(NULL));
  
  
    int vertexSize = Triangulation::vertexTable.size();
    double weights[vertexSize];

    for(int i = 1; i <= vertexSize; i++)
   {
      // weights[i - 1] = 3.141592653589793238/(rand() % 100 + 1.0);
       //weights[i - 1] = (i+0.0)/(2*vertexSize);
    //  weights[i-1] = 0.6027;
      //weights[i - 1] = acos(1)/2 + (rand() % 5+1.0)/100.0;
      weights[i - 1] =   (rand() % 80 + 1)/100.0;
      
   }
//                                                   
   char fileName[] = "Triangulations/ODE Result.txt";
   time_t start, end;
   time(&start);
   vector<double> weightsR;
   vector<double> curvatures;
   double dt = 0.03;
   int Steps = 6000;
   
   sphericalCalcFlow(&weightsR, &curvatures, dt, weights, Steps, true);
   printResultsStep(fileName, &weightsR, &curvatures);
   //printResultsNum(fileName, &weightsR, &curvatures);

//   calcFlow(&weightsR, &curvatures, dt, weights, Steps, false);
//   printResultsStep(fileName, &weightsR, &curvatures);
////printResultsNum(fileName, &weightsR, &curvatures);

//   hyperbolicCalcFlow(&weightsR, &curvatures, dt, weights, Steps, false);
//   printResultsStep(fileName, &weightsR, &curvatures);
//printResultsNum(fileName, &weightsR, &curvatures);

   time(&end);
   cout << difftime(end, start) << " second(s)" << endl;
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

//     firstTriangle(1.0, 1.0, 1.0);
//     addTriangle(Triangulation::edgeTable[1], 1.0, 1.97387802015);
//     addTriangle(Triangulation::edgeTable[2], 1.97387802015, 1.0);
//     addTriangle(Triangulation::edgeTable[3], 1.0, 1.97387802015);
//     addTriangle(Triangulation::edgeTable[4], Triangulation::edgeTable[6]);
//     addTriangle(Triangulation::edgeTable[7], Triangulation::edgeTable[9]);
//     addTriangle(Triangulation::edgeTable[8], Triangulation::edgeTable[5]);
//     Triangulation::vertexTable[1].setWeightIndependent(0);
//     Triangulation::vertexTable[2].setWeightIndependent(0);
//     Triangulation::vertexTable[3].setWeightIndependent(0);
//     Triangulation::vertexTable[4].setWeightIndependent(1.466);
//     Triangulation::vertexTable[5].setWeightIndependent(1.466);
//     Triangulation::vertexTable[6].setWeightIndependent(1.466);
//
//    
//    for(int i = 1; i <= 9; i++)
//     {
//          cout << (*(Triangulation::edgeTable[i].getLocalVertices()))[0] << ", " << (*(Triangulation::edgeTable[i].getLocalVertices()))[1] << endl;
//          cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[i]) << endl;
//          cout << getHeight(Triangulation::faceTable[(*(Triangulation::edgeTable[i].getLocalFaces()))[0]], Triangulation::edgeTable[i]) << " ";
//          cout << getHeight(Triangulation::faceTable[(*(Triangulation::edgeTable[i].getLocalFaces()))[1]], Triangulation::edgeTable[i]) << endl;
//     }
//     
//    TriangulationCoordinateSystem tcs;
//    tcs.generatePlane();
//    tcs.printToFile("Triangulations/flips/Step 0.txt");
//    writeTriangulationFile("Triangulations/flips/Figure 0.txt");
//    
//    weightedFlipAlgorithm();


     
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[1]) << endl;
//    cout << getHeight(Triangulation::faceTable[(*(Triangulation::edgeTable[1].getLocalFaces()))[0]], Triangulation::edgeTable[1]) << " ";
//    cout << getHeight(Triangulation::faceTable[(*(Triangulation::edgeTable[1].getLocalFaces()))[1]], Triangulation::edgeTable[1]) << endl;
//    
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[2]) << endl;
//    cout << getHeight(Triangulation::faceTable[(*(Triangulation::edgeTable[2].getLocalFaces()))[0]], Triangulation::edgeTable[1]) << " ";
//    cout << getHeight(Triangulation::faceTable[(*(Triangulation::edgeTable[2].getLocalFaces()))[1]], Triangulation::edgeTable[1]) << endl;
//
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[3]) << endl;
//    cout << getHeight(Triangulation::faceTable[(*(Triangulation::edgeTable[3].getLocalFaces()))[0]], Triangulation::edgeTable[1]) << " ";
//    cout << getHeight(Triangulation::faceTable[(*(Triangulation::edgeTable[3].getLocalFaces()))[1]], Triangulation::edgeTable[1]) << endl;
//
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[4]) << "*" << endl;
//    cout << getHeight(Triangulation::faceTable[(*(Triangulation::edgeTable[4].getLocalFaces()))[0]], Triangulation::edgeTable[1]) << " ";
//    cout << getHeight(Triangulation::faceTable[(*(Triangulation::edgeTable[4].getLocalFaces()))[1]], Triangulation::edgeTable[1]) << endl;
//
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[5]) << endl;
//    cout << getHeight(Triangulation::faceTable[(*(Triangulation::edgeTable[5].getLocalFaces()))[0]], Triangulation::edgeTable[1]) << " ";
//    cout << getHeight(Triangulation::faceTable[(*(Triangulation::edgeTable[5].getLocalFaces()))[1]], Triangulation::edgeTable[1]) << endl;
//
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[6]) << endl;
//    cout << getHeight(Triangulation::faceTable[(*(Triangulation::edgeTable[6].getLocalFaces()))[0]], Triangulation::edgeTable[1]) << " ";
//    cout << getHeight(Triangulation::faceTable[(*(Triangulation::edgeTable[6].getLocalFaces()))[1]], Triangulation::edgeTable[1]) << endl;
//
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[7]) << "*" << endl;
//    cout << getHeight(Triangulation::faceTable[(*(Triangulation::edgeTable[7].getLocalFaces()))[0]], Triangulation::edgeTable[1]) << " ";
//    cout << getHeight(Triangulation::faceTable[(*(Triangulation::edgeTable[7].getLocalFaces()))[1]], Triangulation::edgeTable[1]) << endl;
//
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[8]) << "*" << endl;
//    cout << getHeight(Triangulation::faceTable[(*(Triangulation::edgeTable[8].getLocalFaces()))[0]], Triangulation::edgeTable[1]) << " ";
//    cout << getHeight(Triangulation::faceTable[(*(Triangulation::edgeTable[8].getLocalFaces()))[1]], Triangulation::edgeTable[1]) << endl;
//
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[9]) << endl;
//    cout << getHeight(Triangulation::faceTable[(*(Triangulation::edgeTable[9].getLocalFaces()))[0]], Triangulation::edgeTable[1]) << " ";
//    cout << getHeight(Triangulation::faceTable[(*(Triangulation::edgeTable[9].getLocalFaces()))[1]], Triangulation::edgeTable[1]) << endl;

//    
//    TriangulationCoordinateSystem tcs;
//    tcs.generatePlane();
//    tcs.printToFile("Triangulations/flips/Step 0.txt");
//    writeTriangulationFile("Triangulations/flips/Figure 0.txt");
//    
//    flip(Triangulation::edgeTable[5]);
//    tcs.update();
//    tcs.printToFile("Triangulations/flips/Step 1.txt");
//    writeTriangulationFile("Triangulations/flips/Figure 1.txt");
//
//    flip(Triangulation::edgeTable[6]);
//    tcs.update();
//    tcs.printToFile("Triangulations/flips/Step 2.txt");
//    writeTriangulationFile("Triangulations/flips/Figure 2.txt");
//    
//    flip(Triangulation::edgeTable[9]);
//    tcs.printToFile("Triangulations/flips/Step 3.txt");
//    writeTriangulationFile("Triangulations/flips/Figure 3.txt");
//
//    cout << "_______________________________________________________________" << endl;
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[1]) << endl;
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[2]) << endl;
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[3]) << endl;
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[4]) << endl;
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[5]) << endl;
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[6]) << endl;
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[7]) << endl;
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[8]) << endl;
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[9]) << endl;
//    
//    flip(Triangulation::edgeTable[1]);
//    tcs.update();
//    tcs.printToFile("Triangulations/flips/Step 4.txt");
//    writeTriangulationFile("Triangulations/flips/Figure 4.txt");
//
//    flip(Triangulation::edgeTable[2]);
//    tcs.update();
//    tcs.printToFile("Triangulations/flips/Step 5.txt");
//    writeTriangulationFile("Triangulations/flips/Figure 5.txt");
//    
//    flip(Triangulation::edgeTable[3]);
//    tcs.update();
//    tcs.printToFile("Triangulations/flips/Step 6.txt");
//    writeTriangulationFile("Triangulations/flips/Figure 6.txt");
//    
//    flip(Triangulation::edgeTable[2]);
//    tcs.update();
//    tcs.printToFile("Triangulations/flips/Step 7.txt");
//    writeTriangulationFile("Triangulations/flips/Figure 7.txt");
//    
//    flip(Triangulation::edgeTable[7]);
//    tcs.update();
//    tcs.printToFile("Triangulations/flips/Step 8.txt");
//    writeTriangulationFile("Triangulations/flips/Figure 8.txt");
//
//    
//    cout << "_______________________________________________________________" << endl;
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[1]) << endl;
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[2]) << endl;
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[3]) << endl;
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[4]) << endl;
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[5]) << endl;
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[6]) << endl;
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[7]) << endl;
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[8]) << endl;
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[9]) << endl;
//    
//    cout << "(" << (*(Triangulation::faceTable[1].getLocalVertices()))[0];
//    cout << ", " << (*(Triangulation::faceTable[1].getLocalVertices()))[1];
//    cout << ", " << (*(Triangulation::faceTable[1].getLocalVertices()))[2];
//    cout << ")" << endl;
//    
//    cout << "(" << (*(Triangulation::faceTable[2].getLocalVertices()))[0];
//    cout << ", " << (*(Triangulation::faceTable[2].getLocalVertices()))[1];
//    cout << ", " << (*(Triangulation::faceTable[2].getLocalVertices()))[2];
//    cout << ")" << endl;
//    
//    cout << "(" << (*(Triangulation::faceTable[3].getLocalVertices()))[0];
//    cout << ", " << (*(Triangulation::faceTable[3].getLocalVertices()))[1];
//    cout << ", " << (*(Triangulation::faceTable[3].getLocalVertices()))[2];
//    cout << ")" << endl;
//
//    cout << "(" << (*(Triangulation::faceTable[4].getLocalVertices()))[0];
//    cout << ", " << (*(Triangulation::faceTable[4].getLocalVertices()))[1];
//    cout << ", " << (*(Triangulation::faceTable[4].getLocalVertices()))[2];
//    cout << ")" << endl;
//
//    cout << "(" << (*(Triangulation::faceTable[5].getLocalVertices()))[0];
//    cout << ", " << (*(Triangulation::faceTable[5].getLocalVertices()))[1];
//    cout << ", " << (*(Triangulation::faceTable[5].getLocalVertices()))[2];
//    cout << ")" << endl;
//
//    cout << "(" << (*(Triangulation::faceTable[6].getLocalVertices()))[0];
//    cout << ", " << (*(Triangulation::faceTable[6].getLocalVertices()))[1];
//    cout << ", " << (*(Triangulation::faceTable[6].getLocalVertices()))[2];
//    cout << ")" << endl;
//    
//    cout << "(" << (*(Triangulation::faceTable[7].getLocalVertices()))[0];
//    cout << ", " << (*(Triangulation::faceTable[7].getLocalVertices()))[1];
//    cout << ", " << (*(Triangulation::faceTable[7].getLocalVertices()))[2];
//    cout << ")" << endl;
//    
//    cout << "(" << (*(Triangulation::edgeTable[1].getLocalFaces()))[0];
//    cout << ", " << (*(Triangulation::edgeTable[1].getLocalFaces()))[1];
//    cout << ")" << endl;
//
//    cout << "(" << (*(Triangulation::edgeTable[2].getLocalFaces()))[0];
//    cout << ", " << (*(Triangulation::edgeTable[2].getLocalFaces()))[1];
//    cout << ")" << endl;
//    
//    cout << "(" << (*(Triangulation::edgeTable[3].getLocalFaces()))[0];
//    cout << ", " << (*(Triangulation::edgeTable[3].getLocalFaces()))[1];
//    cout << ")" << endl;
//
//    cout << "(" << (*(Triangulation::edgeTable[4].getLocalFaces()))[0];
//    cout << ", " << (*(Triangulation::edgeTable[4].getLocalFaces()))[1];
//    cout << ")" << endl;
//
//    cout << "(" << (*(Triangulation::edgeTable[5].getLocalFaces()))[0];
//    cout << ", " << (*(Triangulation::edgeTable[5].getLocalFaces()))[1];
//    cout << ")" << endl;
//
//    cout << "(" << (*(Triangulation::edgeTable[6].getLocalFaces()))[0];
//    cout << ", " << (*(Triangulation::edgeTable[6].getLocalFaces()))[1];
//    cout << ")" << endl;
//
//    cout << "(" << (*(Triangulation::edgeTable[7].getLocalFaces()))[0];
//    cout << ", " << (*(Triangulation::edgeTable[7].getLocalFaces()))[1];
//    cout << ")" << endl;
//
//    cout << "(" << (*(Triangulation::edgeTable[8].getLocalFaces()))[0];
//    cout << ", " << (*(Triangulation::edgeTable[8].getLocalFaces()))[1];
//    cout << ")" << endl;
//
//    cout << "(" << (*(Triangulation::edgeTable[9].getLocalFaces()))[0];
//    cout << ", " << (*(Triangulation::edgeTable[9].getLocalFaces()))[1];
//    cout << ")" << endl;
    
//    flip(Triangulation::edgeTable[9]);
//    tcs.update();
//    tcs.printToFile("Triangulations/flips/Step 7.txt");
//    writeTriangulationFile("Triangulations/flips/Figure 1.txt");
//        
//    cout << "_______________________________________________________________" << endl;
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[1]) << endl;
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[2]) << endl;
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[3]) << endl;
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[4]) << endl;
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[5]) << endl;
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[6]) << endl;
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[7]) << endl;
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[8]) << endl;
//    cout << "  " << isWeightedDelaunay(Triangulation::edgeTable[9]) << endl;

    
    //flip(Triangulation::edgeTable[1]);
    
    //weightedFlipAlgorithm();
    generateTriangulation(25);
    generateWeights();
    TriangulationCoordinateSystem tcs;
    tcs.generatePlane();
    tcs.addDuals(Triangulation::vertexTable[1]);
    tcs.printDuals("Triangulations/duals.txt");
    tcs.printToFile("Triangulation/ODE Result.txt");
    system("PAUSE");
    return 0;
}
