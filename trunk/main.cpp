/**************************************************************
Class: Main
Author: Alex Henniges, Tom Williams, Mitch Wilson
Version: June 10, 2008
**************************************************************/

#include "3DTriangulation/3DInputOutput.h"
#include <iostream>
#include <cmath>
#include <ctime>
#include "triangulation/triangulationPlane.h"
#include "3DTriangulation/3Dtriangulationmath.h"
#include "triangulation/smallmorphs.h"
#define PI 	3.141592653589793238

using namespace std;

int main(int argc, char *argv[])
{   
    vector<double> radii;
    vector<double> curvs;
    
    time_t start, end;
    
    make3DTriangulationFile("Triangulation Files/sphere3.txt",
                            "Triangulation Files/manifold converted.txt");
    read3DTriangulationFile("Triangulation Files/manifold converted.txt");
    write3DTriangulationFile("Triangulation Files/manifold.txt");

    srand(time(NULL));
    double weights[Triangulation::vertexTable.size()];
    for(int i = 0; i < Triangulation::vertexTable.size(); i++) {
//            if(i >= 3 && i <= 12 || i == 14)
//            {
//                 weights[i] = 6;
//            }
//            else { weights[i] = 1; }
            weights[i] = 1;
            //weights[i] = 1.5 + (rand() % 100) / 100.0;
    }
    double dt = 0.03;
    int numSteps = 200;
    
    time(&start);
    yamabeFlow(&radii, &curvs, dt, weights, numSteps, false);
    time(&end);
    printResultsStep("Triangulation Files/ODE Results.txt", &radii, &curvs);
    cout << "Elapsed time was " << difftime(end, start) << " seconds.\n";
    system("PAUSE");
    return 0;
}
