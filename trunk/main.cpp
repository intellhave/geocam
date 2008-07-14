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
#include <ctime>
#include <iomanip>
#include <cmath>
#include "spherical/sphericalmath.h"
#include "hyperbolic/hyperbolicmath.h"
#define PI 	3.141592653589793238

using namespace std;




void runFlow()
{
     readTriangulationFile("Triangulations/double triangle");
     
     addCrossCap(Triangulation::faceTable[1], 1);
                            
//     char fileName[] = "Triangulations/ODE Result.txt";
//     time_t start, end;
//     time(&start);
//     vector<double> weightsR;
//     vector<double> curvatures;
//     sphericalCalcFlow(&weightsR, &curvatures, 0.03, weights,1000, false);
//     printResultsStep(fileName, &weightsR, &curvatures);
//     time(&end);
//     cout << difftime(end, start) << " seconds" << endl;
    
}


int main(int argc, char *argv[])
{
    readTriangulationFile("Triangulations/Tetrahedron.txt");
    cout << endl;
    system("PAUSE");
    return 0;
}





