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
void readWeights(char *filename, double *radii);
int main(int argc, char *argv[])
{   
    // The triangulation file to read in
    char in[] = "Triangulation Files/3D Manifolds/Lutz Format/pentachron.txt";
    // Convert from Lutz Format to standrad format
    make3DTriangulationFile(in, "Triangulation Files/manifold converted.txt");
    // Read in and construct triangulation from file
    read3DTriangulationFile("Triangulation Files/manifold converted.txt");
    
    srand ( time(NULL) ); // Seed the random generator
    
    
    double initWeights[Triangulation::vertexTable.size()];
    // Read in weights from a file
    readWeights("Triangulation Files/radii.txt", initWeights);
    
    vector<double> weights;
    vector<double> curvs;
    double dt = 0.005;
    int numSteps = 250;
    bool normalized = true;
    yamabeFlow(&weights, &curvs, dt, initWeights, numSteps, normalized);
    printResultsStep("Triangulation Files/ODE Result.txt", &weights, &curvs);
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
void readWeights(char *filename, double *radii) 
{
     ifstream input(filename);
     int i = 0;
     while(input.good()) {
        input >> radii[i];
        i++;
     }
}
