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
     generateTriangulation(15);
     char filename[] = "Triangulations/Plane Test.txt";
     writeTriangulationFile(filename);
     TriangulationCoordinateSystem tp;
     tp.generatePlane();
     int index;
     int edgeSize = Triangulation::edgeTable.size();
     int vertexSize = Triangulation::vertexTable.size();
     cout << "Pick a line index between 1 and " << edgeSize << ":  ";
     cin >> index;
     while(index != 0)
     {
        if(!tp.containsLine(index))
        {
           cout << "Line is not in system!\n";
        }
        if(index <= edgeSize && index >= 1)
        {
             Line l = tp.getLine(index);
             cout << "Line " << index << "\n";
             cout << "Initial X: " << l.getInitialX() << endl;
             cout << "Initial Y: " << l.getInitialY() << endl;
             cout << "Ending X: " << l.getEndingX() << endl;
             cout << "Ending Y: " << l.getEndingY() << endl; 
        }
//        cout << "\nPick a point index between 1 and " << vertexSize << ": ";
//        cin >> index;
//        if(index <= vertexSize && index >= 1)
//        {
//           Point p = tp.getPoint(index);
//           cout << "Point " << index << " (" << p.x << ", " << p.y << ")\n";
//        }
        cout << "Pick a line index between 1 and " << edgeSize << ":  ";
        cin >> index;
     }
     tp.printToFile(filename);
}
int main(int argc, char *argv[])
{
    //runFlow();
    //testPointLineCircle();
    //testDelaunay();
    testGeneratePlane();


    system("PAUSE");
    return 0;
}





