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
#define PI 	3.141592653589793238

using namespace std;
void createMatrix();
int main(int argc, char *argv[])
{   
    ifstream infile("Triangulation Files/radii.txt");
    vector<double> weightsVec;
    while(infile.good())
    {
        double weight;
        infile >> weight;
        weightsVec.push_back(weight);
    }
    cout << weightsVec.size();
    system("PAUSE");
    return 0;
}

void createMatrix() 
{
    make3DTriangulationFile("Triangulation Files/sphere3.txt",
                            "Triangulation Files/manifold converted.txt");
    read3DTriangulationFile("Triangulation Files/manifold converted.txt");
    
    for(int l = 0; l <= 5; l++) 
    {
       Triangulation::vertexTable[l].setRadius(5);
    }
    Triangulation::vertexTable[23].setRadius(3);
    ofstream output("Triangulation Files/matrix.txt");
    
    for(int i = 0; i < 50; i++)
    {
        for(int k = 11; k < 23; k++) 
        {
           Triangulation::vertexTable[k].setRadius(.1*i + 0.1);
        }
        for(int j = 0; j < 50; j++)
        {
           for(int k = 5; k < 11; k++) 
           {
                Triangulation::vertexTable[k].setRadius(.1*j + 0.1);
           }
           double k0 = curvature3D(Triangulation::vertexTable[0]);
           double k5 = curvature3D(Triangulation::vertexTable[5]);
           double k11 = curvature3D(Triangulation::vertexTable[11]);
           double k23 = curvature3D(Triangulation::vertexTable[23]);
           output << (.1*i + 0.1) << ", " << (.1*j + 0.1) << ": (" << k0 << ", " << k5;
           output << ", " << k11 << ", " << k23 << ")\n";
        }
    }
            
}
